package com.n2.gameshop.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.n2.gameshop.database.UserDAO;
import com.n2.gameshop.model.User;
import com.n2.gameshop.preferences.SessionManager;
import com.n2.gameshop.utils.Validator;

public class AuthPresenter {
	public interface View {
		void onLoginSuccess(User user);

		void onLoginFailed(String message);

		void onRegisterSuccess(String message);

		void onRegisterFailed(String message);
	}

	private final View view;
	private final UserDAO userDAO;
	private final SessionManager sessionManager;
	private final Handler mainHandler;

	public AuthPresenter(Context context, View view) {
		Context appContext = context.getApplicationContext();
		this.view = view;
		this.userDAO = new UserDAO(appContext);
		this.sessionManager = new SessionManager(appContext);
		this.mainHandler = new Handler(Looper.getMainLooper());
	}

	public void login(final String usernameOrEmail, final String password) {
		if (!Validator.isNotEmpty(usernameOrEmail) || !Validator.isNotEmpty(password)) {
			view.onLoginFailed("Vui lòng nhập đầy đủ thông tin");
			return;
		}

		if (usernameOrEmail.contains("@") && !Validator.isValidEmail(usernameOrEmail)) {
			view.onLoginFailed("Email không hợp lệ");
			return;
		}

		if (!Validator.isValidPassword(password)) {
			view.onLoginFailed("Mật khẩu phải có ít nhất 6 ký tự");
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				final User user = usernameOrEmail.contains("@")
						? userDAO.findByEmail(usernameOrEmail.trim())
						: userDAO.findByUsername(usernameOrEmail.trim());

				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						if (user == null) {
							view.onLoginFailed("Tài khoản không tồn tại");
							return;
						}

						if (!user.isActive()) {
							view.onLoginFailed("Tài khoản đã bị khóa");
							return;
						}

						if (!password.equals(user.getPassword())) {
							view.onLoginFailed("Sai mật khẩu");
							return;
						}

						sessionManager.saveSession(user);
						view.onLoginSuccess(user);
					}
				});
			}
		}).start();
	}

	public void register(final User user) {
		if (!Validator.isNotEmpty(user.getUsername())
				|| !Validator.isNotEmpty(user.getEmail())
				|| !Validator.isNotEmpty(user.getPassword())
				|| !Validator.isNotEmpty(user.getFullName())) {
			view.onRegisterFailed("Vui lòng nhập đầy đủ thông tin bắt buộc");
			return;
		}

		if (!Validator.isValidEmail(user.getEmail())) {
			view.onRegisterFailed("Email không hợp lệ");
			return;
		}

		if (!Validator.isValidPassword(user.getPassword())) {
			view.onRegisterFailed("Mật khẩu phải có ít nhất 6 ký tự");
			return;
		}

		user.setUsername(user.getUsername().trim());
		user.setEmail(user.getEmail().trim());
		user.setFullName(user.getFullName().trim());
		user.setPhone(user.getPhone() == null ? "" : user.getPhone().trim());
		if (!Validator.isNotEmpty(user.getRole())) {
			user.setRole("user");
		}
		user.setActive(true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				final User sameUsername = userDAO.findByUsername(user.getUsername());
				final User sameEmail = userDAO.findByEmail(user.getEmail());

				if (sameUsername != null) {
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							view.onRegisterFailed("Username đã tồn tại");
						}
					});
					return;
				}

				if (sameEmail != null) {
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							view.onRegisterFailed("Email đã được sử dụng");
						}
					});
					return;
				}

				final long insertedId = userDAO.insert(user);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						if (insertedId > 0) {
							user.setId((int) insertedId);
							view.onRegisterSuccess("Đăng ký thành công");
						} else {
							view.onRegisterFailed("Đăng ký thất bại, vui lòng thử lại");
						}
					}
				});
			}
		}).start();
	}

	public void deleteAccount(final int userId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final int deletedRows = userDAO.deleteUser(userId);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						if (deletedRows > 0) {
							sessionManager.clearSession();
							view.onRegisterSuccess("Xóa tài khoản thành công");
						} else {
							view.onRegisterFailed("Không thể xóa tài khoản");
						}
					}
				});
			}
		}).start();
	}
}

