package com.n2.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.model.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartActionListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
        void onSelectionChanged(CartItem item, boolean isSelected);
    }

    private final Context context;
    private List<CartItem> cartItems;
    private OnCartActionListener listener;
    private final NumberFormat nf;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems != null ? cartItems : new ArrayList<CartItem>();
        this.nf = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void setOnCartActionListener(OnCartActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<CartItem> newItems) {
        this.cartItems = newItems != null ? newItems : new ArrayList<CartItem>();
        notifyDataSetChanged();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        final CartItem item = cartItems.get(position);

        holder.cbCartSelect.setOnCheckedChangeListener(null);
        holder.cbCartSelect.setChecked(item.isSelected());

        holder.tvCartProductName.setText(item.getProduct().getName());
        holder.tvCartUnitPrice.setText(nf.format(item.getProduct().getPrice()) + " ₫");
        holder.tvCartQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvCartSubtotal.setText("Tổng: " + nf.format(item.getSubtotal()) + " ₫");

        holder.cbCartSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setSelected(isChecked);
                if (listener != null) {
                    listener.onSelectionChanged(item, isChecked);
                }
            }
        });

        holder.btnCartMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && item.getQuantity() > 1) {
                    listener.onQuantityChanged(item, item.getQuantity() - 1);
                }
            }
        });

        holder.btnCartPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onQuantityChanged(item, item.getQuantity() + 1);
                }
            }
        });

        holder.btnCartRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRemoveItem(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbCartSelect;
        TextView tvCartProductName, tvCartUnitPrice, tvCartQuantity, tvCartSubtotal;
        ImageButton btnCartMinus, btnCartPlus, btnCartRemove;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCartSelect = itemView.findViewById(R.id.cbCartSelect);
            tvCartProductName = itemView.findViewById(R.id.tvCartProductName);
            tvCartUnitPrice = itemView.findViewById(R.id.tvCartUnitPrice);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvCartSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            btnCartMinus = itemView.findViewById(R.id.btnCartMinus);
            btnCartPlus = itemView.findViewById(R.id.btnCartPlus);
            btnCartRemove = itemView.findViewById(R.id.btnCartRemove);
        }
    }
}
