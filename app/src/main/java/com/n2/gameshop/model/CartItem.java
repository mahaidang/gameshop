package com.n2.gameshop.model;

import java.io.Serializable;

/**
 * In-memory cart item that combines product info with quantity.
 * Built by loading Product from DB + quantity from CartManager.
 */
public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private boolean selected = true; // Default to selected

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public double getSubtotal() {
        return product != null ? product.getPrice() * quantity : 0;
    }
}
