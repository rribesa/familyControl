# Agent Roles & Responsibilities Framework

## Roles
- **Software Engineer (Orchestrator):** Defines interfaces. Triggers QA.
- **QA Agent:** Develops tests based on interfaces. Triggers Developer.
- **Developer Agent:** Implements logic (ViewModel/UseCases) and UI (State-based). Must ensure 3 Previews and i18n. Triggers Reviewer.
- **Reviewer Agent (Auditor):** - Performs Deep Code Review: Checks for memory leaks, architectural integrity (logic in View?), security, and code smells.
    - Runs `./gradlew detekt -PautoCorrect=true`.
    - If CRITICAL/HIGH issues: Rejects and returns to Developer.
    - If clear: Fills `/docs/FEATURE_NAME.md` and triggers Human Approval.

## Operational Rules
- **Workflow:** Automated pipeline (Engineer -> QA -> Developer -> Reviewer -> Human).
- **Concurrency:** Max 3 features per batch.
- **Language:** Prompts/Code in English; UI text in Portuguese (pt-BR).
