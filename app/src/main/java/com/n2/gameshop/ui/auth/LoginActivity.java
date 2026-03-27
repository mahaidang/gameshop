package com.n2.gameshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.n2.gameshop.R;
import com.n2.gameshop.model.User;
import com.n2.gameshop.presenter.AuthPresenter;
import com.n2.gameshop.ui.admin.AdminActivity;
import com.n2.gameshop.ui.user.MainActivity;

public class LoginActivity extends AppCompatActivity implements AuthPresenter.View {
	private TextInputEditText edtUsernameOrEmail;
	private TextInputEditText edtPassword;
	private AuthPresenter authPresenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		authPresenter = new AuthPresenter(this, this);
		initViews();
		setupActions();
	}

	private void initViews() {
		edtUsernameOrEmail = findViewById(R.id.edtUsernameOrEmail);
		edtPassword = findViewById(R.id.edtPassword);
	}

	private void setupActions() {
		MaterialButton btnLogin = findViewById(R.id.btnLogin);
		TextView tvRegister = findViewById(R.id.tvGoToRegister);

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authPresenter.login(getText(edtUsernameOrEmail), getText(edtPassword));
			}
		});

		tvRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
			}
		});
	}

	private String getText(TextInputEditText editText) {
		return editText.getText() == null ? "" : editText.getText().toString();
	}

	@Override
	public void onLoginSuccess(User user) {
		Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
		Intent intent = "admin".equalsIgnoreCase(user.getRole())
				? new Intent(this, AdminActivity.class)
				: new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onLoginFailed(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRegisterSuccess(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRegisterFailed(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}

