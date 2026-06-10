# Feature Spec: Reporting

## Stitch Reference
- Project Link: [Stitch Project](https://stitch.withgoogle.com/projects/16629036793824138364)

## Domain Layer
- **UseCases:** `GetMonthlyReportUseCase`, `AggregateExpensesByCategoryUseCase`.
- **Logic:** Aggregation performed in UseCase/Repository to keep ViewModel lean.

## Data Layer
- **Repository Interface:** `ReportRepository`.
- **DataSource:** Firestore (Remote backend managed via Firebase MCP).

## UI/Compose
- **Screens:** `ReportScreen` (SCREEN_56), `ExportOptionsScreen` (SCREEN_31).
- **Previews:** Normal, Large, Expanded.
- **i18n:** All text in `strings.xml`.

## Technical Audit Checklist
- [ ] Detekt Auto-Correct executed (`./gradlew detekt -PautoCorrect=true`).
- [ ] Performance: Checked memory usage for large data aggregation.
- [ ] Logic: State-driven UI for chart rendering.
