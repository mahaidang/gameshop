package com.n2.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.n2.gameshop.R;
import com.n2.gameshop.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdminAdapter extends BaseAdapter {

    public interface OnCategoryActionListener {
        void onEditCategory(Category category);
        void onDeleteCategory(Category category);
    }

    private final Context context;
    private List<Category> categoryList;
    private OnCategoryActionListener listener;

    public CategoryAdminAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<Category>();
    }

    public void setOnCategoryActionListener(OnCategoryActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Category> newList) {
        this.categoryList = newList != null ? newList : new ArrayList<Category>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Category getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categoryList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category_admin, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvAdminCategoryName);
            holder.tvDesc = convertView.findViewById(R.id.tvAdminCategoryDesc);
            holder.btnEdit = convertView.findViewById(R.id.btnEditCategory);
            holder.btnDelete = convertView.findViewById(R.id.btnDeleteCategory);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Category category = getItem(position);
        holder.tvName.setText(category.getName());
        holder.tvDesc.setText(category.getDescription() != null ? category.getDescription() : "");

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onEditCategory(category);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onDeleteCategory(category);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvName, tvDesc;
        ImageButton btnEdit, btnDelete;
    }
}

