package com.n2.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.n2.gameshop.R;
import com.n2.gameshop.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    private final Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<Category>();
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Category> newList) {
        this.categoryList = newList != null ? newList : new ArrayList<Category>();
        this.selectedPosition = 0;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        boolean isSelected = (position == selectedPosition);
        holder.cardCategory.setChecked(isSelected);

        if (isSelected) {
            holder.cardCategory.setCardBackgroundColor(0xFFE94560);
            holder.tvCategoryName.setTextColor(0xFFFFFFFF);
        } else {
            holder.cardCategory.setCardBackgroundColor(0xFFFFFFFF);
            holder.tvCategoryName.setTextColor(0xFF333333);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos == RecyclerView.NO_POSITION) return;
                int oldSelected = selectedPosition;
                selectedPosition = adapterPos;
                notifyItemChanged(oldSelected);
                notifyItemChanged(selectedPosition);
                if (listener != null) {
                    listener.onCategoryClick(category, adapterPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardCategory;
        TextView tvCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}

