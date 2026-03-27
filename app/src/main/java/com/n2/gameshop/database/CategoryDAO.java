package com.n2.gameshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.n2.gameshop.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final DatabaseHelper databaseHelper;

    public CategoryDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_CATEGORIES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_CATEGORY_NAME + " ASC"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    categories.add(mapCursorToCategory(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return categories;
    }

    public long insertCategory(Category category) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_DESC, category.getDescription());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ICON, category.getIconUrl());
        return db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY_DESC, category.getDescription());
        values.put(DatabaseHelper.COLUMN_CATEGORY_ICON, category.getIconUrl());
        return db.update(
                DatabaseHelper.TABLE_CATEGORIES,
                values,
                DatabaseHelper.COLUMN_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(category.getId())}
        );
    }

    public int deleteCategory(int categoryId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_CATEGORIES,
                DatabaseHelper.COLUMN_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(categoryId)}
        );
    }

    private Category mapCursorToCategory(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)));
        category.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)));
        category.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_DESC)));
        category.setIconUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON)));
        return category;
    }
}

