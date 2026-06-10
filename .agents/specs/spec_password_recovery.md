# Feature Spec: Password Recovery

## Stitch Reference
- Project Link: [Stitch Project](https://stitch.withgoogle.com/projects/16629036793824138364)

## Domain Layer
- UseCases: `ForgotPasswordUseCase`.
- Validation: Email address format.

## Data Layer
- Repository Interface: `AuthRepository` (sends password reset email).
- DataSource: Firebase Authentication.

## UI/Compose
- Screens: Password Recovery (SCREEN_FORGOT_PASSWORD).
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Mock AuthRepository forgot password email flow.
- Unit Tests: Verify recovery email trigger and validation states.
