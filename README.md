<div align="center">

# 💬 Chit-Chat — Android App

**A real-time, full-featured chat application built with Kotlin and Firebase.**  
Pairs natively with the [Chit-Chat Web App](https://github.com/Thre4dripper/Chit-Chat-WebApp) — same Firestore backend, same real-time experience, every platform.

![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?logo=kotlin&logoColor=white&style=flat-square)
![Firebase](https://img.shields.io/badge/Firebase-34-FFCA28?logo=firebase&logoColor=black&style=flat-square)
![AGP](https://img.shields.io/badge/AGP-9.2.1-3DDC84?logo=android&logoColor=white&style=flat-square)
![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-3DDC84?logo=android&logoColor=white&style=flat-square)
![Gradle](https://img.shields.io/badge/Gradle-9.5.1-02303A?logo=gradle&logoColor=white&style=flat-square)

</div>

---

## ✨ Features

### 💬 Messaging

- **Direct Messages** — real-time 1-on-1 chat powered by Firestore `onSnapshot` listeners
- **Group Chats** — create groups, send messages, images & stickers as a team
- **Image sharing** — send photos in DMs and groups; tap any image to open a full-screen zoomable viewer
- **Animated stickers** — 30+ Lottie sticker pack with looping animations
- **Deleted message rendering** — messages unsent on web show as _"This message was deleted"_ on Android too

### 👤 User Profiles

- **Google & GitHub sign-in** — OAuth via Firebase Auth UI
- **Username** — set once across a guided 3-step onboarding (Username → Name → Bio)
- **Profile picture** — crop with uCrop, upload to Firebase Storage, update at any time
- **Online / Last Seen** — live status updated in real-time in chat headers

### 📋 Chat Management

- **DM profile panel** — view partner's profile image, name, bio, shared media, and common groups
- **Mute notifications** — per-chat and per-group mute toggle
- **Favourite chats** — star important conversations; they float to a dedicated section on the home screen
- **Clear / Delete chat** — clear message history or remove the DM entirely
- **Group profile panel** — change group image (with crop), view members, leave or delete the group

### 🔔 Push Notifications

- **Firebase Cloud Messaging (FCM)** — background push with dedicated DM and group notification channels
- **AWS Lambda delivery** — notifications dispatched via a serverless Lambda function behind API Gateway
- **Rich notification types** — text (BigText style), image (BigPicture style with inline preview), sticker
- **Smart suppression** — notifications are skipped when the app is in the foreground or the chat is muted
- **Notification click routing** — tapping a notification opens the app and navigates directly to the relevant chat

### 🔍 Discoverability

- **Home screen search** — filter DMs and group chats in real time from the home screen
- **Add new chats** — search for users by username to start a new conversation
- **Common Groups** — DM profile panel lists all groups both users share
- **Group member → DM** — tap any group member to jump directly to their DM

---

## 🤝 Web Companion

The Android app shares its entire Firebase backend with the **[Chit-Chat Web App](https://github.com/Thre4dripper/Chit-Chat-WebApp)** (React 19 + TypeScript). Every feature works seamlessly across both platforms — messages, stickers, group management, read receipts, push notifications — all in real time.

---

## 🛠️ Tech Stack

| Layer               | Technology                                    |
| ------------------- | --------------------------------------------- |
| Language            | Kotlin (AGP 9.0 built-in Kotlin)              |
| Architecture        | MVVM — ViewModel, Repository, LiveData        |
| Build System        | AGP 9.2.1 · Gradle 9.5.1 · Kotlin DSL        |
| Dependency Mgmt     | Gradle Version Catalog (`libs.versions.toml`) |
| UI                  | Material Design 3 · Data Binding              |
| Backend / Auth      | Firebase 34 (Auth, Firestore, Storage, FCM)   |
| Auth UI             | Firebase UI Auth 9.1.1                        |
| Push Notifications  | FCM + AWS Lambda + API Gateway                |
| Image Loading       | Glide 5                                       |
| Image Cropping      | uCrop 2.2.11                                  |
| Animations          | Lottie 6.7.1                                  |
| Zoomable Image View | TouchImageView 3.7.2                          |
| HTTP Client         | Volley 1.2.1                                  |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest stable)
- A Firebase project with **Auth**, **Firestore**, **Storage**, and **Cloud Messaging** enabled
- SHA-1 fingerprint registered in the Firebase Console for your debug/release keystore
- An AWS Lambda function for FCM dispatch (see [Notification Setup](#-push-notifications-setup))

### Clone & Open

```bash
git clone https://github.com/Thre4dripper/Chit-Chat-AndroidApp.git
```

Open the project in Android Studio and let it sync.

### Firebase Configuration

1. Download your `google-services.json` from the Firebase Console
2. Place it in the `app/` directory (replacing any existing file)
3. Ensure your debug SHA-1 is registered: **Firebase Console → Project Settings → Your Android app → SHA certificate fingerprints**

### Local Configuration

Create or update `local.properties` in the project root with the following keys:

```properties
# Notification dispatcher (AWS Lambda)
LAMBDA_FCM_URL=https://your-api-gateway-url/fcm
LAMBDA_FCM_API_KEY=your-api-key
LAMBDA_FCM_AUTH_TOKEN=your-auth-token

# Release signing (optional — only needed for release builds)
RELEASE_KEYSTORE_PATH=path/to/your/keystore.jks
RELEASE_STORE_PASSWORD=your-store-password
RELEASE_KEY_ALIAS=your-key-alias
RELEASE_KEY_PASSWORD=your-key-password
```

> ⚠️ `local.properties` is gitignored by default — never commit secrets to source control.

### Build & Run

Open the project in Android Studio and click **Run**, or build from the terminal:

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config in local.properties)
./gradlew assembleRelease

# Clean build
./gradlew clean assembleDebug
```

---

## 🔔 Push Notifications Setup

Notifications are dispatched by an **AWS Lambda** function using the Firebase Admin SDK. The app calls it via Volley with:

```
POST <LAMBDA_FCM_URL>
x-api-key: <LAMBDA_FCM_API_KEY>
Authorization: Bearer <LAMBDA_FCM_AUTH_TOKEN>
Content-Type: application/json

{
  "deviceToken": "<recipient FCM token>",
  "data": { "title": "...", "body": "...", ... }
}
```

### Notification Channels

| Channel      | ID                   | Content                          |
| ------------ | -------------------- | -------------------------------- |
| Direct chats | `user_chat_channel`  | Text, Image (BigPicture), Sticker |
| Group chats  | `group_chat_channel` | Text, Image (BigPicture), Sticker |

Notifications are only delivered when the app is in the **background** and the chat is **not muted** by the recipient.

---

## 📁 Project Structure

```
app/src/main/java/com/example/chitchatapp/
├── adapters/          # RecyclerView adapters (chats, groups, stickers, members)
├── constants/         # Firestore collection names, notification IDs, shared keys
├── enums/             # ChatMessageType, ChatType, FragmentType, UserStatus
├── firebase/
│   ├── auth/          # FirebaseUI sign-in flow & Firestore user registration
│   ├── chats/         # Chat CRUD — send, get, delete, clear, mark favourite
│   ├── groups/        # Group CRUD — create, update, exit, delete
│   ├── messaging/     # FCM notification dispatch & mute/unmute logic
│   ├── profile/       # Profile get & update
│   ├── user/          # User details, status, FCM token updates, search
│   └── utils/         # Chat utilities, time formatting, storage, CRUD helpers
├── models/            # Kotlin data classes (UserModel, ChatModel, GroupChatModel, …)
├── notifications/     # FCMConfig service, UserChatNotifications, GroupChatNotifications
├── repository/        # Repository layer orchestrating Firebase calls per feature
├── store/             # SharedPreferences helpers (username, FCM token)
├── ui/
│   ├── activities/    # HomeActivity, ChatActivity, GroupChatActivity, SetDetailsActivity, …
│   ├── bottomSheet/   # StickersBottomSheet
│   └── fragments/     # UsernameFragment, NameFragment, BioFragment (onboarding)
└── viewModels/        # MVVM ViewModels per activity/feature
```

---

## 📄 License

MIT

