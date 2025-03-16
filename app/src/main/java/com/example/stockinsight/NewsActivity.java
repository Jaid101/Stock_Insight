package com.example.stockinsight;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.MaterialToolbar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private static final String NEWS_URL = "https://www.cnbc.com/markets/";

    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsModel> newsList;
    private SwipeRefreshLayout swipeRefreshLayout;
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

        fetchNews();
    }

    private void fetchNews() {
        swipeRefreshLayout.setRefreshing(true);
        uniqueTitles.clear();
        new FetchNewsTask().execute();
    }

    private class FetchNewsTask extends AsyncTask<Void, Void, List<NewsModel>> {
        @Override
        protected List<NewsModel> doInBackground(Void... voids) {
            List<NewsModel> newNewsList = new ArrayList<>();
            try {
                Document doc = Jsoup.connect(NEWS_URL).get();
                Elements articles = doc.select("div.Card-standardBreakerCard");

                for (Element article : articles) {
                    String title = article.select("a.Card-title").text().trim();
                    String source = "CNBC";
                    String newsUrl = article.select("a.Card-title").attr("href"); // Extract URL

                    // Ensure full URL
                    if (!newsUrl.startsWith("http")) {
                        newsUrl = "https://www.cnbc.com" + newsUrl;
                    }

                    // Try fetching image URL from data-src, fallback to src
                    String imageUrl = article.select("img").attr("data-src");
                    if (imageUrl.isEmpty()) {
                        imageUrl = article.select("img").attr("src");
                    }

                    if (!title.isEmpty() && !imageUrl.isEmpty() && uniqueTitles.add(title)) {
                        newNewsList.add(new NewsModel(title, source, imageUrl, newsUrl));
                    }
                }
            } catch (IOException e) {
                Log.e("FetchNewsTask", "Error fetching news: " + e.getMessage());
            }
            return newNewsList;
        }

        @Override
        protected void onPostExecute(List<NewsModel> newNewsList) {
            swipeRefreshLayout.setRefreshing(false);
            if (!newNewsList.isEmpty()) {
                newsList.clear();
                newsList.addAll(newNewsList);
                newsAdapter.notifyDataSetChanged();
            }
        }
    }
}
