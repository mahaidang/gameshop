package com.n2.gameshop.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.n2.gameshop.R;
import com.n2.gameshop.adapter.UserAdapter;
import com.n2.gameshop.model.User;
import com.n2.gameshop.presenter.AdminPresenter;

import java.util.ArrayList;
import java.util.List;

public class ManageUserFragment extends Fragment implements AdminPresenter.UserView {

    private ListView lvUsers;
    private TextView tvUserCount;
    private MaterialButton btnAddUser;

    private AdminPresenter presenter;
    private UserAdapter userAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new AdminPresenter(requireContext());

        lvUsers = view.findViewById(R.id.lvUsers);
        tvUserCount = view.findViewById(R.id.tvUserCount);
        btnAddUser = view.findViewById(R.id.btnAddUser);

        userAdapter = new UserAdapter(requireContext(), new ArrayList<User>());
        lvUsers.setAdapter(userAdapter);

        userAdapter.setOnUserActionListener(new UserAdapter.OnUserActionListener() {
            @Override
            public void onEditUser(User user) {
                showUserDialog(user, false);
            }

            @Override
            public void onDeleteUser(User user) {
                showDeleteConfirmation(user);
            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDialog(null, true);
            }
        });

        presenter.loadUsers(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadUsers(this);
    }

    private void showUserDialog(final User existingUser, final boolean isNew) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_user, null);

        final TextInputEditText edtUsername = dialogView.findViewById(R.id.edtDialogUsername);
        final TextInputEditText edtEmail = dialogView.findViewById(R.id.edtDialogEmail);
        final TextInputEditText edtPassword = dialogView.findViewById(R.id.edtDialogPassword);
        final TextInputEditText edtFullName = dialogView.findViewById(R.id.edtDialogFullName);
        final TextInputEditText edtPhone = dialogView.findViewById(R.id.edtDialogPhone);
        final CheckBox cbActive = dialogView.findViewById(R.id.cbDialogActive);
        final TextInputLayout tilPassword = dialogView.findViewById(R.id.tilDialogPassword);

        if (!isNew && existingUser != null) {
            edtUsername.setText(existingUser.getUsername());
            edtEmail.setText(existingUser.getEmail());
            edtFullName.setText(existingUser.getFullName());
            edtPhone.setText(existingUser.getPhone());
            cbActive.setChecked(existingUser.isActive());
            tilPassword.setHint("Mật khẩu (để trống nếu không đổi)");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(isNew ? "Thêm User" : "Sửa User")
                .setView(dialogView)
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = isNew ? new User() : existingUser;
                        user.setUsername(getText(edtUsername));
                        user.setEmail(getText(edtEmail));
                        user.setFullName(getText(edtFullName));
                        user.setPhone(getText(edtPhone));
                        user.setActive(cbActive.isChecked());
                        user.setRole("user");

                        String password = getText(edtPassword);
                        if (isNew) {
                            user.setPassword(password);
                        } else if (!password.isEmpty()) {
                            user.setPassword(password);
                        }

                        presenter.saveUser(ManageUserFragment.this, user, isNew);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirmation(final User user) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá user \"" + user.getUsername() + "\"?")
                .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.deleteUser(ManageUserFragment.this, user.getId());
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    // ===== AdminPresenter.UserView callbacks =====

    @Override
    public void onUsersLoaded(List<User> users) {
        userAdapter.updateData(users);
        tvUserCount.setText("Tổng: " + users.size() + " users");
    }

    @Override
    public void onUserSaved(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        presenter.loadUsers(this);
    }

    @Override
    public void onUserDeleted(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        presenter.loadUsers(this);
    }

    @Override
    public void onUserError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
