package com.n2.gameshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.n2.gameshop.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private final DatabaseHelper databaseHelper;

    public ReviewDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public long insertReview(Review review) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REVIEW_USER_ID, review.getUserId());
        values.put(DatabaseHelper.COLUMN_REVIEW_PRODUCT_ID, review.getProductId());
        values.put(DatabaseHelper.COLUMN_REVIEW_RATING, review.getRating());
        values.put(DatabaseHelper.COLUMN_REVIEW_COMMENT, review.getComment());
        values.put(DatabaseHelper.COLUMN_REVIEW_CREATED_AT, review.getCreatedAt());
        return db.insert(DatabaseHelper.TABLE_REVIEWS, null, values);
    }

    public List<Review> getReviewsByProduct(int productId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        // JOIN with users to get username
        String query = "SELECT r.*, u." + DatabaseHelper.COLUMN_USERNAME
                + " FROM " + DatabaseHelper.TABLE_REVIEWS + " r"
                + " LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u"
                + " ON r." + DatabaseHelper.COLUMN_REVIEW_USER_ID + " = u." + DatabaseHelper.COLUMN_USER_ID
                + " WHERE r." + DatabaseHelper.COLUMN_REVIEW_PRODUCT_ID + " = ?"
                + " ORDER BY r." + DatabaseHelper.COLUMN_REVIEW_CREATED_AT + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Review review = new Review();
                    review.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_ID)));
                    review.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_USER_ID)));
                    review.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_PRODUCT_ID)));
                    review.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_RATING)));
                    review.setComment(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_COMMENT)));
                    review.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_CREATED_AT)));
                    int usernameIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
                    if (usernameIdx >= 0) {
                        review.setUsername(cursor.getString(usernameIdx));
                    }
                    reviews.add(review);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return reviews;
    }

    public boolean hasUserReviewed(int userId, int productId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_REVIEWS,
                new String[]{DatabaseHelper.COLUMN_REVIEW_ID},
                DatabaseHelper.COLUMN_REVIEW_USER_ID + " = ? AND "
                        + DatabaseHelper.COLUMN_REVIEW_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)},
                null, null, null, "1"
        );
        try {
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public float getAverageRating(int productId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT AVG(" + DatabaseHelper.COLUMN_REVIEW_RATING + ") FROM "
                        + DatabaseHelper.TABLE_REVIEWS
                        + " WHERE " + DatabaseHelper.COLUMN_REVIEW_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)}
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getFloat(0);
            }
            return 0f;
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}

