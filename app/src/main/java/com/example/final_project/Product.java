package com.example.final_project;

public class Product {
    String title, description, imageUrl;
    int price, id, quantity;


    public Product() {

    }

    public Product(String title, String description, String imageUrl, int price, int id, int quantity) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.id = id;
        this.quantity = quantity;
    }
    public Product(String title, String imageUrl, double price, long quantity) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = (int) price;
        this.quantity = (int) quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getPrice() {
        return (long) price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
