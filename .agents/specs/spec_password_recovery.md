Title
docs: define agent roles, workflows, and automated pipeline rules

Description
This PR updates the agent context documents and introduces a structured roles and responsibilities framework to implement an automated agent collaboration pipeline. It defines clear handoffs between the Software Engineer (Orchestrator), QA, Developer, and Reviewer (Auditor) agents.

Key Changes
agents.md: Updated general guidelines to strictly separate UI logic (views observe ViewState/emit ViewEvents) and enforce running Detekt auto-correct before finalizing features.
agents_roles.md (NEW): Created the Agent Roles & Responsibilities Framework detailing the operational rules, roles definitions, and pipeline steps.
.agents/arch/index.md: Restructured the workflow process flow into an automated pipeline (Engineer $\rightarrow$ QA $\rightarrow$ Developer $\rightarrow$ Reviewer $\rightarrow$ Human Approval).
.agents/docs/FEATURE_TEMPLATE.md: Expanded the template with a detailed "Technical Audit (Deep Review)" checklist covering Memory/Leaks, Security, Detekt, and Architecture Integrity.
How to Test / Verify
Verify that the files exist and contain the correct content on the current branch upd/agent-workflows-config.
Inspect the updated markdown structure for readability and formatting.
