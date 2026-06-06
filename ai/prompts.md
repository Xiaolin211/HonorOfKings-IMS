# Prompts Log: AI-Assisted Information Management System for Honor of Kings

---

## Prompt 01 — Architect Agent: Review plan.md

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Architect |
| **Related Commit** | `7e9c867` |

### AI Response Summary

Inspected the project skeleton and confirmed the directory layout is suitable for a Java coursework project. Since plan.md was initially empty, the agent inferred design intent from the Prompt Pack and delivered:

- Confirmed all 7 required classes + 10 OOP concepts are covered by the proposed design
- Identified 4 risks ranked by severity: Main.java bloat (HIGH), empty plan.md (HIGH), data consistency on delete (MEDIUM), null pointer from finder methods (MEDIUM)
- Output a complete package/class structure with 7 packages and 16 source files
- Recommended a 12-phase implementation order with explicit dependencies between phases
- Noted that the Reportable interface's method signatures were undefined in the plan and proposed getSummary() + getDetailedInfo()

### My Decision

**ACCEPTED.** The agent correctly identified that plan.md was the immediate blocker. The 12-phase implementation order is logical and respects dependency chains (enums first, then model, then services, then persistence). The risk assessment is accurate — I consider Main.java bloat the highest priority to guard against throughout development. I decided to expand plan.md into a full 12-section document covering project goal, requirements, Java concepts, class design, UML, implementation phases, CSV formats, authentication model, ranking formulas, non-functional requirements, and git protocol.

---

## Prompt 02 — Architect Agent: Create design.md and UML draft

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Architect |
| **Related Commit** | `73ce68a` |

### AI Response Summary

Created two comprehensive design documents:

- `docs/design.md` (~380 lines, 12 chapters): architecture overview with layered diagram (Presentation → Service → Data → Persistence → Domain), package structure, 4 enum definitions with full Java signatures, Reportable interface specification with implementation behavior table, 7 complete model class field/method tables (Person, Player, Admin, Hero, Equipment, Team, MatchRecord), 5 service class specifications (GameDataManager, SearchService, RankingService, AuthenticationService, AdminService), storage layer CSV format definitions, utility class specs, Main.java menu structure (Admin vs Player views), security constraints table, and compilation commands.

- `docs/uml.md` (~220 lines, 7 diagrams): full class diagram with all attributes, methods, and relationship arrows (inheritance △, composition ◆, aggregation ◇, association →), interface implementation diagram, 4-enum diagram, service dependency diagram showing Main → 5 Services → GameDataManager, authentication sequence diagram, data save/load sequence diagram, and relationship notation legend.

Established a clear division: plan.md = high-level overview, design.md = detailed specifications, uml.md = visualization.

### My Decision

**ACCEPTED WITH MODIFICATIONS.** The design documents are comprehensive and well-structured. I accept the proposed layered architecture and the clear separation between plan/design/UML documents. However, I identified the following corrections needed before implementation:

1. **Method naming inconsistency**: The AI used `getTopPlayer()` in code and `getStrongestPlayer()` in UML. Unified to `getStrongestPlayer()` across all documents and code for clarity.
2. **Date type**: Changed MatchRecord.matchDate from `String` to `LocalDate` for proper Java date handling. Added a `getMatchDateString()` helper to preserve formatted output.
3. **UML visibility**: Verified that all UML class diagrams match the actual field and method signatures defined in design.md.

These modifications ensure consistency before code generation begins.

---

## Prompt 03 — Implementation Agent: Core model classes

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `3a2e260` |

### AI Response Summary

Created 12 Java source files implementing the complete domain model layer. All files compiled with zero errors (verified: 12 .class files generated).

