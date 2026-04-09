package com.n2.gameshop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.n2.gameshop.R;
import com.n2.gameshop.model.User;
import com.n2.gameshop.presenter.AuthPresenter;
import com.n2.gameshop.ui.user.MainActivity;

public class RegisterActivity extends AppCompatActivity implements AuthPresenter.View {
	private TextInputEditText edtUsername;
	private TextInputEditText edtEmail;
	private TextInputEditText edtPassword;
	private TextInputEditText edtFullName;
	private TextInputEditText edtPhone;
	private AuthPresenter authPresenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		setupToolbar();

		authPresenter = new AuthPresenter(this, this);
		initViews();
		setupActions();
	}

	private void setupToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbarRegister);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Đăng ký");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
		});
	}

	private void initViews() {
		edtUsername = findViewById(R.id.edtRegisterUsername);
		edtEmail = findViewById(R.id.edtRegisterEmail);
		edtPassword = findViewById(R.id.edtRegisterPassword);
		edtFullName = findViewById(R.id.edtRegisterFullName);
		edtPhone = findViewById(R.id.edtRegisterPhone);
	}

	private void setupActions() {
		MaterialButton btnRegister = findViewById(R.id.btnRegister);
		TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = new User();
				user.setUsername(getText(edtUsername));
				user.setEmail(getText(edtEmail));
				user.setPassword(getText(edtPassword));
				user.setFullName(getText(edtFullName));
				user.setPhone(getText(edtPhone));
				user.setRole("user");
				user.setActive(true);
				authPresenter.register(user);
			}
		});

		tvGoToLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private String getText(TextInputEditText editText) {
		return editText.getText() == null ? "" : editText.getText().toString();
	}

	@Override
	public void onLoginSuccess(User user) {
		Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoginFailed(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRegisterSuccess(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onRegisterFailed(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}

