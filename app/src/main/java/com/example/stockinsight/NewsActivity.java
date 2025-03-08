package com.example.stockinsight;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private static final String API_KEY = "xZSnlGyfDnhia3CHN7yZbCy0zNdUJK6c4fPPQKzn";
    private static final String API_URL = "https://api.marketaux.com/v1/news/all?language=en&limit=20&api_token=" + API_KEY;

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsModel> newsList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue requestQueue;
    private HashSet<String> uniqueTitles = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        MaterialToolbar toolbar = findViewById(R.id.newsToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchNews);

        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList, this);
        newsRecyclerView.setAdapter(newsAdapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchNews();
    }

    private void fetchNews() {
        swipeRefreshLayout.setRefreshing(true);
        uniqueTitles.clear(); // Clear previous titles to allow new articles

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    Log.d("NewsActivity", "API Response: " + response.toString()); // Debugging log
                    processNewsResponse(response);
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    Log.e("NewsActivity", "API Request Failed: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    swipeRefreshLayout.setRefreshing(false);
                }
        );
        requestQueue.add(request);
    }

    private void processNewsResponse(JSONObject response) {
        try {
            JSONArray articles = response.getJSONArray("data");
            List<NewsModel> newNewsList = new ArrayList<>();

            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);
                String title = article.optString("title", "").trim();

                // Ensure we only fetch articles with complete and non-empty titles
                if (!title.isEmpty() && uniqueTitles.add(title)) {
                    String source = article.optString("source", "Marketaux");
                    String imageUrl = article.optString("image_url", "");
                    newNewsList.add(new NewsModel(title, source, imageUrl));
                }
            }

            if (!newNewsList.isEmpty()) {
                newsList.clear();
                newsList.addAll(newNewsList);
                newsAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.e("NewsActivity", "JSON Parsing Error: " + e.getMessage());
        }
    }
}
