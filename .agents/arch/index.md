# Agent Workflow Index: FamilyControl (Automated Pipeline)

## Process Flow
1. Engineer: Defines interfaces -> Triggers QA.
2. QA: Develops Tests -> Triggers Developer.
3. Developer: Implements Logic (ViewModel) & UI (UDF) -> Triggers Reviewer.
4. Reviewer: Audits (Security/Leaks/Architecture) + Detekt -> Triggers Human.
5. Human: Final Approval -> Commit (`feat/[name]`, `fix/[name]`, etc.).

## Git Standards
- feat/[name], upd/[name], core/[name], fix/[name].

## Documentation
- All features must have an audit report in `/docs`.
