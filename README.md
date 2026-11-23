# Sticky Notes

A native Android sticky-notes app built with **Kotlin**, **Jetpack Compose** and **Material 3**, structured with
**Clean Architecture** and the **MVI (Model-View-Intent)** presentation pattern.

## Tech stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose (BOM), Material 3, dynamic colour, edge-to-edge |
| Navigation | Navigation Compose with type-safe `@Serializable` routes |
| State | `StateFlow` + `collectAsStateWithLifecycle`, one-off effects via `Channel` |
| DI | Hilt (KSP) + `hiltViewModel()` |
| Persistence | Room (KSP, `@Upsert`, `Flow` queries, exported schemas) |
| Async | Kotlin Coroutines & Flow |
| Build | Gradle Kotlin DSL, version catalog (`gradle/libs.versions.toml`), AGP 9 built-in Kotlin |

## Architecture

```
┌────────────────────────── :app ──────────────────────────┐
│ presentation  (Compose UI, MVI ViewModels, navigation)   │
│      │ uses                                              │
│      ▼                                                   │
│ di  ── binds ──► data (Room entity/DAO, mappers, repo)   │
└──────┼───────────────────────┼───────────────────────────┘
       ▼                       ▼ implements
┌──────────────────────── :domain ─────────────────────────┐
│ model · repository contracts · use cases (pure Kotlin)   │
└──────────────────────────────────────────────────────────┘
```

* **`:domain`** – pure Kotlin/JVM module with no Android dependencies: `Note` model, `NoteRepository`
  contract and use cases (`ObserveNotesUseCase`, `GetNoteUseCase`, `SaveNoteUseCase`, `DeleteNoteUseCase`,
  `TogglePinNoteUseCase`).
* **`:app` / `data`** – Room database, entity ↔ domain mappers and `OfflineNoteRepository`.
* **`:app` / `di`** – Hilt modules wiring the data layer to domain contracts.
* **`:app` / `presentation`** – Compose screens and MVI ViewModels.

### MVI

Each screen has a *contract* file describing its:

* **State** (`UiState`) – an immutable data class rendered by the UI.
* **Intent** (`UiIntent`) – user actions sent to the ViewModel through a single `onIntent()` entry point.
* **Effect** (`UiEffect`) – one-off events such as navigation or snackbars.

`MviViewModel` exposes `state: StateFlow<S>` and `effects: Flow<E>`; screens are split into a stateful
`XxxScreen` (wires the ViewModel) and a stateless, previewable `XxxContent(state, onIntent)`.

```
View ──Intent──► ViewModel ──reduce──► State ──► View
                     └──────Effect (once)──────► View
```

## Project structure

```
app/src/main/java/com/gnarayans/stickynotes
├── MainActivity.kt / StickyNotesApplication.kt
├── data/        local (Room), mapper, repository
├── di/          Hilt modules
└── presentation/
    ├── mvi/         MVI base classes
    ├── navigation/  type-safe destinations + NavHost
    ├── notes/       notes grid (contract, ViewModel, screen)
    ├── editor/      note editor (contract, ViewModel, screen)
    ├── components/  reusable composables
    └── theme/       Material 3 theme & note colours
domain/src/main/kotlin/com/gnarayans/stickynotes/domain
├── model/  repository/  usecase/  util/
```

## Build & test

Requires JDK 17 and the Android SDK.

```bash
./gradlew assembleDebug          # build the app
./gradlew :domain:test           # domain unit tests
./gradlew testDebugUnitTest      # ViewModel unit tests
```
