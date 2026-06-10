# Feature Spec: Dashboard

## Stitch Reference
- Project Link: [Stitch Project](https://stitch.withgoogle.com/projects/16629036793824138364)

## Domain Layer
- Entities:
  - `BudgetStats` (totalExpenses, budgetLimit, healthStatus).
  - `CategorySummary` (category, totalAmount, percentage).
  - `User` (id, email, name, role).
- UseCases: `GetDashboardStatsUseCase`.
- Business Rules:
  - **Budget Limit:** Users can edit the budget limit.
  - **Income Restriction:** No income/salary input or calculations allowed (totalIncome is hardcoded to 0). UI ignores any income fields.
  - **Reactive Sync:** Upon navigation or changes, a real-time Firestore listener automatically syncs transactions to refresh dashboard stats.
  - **Categories:** Created and managed exclusively by the user.

## Data Layer
- Repository Interface: `FinanceRepository` (getBudgetStats, getCategorySummaries), `TransactionRepository`.
- DataSource: Room Database (Local Cache) & Firestore (Remote backend and source of truth managed via Firebase MCP).
- Sync: Implemented Firestore Realtime Listener for dashboard stats.

## UI/Compose
- Screens: Dashboard (SCREEN_01).
- Components: MetricsGrid, ExpenseBreakdown list (no BalanceCard or Total Income/Balance cards).
- Insets: Added safe drawing margins to prevent status/nav bar overlaps.
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Technical Audit Checklist
- [x] Sync: Implemented Firestore Realtime Listener for dashboard stats.
- [x] Logic: UI ignores any income-related fields; restricted to Expense/Limit.

## Tests
- MockK: Mock GetDashboardStatsUseCase and AuthRepository for testing.
- Unit Tests: Verify healthStatus thresholds based on totalExpenses vs budgetLimit, and verify role-based validation.
- Compose UI Test: Validate metrics, health thresholds, and elements.
