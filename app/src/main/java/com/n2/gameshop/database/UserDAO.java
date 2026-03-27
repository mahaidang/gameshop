package com.n2.gameshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.n2.gameshop.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	private final DatabaseHelper databaseHelper;

	public UserDAO(Context context) {
		this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
	}

	public long insert(User user) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = toContentValues(user, false);
		return db.insert(DatabaseHelper.TABLE_USERS, null, values);
	}

	public User findByUsername(String username) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(
				DatabaseHelper.TABLE_USERS,
				null,
				DatabaseHelper.COLUMN_USERNAME + " = ?",
				new String[]{username},
				null,
				null,
				null,
				"1"
		);

		try {
			if (cursor != null && cursor.moveToFirst()) {
				return mapCursorToUser(cursor);
			}
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public User findByEmail(String email) {
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(
				DatabaseHelper.TABLE_USERS,
				null,
				DatabaseHelper.COLUMN_EMAIL + " = ?",
				new String[]{email},
				null,
				null,
				null,
				"1"
		);

		try {
			if (cursor != null && cursor.moveToFirst()) {
				return mapCursorToUser(cursor);
			}
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int updateUser(User user) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = toContentValues(user, true);
		return db.update(
				DatabaseHelper.TABLE_USERS,
				values,
				DatabaseHelper.COLUMN_USER_ID + " = ?",
				new String[]{String.valueOf(user.getId())}
		);
	}

	public int deleteUser(int userId) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		return db.delete(
				DatabaseHelper.TABLE_USERS,
				DatabaseHelper.COLUMN_USER_ID + " = ?",
				new String[]{String.valueOf(userId)}
		);
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(
				DatabaseHelper.TABLE_USERS,
				null,
				null,
				null,
				null,
				null,
				DatabaseHelper.COLUMN_USER_ID + " DESC"
		);

		try {
			if (cursor != null && cursor.moveToFirst()) {
				do {
					users.add(mapCursorToUser(cursor));
				} while (cursor.moveToNext());
			}
			return users;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private ContentValues toContentValues(User user, boolean includeId) {
		ContentValues values = new ContentValues();
		if (includeId) {
			values.put(DatabaseHelper.COLUMN_USER_ID, user.getId());
		}
		values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
		values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());
		values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
		values.put(DatabaseHelper.COLUMN_FULL_NAME, user.getFullName());
		values.put(DatabaseHelper.COLUMN_PHONE, user.getPhone());
		values.put(DatabaseHelper.COLUMN_ROLE, user.getRole());
		values.put(DatabaseHelper.COLUMN_IS_ACTIVE, user.isActive() ? 1 : 0);
		return values;
	}

	private User mapCursorToUser(Cursor cursor) {
		User user = new User();
		user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
		user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)));
		user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
		user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD)));
		user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULL_NAME)));
		user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)));
		user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE)));
		user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1);
		return user;
	}
}

