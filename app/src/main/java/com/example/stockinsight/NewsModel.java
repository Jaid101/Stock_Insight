package com.example.stockinsight;

public class NewsModel {
    private String title, source, imageUrl, newsUrl;

    public NewsModel(String title, String source, String imageUrl, String newsUrl) {
        this.title = title;
        this.source = source;
        this.imageUrl = imageUrl;
        this.newsUrl = newsUrl;
    }

    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getImageUrl() { return imageUrl; }
    public String getNewsUrl() { return newsUrl; }
}
