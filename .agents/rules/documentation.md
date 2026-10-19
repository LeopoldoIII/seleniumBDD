# Language and Documentation Standards

## Language Policy

1. **Mandatory English for Repository Artifacts**:
   - **All documentation files** (`README*.md`, guides, architecture notes, cheat sheets) MUST be written in standard English.
   - **All Gherkin Feature files** (`.feature`), scenario titles, Given/When/Then steps, and Examples tables MUST be written in English.
   - **All Java code**: Class names, method names, variables, log messages, and JavaDoc comments MUST be written in English.
   - **Git commit messages and PR descriptions** MUST be written in English.

2. **Conversational Flexibility (Chat vs. Code/Docs)**:
   - When the user communicates in Spanish or another language, autonomous agents may reply, converse, and explain concepts in the user's language within the chat.
   - However, **any file, documentation, feature file, or code generated and written to disk MUST strictly be in English**, regardless of the language used in the prompt or request.

3. **Documentation Style**:
   - Use clear, declarative, professional technical writing.
   - Use standard GitHub Flavored Markdown (tables, code fences, collapsible `<details><summary>` accordions where appropriate).
   - Never mix languages within code comments or documentation files.
