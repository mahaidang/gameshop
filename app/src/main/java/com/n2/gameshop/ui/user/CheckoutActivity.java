package com.n2.gameshop.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.n2.gameshop.R;
import com.n2.gameshop.adapter.OrderDetailAdapter;
import com.n2.gameshop.model.CartItem;
import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;
import com.n2.gameshop.model.User;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.presenter.OrderPresenter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity implements OrderPresenter.View {

    private RecyclerView rvCheckoutItems;
    private TextView tvCheckoutTotal;
    private RadioGroup rgPaymentMethod;
    private TextInputEditText edtCheckoutNote;
    private MaterialButton btnBackToCart;
    private MaterialButton btnPlaceOrder;

    private OrderPresenter presenter;
    private SessionManager sessionManager;
    private OrderDetailAdapter summaryAdapter;

    private List<CartItem> cartItems = new ArrayList<CartItem>();
    private double totalAmount = 0;
    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        presenter = new OrderPresenter(this, this);
        sessionManager = new SessionManager(this);

        initViews();
        setupSummaryList();
        setupActions();

        // Check if we received selected items from Intent
        List<CartItem> selectedItems = (List<CartItem>) getIntent().getSerializableExtra("selected_items");
        if (selectedItems != null) {
            displayCheckoutItems(selectedItems);
        } else {
            // Fallback: load full cart if no specific items passed
            presenter.loadCart();
        }
    }

    private void initViews() {
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        edtCheckoutNote = findViewById(R.id.edtCheckoutNote);
        btnBackToCart = findViewById(R.id.btnBackToCart);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void setupSummaryList() {
        summaryAdapter = new OrderDetailAdapter(this, new ArrayList<OrderDetail>());
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setNestedScrollingEnabled(false);
        rvCheckoutItems.setAdapter(summaryAdapter);
    }

    private void setupActions() {
        btnBackToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = sessionManager.getLoggedInUser();
                if (user == null) {
                    Toast.makeText(CheckoutActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartItems.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                String paymentMethod = rgPaymentMethod.getCheckedRadioButtonId() == R.id.rbCash
                        ? "cash" : "transfer";
                String note = edtCheckoutNote.getText() != null
                        ? edtCheckoutNote.getText().toString().trim() : "";

                btnPlaceOrder.setEnabled(false);
                presenter.placeOrder(user.getId(), cartItems, paymentMethod, note);
            }
        });
    }

    private void displayCheckoutItems(List<CartItem> items) {
        this.cartItems = items;
        double total = 0;
        List<OrderDetail> summaryList = new ArrayList<>();
        
        for (CartItem ci : items) {
            total += ci.getSubtotal();
            
            OrderDetail od = new OrderDetail();
            od.setProductName(ci.getProduct().getName());
            od.setQuantity(ci.getQuantity());
            od.setUnitPrice(ci.getProduct().getPrice());
            summaryList.add(od);
        }
        
        this.totalAmount = total;
        tvCheckoutTotal.setText(nf.format(total) + " ₫");
        summaryAdapter.updateData(summaryList);
    }

    // ===== OrderPresenter.View callbacks =====

    @Override
    public void onCartLoaded(List<CartItem> items, double total) {
        displayCheckoutItems(items);
    }

    @Override
    public void onOrderPlaced(int orderId) {
        new AlertDialog.Builder(this)
                .setTitle("Đặt hàng thành công!")
                .setMessage("Đơn hàng #" + orderId + " đã được tạo.\nTổng: "
                        + nf.format(totalAmount) + " ₫")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onOrdersLoaded(List<Order> orders) { /* not used */ }

    @Override
    public void onOrderDetailsLoaded(List<OrderDetail> details) { /* not used */ }

    @Override
    public void onError(String message) {
        btnPlaceOrder.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
