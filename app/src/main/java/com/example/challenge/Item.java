package com.example.challenge;

import java.util.SortedMap;

public class Item {

    private String title;
    private String price;
    private String thumbnail;
    private String id;

    public Item() {
        super();
    }

    public Item(String title, String price, String thumbnail, String id) {
        super();
        this.title = title;
        this.price = price;
        this.thumbnail = thumbnail;
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}