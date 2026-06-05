# Feature Spec: Authentication

## Domain Layer
- Entities: `User` (id, email, name, birthDate).
- UseCases: `LoginWithEmailUseCase`, `LoginWithGoogleUseCase`, `RegisterUserUseCase`, `ForgotPasswordUseCase`.
- Validation Rules: Password (8+ chars, 1 upper, 1 lower, 1 special, 1 number).

## Data Layer
- Repository Interface: `AuthRepository` (login, register, logout, getCurrentUser).
- DataSource: Firebase Authentication.

## UI/Compose
- Screens: Login (SCREEN_16), Register (SCREEN_42).
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Mock AuthRepository and Firebase Auth state.
- Compose UI Test: Validate password validation feedback and input error states.
