# AgriMata - Farming Community App
AgriMata is a mobile application designed to connect farmers directly with buyers and provide agricultural insights, weather updates, and an interactive marketplace for farm products.

## 📌 Features
- 🔑 **Authentication**: Firebase Authentication for secure user login.
- 🏪 **Marketplace**: Farmers can list products for sale, and buyers can browse and purchase.
- 🌦 **Weather Updates**: Real-time weather data using OpenWeatherMap API.
- 📦 **Product Management**: Farmers can add, update, and delete product listings.
- 📌 **Location-Based Filtering**: Buyers can search for farm products based on location.
- 🔔 **Notifications**: Firebase Cloud Messaging (FCM) for real-time updates.
- 🏗 **Supabase Integration**: Used for database, real-time storage, and image uploads.
- 
## 🛠 Tech Stack

- **Frontend**: Jetpack Compose (Kotlin)
- **Authentication**: Firebase Authentication/Supabase Gotrue
- **Database**: Supabase PostgreSQL
- **Storage**: Supabase Storage (Images, Videos, Documents)
- **Notifications**: Firebase Cloud Messaging (FCM)
- **Weather API**: OpenWeatherMap API
- **Architecture**: MVVM (Model-View-ViewModel)

## 📂 Project Structure
```
AgriMata/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/agrimata/
│   │   │   │   ├── screens/      # UI Components (Jetpack Compose)
│   │   │   │   ├── viewmodel/    # ViewModel Layer (Handles Logic)
│   │   │   │   ├── repository/   # Repository Layer (Handles Data Fetching)
│   │   │   │   ├── model/        # Data Models (FarmerProduct, User, etc.)
│   │   │   │   ├── network/      # API and Supabase Configurations
│   │   │   │   ├── utils/        # Helper Functions and Utilities
│   │   │   ├── res/              # UI Resources (Icons, Strings, Themes)
```

## 📌 Installation & Setup

1. Clone the repository:
   ```sh
   git clone https://github.com/DarwinsOmondi/AgriMata.git
   cd AgriMata
   ```

2. Open the project in **Android Studio**.

3. Configure Firebase:
   - Create a Firebase project.
   - Enable Firebase Authentication.
   - Download `google-services.json` and place it in `app/`.

4. Set up Supabase:
   - Create a Supabase project.
   - Set up PostgreSQL database.
   - Configure storage buckets for images and videos.

5. Update `local.properties` or `constants.kt` with API keys:
   ```kt
   const val SUPABASE_URL = "your_supabase_url"
   const val SUPABASE_API_KEY = "your_supabase_key"
   const val OPENWEATHER_API_KEY = "your_openweather_api_key"
   ```

6. Run the app:
   ```sh
   ./gradlew build
   ```

## 💡 Future Enhancements

- AI-based crop recommendations
- Advanced filtering options for buyers
- Blockchain integration for transparent transactions
- Offline mode support

## 🤝 Contributing

Contributions are welcome! Feel free to fork this repository and submit pull requests.

## 📜 License

This project is licensed under the MIT License.

---
**🌱 AgriMata - Empowering Farmers, Connecting Buyers!**
