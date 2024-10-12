package com.example.stockinsight;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView stockPriceTextView, companyTextView, exchangeTextView, newsTextView;
    EditText symbol;
    RequestQueue requestQueue;
    double previousPrice = 0.0;
    AppCompatButton updateButton;
    String symbolString;

    private static final String API_KEY = "72a92a4d3e0544f1a4fa899fc10d3206";  // Replace with your Twelve Data API key
    private static final String NEWS_API_KEY = "c1ffb10d1b8a8b2b26c23dd79ce6ba3c";  // Your GNews API key

    // For auto-update
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockPriceTextView = findViewById(R.id.stockPriceTextView);
        companyTextView = findViewById(R.id.CompanyTextView);
        exchangeTextView = findViewById(R.id.ExchangeTextView);
        newsTextView = findViewById(R.id.newsTextView);
        updateButton = findViewById(R.id.updateButton);
        symbol = findViewById(R.id.stock_symbol);
        requestQueue = Volley.newRequestQueue(this);

        // Set onClickListener for updateButton
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                symbolString = symbol.getText().toString().trim();
                if (!symbolString.isEmpty()) {
                    fetchStockPrice();
                    fetchStockNews();
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter A Valid Symbol", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Timer to auto-update stock price and news
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                symbolString = symbol.getText().toString().trim();
                if (!symbolString.isEmpty()) {
                    fetchStockPrice();
                    fetchStockNews(); // Auto-fetch news
                }
            }
        }, 0, 60000); // 1-minute interval
    }

    // Fetch stock price from API
    private void fetchStockPrice() {
        String apiUrl = "https://api.twelvedata.com/quote?symbol=" + symbolString + "&apikey=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Handle errors in response
                            if (response.has("code") && response.getString("code").equals("400")) {
                                Toast.makeText(MainActivity.this, "Invalid stock symbol", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Check for price and handle missing or null values
                            if (response.has("close") && !response.isNull("close")) {
                                String stockPrice = response.getString("close");
                                String companyName = response.optString("symbol", "Unknown");
                                String exchange = response.optString("exchange", "Unknown");

                                double stockPriceValue = Double.parseDouble(stockPrice);
                                updateStockPrice(stockPriceValue, "Exchange: " + exchange, "Company: " + companyName);
                            } else {
                                Toast.makeText(MainActivity.this, "Stock price not available", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e("API ERROR", "Error response: " + responseBody);
                        }
                        Log.e("API ERROR", "Error fetching stock price: " + error.getMessage());
                    }
                }
        );
        requestQueue.add(request);
    }

    // Fetch stock news using GNews API
    // Fetch stock news using GNews API
    private void fetchStockNews() {
        String query = symbolString + " stock market news OR investment OR trading OR buy OR sell";
        String newsUrl = "https://gnews.io/api/v4/search?q=" + query + "&token=" + NEWS_API_KEY;

        JsonObjectRequest newsRequest = new JsonObjectRequest(Request.Method.GET, newsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("NEWS API RESPONSE", response.toString()); // Log the raw API response for debugging

                            // Clear previous news
                            newsTextView.setText("");

                            // Check if 'articles' exists and is an array
                            if (response.has("articles") && !response.isNull("articles")) {
                                JSONArray newsArray = response.getJSONArray("articles");

                                // If there is at least one news article
                                if (newsArray.length() > 0) {
                                    JSONObject firstNewsItem = newsArray.getJSONObject(0); // Get the first news article

                                    // Check if the title exists and display it
                                    if (firstNewsItem.has("title")) {
                                        String headline = firstNewsItem.getString("title");
                                        newsTextView.setText("- " + headline);
                                    } else {
                                        newsTextView.setText("No title available for this article.");
                                    }
                                } else {
                                    newsTextView.setText("No news available.");
                                }
                            } else {
                                newsTextView.setText("No news available.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ERROR", "Error parsing news JSON: " + e.getMessage());
                            newsTextView.setText("Error loading news.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API ERROR", "Error fetching stock news: " + error.getMessage());

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e("API ERROR BODY", "Status Code: " + error.networkResponse.statusCode + ", Body: " + errorBody);
                        }

                        newsTextView.setText("Failed to fetch news.");
                    }
                }
        );

        requestQueue.add(newsRequest);
    }


    // Update the stock price on the UI
    private void updateStockPrice(double currentPrice, String exchange, String company) {
        double instantaneousChange = currentPrice - previousPrice;
        previousPrice = currentPrice;
        handler.post(new Runnable() {
            @Override
            public void run() {
                startBlinkAnimation(stockPriceTextView);
                String formattedPrice = String.format("%.2f", currentPrice);
                stockPriceTextView.setText(formattedPrice);
                int textColor = (instantaneousChange >= 0.0) ? Color.GREEN : Color.RED;
                stockPriceTextView.setTextColor(textColor);
                exchangeTextView.setText(exchange);
                companyTextView.setText(company);
            }
        });
    }

    // Animation for stock price value
    private void startBlinkAnimation(final TextView textView) {
        ObjectAnimator blink = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f, 1f);
        blink.setDuration(200);
        blink.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
