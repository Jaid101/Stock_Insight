package com.example.stockinsight;

public class NewsModel {
    private String title, source, imageUrl;

    public NewsModel(String title, String source, String imageUrl) {
        this.title = title;
        this.source = source;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getImageUrl() { return imageUrl; }
}
