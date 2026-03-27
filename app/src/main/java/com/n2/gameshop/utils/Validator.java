package com.n2.gameshop.utils;

import android.util.Patterns;

public final class Validator {
	private Validator() {
	}

	public static boolean isValidEmail(String email) {
		return isNotEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
	}

	public static boolean isValidPassword(String password) {
		return isNotEmpty(password) && password.trim().length() >= 6;
	}

	public static boolean isNotEmpty(String value) {
		return value != null && !value.trim().isEmpty();
	}
}

