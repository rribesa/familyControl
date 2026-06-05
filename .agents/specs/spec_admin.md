# Feature Spec: Administration

## Domain Layer
- Entities: `FamilyMember`, `Role` (Admin/Viewer).
- UseCases: `InviteMemberUseCase`, `UpdatePermissionsUseCase`, `GetFamilyListUseCase`.

## Data Layer
- Repository Interface: `FamilyRepository`.
- DataSource: Firestore.

## UI/Compose
- Screens: Profile Admin (SCREEN_38), Permissions (SCREEN_35).
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Mock member role assignment and permission toggles.
- Compose UI Test: Validate permission change UI state.
