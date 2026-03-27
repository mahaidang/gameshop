package com.n2.gameshop.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple SharedPreferences-backed cart.
 * Stores product IDs and quantities as key-value pairs: "cart_<productId>" → quantity.
 */
public class CartManager {
    private static final String PREF_NAME = "game_shop_cart";
    private static final String KEY_PREFIX = "cart_";

    private final SharedPreferences prefs;

    public CartManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addToCart(int productId, int quantity) {
        String key = KEY_PREFIX + productId;
        int current = prefs.getInt(key, 0);
        prefs.edit().putInt(key, current + quantity).apply();
    }

    public void setQuantity(int productId, int quantity) {
        String key = KEY_PREFIX + productId;
        if (quantity <= 0) {
            prefs.edit().remove(key).apply();
        } else {
            prefs.edit().putInt(key, quantity).apply();
        }
    }

    public int getQuantity(int productId) {
        return prefs.getInt(KEY_PREFIX + productId, 0);
    }

    public void removeFromCart(int productId) {
        prefs.edit().remove(KEY_PREFIX + productId).apply();
    }

    public Map<Integer, Integer> getAllCartItems() {
        Map<Integer, Integer> items = new HashMap<>();
        Map<String, ?> all = prefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(KEY_PREFIX)) {
                try {
                    int productId = Integer.parseInt(key.substring(KEY_PREFIX.length()));
                    int qty = (Integer) entry.getValue();
                    if (qty > 0) {
                        items.put(productId, qty);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return items;
    }

    public void clearCart() {
        prefs.edit().clear().apply();
    }

    public int getCartSize() {
        return getAllCartItems().size();
    }
}

