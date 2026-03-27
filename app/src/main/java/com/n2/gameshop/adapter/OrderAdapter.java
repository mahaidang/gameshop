package com.n2.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;
    private final NumberFormat nf;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList != null ? orderList : new ArrayList<Order>();
        this.nf = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Order> newList) {
        this.orderList = newList != null ? newList : new ArrayList<Order>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        final Order order = orderList.get(position);

        holder.tvOrderId.setText("Đơn #" + order.getId());
        holder.tvOrderDate.setText(order.getCreatedAt());
        holder.tvOrderTotal.setText(nf.format(order.getTotalAmount()) + " ₫");
        holder.tvOrderStatus.setText(order.getStatus());

        String payment = "cash".equals(order.getPaymentMethod()) ? "Tiền mặt" : "Chuyển khoản";
        holder.tvOrderPayment.setText(payment);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvOrderStatus, tvOrderPayment;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderPayment = itemView.findViewById(R.id.tvOrderPayment);
        }
    }
}

