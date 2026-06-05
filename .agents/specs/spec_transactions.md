# Feature Spec: Transactions

## Domain Layer
- Entities: `Transaction` (id: UUID, amount, category, date, desc, userId).
- UseCases: `AddTransactionUseCase`, `GetTransactionHistoryUseCase`, `SyncTransactionsUseCase`.

## Data Layer
- Repository Interface: `TransactionRepository` (Offline-first).
- Sync Logic: Unique UUID generation per transaction. Append-only sync strategy.
- DataSource: Room (Local), Firestore (Remote).

## UI/Compose
- Screens: Register Transaction (SCREEN_53), History (SCREEN_54).
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Mock TransactionRepository sync flow.
- Compose UI Test: Validate offline submission queue and history filter chips.
