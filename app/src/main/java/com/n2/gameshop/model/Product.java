package com.n2.gameshop.model;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private int categoryId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int stock;
    private String platform;
    private boolean isActive;

    public Product() {
        this.isActive = true;
        this.platform = "PC";
    }

    public Product(int id, int categoryId, String name, String description, double price,
                   String imageUrl, int stock, String platform, boolean isActive) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.platform = platform;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
