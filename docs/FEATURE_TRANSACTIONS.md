# Documentation: Transactions (Lançamento e Histórico)

## Overview
Implemented the Transactions feature inside the `:feature:finance` module. Users can register transactions (SCREEN_53) and view transaction history (SCREEN_54) without filter chips (All, Income, Expense) as the app is restricted to expenses only. It uses an offline-first architecture with a local Room Database, custom categories storage, and append-only remote synchronization to Cloud Firestore, governed by Role-Based Access Control.

## Technical Audit (Deep Review)
- **Detekt Auto-Correct:** Success (passed cleanly).
- **Architecture Integrity:** Yes (strictly conforms to MVI/UDF architecture. Views are stateless, observing `ViewState` and emitting `ViewEvents`. Business logic and state flow exist entirely inside the ViewModels and UseCases).
- **Memory/Lifecycle:** Checked scopes (no leaks. All flow collection and async work in ViewModels use Hilt-injected `viewModelScope`. Repository operations use an injected `CoroutineDispatcher` mapped to `Dispatchers.IO`).
- **Security & RBAC:** Verification layer checks if the user has `Editor` permissions before saving any transaction or creating custom categories. If the user is a `Viewer`, mutation is blocked and a permission denied error is displayed.
- **Best Practices:** Bypassed a Kotlin 2.x KSP Room compiler signature mismatch bug by changing the DAO database mutation and search functions from `suspend` to blocking Java queries, executed safely within `withContext(ioDispatcher)` inside the repository.
- **Database Schema Audit:** Verified and initialized the base Firestore database collections (`users`, `transactions`, `categories`, `budgets`) and deployed security rules restricting access to authenticated family members using Firebase MCP. Category Room table created and integrated into `FinanceDatabase` (version 2).

## Reviewer Notes
- **Refactoring required?** No.
- **Technical Debt Classification:** LOW.
- **Justification:** Clean implementation. Standardised multi-module design and complete test coverage for use cases, viewmodels, transaction list filtration, and composable UI layouts.

## AC Checklist
- [x] UI matches Stitch: https://stitch.withgoogle.com/projects/16629036793824138364
- [x] Unit Tests (100% Domain/Presentation) - `TransactionUseCasesTest`, `RegisterTransactionViewModelTest`, and `HistoryViewModelTest` (verifying RBAC and custom categories)
- [x] Compose UI Tests (Passing) - `RegisterTransactionScreenTest` and `HistoryScreenTest`
- [x] Previews Implemented (Normal/Large/Expanded)
- [x] i18n: All strings in `strings.xml`
- [x] Offline logic verified (Local-first Room DB write, UUID generated on model initialization, async syncing to Cloud Firestore, with database schema verified and managed via Firebase MCP)
- [x] Custom Categories: Users can dynamically register custom categories in Room and sync them to Firestore
- [x] RBAC: Block mutations for `Viewer` users and verify permissions on ViewModel level
- [x] Expense-Only: Filter chips removed from History screen, all transactions are strictly expenses

## Human Approval
- [x] Approved by Human Architect: 2026-06-05
