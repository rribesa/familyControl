# Agent Context: Android Financial App

## Purpose
Maintain and evolve the Family Budget Control application.

## Architecture & Standards
- Pattern: Clean Architecture + MVVM + MVI (UDF).
- Structure: Multi-module (Feature:public / Feature:impl).
- Principles: SOLID, Single Source of Truth (Repository Pattern).
- UseCases: Single function, `operator fun invoke()`. Interface name `*UseCase`, Impl name `*`.

## Tech Stack
- Language: Kotlin, Hilt (DI), Navigation 3, Compose (UI), Room (Local), Firestore (Remote), Coil (Images).
- Dependencies: TOML (Version Catalog).

## Skills & Tooling (MCP)
- Always prioritize the use of available MCP skills for file system operations, code analysis, Git operations, and environment configuration.
- When performing refactoring or implementation, use the skill to verify existing code before applying changes.
- Ensure all automated tasks (linting, testing) are executed via skills whenever possible.

## Development Rules
- Security: No hardcoded keys/secrets. Use Proguard/R8.
- Quality: Detekt for static analysis (Use skill for execution).
- Testing: 100% Domain/Presentation coverage + Compose UI tests must pass.
- Previews: 3 mandatory previews per Composable (Normal, Large, Expanded).
- Internationalization (i18n): All text must be in `strings.xml`. Hardcoding is forbidden.
- Imports: No wildcards. No deprecated APIs.

## Acceptance Criteria
- Pixel-perfect to Stitch mockups.
- Offline-first logic for "Lançar Gastos" (Unique UUID per transaction).
