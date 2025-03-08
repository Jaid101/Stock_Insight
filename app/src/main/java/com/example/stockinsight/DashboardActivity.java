package com.example.stockinsight;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private EditText searchBar;
    private DrawerLayout drawerLayout;
    private ImageView searchButton;
    private TextView searchedStockSymbol, searchedStockPrice;
    private RecyclerView popularStocksRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StockAdapter stockAdapter;
    private ShimmerFrameLayout shimmerView;
    private RequestQueue requestQueue;

    private static final String ALPHA_VANTAGE_API_KEY = "Z2C628MF8OJZGQ6B";
    private static final String ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=" + ALPHA_VANTAGE_API_KEY;
    private static final String TWELVE_DATA_API = "https://api.twelvedata.com/quote?symbol=%s&apikey=da8739d46f2f4459b751500ff544f79c";

    private static final String PREFS_NAME = "StockPrefs";
    private static final String LAST_FETCH_DATE = "LastFetchDate";
    private static final String STOCK_DATA = "StockData";

    private final List<String> stockSymbols = Arrays.asList("AAPL", "MSFT", "GOOGL", "TSLA", "AMZN");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // Get NavigationView and its header
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.tvUserEmail);

        // Get current user email from Firebase Authentication
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());  // Set user's email in the drawer
        }
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_news) {
                    // Open News Activity
                    Intent intent = new Intent(DashboardActivity.this, NewsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_predict) {
                    // Open Predict Stock Price Activity
                    Intent intent = new Intent(DashboardActivity.this, StockPredictionActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    // Open Settings Activity
                    Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    // Handle Logout
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(DashboardActivity.this, LoginSignupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                drawerLayout.closeDrawers(); // Close drawer after selecting item
                return true;
            }
        });

        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        searchedStockSymbol = findViewById(R.id.searched_stock_symbol);
        searchedStockPrice = findViewById(R.id.searched_stock_price);
        popularStocksRecyclerView = findViewById(R.id.popular_stocks_recycler);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        shimmerView = findViewById(R.id.shimmer_view);
        requestQueue = Volley.newRequestQueue(this);

        popularStocksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter = new StockAdapter(new ArrayList<>());
        popularStocksRecyclerView.setAdapter(stockAdapter);

        fetchTopTradedStocks();

        searchButton.setOnClickListener(v -> {
            String stockSymbol = searchBar.getText().toString().trim().toUpperCase();
            if (!stockSymbol.isEmpty()) {
                fetchSearchedStock(stockSymbol);
            } else {
                showToast("Enter a stock symbol to search.");
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchTopTradedStocks();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private boolean isNewDay() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastDate = prefs.getString(LAST_FETCH_DATE, "");
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return !todayDate.equals(lastDate);
    }

    private void saveStockData(String jsonData) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LAST_FETCH_DATE, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        editor.putString(STOCK_DATA, jsonData);
        editor.apply();
    }

    private void fetchTopTradedStocks() {
        if (!isInternetAvailable()) {
            showToast("No internet connection.");
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String cachedData = prefs.getString(STOCK_DATA, null);

        if (!isNewDay() && cachedData != null) {
            updateStockListFromJson(cachedData);
            return;
        }

        showLoading();
        List<StockItem> stockList = new ArrayList<>();
        JSONObject stockJson = new JSONObject();

        for (String symbol : stockSymbols) {
            String apiUrl = String.format(ALPHA_VANTAGE_BASE_URL, symbol);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                    response -> {
                        hideLoading();
                        try {
                            JSONObject quote = response.getJSONObject("Global Quote");
                            String symbolData = quote.getString("01. symbol");
                            double price = quote.getDouble("05. price");
                            stockList.add(new StockItem(symbolData, symbolData, price));

                            stockJson.put(symbolData, quote);
                            if (stockList.size() == stockSymbols.size()) {
                                saveStockData(stockJson.toString());
                                stockAdapter.updateStockList(stockList);
                            }
                        } catch (JSONException e) {
                            logError("Parsing error", e);
                        }
                    },
                    error -> logError("Failed to fetch stock data", error));

            requestQueue.add(request);
        }
    }

    private void updateStockListFromJson(String jsonData) {
        try {
            JSONObject stockJson = new JSONObject(jsonData);
            List<StockItem> stockList = new ArrayList<>();

            for (String symbol : stockSymbols) {
                if (stockJson.has(symbol)) {
                    JSONObject quote = stockJson.getJSONObject(symbol);
                    String symbolData = quote.getString("01. symbol");
                    double price = quote.getDouble("05. price");
                    stockList.add(new StockItem(symbolData, symbolData, price));
                }
            }

            stockAdapter.updateStockList(stockList);
            hideLoading();
        } catch (JSONException e) {
            logError("Error parsing cached stock data", e);
        }
    }

    // Existing methods (fetchSearchedStock, isInternetAvailable, showLoading, etc.) remain unchanged.
    private void fetchSearchedStock(String stockSymbol) {
        if (!isInternetAvailable()) {
            showToast("No internet connection.");
            return;
        }

        showLoading();
        String apiUrl = String.format(TWELVE_DATA_API, stockSymbol);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    hideLoading();
                    try {
                        String symbol = response.getString("symbol");
                        String companyName = response.getString("name");
                        String price = response.getString("close");

                        searchedStockSymbol.setText(symbol + " - " + companyName);
                        searchedStockPrice.setText("$" + price);
                        startBlinkAnimation(searchedStockPrice);
                    } catch (JSONException e) {
                        logError("Parsing error", e);
                        showToast("Error fetching stock data.");
                    }
                },
                error -> {
                    hideLoading();
                    logError("Failed to fetch stock", error);
                    showToast("Invalid stock symbol or network error.");
                });

        requestQueue.add(request);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showLoading() {
        shimmerView.setVisibility(View.VISIBLE);
        popularStocksRecyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        shimmerView.setVisibility(View.GONE);
        popularStocksRecyclerView.setVisibility(View.VISIBLE);
    }

    private void startBlinkAnimation(TextView textView) {
        ObjectAnimator blink = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f, 1f);
        blink.setDuration(200);
        blink.start();
    }

    private void logError(String message, Exception e) {
        Log.e("API ERROR", message + ": " + e.getMessage());
    }

    private void showToast(String message) {
        Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
    }


}
