package com.n2.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.model.OrderDetail;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private final Context context;
    private List<OrderDetail> items;
    private final NumberFormat nf;

    public OrderDetailAdapter(Context context, List<OrderDetail> items) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<OrderDetail>();
        this.nf = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void updateData(List<OrderDetail> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<OrderDetail>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail od = items.get(position);
        String name = od.getProductName() != null ? od.getProductName() : "Sản phẩm #" + od.getProductId();
        holder.tvODItemName.setText(name);
        holder.tvODItemQty.setText("x" + od.getQuantity());
        holder.tvODItemSubtotal.setText(nf.format(od.getSubtotal()) + " ₫");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvODItemName, tvODItemQty, tvODItemSubtotal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvODItemName = itemView.findViewById(R.id.tvODItemName);
            tvODItemQty = itemView.findViewById(R.id.tvODItemQty);
            tvODItemSubtotal = itemView.findViewById(R.id.tvODItemSubtotal);
        }
    }
}

