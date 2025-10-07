# 🏠 Rentieo - Rental Marketplace App

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](LICENSE)

A modern, feature-rich Android rental marketplace application built with Kotlin and Jetpack Compose. Rentieo connects people who want to rent items with those who have items to rent.

## 📱 Features

### 🔐 Authentication
- **Email/Password** authentication
- **Google Sign-In** integration
- **Guest Mode** for browsing
- Email verification
- Secure password reset

### 🏪 Marketplace
- **Browse Listings** - View all available rental items
- **Categories** - Electronics, Vehicles, Real Estate, Fashion, Sports, Tools
- **Search & Filter** - Find exactly what you need
- **Location-Based** - See items near you
- **Image Gallery** - Multiple images per listing with swipe navigation

### 💬 Communication
- **Real-time Chat** - Message sellers directly
- **Chat History** - View all conversations
- **Notifications** - Stay updated on messages

### 👤 User Features
- **Profile Management** - Edit your profile and preferences
- **My Listings** - Manage your rental items
- **Saved Items** - Bookmark favorites
- **Verification Badge** - Build trust with verified accounts

### 🛡️ Admin Panel (Redesigned!)
- **Dashboard** - Overview statistics with modern cards
- **User Management** - Verify users, toggle admin roles, delete accounts
- **Listing Management** - Hide/show listings, delete items
- **Analytics** - View platform statistics and trends
- **Responsive Design** - Beautiful UI with proper image display

## 🎨 Screenshots

### User Interface
- Modern Material3 design
- Dark/Light theme support
- Smooth animations
- Intuitive navigation

### Admin Panel
- Professional dashboard with gradient cards
- User cards with avatars and action buttons
- Listing cards with image thumbnails
- Real-time updates

## 🛠️ Tech Stack

### Frontend
- **Kotlin** - Modern, concise programming language
- **Jetpack Compose** - Declarative UI framework
- **Material3** - Latest Material Design components
- **Navigation Compose** - Type-safe navigation
- **Coil** - Image loading library

### Backend
- **Firebase Authentication** - User management
- **Cloud Firestore** - NoSQL database
- **Cloud Storage** - Image storage
- **Cloud Messaging** - Push notifications

### Architecture
- **MVVM** - Model-View-ViewModel pattern
- **Repository Pattern** - Data abstraction
- **StateFlow** - Reactive state management
- **Coroutines** - Asynchronous programming

## 📦 Project Structure

```
Rentieo/
├── app/
│   ├── build.gradle.kts          # App-level build config
│   └── google-services.json      # Firebase configuration
├── src/
│   └── main/
│       ├── java/com/mavrix/Olx_Rental/
│       │   ├── data/
│       │   │   ├── model/        # Data models (User, Listing, Chat)
│       │   │   ├── repository/   # Data repositories
│       │   │   └── service/      # Services (Location, Storage)
│       │   ├── ui/
│       │   │   ├── screen/       # Compose screens
│       │   │   ├── theme/        # App theme
│       │   │   └── viewmodel/    # ViewModels
│       │   └── MainActivity.kt   # Main activity
│       ├── res/                  # Resources
│       └── AndroidManifest.xml   # App manifest
├── gradle/                       # Gradle configuration
├── build.gradle.kts             # Root build config
└── README.md                    # This file
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK 24 or later
- Firebase project

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/satvik8373/Rentieo.git
   cd Rentieo
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Enable Authentication (Email/Password, Google)
   - Create a Firestore Database
   - Enable Cloud Storage
   - Download `google-services.json`
   - Place it in the `app/` directory

3. **Update Firebase Rules**
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

## 🔧 Configuration

### Firebase Collections

#### users
```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "role": "buyer|seller|admin",
  "isVerified": "boolean",
  "createdAt": "timestamp"
}
```

#### listings
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "price": "number",
  "category": "string",
  "images": ["string"],
  "userId": "string",
  "isActive": "boolean",
  "createdAt": "timestamp"
}
```

#### chats
```json
{
  "id": "string",
  "participants": ["string"],
  "lastMessage": "string",
  "lastMessageTime": "timestamp"
}
```

## 📱 APK Download

Latest version: **Rentieo-App-Redesigned.apk** (37.83 MB)

### Installation Steps
1. Download the APK from releases
2. Enable "Install from Unknown Sources" in Settings
3. Open the APK file and install
4. Launch Rentieo!

## 🎯 Key Features Explained

### Admin Panel Redesign
The admin panel has been completely redesigned with:
- ✅ Modern gradient cards with better typography
- ✅ Image thumbnails in listing cards
- ✅ Responsive text handling (no overflow)
- ✅ Full-width action buttons with labels
- ✅ Status indicators with colored badges
- ✅ Confirmation dialogs with icons
- ✅ Professional color scheme

### Real-time Chat
- Instant message delivery
- Read receipts
- Typing indicators
- Message history

### Location Services
- GPS-based location detection
- Distance calculation
- Location-based search
- Map integration ready

## 🔐 Security

- Firebase Authentication for secure login
- Firestore Security Rules for data protection
- Storage Rules for image access control
- Input validation and sanitization
- Secure password handling

## 📊 Analytics

The admin panel provides insights into:
- Total users and verified users
- Total listings and active listings
- User roles distribution
- Platform activity trends

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Write clean, maintainable code

## 📝 Documentation

- [Authentication Flow](AUTH_FLOW_COMPLETE.md)
- [Release Notes](RELEASE_NOTES.md)
- [Admin Panel Redesign](ADMIN_PANEL_REDESIGN.md)
- [Cleanup Summary](CLEANUP_COMPLETE.md)

## 🐛 Known Issues

- Some Material icons show deprecation warnings (non-breaking)
- Google Sign-In requires SHA-1 certificate configuration

## 🔄 Roadmap

- [ ] Payment integration
- [ ] Rating and review system
- [ ] Advanced search filters
- [ ] Booking calendar
- [ ] In-app notifications
- [ ] Multi-language support
- [ ] Dark mode improvements

## 📄 License

This project is proprietary software. All rights reserved.

## 👥 Team

- **Developer** - Satvik
- **Project** - Rentieo Rental Marketplace

## 📧 Contact

For questions or support:
- GitHub: [@satvik8373](https://github.com/satvik8373)
- Repository: [Rentieo](https://github.com/satvik8373/Rentieo)

## 🙏 Acknowledgments

- Firebase for backend services
- Jetpack Compose for modern UI
- Material Design for design guidelines
- Coil for image loading
- All open-source contributors

---

**Built with ❤️ using Kotlin and Jetpack Compose**

⭐ Star this repository if you find it helpful!
