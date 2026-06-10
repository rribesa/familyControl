# Agent Context: Android Financial App (FamilyControl)

## Purpose
Maintain and evolve the Family Budget Control application.

## Core Philosophy
- **Control, not Income:** The app tracks expenses and limits. It does NOT manage income/salary input or act as a bank account/wallet.
- **Offline-First:** All actions (spend, update) are saved locally in Room first (using unique UUIDs) and synced automatically to Firebase/Firestore when a connection is available.
- **Reactive Dashboard:** The dashboard must reflect real-time updates from any family member immediately (using a real-time Firestore listener).

## Architecture & Standards
- **Pattern:** Clean Architecture + MVVM + MVI (UDF).
- **Structure:** Multi-module (Feature:public / Feature:impl).
- **UseCases:** Single function, `operator fun invoke()`.
- **UI Logic Rules:** No business logic in Views. Views must strictly observe `ViewState` and emit `ViewEvents`. All logic must reside in ViewModel or UseCases.

## Tech Stack
- Kotlin, Hilt, Navigation 3, Compose, Room, Firestore, Coil.
- Dependencies: TOML (Version Catalog).

## Permissions & Collaboration
- **Shared Budget:** Inviting a user grants access to a specific budget context.
- **Role-Based Access (RBAC):** Users can have 'Viewer' (read-only) or 'Editor' (full control over expenses, limits, and categories) permissions. ViewModels validate the user's role before allowing mutations.

## Skills & Tooling (MCP)
- Always prioritize MCP skills for file system, Git, and automated tasks.
- Must execute `./gradlew detekt -PautoCorrect=true` before finalizing any feature.

## Development Rules
- **Security:** No hardcoded keys/secrets. Use Proguard/R8. Role validation checks exist on ViewModel levels to prevent unauthorized operations.
- **Testing:** 100% Domain/Presentation coverage + Compose UI tests must pass.
- **Previews:** 3 mandatory previews per Composable (Normal, Large, Expanded).
- **i18n:** All text MUST be in `strings.xml`. Hardcoding is strictly forbidden.
- **Imports:** No wildcards. No deprecated APIs.

## Acceptance Criteria
- Pixel-perfect to Stitch (https://stitch.withgoogle.com/projects/16629036793824138364).
- Offline-first logic (Unique UUID per transaction).

## Data Persistence & Sync Rules
- **Firebase/Firestore:** centralize collections/schema via Centralized FirestorePaths.
- **Security Rules:** Firestore rules restrict read/write access to authenticated family members using Firebase MCP.
- **Real-time Sync:** Firestore Realtime Listener automatically updates the Room database when other family members modify transactions or categories.
