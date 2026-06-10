# Feature Spec: Transactions

## Stitch Reference
- Project Link: [Stitch Project](https://stitch.withgoogle.com/projects/16629036793824138364)

## Domain Layer
- Entities:
  - `Transaction` (id: UUID, amount, category, date, description, userId).
  - `Category` (id: UUID, name, userId).
- UseCases: `AddTransactionUseCase`, `GetCategoriesUseCase`, `ManageCategoriesUseCase`, `SyncTransactionsUseCase`.
- Logic:
  - Transactions must validate existence of `category` from the user's defined categories.
  - Limit mutations (save transaction, create custom category) to users with `Editor` role.

## Data Layer
- Repository Interface: `TransactionRepository` (Offline-first).
- Sync Logic: Unique UUID generation per transaction. Append-only sync strategy. Automatic sync on connectivity restore. Real-time Category and Transaction sync with Firestore.
- DataSource: Room (Local Database with `TransactionEntity` and `CategoryEntity`), Firestore (Remote backend and source of truth managed via Firebase MCP).

## UI/Compose
- Screens: Register Transaction (SCREEN_53), History (SCREEN_54).
- Components: Category chips dynamically rendered, input field for adding custom categories (with role check), scrollable list for History without filter chips (all are expenses).
- Insets: Added safe drawing padding to avoid status/nav bar overlaps.
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Technical Audit Checklist
- [x] Sync: Automatic sync on connectivity restore.
- [x] Authorization: Verify `Editor` permission before processing Add/Edit mutations.

## Tests
- MockK: Mock TransactionRepository sync flow and categories use cases.
- Unit Tests: Verify saving transactions and categories with correct validation (including RBAC checks).
- Compose UI Test: Validate custom category addition, register screen layout, and history list.
