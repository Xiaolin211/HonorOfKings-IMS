# AI-Assisted Honor of Kings Information Management System

---

## 1. Project Overview

This is a Java console-based Information Management System for the game "Honor of Kings" (王者荣耀). It manages players, heroes, equipment, teams, and match records, featuring an AI-powered recommendation engine that suggests optimal heroes and equipment based on multi-factor weighted scoring.

**Key Features:**
- Player/Team/Hero lookup with detailed statistics
- Equipment ranking with weighted composite scoring
- Match history tracking with win/loss records
- Player leaderboards (win rate, level, matches, custom score)
- Role-based authentication (Admin vs Player)
- Admin CRUD for all entity types
- CSV file persistence (6 files)
- AI-Assisted Recommendation Engine with explainable reasoning

---

## 2. How to Run

### Prerequisites
- JDK 8 or higher
- No external libraries required (standard library only)

### Compile
```bash
javac -encoding UTF-8 -d out -sourcepath src src/hok/**/*.java
```

Or on Windows:
```bash
javac -encoding UTF-8 -d out src/hok/enums/*.java src/hok/interfacepkg/*.java src/hok/model/*.java src/hok/util/*.java src/hok/service/*.java src/hok/storage/*.java src/hok/Main.java
```

### Run
```bash
java -cp out hok.Main
```

---

## 3. Default Login Accounts

| Role | ID | Password |
|------|----|----------|
| Admin | `admin` | `admin123` |
| Player | `p001` | `p001123` |

All player passwords follow the pattern: `{playerId}123` (e.g., `p002123` for player p002).

---

## 4. Implemented Features

| # | Feature | Description | Status |
|---|---------|-------------|--------|
| 1 | Player Lookup | Search by ID or name, shows team/level/win rate/heroes/equipment | ✅ |
| 2 | Team Overview | Search by ID or name, shows roster/avg level/win rate/top player/recent matches | ✅ |
| 3 | Hero Details | Search by name, shows type/stats/compatible equipment/owners | ✅ |
| 4 | Equipment Statistics | Rank all 20 items by weighted formula (usage×0.4 + rating×0.4 + heroCount×0.2) | ✅ |
| 5 | Match History | View last N matches for any team or player | ✅ |
| 6 | Leaderboard | Top N by win rate / level / matches / custom score with tie-breaking | ✅ |
| 7 | AI Recommendation | Hero & equipment recommendations with multi-factor weighted scoring and explainable reasoning | ✅ |
| 8 | Admin Management | Full CRUD for players, heroes, equipment, teams, match records with cascade safety | ✅ |
| 9 | Authentication | Admin/Player login with role-based access control | ✅ |
| 10 | Data Persistence | Save/Load all data via 6 CSV files | ✅ |

---

## 5. Java Concepts Used

| Concept | Application |
|---------|-------------|
| **Inheritance** | `Player` and `Admin` extend abstract `Person` |
| **Abstract Class** | `Person` declared abstract — cannot be instantiated |
| **Interface** | `Reportable` with `getSummary()` + `getDetailedInfo()`, implemented by Player, Team, Hero |
| **Polymorphism** | `Person currentUser` in AuthenticationService holds Player or Admin at runtime |
| **Encapsulation** | All fields private; defensive copies on 12 collection getters |
| **Aggregation** | Team contains Players (players survive team deletion) |
| **Composition** | Player owns Heroes (heroes cascade-deleted) |
| **Association** | Hero references Equipment; MatchRecord references Teams |
| **Collections** | `ArrayList` (ordered storage) + `HashMap` (O(1) ID lookup) |
| **Exception Handling** | try-catch on all File I/O; `NoSuchElementException` guard in InputHelper |
| **File I/O** | 6 CSV files with header rows, semicolon sub-separators |
| **Enums** | `Role`, `HeroType`, `EquipmentType`, `MatchResult`, `RecommendationType` |
| **Generics** | `RecommendationResult` type-safe DTO |
| **Functional Interfaces** | Lambda expressions in custom `Comparator<Player>` for leaderboard sorting |
| **Streams** | `stream().filter().map().sorted().limit()` pipeline in RecommendationEngine |

---

## 6. AI Usage Summary

This project was developed with AI assistance throughout all phases:

| Agent Role | Contributions | Commit Prefix |
|------------|--------------|---------------|
| **Architect Agent** | Class design, UML, package structure, recommendation engine architecture | [AI-Architect] |
| **Implementation Agent** | Model classes, services, CRUD logic, recommendation algorithms, file I/O | [AI-Implementation] |
| **Testing/Review Agent** | Bug detection, code quality audit, test case verification, edge case analysis | [AI-Review] |
| **Human** | Planning, design approvals, test execution, reflection, documentation | [Human] |

**AI tools used**: Claude Code (CherryClaw)

**Evidence**: See `ai/prompts.md`, `ai/agent-log.md`, and `ai/reflection.md` for full AI interaction records.

---

## 7. Testing Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 24 |
| Passed | 24 (100%) |
| Failed | 0 (0%) |

**Test Coverage**: Player Lookup (3), Team Overview (3), Hero Details (2), Equipment Statistics (1), Match History (3), Leaderboard (2), Recommendation Engine (4), Authentication (2), Admin Management (2), Player Account (1), File I/O (1).

**JUnit 5 Unit Tests**: 36 automated tests across 5 classes covering OOP concepts, cascade delete, tie-breaking, and more. See `test/README.md`.

Full test documentation: `docs/test-cases.md`

---

## 8. Known Limitations

1. **Match History hero picks**: The current `MatchRecord` model records teams and results but does not track per-player hero selections within each match.
2. **Player self-edit**: Player users can view their own data but do not have a dedicated "Edit Profile" menu option in the current console interface.
3. **CSV load relationship restoration**: After saving and reloading from CSV, Player↔Hero and Hero↔Equipment relationships are not fully re-wired (entities load as independent objects).
4. **Leaderboard display**: Minor formatting issue with `%` sign placement in the win rate column.
5. **No unit tests**: Testing is done manually via documented test cases in `docs/test-cases.md` rather than automated JUnit tests.

---

*Project completed: June 2026 | Java Coursework | AI-Assisted Development*
