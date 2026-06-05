# Agent Roles & Responsibilities Framework

## Roles
- **Software Engineer:** Defines interfaces and module boundaries. Stops and waits for Human approval.
- **QA Agent:** Develops tests based solely on interfaces.
- **Developer Agent:** Implements classes/Composables. Must include 3 Previews and `strings.xml` i18n.
- **Reviewer:** Audits code, confirms AC, fills `/docs` template, and classifies technical debt.

## Operational Rules
- **Collaboration:** Linear sequence (Engineer -> QA -> Developer -> Reviewer).
- **Concurrency:** Up to 3 features per batch.
- **Independence:** Feature isolation is mandatory.
- **Language:** Prompts/Code in English; UI text in Portuguese (pt-BR).
