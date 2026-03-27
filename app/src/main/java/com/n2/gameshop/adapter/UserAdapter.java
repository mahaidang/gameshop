package com.n2.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.n2.gameshop.R;
import com.n2.gameshop.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends BaseAdapter {

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }

    private final Context context;
    private List<User> userList;
    private OnUserActionListener listener;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList != null ? userList : new ArrayList<User>();
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<User> newList) {
        this.userList = newList != null ? newList : new ArrayList<User>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder();
            holder.tvUsername = convertView.findViewById(R.id.tvUserUsername);
            holder.tvEmail = convertView.findViewById(R.id.tvUserEmail);
            holder.tvFullName = convertView.findViewById(R.id.tvUserFullName);
            holder.tvStatus = convertView.findViewById(R.id.tvUserStatus);
            holder.btnEdit = convertView.findViewById(R.id.btnEditUser);
            holder.btnDelete = convertView.findViewById(R.id.btnDeleteUser);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User user = getItem(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());
        holder.tvFullName.setText(user.getFullName() != null ? user.getFullName() : "");
        holder.tvStatus.setText(user.isActive() ? "Hoạt động" : "Bị khoá");
        holder.tvStatus.setTextColor(user.isActive() ? 0xFF4CAF50 : 0xFFF44336);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onEditUser(user);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onDeleteUser(user);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvUsername, tvEmail, tvFullName, tvStatus;
        ImageButton btnEdit, btnDelete;
    }
}

