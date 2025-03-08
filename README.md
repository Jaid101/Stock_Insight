# Stock Insights

Stock Insights is an Android application that provides real-time stock price updates, company information, financial news, and stock price predictions. It integrates multiple APIs to offer a comprehensive stock market experience.

## Features
- **Real-time Stock Prices**: Fetches live stock prices from various sources.
- **Company Information**: Displays the company name and stock exchange details.
- **Market News**: Retrieves and displays the latest financial news.
- **Stock Price Prediction**: Predicts the next day's stock price using a machine learning model.
- **Live Updates**: Updates stock prices every 5 seconds for popular stocks and on demand for searched stocks.
- **Color-Coded Price Changes**:
  - Green: Price increased
  - Red: Price decreased
  - Yellow: No change
- **User-Friendly Interface**: Easy navigation with multiple activities for news, settings, logout, and stock prediction.

## Technologies Used
- **Android SDK**
- **Java**
- **Volley library** for network requests
- **JSON** for data interchange

## APIs Integrated
- **Yahoo Finance API**: Fetches stock prices for the 'Popular' section.
- **Twelve Data API**: Fetches stock prices for searched symbols.
- **StockData API**: Provides stock market-related news.
- **Render API**: Predicts next-day stock prices.
  - Format: `/predict?symbol={symbol}&date={next_date}`

## Getting Started

### Prerequisites
- Android Studio installed on your computer
- Android device or emulator for testing

### Installation
1. **Clone the repository**:
   ```sh
   git clone https://github.com/Jaid101/StockInsight.git
   ```
2. **Open the project in Android Studio**:
   - Launch Android Studio and select "Open an Existing Project."
   - Navigate to the cloned repository and open it.
3. **Add API keys**:
   - Replace placeholder API keys in `MainActivity.java` and related files with your actual API keys for Yahoo Finance, Twelve Data, StockData API, and Render API.
4. **Build and run the application**:
   - Connect your Android device or start an emulator.
   - Click on the "Run" button in Android Studio.

## Usage
- **Search Stock Prices**:
  - Enter a valid stock symbol (e.g., `AAPL` for Apple).
  - Press the "Update" button to fetch the stock price and news.
- **View Market News**:
  - Navigate to the News section to see the latest stock market news.
- **Predict Stock Prices**:
  - Enter a stock symbol and view the predicted price for the next day.

## API Usage Limits
- **Yahoo Finance API**: Limited by Yahoo's request policies.
- **Twelve Data API**: Check your account plan for request limits.
- **StockData API**: News retrieval limits depend on the plan.
- **Render API**: Stock prediction request limits apply.

## Contributing
Contributions are welcome! If you'd like to contribute, please fork the repository and submit a pull request with your changes.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments
- **Yahoo Finance API**
- **Twelve Data API**
- **StockData API**
- **Render API**

