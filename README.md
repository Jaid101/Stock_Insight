
# Stock Insight App

![WhatsApp Image 2024-10-12 at 4 53 20 PM](https://github.com/user-attachments/assets/36383bec-8766-4afa-b7d7-43dadbd55900)
![WhatsApp Image 2024-10-12 at 4 53 20 PM (1)](https://github.com/user-attachments/assets/29931881-525e-4e7d-9260-529ed6e6d5c6)
![WhatsApp Image 2024-10-12 at 4 53 21 PM](https://github.com/user-attachments/assets/4f5cb2da-b6b9-4610-b0dd-9fe6a9fac8d5)

## Overview
The Stock Insight App is an Android application that provides real-time stock price updates, company information, and related news. It utilizes the Twelve Data API for stock prices and the GNews API for financial news.

## Features

- Fetches real-time stock prices based on user-inputted stock symbols.
- Displays company name and stock exchange information.
- Retrieves and displays the latest news related to the stock market.
- Automatic updates every minute for live tracking of stock prices.
- Visual indications of stock price changes with color coding (green for increase, red for decrease, and yellow for no change).

## Technologies Used

- Android SDK
- Java
- Volley library for network requests
- JSON for data interchange

## Getting Started

### Prerequisites

- Android Studio installed on your computer
- Android device or emulator for testing

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Jaid101/StockInsight.git
   ```
   
2. **Open the project in Android Studio:**
   - Launch Android Studio and select "Open an Existing Project."
   - Navigate to the cloned repository and open it.

3. **Add API keys:**
   - Replace the placeholder API keys in the `MainActivity.java` file with your actual API keys from [Twelve Data](https://twelvedata.com/) and [GNews](https://gnews.io/).

4. **Build and run the application:**
   - Connect your Android device or start an emulator.
   - Click on the "Run" button in Android Studio.

## Usage

1. Enter a valid stock symbol in the input field (e.g., `AAPL` for Apple).
2. Press the "Update" button to fetch the stock price and news.
3. The stock price will update live with visual cues indicating price changes.

## API Usage Limits

- **Twelve Data API**: Check your account plan for the request limits.
- **GNews API**: Check your account plan for the request limits.

## Contributing

Contributions are welcome! If you'd like to contribute, please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Twelve Data API](https://twelvedata.com/)
- [GNews API](https://gnews.io/)
