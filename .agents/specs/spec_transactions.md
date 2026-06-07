# Feature Spec: Transactions

## Stitch Reference
- Project Link: [Stitch Project](https://stitch.withgoogle.com/projects/16629036793824138364)

## Domain Layer
- Entities: `Transaction` (id: UUID, amount, category, date, desc, userId).
- UseCases: `AddTransactionUseCase`, `GetTransactionHistoryUseCase`, `SyncTransactionsUseCase`.
- Logic: Transactions must validate existence of `category` from the user's defined categories.

## Data Layer
- Repository Interface: `TransactionRepository` (Offline-first).
- Sync Logic: Unique UUID generation per transaction. Append-only sync strategy.
- DataSource: Room (Local Database), Firestore (Remote backend and source of truth managed via Firebase MCP).

## UI/Compose
- Screens: Register Transaction (SCREEN_53), History (SCREEN_54).
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Mock TransactionRepository sync flow.
- Compose UI Test: Validate offline submission queue and history filter chips.
