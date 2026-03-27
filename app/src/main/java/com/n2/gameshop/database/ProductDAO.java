package com.n2.gameshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.n2.gameshop.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final DatabaseHelper databaseHelper;

    public ProductDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                null,
                DatabaseHelper.COLUMN_PRODUCT_IS_ACTIVE + " = 1",
                null, null, null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " DESC"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    products.add(mapCursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return products;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                null,
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID + " = ? AND "
                        + DatabaseHelper.COLUMN_PRODUCT_IS_ACTIVE + " = 1",
                new String[]{String.valueOf(categoryId)},
                null, null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " DESC"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    products.add(mapCursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return products;
    }

    public Product getProductById(int productId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)},
                null, null, null, "1"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToProduct(cursor);
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String likeKeyword = "%" + keyword + "%";
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                null,
                "(" + DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE ? OR "
                        + DatabaseHelper.COLUMN_PRODUCT_DESC + " LIKE ?) AND "
                        + DatabaseHelper.COLUMN_PRODUCT_IS_ACTIVE + " = 1",
                new String[]{likeKeyword, likeKeyword},
                null, null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " DESC"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    products.add(mapCursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return products;
    }

    public long insertProduct(Product product) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = toContentValues(product);
        return db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = toContentValues(product);
        return db.update(
                DatabaseHelper.TABLE_PRODUCTS,
                values,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(product.getId())}
        );
    }

    public int deleteProduct(int productId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_PRODUCTS,
                DatabaseHelper.COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)}
        );
    }

    /** Admin: get ALL products (including inactive). */
    public List<Product> getAllProductsAdmin() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " DESC"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    products.add(mapCursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return products;
    }

    /** Admin: get products by category (including inactive). */
    public List<Product> getProductsByCategoryAdmin(int categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PRODUCTS,
                null,
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(categoryId)},
                null, null,
                DatabaseHelper.COLUMN_PRODUCT_ID + " DESC"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    products.add(mapCursorToProduct(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return products;
    }

    private ContentValues toContentValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID, product.getCategoryId());
        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getName());
        values.put(DatabaseHelper.COLUMN_PRODUCT_DESC, product.getDescription());
        values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE, product.getImageUrl());
        values.put(DatabaseHelper.COLUMN_PRODUCT_STOCK, product.getStock());
        values.put(DatabaseHelper.COLUMN_PRODUCT_PLATFORM, product.getPlatform());
        values.put(DatabaseHelper.COLUMN_PRODUCT_IS_ACTIVE, product.isActive() ? 1 : 0);
        return values;
    }

    private Product mapCursorToProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)));
        product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY_ID)));
        product.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)));
        product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESC)));
        product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)));
        product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE)));
        product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)));
        product.setPlatform(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PLATFORM)));
        product.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IS_ACTIVE)) == 1);
        return product;
    }
}

