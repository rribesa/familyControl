# Feature Spec: Dashboard

## Stitch Reference
- Project Link: [Stitch Project](https://stitch.withgoogle.com/projects/16629036793824138364)

## Domain Layer
- Entities: 
  - `DashboardSummary` (totalIncome, totalExpenses, balance, currency).
  - `Transaction` (id, title, amount, type [INCOME, EXPENSE], category, timestamp).
  `Budget`, `CategorySummary`, `UserLimit`.
- UseCases: `GetDashboardSummaryUseCase`, `GetRecentTransactionsUseCase`,`GetDashboardStatsUseCase`, `ManageCategoriesUseCase` (Add/Edit/Delete).
- Business Rules:
- Category Management: Allow users to create custom categories that reflect in transactions and reporting.
  - Balance = TotalIncome - TotalExpenses.
  - Recent transactions list should show the top 5 latest entries.

## Data Layer
- Repository Interface: `DashboardRepository` (getSummary, getTransactions).
- DataSource: Room Database (Local Cache) & Firestore (Remote backend and source of truth managed via Firebase MCP).

## UI/Compose
- Screens: Dashboard (SCREEN_01).
- Components: BalanceCard, TransactionListItem, ExpensePieChart.
- Previews: Normal, Large, Expanded.
- i18n: All text defined in `values/strings.xml`.

## Tests
- MockK: Mock DashboardRepository for data flow.
- Unit Tests: Verify correct balance calculation rules.
- Compose UI Test: Validate empty state layout and correct item display in the recent transactions list.
