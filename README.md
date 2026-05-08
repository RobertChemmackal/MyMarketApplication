# MyMarketPlace - Local-First Android Marketplace

A robust, offline-first marketplace application built with modern Android development practices. This app ensures a seamless user experience by allowing browsing, creating, and favoriting listings even without an active internet connection, with automatic background synchronization.

---

## Architecture (Clean Architecture)

The project follows **Clean Architecture** principles, separated into three distinct layers to ensure maintainability, testability, and scalability:

### 1. Presentation Layer (MVVM)
- **Jetpack Compose**: Declarative UI components.
- **ViewModel**: Manages UI state using `StateFlow` and handles user events.
- **UI State**: Atomic state management for reliable updates (Loading, Syncing, Offline, Error).

### 2. Domain Layer
- **Models**: Pure Kotlin data classes representing the business entities.
- **Use Cases**: Encapsulates specific business logic (e.g., `SyncPendingListingsUseCase`, `ToggleFavoriteUseCase`).
- **Repository Interfaces**: Defines the contract for data operations.

### 3. Data Layer
- **Room Persistence**: The single source of truth for the application.
- **Repository Implementation**: Coordinates data between the local database and remote API.
- **Retrofit & OkHttp**: Handles network requests with a custom **MockInterceptor** for a reliable testing environment.
- **Mappers**: Converts between Data Transfer Objects (DTOs), Entities, and Domain Models.

---

## 🔄 Synchronization Flow (The Heart of the App)

MyMarketPlace implements a sophisticated synchronization engine designed for reliability:

###  Real-time Network Monitoring
- Uses a `NetworkObserver` built on `ConnectivityManager` to detect network changes (Available, Lost, etc.).
- **Initial State Detection**: Immediately detects if the app starts offline.

###  Offline-First CRUD
- **Create Listing**: New listings are instantly saved to Room with `syncStatus = 0`. They appear in the list immediately with an "Unsynced" status.
- **Favorite/Unfavorite**: Favoriting updates the local DB immediately and queues the change for the next sync.

###  Background Synchronization
- **Auto-Sync on Restore**: When the network is restored, the app automatically triggers  sync of all pending items.
- **Manual Sync**: Users can manually trigger a sync for specific items or pull-to-refresh to reconcile the entire list.


##  Tech Stack & Standards

- **Language**: Kotlin 2.0+
- **UI**: Jetpack Compose (Material 3)
- **DI**: Dagger Hilt
- **Asynchronous**: Coroutines & Flow
- **Network**: Retrofit 2, OkHttp 4, Kotlinx Serialization
- **Image Loading**: Coil (with caching)
- **Local DB**: Room (with Flow support)

---

##  Performance & Scalability

- **Large Dataset Handling**: Optimized to handle **200+ listings** using `LazyColumn` and efficient Room queries.
- **Mock API**: Custom interceptor simulates real-world latency and data persistence for 200 items.

---

## 🧪 Testing

- **Unit Tests**: Core synchronization logic and Use Cases are covered by unit tests.
- **Mocking**: Custom `MockInterceptor` allows for consistent and reproducible testing of offline scenarios.

---

##  Deliverables

- [x] Full Source Code (Native Android)
- [x] Room Integration & Local-First Logic
- [x] Automatic Background Synchronization
- [x] Detailed UI for Sync Statuses
- [x] Unit Test Suite
- [x] Architecture Diagram (Attachments)
