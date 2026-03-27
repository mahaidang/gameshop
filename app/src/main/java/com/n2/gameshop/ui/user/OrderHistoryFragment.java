package com.n2.gameshop.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.adapter.OrderAdapter;
import com.n2.gameshop.model.CartItem;
import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;
import com.n2.gameshop.model.User;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.presenter.OrderPresenter;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment implements OrderPresenter.View {

    private RecyclerView rvOrderHistory;
    private TextView tvOrderHistoryEmpty;

    private OrderPresenter presenter;
    private OrderAdapter orderAdapter;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new OrderPresenter(requireContext(), this);
        sessionManager = new SessionManager(requireContext());

        rvOrderHistory = view.findViewById(R.id.rvOrderHistory);
        tvOrderHistoryEmpty = view.findViewById(R.id.tvOrderHistoryEmpty);

        orderAdapter = new OrderAdapter(requireContext(), new ArrayList<Order>());
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrderHistory.setAdapter(orderAdapter);

        orderAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(requireContext(), OrderDetailActivity.class);
                intent.putExtra("order_id", order.getId());
                intent.putExtra("order_status", order.getStatus());
                intent.putExtra("order_payment", order.getPaymentMethod());
                intent.putExtra("order_date", order.getCreatedAt());
                intent.putExtra("order_total", order.getTotalAmount());
                intent.putExtra("order_note", order.getNote());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = sessionManager.getLoggedInUser();
        if (user != null) {
            presenter.loadOrdersByUser(user.getId());
        }
    }

    // ===== OrderPresenter.View callbacks =====

    @Override
    public void onOrdersLoaded(List<Order> orders) {
        orderAdapter.updateData(orders);
        if (orders == null || orders.isEmpty()) {
            rvOrderHistory.setVisibility(View.GONE);
            tvOrderHistoryEmpty.setVisibility(View.VISIBLE);
        } else {
            rvOrderHistory.setVisibility(View.VISIBLE);
            tvOrderHistoryEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCartLoaded(List<CartItem> cartItems, double total) { /* not used */ }

    @Override
    public void onOrderPlaced(int orderId) { /* not used */ }

    @Override
    public void onOrderDetailsLoaded(List<OrderDetail> details) { /* not used */ }

    @Override
    public void onError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}