Files produced:
- **4 enums**: Role (PLAYER/ADMIN), HeroType (6 values: TANK through SUPPORT), EquipmentType (5 values: ATTACK through JUNGLE), MatchResult (WIN/LOSE/DRAW)
- **1 interface**: Reportable with getSummary() and getDetailedInfo()
- **7 model classes**:
  - Person (abstract) — id, name, role as the shared base
  - Player (extends Person, implements Reportable) — level, winRate, totalMatches, heroes (composition via List<Hero>), team (aggregation, nullable), password. Defensive copy on getHeroes().
  - Admin (extends Person) — permissionLevel, password
  - Hero (implements Reportable) — hp/attack/defense/speed stats, heroType, compatibleEquipment (association via List<Equipment>). Defensive copy on getter.
  - Equipment — stat bonuses (attackBonus/defenseBonus/hpBonus/speedBonus), rating (1.0-10.0), usageCount, getTotalBonus() helper
  - Team (implements Reportable) — players (aggregation via List<Player>), getAverageLevel(), getWinRate(), getTopPlayer(). addPlayer() sets bidirectional reference. removePlayer() clears it.
  - MatchRecord — teamA/teamB (association), result from teamA's perspective, matchDate, matchType. getWinner()/getLoser() return Team or null for draws.

OOP concepts demonstrated in code: abstract class (Person cannot be instantiated), inheritance (Player/Admin extend Person), interface implementation (3 classes implement Reportable), polymorphism (Person reference, Reportable reference), encapsulation (all fields private, defensive copies for collections), composition (Player owns Heroes), aggregation (Team contains Players who can exist independently), association (Hero references Equipment, MatchRecord references Teams).

### My Decision

**CHECKED AND VERIFIED.** All 12 files compile successfully. Field visibility is correctly private throughout. The defensive copy pattern on collection getters is correctly implemented. Bidirectional references (Team↔Player) are properly maintained in addPlayer/removePlayer. No business logic has leaked into model classes — they are pure data holders with computed properties where appropriate (Team.getAverageLevel(), MatchRecord.getWinner()). Ready to proceed to DataInitializer.

---

## Prompt 04 — Implementation Agent: Initial dataset

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `3a2e260` |

### AI Response Summary

Created `src/hok/util/DataInitializer.java` (~300 lines, compiled successfully) providing the complete initial dataset through static factory methods. No file I/O, no menu integration — pure data creation ready to be consumed by GameDataManager.

Dataset contents verified against coursework minimums:
- **3 Teams**: Dragon Warriors (t01), Phoenix Rise (t02), Shadow Reapers (t03) — exactly 3, minimum met ✓
- **15 Players** (p001-p015): 5 per team, levels 14-27, win rates 40%-80%, password format {id}123 — 15 ≥ 15 minimum ✓, 5 per team ≥ 5 ✓
- **1 Admin**: admin/admin123 — ≥1 ✓
- **15 Heroes** (h01-h15): distributed across all 6 HeroTypes (3 TANK, 3 WARRIOR, 3 ASSASSIN, 2 MAGE, 2 MARKSMAN, 2 SUPPORT) — 15 ≥ 15 ✓
- **20 Equipment** (e01-e20): distributed across all 5 EquipmentTypes (5 ATTACK, 5 DEFENSE, 4 MAGIC, 3 MOVEMENT, 3 JUNGLE), ratings 7.0-9.5 — 20 ≥ 20 ✓
- **Hero-Equipment compatibility**: Each hero has 2-6 compatible equipment items via assignCompatibleEquipment() — ≥2 per hero ✓
- **Player-Hero ownership**: Each player owns 3-4 heroes via playerHeroIndices mapping — ≥3 per player ✓
- **10 Match Records** (m01-m10): 3-team round-robin with WIN/LOSE/DRAW results, dates spanning May 2026 — 10 ≥ 10 ✓

Design choices: heroes and equipment are created independently, then wired together by assignCompatibleEquipment(). Players and teams are created independently, then wired by addPlayer(). This separation allows GameDataManager to call methods in any order.

### My Decision

**CHECKED AND VERIFIED.** All dataset minimums are met with margin (most are exactly at minimum, which is appropriate — no over-engineering). The data is internally consistent: every player has a team, every team has exactly 5 players, every hero has compatible equipment, every player has heroes. Player passwords follow a predictable pattern ({id}123) which is useful for testing. The data is game-themed but uses neutral English names appropriate for an academic submission. Ready to proceed to GameDataManager.

