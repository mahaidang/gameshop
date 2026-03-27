package com.n2.gameshop.ui.user;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.adapter.OrderDetailAdapter;
import com.n2.gameshop.model.CartItem;
import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;
import com.n2.gameshop.presenter.OrderPresenter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity implements OrderPresenter.View {

    private TextView tvODOrderId, tvODStatus, tvODPayment, tvODDate, tvODTotal, tvODNote;
    private RecyclerView rvOrderDetailItems;
    private OrderDetailAdapter detailAdapter;
    private OrderPresenter presenter;
    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        presenter = new OrderPresenter(this, this);
        initViews();
        displayOrderInfo();

        int orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId > 0) {
            presenter.loadOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Đơn hàng không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvODOrderId = findViewById(R.id.tvODOrderId);
        tvODStatus = findViewById(R.id.tvODStatus);
        tvODPayment = findViewById(R.id.tvODPayment);
        tvODDate = findViewById(R.id.tvODDate);
        tvODTotal = findViewById(R.id.tvODTotal);
        tvODNote = findViewById(R.id.tvODNote);
        rvOrderDetailItems = findViewById(R.id.rvOrderDetailItems);

        detailAdapter = new OrderDetailAdapter(this, new ArrayList<OrderDetail>());
        rvOrderDetailItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderDetailItems.setAdapter(detailAdapter);
    }

    private void displayOrderInfo() {
        int orderId = getIntent().getIntExtra("order_id", -1);
        String status = getIntent().getStringExtra("order_status");
        String payment = getIntent().getStringExtra("order_payment");
        String date = getIntent().getStringExtra("order_date");
        double total = getIntent().getDoubleExtra("order_total", 0);
        String note = getIntent().getStringExtra("order_note");

        tvODOrderId.setText("#" + orderId);
        tvODStatus.setText(status != null ? status : "");
        tvODPayment.setText("cash".equals(payment) ? "Tiền mặt" : "Chuyển khoản");
        tvODDate.setText(date != null ? date : "");
        tvODTotal.setText(nf.format(total) + " ₫");

        if (note != null && !note.isEmpty()) {
            tvODNote.setText("Ghi chú: " + note);
            tvODNote.setVisibility(android.view.View.VISIBLE);
        }
    }

    // ===== OrderPresenter.View callbacks =====

    @Override
    public void onOrderDetailsLoaded(List<OrderDetail> details) {
        detailAdapter.updateData(details);
    }

    @Override
    public void onCartLoaded(List<CartItem> cartItems, double total) { /* not used */ }

    @Override
    public void onOrderPlaced(int orderId) { /* not used */ }

    @Override
    public void onOrdersLoaded(List<Order> orders) { /* not used */ }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

