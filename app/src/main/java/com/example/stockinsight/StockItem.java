package com.example.stockinsight;

public class StockItem {
    private String name;
    private String symbol;
    private double price;

    public StockItem(String symbol, String name,  double price) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
    }

    // Getter methods
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
}