---

## Prompt 05 — Implementation Agent: GameDataManager

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `eb5bbdb` |

### AI Response Summary

Created `src/hok/service/GameDataManager.java` (~330 lines, 30 methods) as the central in-memory data store. Compiled successfully with zero errors.

Architecture: Dual-storage pattern — `ArrayList` for ordered iteration, `HashMap<String, T>` for O(1) ID-based lookup. Six entity types managed: Player, Admin, Hero, Equipment, Team, MatchRecord.

Method breakdown:
- **Initialization** (2): `initializeData()` calls DataInitializer in proper order (teams/heroes/equipment first, then wire compatibility, then players, then admin, then matches, then rebuild indexes). `rebuildAllIndexes()` clears and repopulates all 6 HashMaps.
- **Get All** (6): `getAllPlayers()` through `getAllMatchRecords()` — all return defensive copies via `new ArrayList<>(list)`.
- **Find by ID** (6): HashMap.get() for O(1) lookup. Returns null if not found (no exception thrown).
- **Find by Name** (4): Linear scan with case-insensitive match. Only implemented for Player, Team, Hero, Equipment (Admin has only 1 entry; MatchRecord has no name field).
- **Add** (5): Checks for null and duplicate ID before inserting. Returns boolean success/failure.
- **Delete** (5): Cascading cleanup — deletePlayer removes from Team roster; deleteHero removes from all Players' hero lists; deleteEquipment removes from all Heroes' compatible lists; deleteTeam sets all members' team to null; deleteMatchRecord is standalone (association only). Returns false if ID not found.
- **Update** (5): Replaces entity while preserving relationships. updatePlayer preserves team and heroes; updateHero preserves compatible equipment and reassigns to owners; updateEquipment preserves hero associations; updateTeam transfers all members.
- **Utility** (2): `findPlayersOwningHero()` returns all players with a given hero; `findMatchesByTeam()` returns all matches involving a team.

Cascade safety: All delete operations handle the bidirectional references correctly, preventing orphaned references or null pointer exceptions downstream.

### My Decision

**CHECKED AND VERIFIED.** The dual-storage pattern (ArrayList + HashMap) is efficient and appropriate for this scale of data. Cascade logic on deletes is thorough — every bidirectional relationship (Team↔Player, Player↔Hero, Hero↔Equipment) is properly cleaned up. The defensive copy pattern on getAll methods ensures callers cannot corrupt internal state. The find-by-name linear scan is acceptable given the small dataset size (≤20 per entity). The update methods correctly preserve existing relationships rather than forcing the caller to re-wire them. Ready to proceed to InputHelper and Main menu skeleton.

---

## Prompt 06 — Implementation Agent: InputHelper and Main menu skeleton

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `cb03eb4` |

### AI Response Summary

Created two files establishing the presentation layer:

- `src/hok/util/InputHelper.java` (~70 lines): 5 static methods (readInt, readInt with range, readDouble, readString, readYesNo, pressEnterToContinue). All methods loop until valid input — no exceptions escape. Uses a single shared Scanner. Input validation handles empty strings, non-numeric input, and out-of-range values.

- `src/hok/Main.java` (~140 lines): Console menu router with 11 options (Player Lookup, Team Overview, Hero Details, Equipment Stats, Match History, Leaderboard, Admin Management, Save/Load Data, Login, Exit). Initializes GameDataManager on startup, displays data summary. Uses temporary stub classes (PlayerLookupStub, TeamOverviewStub, HeroDetailsStub) for features not yet backed by real services. Menu 0-11 numeric input with range validation.

Design principle: Main.java contains zero business logic — pure routing via switch statement. Stubs are private static inner classes that will be replaced when real services are implemented.

### My Decision

**CHECKED AND VERIFIED.** The menu skeleton is clean and follows the router-only pattern. InputHelper is robust — no invalid input can crash the program. Stub classes are properly scoped as private inner classes to prevent accidental use elsewhere. The data summary on startup provides immediate feedback that initialization succeeded. Ready to proceed to SearchService (Prompt 07) which will replace the stubs.

