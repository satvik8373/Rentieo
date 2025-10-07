# Rentieo - Rental Marketplace App

A modern Android rental marketplace application built with Kotlin and Jetpack Compose.

## 📱 Features

### User Features
- **Authentication**: Email/Password, Google Sign-In, Guest Mode
- **Listings**: Browse, search, and filter rental items
- **Categories**: Electronics, Vehicles, Real Estate, Fashion, Sports, Tools
- **Chat**: Real-time messaging with sellers
- **Profile Management**: Edit profile, view listings, saved items
- **Location Services**: Location-based listings
- **Image Upload**: Multiple images per listing

### Admin Features
- **User Management**: Verify users, toggle admin roles, delete users
- **Listing Management**: Hide/unhide listings, delete listings
- **Dashboard**: View statistics and analytics
- **Real-time Updates**: All changes reflect immediately

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase
  - Authentication
  - Firestore Database
  - Cloud Storage
  - Cloud Messaging
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil
- **Location**: Google Play Services Location

## 📦 Project Structure

```
Rentieo/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   └── google-services.json      # Firebase configuration
├── src/
│   └── main/
│       ├── java/com/mavrix/Olx_Rental/
│       │   ├── data/
│       │   │   ├── model/        # Data models
│       │   │   ├── repository/   # Data repositories
│       │   │   └── service/      # Services (Location, etc.)
│       │   ├── ui/
│       │   │   ├── screen/       # Compose screens
│       │   │   ├── theme/        # App theme
│       │   │   └── viewmodel/    # ViewModels
│       │   └── MainActivity.kt   # Main activity
│       ├── res/                  # Resources (drawables, values, etc.)
│       └── AndroidManifest.xml   # App manifest
├── gradle/                       # Gradle wrapper and version catalog
├── build.gradle.kts             # Root build configuration
├── settings.gradle.kts          # Project settings
└── README.md                    # This file
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK 24 or later
- Firebase project with:
  - Authentication enabled (Email/Password, Google)
  - Firestore Database
  - Cloud Storage
  - Cloud Messaging

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Olx_Rental
   ```

2. **Configure Firebase**
   - Download `google-services.json` from your Firebase project
   - Place it in the `app/` directory

3. **Update Firebase configuration**
   - Update Firestore rules in `firestore.rules`
   - Update Storage rules in `storage.rules`
   - Deploy rules using Firebase CLI:
     ```bash
     firebase deploy --only firestore:rules
     firebase deploy --only storage:rules
     ```

4. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Install on device**
   ```bash
   ./gradlew installDebug
   ```
   Or use the pre-built APK: `Rentieo-App-Working.apk`

## 🔧 Configuration

### Gradle Dependencies

Key dependencies are managed in `gradle/libs.versions.toml`:
- Kotlin 2.0.21
- Compose BOM 2024.09.00
- Firebase BOM 32.7.0
- Material3
- Coil for image loading
- Accompanist libraries

### Build Configuration

- **compileSdk**: 36
- **minSdk**: 24
- **targetSdk**: 36
- **JVM Target**: 11

## 📱 APK

The latest working APK is available: **Rentieo-App-Working.apk** (37.83 MB)

### Installation
1. Transfer the APK to your Android device
2. Enable "Install from Unknown Sources" in Settings
3. Open the APK file and install

## 🔐 Firebase Setup

### Firestore Collections

- **users**: User profiles and authentication data
- **listings**: Rental item listings
- **chats**: Chat conversations
- **messages**: Chat messages

### Security Rules

Firestore and Storage rules are configured to:
- Allow authenticated users to read/write their own data
- Allow admins to manage all data
- Validate data structure and types
- Prevent unauthorized access

## 👥 User Roles

- **Guest**: Browse listings only
- **Buyer**: Browse, chat, save listings
- **Seller**: Create and manage listings
- **Admin**: Full access to user and listing management

## 📝 Documentation

- **AUTH_FLOW_COMPLETE.md**: Authentication implementation details
- **RELEASE_NOTES.md**: Version history and changes
- **ADMIN_LISTING_FIX.md**: Admin panel listing visibility fix

## 🐛 Known Issues

- Hidden listings disappear from admin panel (by design - only active listings shown)
- Some Material icons show deprecation warnings (non-breaking)

## 🔄 Recent Updates

### Latest Version
- ✅ Fixed admin panel buttons (verify, toggle admin, delete)
- ✅ Fixed chat navigation crash
- ✅ Added confirmation dialogs for destructive actions
- ✅ Real-time updates for admin actions
- ✅ Proper error handling and logging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is proprietary software. All rights reserved.

## 📧 Contact

For questions or support, please contact the development team.

---

**Built with ❤️ using Kotlin and Jetpack Compose**
