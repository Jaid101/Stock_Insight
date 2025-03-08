package com.example.stockinsight;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private List<StockItem> stockList;

    public StockAdapter(List<StockItem> stockList) {
        this.stockList = stockList;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        if (stockList != null && position < stockList.size()) {
            StockItem stock = stockList.get(position);
            holder.stockSymbol.setText(stock.getSymbol());
            holder.stockPrice.setText(String.format("Price: $%.2f", stock.getPrice())); // Ensures price formatting
        }
    }

    @Override
    public int getItemCount() {
        return (stockList != null) ? stockList.size() : 0;
    }

    public void updateStockList(List<StockItem> newStockList) {
        this.stockList = newStockList;
        notifyDataSetChanged(); // Refresh RecyclerView data
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView stockSymbol, stockPrice;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            stockSymbol = itemView.findViewById(R.id.stock_symbol);
            stockPrice = itemView.findViewById(R.id.stock_price);
        }
    }
}
