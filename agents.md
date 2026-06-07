# Agent Context: Android Financial App (FamilyControl)

## Purpose
Maintain and evolve the Family Budget Control application.

## Architecture & Standards
- Pattern: Clean Architecture + MVVM + MVI (UDF).
- Structure: Multi-module (Feature:public / Feature:impl).
- UseCases: Single function, `operator fun invoke()`.
- **UI Logic Rules:** No business logic in Views. Views must strictly observe `ViewState` and emit `ViewEvents`. All logic must reside in ViewModel or UseCases.

## Tech Stack
- Kotlin, Hilt, Navigation 3, Compose, Room, Firestore, Coil.
- Dependencies: TOML (Version Catalog).

## Skills & Tooling (MCP)
- Always prioritize MCP skills for file system, Git, and automated tasks.
- Must execute `./gradlew detekt -PautoCorrect=true` before finalizing any feature.

## Development Rules
- Security: No hardcoded keys/secrets. Use Proguard/R8.
- Testing: 100% Domain/Presentation coverage + Compose UI tests must pass.
- Previews: 3 mandatory previews per Composable (Normal, Large, Expanded).
- i18n: All text MUST be in `strings.xml`. Hardcoding is strictly forbidden.
- Imports: No wildcards. No deprecated APIs.

## Acceptance Criteria
- Pixel-perfect to Stitch (https://stitch.withgoogle.com/projects/16629036793824138364).
- Offline-first logic (Unique UUID per transaction).

## Data Persistence & Backend Rules
- **Firebase/Firestore:** All schema definitions (collections, document structure) must be managed via Firebase MCP.
- **Rules:** Firestore security rules must be defined to ensure only authenticated family members can read/write data.
- **Initialization:** The application must verify the existence of required collections on startup. If missing, use Firebase MCP to initialize the base schema.