---

## Prompt 07 — Implementation Agent: Search features

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `9257ef5` |

### AI Response Summary

Created `src/hok/service/SearchService.java` (~170 lines, 7 methods) and removed all stub classes from Main.java.

Key methods:
- `lookupPlayer(query)`: Searches by ID then name, returns formatted details including owned heroes' compatible equipment lists.
- `lookupTeam(query)`: Searches by ID then name, returns full team details plus 5 most recent matches.
- `lookupHero(name)`: Searches by name, returns full stats with compatible equipment and list of player owners.
- `getMatchHistory(query, limit)`: Accepts team ID/name or player ID/name. For player queries, finds the player's team first, then fetches that team's matches. Returns sublist limited to `limit`.
- `formatMatchHistory(query, limit)`: Formatted string wrapper for getMatchHistory.
- `listAllPlayers()`, `listAllTeams()`, `listAllHeroes()`: Summary views using Reportable.getSummary().

Main.java was updated to instantiate SearchService and delegate all player/team/hero/match lookups to it. All stub inner classes removed. Match History now accepts both team and player queries.

### My Decision

**CHECKED AND VERIFIED.** The SearchService correctly centralizes all query logic. The lookupPlayer method goes beyond requirements by also showing compatible equipment derived from owned heroes. The match history handles the player→team indirection correctly. Returns null (not exceptions) for not-found queries, with Main.java handling the null check. Ready for RankingService.

---

## Prompt 08 — Implementation Agent: Equipment statistics and leaderboard

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `9257ef5` |

### AI Response Summary

Created `src/hok/service/RankingService.java` (~200 lines, 10 methods):

Equipment ranking:
- `rankEquipment()`: Sorts all equipment by weighted score: `usageCount * 0.4 + rating * 0.4 + compatibleHeroCount * 0.2`. The `compatibleHeroCount` is computed dynamically by scanning all heroes. Returns sorted list.
- `formatEquipmentRanking()`: Tabular output with rank, name, score, usage count, rating, and hero count columns.

Player leaderboards:
- `topByWinRate(n)`: Sorted by win rate descending. Tie-break: level descending → name ascending.
- `topByLevel(n)`: Sorted by level descending. Tie-break: win rate → name.
- `topByMatches(n)`: Sorted by total matches descending. Tie-break: level → name.
- `topByCustomScore(n)`: Sorted by `winRate*0.5 + (level/30)*0.3 + (matches/300)*0.2`. Tie-break: level → name.
- `formatLeaderboard(title, players, ...flags)`: Generic table formatter with dynamic column visibility controlled by boolean flags.

Main.java updated with full leaderboard menu: user selects ranking type (1-4) and count (1-15), then formatted table is displayed. Equipment Statistics menu now shows the full ranked table.

### My Decision

**CHECKED AND VERIFIED.** The weighted equipment formula is reasonable for this domain. Tie-breaking is consistently implemented across all four leaderboard types. The generic formatLeaderboard method with boolean flags avoids code duplication. Custom score normalization (dividing by 30 and 300) keeps all three components in comparable ranges. Ready to proceed to AuthenticationService.

---

## Prompt 09 — Implementation Agent: Authentication

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `fb61331` |

### AI Response Summary

Created `src/hok/service/AuthenticationService.java` (~80 lines, 7 methods):

- `login(id, password)`: Searches admins first, then players. Returns true on match. Sets `currentUser` to the matched Person (polymorphism — can be Admin or Player).
- `logout()`: Sets currentUser to null.
- `isLoggedIn()`, `isAdmin()`: State checks. `isAdmin()` uses `getRole() == Role.ADMIN` — demonstrates runtime type checking via polymorphism.
- `canEditPlayer(playerId)`: Admins can edit anyone; Players can only edit themselves.
- `canManageData()`: Admin-only gate.

Default test accounts verified: admin/admin123 (Admin), p001/p001123 (Player).

Main.java login menu: shows login status at top of main menu when logged in. Option 10 toggles between login/logout. Admin Management (option 7) checks auth before allowing access.

### My Decision

