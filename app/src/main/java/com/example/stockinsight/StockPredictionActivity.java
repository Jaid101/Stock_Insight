package com.example.stockinsight;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StockPredictionActivity extends AppCompatActivity {

    private AutoCompleteTextView searchStock;
    private TextView predictionResult;
    private RequestQueue requestQueue;
    private static final String API_URL = "https://stock-prediction-api-mku6.onrender.com/predict?symbol=%s&date=%s";
    private static final String[] STOCK_SYMBOLS = {"AAPL", "ABBV", "ADBE", "ADP", "AES", "ALGN", "AMD", "AMGN", "AMZN", "APPS",
            "ASML", "ATVI", "AVGO", "AXP", "BA", "BAC", "BAX", "BBY", "BDX", "BIIB", "BMY", "BNTX", "BP", "C", "CAT", "CHTR", "CL",
            "CMCSA", "CNI", "COF", "COP", "COST", "CP", "CRM", "CRWD", "CSCO", "CSX", "CTRA", "CVX", "D", "DD", "DDOG", "DE", "DFS",
            "DG", "DHR", "DIS", "DOCU", "DUK", "DVN", "EA", "EMR", "EOG", "ESTC", "EW", "EXC", "F", "FDX", "FIS", "FOX", "FSLY", "FTI",
            "GE", "GILD", "GIS", "GM", "GOOG", "GOOGL", "GPS", "GS", "HAL", "HAS", "HD", "HES", "HON", "IBM", "INTC", "ISRG", "ITW",
            "JBHT", "JNJ", "JPM", "KHC", "KO", "KSS", "KSU", "LLY", "LMT", "LOW", "LRCX", "LULU", "LYV", "MA", "MAT", "MCD", "MDB",
            "MMM", "MO", "MPC", "MRNA", "MS", "MSFT", "MU", "NEE", "NET", "NFLX", "NIO", "NKE", "NOC", "NOW", "NSC", "NVDA", "NXPI",
            "ODFL", "OKTA", "ORCL", "OXY", "PANW", "PAYX", "PEP", "PFE", "PG", "PLUG", "PM", "PSX", "PYPL", "QCOM", "REGN", "RL", "RMD",
            "ROK", "ROKU", "ROST", "SBUX", "SE", "SLB", "SNY", "SO", "SPOT", "SQ", "STE", "STM", "SYK", "T", "TFC", "TGT", "TJX",
            "TMO", "TMUS", "TSLA", "TTWO", "TWLO", "TXN", "ULTA", "UNH", "UNP", "UPS", "V", "VLO", "VRTX", "VZ", "WFC", "WMT", "XEL",
            "XOM", "YUM", "ZBH", "ZS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_prediction);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(v -> {
            String stockSymbol = searchStock.getText().toString().trim();
            if (!stockSymbol.isEmpty()) {
                fetchPrediction(stockSymbol);
            } else {
                Toast.makeText(this, "Enter a stock symbol", Toast.LENGTH_SHORT).show();
            }
        });

        searchStock = findViewById(R.id.searchStock);
        predictionResult = findViewById(R.id.predictionResult);

        requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, STOCK_SYMBOLS);
        searchStock.setAdapter(adapter);

        searchStock.setOnItemClickListener((parent, view, position, id) -> fetchPrediction(searchStock.getText().toString()));

    }

    private void fetchPrediction(String symbol) {
        String nextDate = getNextDate();
        String url = String.format(API_URL, symbol, nextDate);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        double predictedPrice = response.getDouble("predicted_price");
                        predictionResult.setText("Predicted Price: " + predictedPrice + "\nCompany: " + symbol);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing prediction", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch prediction", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private String getNextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
