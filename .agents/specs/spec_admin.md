# Feature Spec: Administration

## Stitch Reference
- Project Link: [Stitch Project](https://stitch.withgoogle.com/projects/16629036793824138364)

## Domain Layer
- Entities: `FamilyMember`, `Role`, `UserProfile` (Preferences, Avatar).
- UseCases: `UpdateProfileUseCase`, `InviteMemberUseCase`, `UpdatePermissionsUseCase`.

## Data Layer
- Repository Interface: `FamilyRepository`.
- DataSource: Firestore (Remote backend managed via Firebase MCP).

## UI/Compose
- Screens: Profile Admin (SCREEN_38), Permissions (SCREEN_35).
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Mock member role assignment and permission toggles.
- Compose UI Test: Validate permission change UI state.
