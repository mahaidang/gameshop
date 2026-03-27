package com.n2.gameshop.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.google.android.material.button.MaterialButton;
import com.n2.gameshop.R;
import com.n2.gameshop.model.User;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.presenter.AuthPresenter;
import com.n2.gameshop.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment implements AuthPresenter.View {

    private TextView tvProfileUsername, tvProfileRole;
    private MaterialButton btnLogout, btnDeleteAccount;
    private SessionManager sessionManager;
    private AuthPresenter authPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        authPresenter = new AuthPresenter(requireContext(), this);

        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        tvProfileRole = view.findViewById(R.id.tvProfileRole);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        User user = sessionManager.getLoggedInUser();
        if (user != null) {
            tvProfileUsername.setText(user.getUsername());
            tvProfileRole.setText("Vai trò: " + user.getRole());
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.clearSession();
                navigateToLogin();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xoá tài khoản")
                        .setMessage("Bạn có chắc chắn muốn xoá tài khoản? Hành động này không thể hoàn tác.")
                        .setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User u = sessionManager.getLoggedInUser();
                                if (u != null) {
                                    authPresenter.deleteAccount(u.getId());
                                }
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // ===== AuthPresenter.View callbacks =====

    @Override
    public void onLoginSuccess(User user) { /* not used */ }

    @Override
    public void onLoginFailed(String message) { /* not used */ }

    @Override
    public void onRegisterSuccess(String message) {
        // Also used for delete account success
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    @Override
    public void onRegisterFailed(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

