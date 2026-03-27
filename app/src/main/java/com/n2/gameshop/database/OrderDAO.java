package com.n2.gameshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.n2.gameshop.model.Order;
import com.n2.gameshop.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final DatabaseHelper databaseHelper;

    public OrderDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    /**
     * Insert an order and return the new order ID, or -1 on failure.
     */
    public long insertOrder(Order order) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ORDER_USER_ID, order.getUserId());
        values.put(DatabaseHelper.COLUMN_ORDER_TOTAL, order.getTotalAmount());
        values.put(DatabaseHelper.COLUMN_ORDER_PAYMENT, order.getPaymentMethod());
        values.put(DatabaseHelper.COLUMN_ORDER_STATUS, order.getStatus());
        values.put(DatabaseHelper.COLUMN_ORDER_NOTE, order.getNote());
        values.put(DatabaseHelper.COLUMN_ORDER_CREATED_AT, order.getCreatedAt());
        return db.insert(DatabaseHelper.TABLE_ORDERS, null, values);
    }

    public long insertOrderDetail(OrderDetail detail) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_OD_ORDER_ID, detail.getOrderId());
        values.put(DatabaseHelper.COLUMN_OD_PRODUCT_ID, detail.getProductId());
        values.put(DatabaseHelper.COLUMN_OD_QUANTITY, detail.getQuantity());
        values.put(DatabaseHelper.COLUMN_OD_UNIT_PRICE, detail.getUnitPrice());
        return db.insert(DatabaseHelper.TABLE_ORDER_DETAILS, null, values);
    }

    public List<Order> getOrdersByUser(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ORDERS, null,
                DatabaseHelper.COLUMN_ORDER_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null,
                DatabaseHelper.COLUMN_ORDER_CREATED_AT + " DESC"
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do { orders.add(mapOrder(cursor)); } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return orders;
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT d.*, p." + DatabaseHelper.COLUMN_PRODUCT_NAME
                + " FROM " + DatabaseHelper.TABLE_ORDER_DETAILS + " d"
                + " LEFT JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p"
                + " ON d." + DatabaseHelper.COLUMN_OD_PRODUCT_ID + " = p." + DatabaseHelper.COLUMN_PRODUCT_ID
                + " WHERE d." + DatabaseHelper.COLUMN_OD_ORDER_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(orderId)});
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    OrderDetail od = new OrderDetail();
                    od.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OD_ID)));
                    od.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OD_ORDER_ID)));
                    od.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OD_PRODUCT_ID)));
                    od.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OD_QUANTITY)));
                    od.setUnitPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OD_UNIT_PRICE)));
                    int nameIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME);
                    if (nameIdx >= 0) od.setProductName(cursor.getString(nameIdx));
                    details.add(od);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return details;
    }

    public int updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ORDER_STATUS, status);
        return db.update(DatabaseHelper.TABLE_ORDERS, values,
                DatabaseHelper.COLUMN_ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)});
    }

    /**
     * For admin: get orders between two date strings (inclusive, format yyyy-MM-dd).
     */
    public List<Order> getOrdersByDateRange(String from, String to) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + DatabaseHelper.TABLE_ORDERS
                + " WHERE date(" + DatabaseHelper.COLUMN_ORDER_CREATED_AT + ") BETWEEN ? AND ?"
                + " ORDER BY " + DatabaseHelper.COLUMN_ORDER_CREATED_AT + " DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{from, to});
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do { orders.add(mapOrder(cursor)); } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ORDERS, null,
                null, null, null, null,
                DatabaseHelper.COLUMN_ORDER_CREATED_AT + " DESC");
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do { orders.add(mapOrder(cursor)); } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return orders;
    }

    /**
     * Decrease product stock after order is placed.
     */
    public void decreaseStock(int productId, int quantity) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("UPDATE " + DatabaseHelper.TABLE_PRODUCTS
                        + " SET " + DatabaseHelper.COLUMN_PRODUCT_STOCK + " = MAX(0, "
                        + DatabaseHelper.COLUMN_PRODUCT_STOCK + " - ?)"
                        + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = ?",
                new Object[]{quantity, productId});
    }

    private Order mapOrder(Cursor cursor) {
        Order order = new Order();
        order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ID)));
        order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_USER_ID)));
        order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_TOTAL)));
        order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_PAYMENT)));
        order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_STATUS)));
        order.setNote(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_NOTE)));
        order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_CREATED_AT)));
        return order;
    }
}

