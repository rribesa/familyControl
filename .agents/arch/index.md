# Agent Workflow Index: FamilyControl

## Process Flow
1. Software Engineer: Defines interfaces + Architecture.
2. Architect (Human): Approves interfaces.
3. QA Agent: Unit/UI Tests (MockK) + Compose UI test validation.
4. Developer Agent: Implements concrete classes + 3 Previews.
5. Software Engineer (Reviewer): Review, AC compliance, docs, debt classification.
6. Architect (Human): Final approval.
7. Execution: Commit to branch `feat/[name]`, `upd/[name]`, `core/[name]`, `fix/[name]`.

## Documentation Requirements
- All feature docs reside in `/docs`.
- Debt Classification: CRITICAL, HIGH, MEDIUM, LOW.
- MEDIUM/LOW debts require human approval.