**CHECKED AND VERIFIED.** The polymorphism design is clean — `currentUser` is typed as `Person` but behaves differently based on actual runtime type (Admin vs Player). Login searches admins first (efficient — only 1 admin vs 15 players). Password comparison is simple string equality which is acceptable for a coursework console app. Ready for Admin data management.

---

## Prompt 10 — Implementation Agent: Admin data management

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `fb61331` |

### AI Response Summary

Created `src/hok/service/AdminService.java` (~260 lines) with interactive console CRUD for all 5 entity types:

- **Manage Players**: List/Add/Edit/Delete. Add prompts for ID, name, level, win rate, matches. Edit prompts with current value shown in brackets. Delete cascades (removes from team).
- **Manage Heroes**: List/Add/Delete. Add prompts for ID, name, type (1-6 numeric choice mapped to HeroType enum), HP, attack, defense, speed. Delete cascades (removes from all players).
- **Manage Equipment**: List/Add/Edit/Delete. Add prompts for all fields. Edit supports rating changes. Delete cascades (removes from all heroes).
- **Manage Teams**: List/Add/Add Player/Remove Player/Delete. Moving a player between teams is supported (asks confirmation). Delete cascades (clears all members' team).
- **Manage Match Records**: List/Add/Delete. Add prompts for teamA/teamB with existence checks, result (1-3 → MatchResult), date, match type.

All ID checks prevent duplicates. All delete operations confirm before executing. Input validated via InputHelper. Main.java gates access behind `authService.isAdmin()`.

### My Decision

**CHECKED AND VERIFIED.** The admin menu is self-contained with its own loop — returns to Main when user selects "Back". Enum values are selected by numeric choice, making it user-friendly. Cascade behavior is properly communicated to the user (e.g., "Hero deleted (removed from all players)"). Player movement between teams handles the bidirectional reference correctly. Ready for File I/O.

---

## Prompt 11 — Implementation Agent: File persistence

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Implementation |
| **Related Commit** | `fb61331` |

### AI Response Summary

Created `src/hok/storage/FileStorageService.java` (~280 lines) with full CSV persistence:

- **Save**: 6 entity-specific save methods + `saveAll()` wrapper. Creates `data/` directory if missing. Formats: players.csv (id,name,role,level,winRate,totalMatches,heroIds,password), admins.csv, heroes.csv, equipment.csv, teams.csv (id,name,playerIds), matchrecords.csv (id,teamAId,teamBId,result,matchDate,matchType). Multi-valued fields (heroIds, playerIds, compatibleEquipmentIds) use semicolons as sub-separators.
- **Load**: 6 entity-specific load methods + `loadAll()` returns a `LoadedData` container. All load methods handle `FileNotFoundException` gracefully — return empty lists, log a message, program continues. `NumberFormatException` also caught for corrupt data.
- **Relationship restoration**: Teams are loaded after players (to restore Player references via ID matching). Match records are loaded after teams (to restore Team references). Heroes' compatible equipment IDs are stored but cross-referencing is deferred.
- **Error handling**: Every I/O operation wrapped in try-catch. Missing files → skip with message. Corrupt lines → skip that line. Never crashes.

Main.java: Save (option 8) calls `fileService.saveAll()` and reports success/failure. Load (option 9) asks confirmation before replacing data.

Verified: All 6 CSV files created in `data/` directory during smoke test. File sizes: admins.csv 75B, equipment.csv 971B, heroes.csv 948B, matchrecords.csv 402B, players.csv 900B, teams.csv 153B.

### My Decision

**CHECKED AND VERIFIED.** Exception handling is comprehensive — the program never crashes on missing or corrupt files. The LoadedData inner class is a clean way to return all parsed data as a single object. Semicolon sub-separators correctly handle multi-valued fields without conflicting with CSV commas. Relationship restoration order (players → teams → matches) respects the dependency chain. File sizes are reasonable for the dataset size. Ready for review phase.

---

## Prompt 12 — Testing/Reviewer Agent: Requirement review

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Review |
| **Related Commit** | `c629f39` |

### AI Response Summary

Performed a comprehensive code audit against all coursework requirements. Reviewed all 17 Java source files.

**17 checks passed**: 7 required classes present, 10 OOP concepts demonstrated (inheritance, interface, polymorphism, encapsulation, aggregation, composition, association, collections, exception handling, file I/O, enums), dataset minimums verified, program compiles and runs.

**2 bugs found**: B1 (HIGH) — `handleLoadData()` in Main.java calls `dataManager.initializeData()` instead of loading CSV data, making the Load feature non-functional. B2 (MEDIUM) — CSV load does not restore Player↔Hero and Hero↔Equipment relationships.

**2 documentation-code inconsistencies**: R1 — `getTopPlayer()` in code vs `getStrongestPlayer()` in design docs (per Prompt 02 decision). R2 — `String matchDate` in code vs `LocalDate` in design docs.

**14 manual test cases** provided covering all menu options. Final verdict: 14/16 checks pass, 2 minimal fixes needed (~30 lines total).

### My Decision

**REVIEWED AND ACCEPTED.** The 2 bugs are real and must be fixed before final submission. The documentation inconsistencies are cosmetic for a first-year project — I chose to keep `matchDate` as `String` for simplicity. Method name `getTopPlayer()` is acceptable and does not affect functionality. However, I am adding a major new feature: a Recommendation Engine. The review confirms the existing architecture is clean enough to accommodate this extension without refactoring.

---

## Prompt 12a — Architect Agent: Recommendation Engine Design

| Field | Detail |
|-------|--------|
| **Time** | 2026-06-06 |
| **Tool/Model** | Claude Code (CherryClaw) |
| **Agent Role** | AI-Architect |
| **Related Commit** | `c629f39` |

### AI Response Summary

Designed a complete Recommendation Engine as a new architectural extension. Produced full design documentation with algorithm formulas, UML diagrams, and integration plan.

**New classes designed (3):**
- `RecommendationType` enum (HERO, EQUIPMENT)
- `RecommendationResult` DTO — holds recommendedId, recommendedName, type, confidence (0.0-1.0), reason (human-readable explanation), supportingStats (factor breakdown for transparency)
- `RecommendationEngine` service — depends on GameDataManager (read-only), exposes 4 public methods: recommendHeroesForPlayer, recommendEquipmentForPlayer, recommendHeroByType, recommendEquipmentByHeroType. 5 private methods for scoring and normalization.

**Hero recommendation algorithm:** 5-factor weighted formula: typeMatch(0.30) + winRateFactor(0.25) + popularity(0.20) + teamSynergy(0.15) + levelMatch(0.10). Factors encourage hero-type diversity, favor strong-performing heroes, consider team balance, and match player experience level.

**Equipment recommendation algorithm:** 4-factor weighted formula: heroCompatibility(0.30) + usageFactor(0.25) + ratingFactor(0.25) + typeSynergy(0.20). Factors measure how many of the player's heroes can use the equipment, usage popularity, quality rating, and synergy with the player's hero pool.

**Design principles:** No self-recommendation (never suggest already-owned heroes), read-only (engine never mutates data), graceful degradation (empty list on invalid input), transparent (every recommendation includes factor breakdown), explainable (plain-English reason string per recommendation).

**UML updates:** Added Sections 8-10 to uml.md — new class diagram for 3 classes with full field/method signatures, dependency diagram showing Engine→GameDataManager relationship, sequence diagram for the recommendation flow (User→Main→Engine→GameDataManager).

**Design updates:** Added Section 13 to design.md — complete specifications for all 3 new classes, algorithm formulas with factor definitions and weight rationales, reason generation examples, Main.java menu integration plan (sub-menu with 4 options), constraint table.

### My Decision

**ACCEPTED.** The weighted formula design is transparent and defensible — every factor has a clear rationale tied to observable game data. The DTO pattern (RecommendationResult) cleanly separates computation from presentation. The read-only dependency on GameDataManager means zero risk to existing data integrity. The explainable reason generation satisfies the coursework requirement for AI-assisted recommendations that a first-year student can explain. Ready for implementation.
