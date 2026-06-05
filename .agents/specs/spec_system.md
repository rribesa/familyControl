# Feature Spec: System & Connectivity

## Domain Layer
- UseCases: `CheckConnectionUseCase`.

## Data Layer
- Repository Interface: `SystemRepository`.

## UI/Compose
- Screens: Empty State (SCREEN_14), Offline (SCREEN_23), Export Options (SCREEN_31).
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Simulate connection status changes.
- Compose UI Test: Validate display of offline/empty state illustrations.
