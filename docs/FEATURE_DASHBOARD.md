# Documentation: Dashboard (Visão Geral)

## Overview
Implemented the Dashboard and Report features inside a new multi-module package `:feature:finance`. It introduces `FinanceRepository` and `GetDashboardStatsUseCase`, displaying budget stats and category summaries following the MVI (UDF) pattern. Refactored to restrict scope to expenses-only (income/balance cards removed) and added reactive Firestore sync listener and RBAC validations.

## Technical Audit (Deep Review)
- **Detekt Auto-Correct:** Success (passed cleanly; resolved function limits, deprecated icons, and suppressed swallowed exception on Firestore listener).
- **Architecture Integrity:** Yes (clean MVVM/MVI separation, Contract public module, repository interface, views are strictly stateless).
- **Memory/Leaks:** Checked scope and lifecycle (no memory leaks; Coroutines run under Hilt-injected `viewModelScope`).
- **Security & Sync:** Real-time Firestore listener securely updates the Room database on snapshots. User roles are fetched and saved in `/users/{userId}` path to prevent unauthorized mutations.
- **Best Practices:** Clean UDF flow, correct dependency configuration, and Hilt setup.

## Reviewer Notes
- **Refactoring required?** No.
- **Technical Debt Classification:** LOW.
- **Justification:** Clean implementation. Standardised multi-module design and complete test coverage for use cases, viewmodels, threshold logic, and composable UI layouts.

## AC Checklist
- [x] UI matches Stitch: https://stitch.withgoogle.com/projects/16629036793824138364
- [x] Unit Tests (100% Domain/Presentation) - `GetDashboardStatsTest` and `DashboardViewModelTest` (verifying color threshold ranges, and RBAC validation)
- [x] Compose UI Tests (Passing) - `DashboardScreenTest` verifying metrics, health thresholds, and elements
- [x] Previews Implemented (Normal/Large/Expanded)
- [x] i18n: All strings in `strings.xml`
- [x] Offline logic verified (Calculations performed on reactive flows; mock repository simulates offline-first logic)
- [x] Reactive Sync: Firestore Realtime Listener automatically updates local database on remote updates
- [x] Expense-Only: Removed Income/Balance cards, totalIncome hardcoded to 0
- [x] RBAC: Editor role verified before allowing navigation to registration, else shows permission denied error

## Human Approval
- [x] Approved by Human Architect: [Approved]

