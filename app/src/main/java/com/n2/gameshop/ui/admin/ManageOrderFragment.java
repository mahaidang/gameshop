package com.n2.gameshop.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.adapter.OrderAdapter;
import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;
import com.n2.gameshop.presenter.AdminPresenter;
import com.n2.gameshop.utils.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageOrderFragment extends Fragment implements AdminPresenter.OrderView {

    private Spinner spinnerOrderFilter;
    private TextView tvTotalRevenue, tvOrderCount;
    private RecyclerView rvOrders;

    private AdminPresenter presenter;
    private OrderAdapter orderAdapter;
    private int currentFilter = Constants.FILTER_ALL;
    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

    private static final String[] FILTER_LABELS = {
            "Tất cả", "Hôm nay", "Tuần này", "Tháng này", "Năm nay"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new AdminPresenter(requireContext());

        spinnerOrderFilter = view.findViewById(R.id.spinnerOrderFilter);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvOrderCount = view.findViewById(R.id.tvOrderCount);
        rvOrders = view.findViewById(R.id.rvOrders);

        setupFilterSpinner();
        setupOrderList();

        presenter.loadOrders(this, currentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadOrders(this, currentFilter);
    }

    private void setupFilterSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_spinner_item, FILTER_LABELS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderFilter.setAdapter(adapter);

        spinnerOrderFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFilter = position;
                presenter.loadOrders(ManageOrderFragment.this, currentFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });
    }

    private void setupOrderList() {
        orderAdapter = new OrderAdapter(requireContext(), new ArrayList<Order>());
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(orderAdapter);

        orderAdapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                presenter.loadOrderDetails(ManageOrderFragment.this, order);
            }
        });
    }

    private void showOrderDetailsDialog(final Order order, List<OrderDetail> details) {
        StringBuilder sb = new StringBuilder();
        sb.append("Đơn hàng #").append(order.getId()).append("\n");
        sb.append("Ngày: ").append(order.getCreatedAt()).append("\n");
        sb.append("Thanh toán: ").append("cash".equals(order.getPaymentMethod()) ? "Tiền mặt" : "Chuyển khoản").append("\n");
        sb.append("Trạng thái: ").append(order.getStatus()).append("\n\n");

        sb.append("--- Chi tiết ---\n");
        for (OrderDetail od : details) {
            String name = od.getProductName() != null ? od.getProductName() : "SP #" + od.getProductId();
            sb.append("• ").append(name)
                    .append(" x").append(od.getQuantity())
                    .append(" = ").append(nf.format(od.getSubtotal())).append(" ₫\n");
        }
        sb.append("\nTổng: ").append(nf.format(order.getTotalAmount())).append(" ₫");

        if (order.getNote() != null && !order.getNote().isEmpty()) {
            sb.append("\nGhi chú: ").append(order.getNote());
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Chi tiết đơn hàng")
                .setMessage(sb.toString())
                .setPositiveButton("Đóng", null)
                .setNeutralButton("Cập nhật trạng thái", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showStatusUpdateDialog(order);
                    }
                })
                .show();
    }

    private void showStatusUpdateDialog(final Order order) {
        // Find current status index
        int currentIdx = 0;
        for (int i = 0; i < Constants.ORDER_STATUSES.length; i++) {
            if (Constants.ORDER_STATUSES[i].equals(order.getStatus())) {
                currentIdx = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Cập nhật trạng thái đơn #" + order.getId())
                .setSingleChoiceItems(Constants.ORDER_STATUSES, currentIdx,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newStatus = Constants.ORDER_STATUSES[which];
                                presenter.updateOrderStatus(ManageOrderFragment.this, order.getId(), newStatus);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ===== AdminPresenter.OrderView callbacks =====

    @Override
    public void onOrdersLoaded(List<Order> orders, double totalRevenue) {
        orderAdapter.updateData(orders);
        tvTotalRevenue.setText(nf.format(totalRevenue) + " ₫");
        tvOrderCount.setText(orders.size() + " đơn hàng");
    }

    @Override
    public void onOrderDetailsLoaded(Order order, List<OrderDetail> details) {
        showOrderDetailsDialog(order, details);
    }

    @Override
    public void onOrderStatusUpdated(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        presenter.loadOrders(this, currentFilter);
    }

    @Override
    public void onOrderError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
