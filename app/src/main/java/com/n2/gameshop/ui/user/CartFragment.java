package com.n2.gameshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.n2.gameshop.R;
import com.n2.gameshop.adapter.CartAdapter;
import com.n2.gameshop.model.CartItem;
import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;
import com.n2.gameshop.presenter.OrderPresenter;

import java.text.NumberFormat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements OrderPresenter.View {

    private RecyclerView rvCart;
    private TextView tvCartEmpty, tvCartTotal;
    private LinearLayout layoutCartBottom;
    private MaterialButton btnCheckout;

    private OrderPresenter presenter;
    private CartAdapter cartAdapter;
    private List<CartItem> currentCartItems = new ArrayList<CartItem>();
    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new OrderPresenter(requireContext(), this);
        initViews(view);
        setupRecyclerView();
        setupActions();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadCart();
    }

    private void initViews(View view) {
        rvCart = view.findViewById(R.id.rvCart);
        tvCartEmpty = view.findViewById(R.id.tvCartEmpty);
        tvCartTotal = view.findViewById(R.id.tvCartTotal);
        layoutCartBottom = view.findViewById(R.id.layoutCartBottom);
        btnCheckout = view.findViewById(R.id.btnCheckout);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(requireContext(), new ArrayList<CartItem>());
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);

        cartAdapter.setOnCartActionListener(new CartAdapter.OnCartActionListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                presenter.updateQuantity(item.getProduct().getId(), newQuantity);
                presenter.loadCart();
            }

            @Override
            public void onRemoveItem(CartItem item) {
                presenter.removeFromCart(item.getProduct().getId());
                presenter.loadCart();
            }

            @Override
            public void onSelectionChanged(CartItem item, boolean isSelected) {
                calculateSelectedTotal();
            }
        });
    }

    private void setupActions() {
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CartItem> selectedItems = getSelectedItems();
                if (selectedItems.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Intent intent = new Intent(requireContext(), CheckoutActivity.class);
                // We'll pass the selected items to checkout
                intent.putExtra("selected_items", (Serializable) selectedItems);
                startActivity(intent);
            }
        });
    }

    private List<CartItem> getSelectedItems() {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : currentCartItems) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        return selected;
    }

    private void calculateSelectedTotal() {
        double total = 0;
        for (CartItem item : currentCartItems) {
            if (item.isSelected()) {
                total += item.getSubtotal();
            }
        }
        tvCartTotal.setText(nf.format(total) + " ₫");
        btnCheckout.setEnabled(total > 0);
    }

    private void updateEmptyState() {
        if (currentCartItems.isEmpty()) {
            rvCart.setVisibility(View.GONE);
            tvCartEmpty.setVisibility(View.VISIBLE);
            layoutCartBottom.setVisibility(View.GONE);
        } else {
            rvCart.setVisibility(View.VISIBLE);
            tvCartEmpty.setVisibility(View.GONE);
            layoutCartBottom.setVisibility(View.VISIBLE);
        }
    }

    // ===== OrderPresenter.View callbacks =====

    @Override
    public void onCartLoaded(List<CartItem> cartItems, double total) {
        this.currentCartItems = cartItems;
        cartAdapter.updateData(cartItems);
        calculateSelectedTotal();
        updateEmptyState();
    }

    @Override
    public void onOrderPlaced(int orderId) { /* not used here */ }

    @Override
    public void onOrdersLoaded(List<Order> orders) { /* not used here */ }

    @Override
    public void onOrderDetailsLoaded(List<OrderDetail> details) { /* not used here */ }

    @Override
    public void onError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
