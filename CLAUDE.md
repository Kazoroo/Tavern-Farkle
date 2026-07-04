# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Tavern Farkle is a native Android dice game (Kotlin + Jetpack Compose), inspired by the Farkle mini-game from Kingdom Come: Deliverance. It supports singleplayer (vs. an AI opponent) and realtime multiplayer (via Firebase Realtime Database), special dice with strategic effects, a coin betting/shop economy, AdMob rewarded ads, and onboarding tutorials.

## Common commands

All commands run from the repo root using the Gradle wrapper.

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests (JVM, app/src/test) — this is what CI runs
./gradlew :app:testDebugUnitTest

# Run a single unit test class
./gradlew :app:testDebugUnitTest --tests "pl.kazoroo.tavernFarkle.domain.usecase.CalculatePointsUseCaseTest"

# Run a single unit test method
./gradlew :app:testDebugUnitTest --tests "pl.kazoroo.tavernFarkle.domain.usecase.CalculatePointsUseCaseTest.check if three 1s gives 1000 points"

# Run instrumented tests (app/src/androidTest, requires connected device/emulator)
./gradlew :app:connectedDebugAndroidTest
```

Notes:
- `app/build.gradle` requires a `google-services.json` file in `app/` (Firebase). CI generates it from a secret (`.github/workflows/gradle.yml`); locally you need your own copy to build the `app` module at all.
- `compileSdk`/`targetSdk` 36, `minSdk` 28, Java/Kotlin target 17.
- The project uses Jetpack Compose (no XML layouts for UI) with Compose BOM version management.

## Architecture

The app follows a layered/clean architecture, organized by **feature package** rather than by layer at the top level. Each feature package (`singleplayer`, `multiplayer`, `shop`, `settings`, `menu`) contains its own `data`/`domain`/`presentation` subpackages; cross-cutting game concepts live under `core`.

```
pl.kazoroo.tavernFarkle/
├── core/            # Shared domain models, repository interface, use cases, navigation, shared UI
├── di/              # Manual DI container + Application class (no Hilt/Koin)
├── menu/            # Main menu screen + sound management + splashscreen
├── singleplayer/    # Local game repository, opponent AI turn logic, GameScreen/GameViewModel
├── multiplayer/     # Firebase-backed repository, lobby, WorkManager status updates
├── shop/            # Special dice shop, inventory (DataStore/proto), ads
├── settings/        # User settings screen/viewmodel
└── ui/              # Compose theme
```

### Dependency injection

There is **no DI framework** (no Hilt/Koin/Dagger). `di/DependencyContainer.kt` is a hand-written container of `by lazy` properties wiring repositories and use cases together, plus `ViewModelProvider.Factory` builders for each screen's ViewModel. It's instantiated once in `di/TavernFarkleApp.kt` (`Application.onCreate`) and threaded down through Compose navigation (see `Navigation.kt`), which pulls factories off the container to construct ViewModels per-route via `viewModel(factory = ...)`.

When adding a new use case/repository/ViewModel, wire it into `DependencyContainer` following the existing `by lazy` / `viewModelFactoryHelper { }` patterns — don't introduce a new DI mechanism.

### Game state: single source of truth + repository swap

The central abstraction is `core/domain/repository/GameRepository`, an interface exposing `gameState: StateFlow<GameState>` plus mutator methods (`toggleDiceSelection`, `sumRoundPoints`, `changeCurrentPlayer`, etc.). There are two implementations:

- `singleplayer/data/repository/LocalGameRepository` — mutates only in-memory state.
- `multiplayer/data/repository/RemoteGameRepository` — mutates the same in-memory `StateFlow`, then pushes the diff to Firebase via `FirebaseDataSource`, and separately listens to Firebase for the opponent's changes (`observeGameData`), overwriting local state on remote updates.

Both implementations delegate all actual state-transition logic to `core/domain/GameStateUpdater` — a stateless class of pure `(GameState, ...) -> GameState` functions. This is the key place to look/modify when changing game rules mechanics (dice selection, scoring application, turn switching, skucha/farkle handling, etc.), since neither repository reimplements the transitions itself.

A single `GameViewModel` (`singleplayer/presentation/GameViewModel.kt`) is used for **both** singleplayer and multiplayer — `DependencyContainer.gameViewModelFactory(isMultiplayer: Boolean)` injects either `localGameRepository` or `remoteGameRepository` into it, and an `isMultiplayer` flag branches behavior (e.g. whether to trigger the AI opponent's turn or wait on Firebase).

### Multiplayer flow

1. Host creates a lobby (visible to others via `RemoteGameRepository.observeLobbyList`/Firebase `Lobby` nodes).
2. Another player joins via `JoinLobbyUseCase`.
3. Each player's actions apply immediately to local `StateFlow` state and are also written to their Firebase game node; the opponent's client is listening to the same node and updates reactively.
4. On game end, the lobby node is removed and rewards are granted.
5. `multiplayer/data/UpdatePlayerStatusWorker` (WorkManager) tracks/reports whether a player is still connected, so the lobby list can hide lobbies whose host process died without cleanup (see `PlayerStatus`).

DTOs for Firebase live in `multiplayer/data/model/` (`GameStateDto`, `PlayerDto`, `DiceDto`, `Lobby`) with `toDomain()`/`toDto()` conversions on the domain models — never store domain models directly in Firebase.

### Points calculation

`core/domain/usecase/game/CalculatePointsUseCase` implements Farkle scoring rules (triples, straights, single 1s/5s, four/five/six-of-a-kind, etc.) — see `CalculatePointsUseCaseTest` for the exact expected point values per combination when modifying scoring logic. `CheckForSkuchaUseCase` determines a "skucha" (Farkle bust — no scoring dice left).

### Singleplayer AI opponent

`singleplayer/domain/usecase/PlayOpponentTurnUseCase` drives the AI opponent's turn using `LocalGameRepository`, `DrawDiceUseCase`, and `CalculatePointsUseCase` — this is where opponent decision-making (which dice to keep/bank) lives.

### Shop / inventory / economy

`shop/domain/InventoryDataRepositoryImpl` persists owned special dice via a proto DataStore (`OwnedSpecialDiceSerializer`, `shop/domain/protoDataStore`). Coins/user data (name, coin balance, settings) persist via `core/data/local/repository/UserDataRepository` (preferences DataStore), accessed through `ReadUserDataUseCase`/`SaveUserDataUseCase`. `AdManager`/`AdViewModel` handle AdMob rewarded ads that grant bonus coins.

### Navigation

Single-Activity app (`MainActivity.kt`) with Compose Navigation (`core/presentation/navigation/Navigation.kt`, `Screen.kt`). Routes are plain string constants on a `Screen` sealed class; the `GameScreen` route takes an `isMultiplayer: Boolean` nav argument that determines which `GameRepository` gets injected into the shared `GameViewModel`.

### Onboarding / reveal overlays

Onboarding tutorial highlights use the `reveal-core` library (`RevealCanvas`/`rememberRevealCanvasState`, threaded through `Navigation` into each screen). Per-screen "reveal keys" (e.g. `GameRevealableKeys`, `ShopRevealableKeys`) identify which composables get highlighted during the tutorial.

## Testing conventions

- Unit tests (`app/src/test`) test use cases and repositories against `LocalGameRepository` with hand-built `GameState`/`Player`/`Dice` fixtures (no mocking framework needed for pure state; MockK is available for interface mocking where needed).
- Test method names are backtick-quoted natural-language sentences (e.g. `` `check if three 1s gives 1000 points` ``) — follow this style for new use case tests.
- Instrumented tests (`app/src/androidTest`) cover Compose navigation and DataStore-backed repositories that need a real Android context.
