package com.n2.gameshop.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.n2.gameshop.model.User;

public class SessionManager {
	private static final String PREF_NAME = "game_shop_session";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_ROLE = "role";

	private final SharedPreferences sharedPreferences;

	public SessionManager(Context context) {
		this.sharedPreferences = context.getApplicationContext()
				.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	public void saveSession(User user) {
		sharedPreferences.edit()
				.putInt(KEY_USER_ID, user.getId())
				.putString(KEY_USERNAME, user.getUsername())
				.putString(KEY_ROLE, user.getRole())
				.apply();
	}

	public User getLoggedInUser() {
		if (!isLoggedIn()) {
			return null;
		}

		User user = new User();
		user.setId(sharedPreferences.getInt(KEY_USER_ID, -1));
		user.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
		user.setRole(sharedPreferences.getString(KEY_ROLE, "user"));
		user.setActive(true);
		return user;
	}

	public boolean isLoggedIn() {
		return sharedPreferences.contains(KEY_USER_ID)
				&& sharedPreferences.contains(KEY_USERNAME)
				&& sharedPreferences.getInt(KEY_USER_ID, -1) > 0;
	}

	public void clearSession() {
		sharedPreferences.edit().clear().apply();
	}
}

