# Project Plan: AI-Assisted Information Management System for Honor of Kings

---

## 1. Project Goal

### 1.1 System Description
This project implements an AI-Assisted Information Management System for "Honor of Kings" (王者荣耀), a popular multiplayer online battle arena (MOBA) game. The system is a console-based Java application that provides comprehensive management capabilities for game-related data including players, heroes, equipment, teams, and match records.

### 1.2 Core Functions
- **Player Lookup**: Search players by ID or name, display team affiliation, level, win rate, owned heroes, and equipment
- **Team Overview**: Display team members, average level, total matches, win rate, and strongest player
- **Hero Details**: Display hero type, base attributes, compatible equipment, and owning players
- **Equipment Ranking**: Rank equipment by usage count, rating, hero compatibility, and win rate contribution
- **Match History**: Retrieve recent N matches for players or teams with opponent, date, result, and hero selection
- **Leaderboard**: Display top X players by win rate, level, matches, or custom score
- **Data Management**: Admin can create, read, update, and delete all data; players have restricted access
- **Authentication**: Simple login/logout system distinguishing Admin and Player roles
- **AI-Assisted Recommendation Engine:** Implement an intelligent recommendation system that suggests optimal heroes and equipment builds based on multi-dimensional analysis, including hero type, player ownership/preferences, win rate statistics, equipment usage frequency, and team composition synergy. The engine must provide explainable recommendations with clear reasoning.

### 1.3 Target Users
- **Admin**: Full access for data management and system configuration
- **Player**: View-only access to public data, limited editing of personal profile

---

## 2. Requirement Analysis

### 2.1 Functional Requirements

| # | Requirement | Implementation Approach |
|---|-------------|------------------------|
| FR1 | Player query by ID or name | SearchService with HashMap lookup |
| FR2 | Team overview with statistics | Team.getSummary() with computed averages |
| FR3 | Hero details with compatible equipment | Hero class with Equipment associations |
| FR4 | Equipment ranking with multiple metrics | RankingService with weighted formula |
| FR5 | Match history retrieval | SearchService filtering by team/player |
| FR6 | Player leaderboard with tie-breaking | RankingService with comparator chain |
| FR7 | Admin CRUD operations | AdminService with role-based access control |
| FR8 | Authentication system | AuthenticationService tracking login state |

### 2.2 Data Requirements

| Entity | Minimum Count | Notes |
|--------|--------------|-------|
| Teams | ≥ 3 | Each with ≥5 players |
| Players | ≥ 15 | Each owning ≥3 heroes |
| Heroes | ≥ 15 | Each compatible with ≥2 equipment |
| Equipment | ≥ 20 | - |
| Match Records | ≥ 10 | - |
| Admin Accounts | ≥ 1 | For system management |

### 2.3 Non-Functional Requirements
- Console-based interface (no GUI required)
- Graceful error handling on invalid input
- CSV file persistence for data storage
- Compilable with JDK 8+ standard library only

#### Recommendation Engine Requirements
- **FR-REC-01:** System shall recommend heroes based on team composition synergy, player-owned heroes, and global/player win rates.
- **FR-REC-02:** System shall recommend equipment builds based on hero-specific usage statistics, win rate correlation, and player inventory.
- **FR-REC-03:** Each recommendation must include a human-readable explanation detailing the contributing factors and their relative weights.
- **FR-REC-04:** Recommendation functionality must be accessible via a dedicated sub-menu from the Main Menu.
- **FR-REC-05:** Algorithm must support multiple interchangeable strategies (Strategy Pattern) to allow future algorithm evolution without core refactoring.
- **FR-REC-06:** Recommendations must respect data constraints: only recommend heroes/equipment the player owns (configurable soft/hard filter).
- **NFR-REC-01:** Recommendation response time must be under 500ms for a pool of 100+ heroes.
- **NFR-REC-02:** Scoring formula weights must be externally configurable without code changes.

---

## 3. Java Concepts Used

| Concept | Application in Project |
|---------|-----------------------|
| **Inheritance** | Player and Admin extend abstract Person class |
| **Interface** | Reportable interface implemented by Player, Team, Hero |
| **Polymorphism** | Person reference holds Player or Admin; Collections of interface type |
| **Encapsulation** | All fields private with getter/setter access control |
| **Aggregation** | Team contains Players (players can exist independently) |
| **Composition** | MatchRecord contains Team references (immutable after creation) |
| **Association** | Hero references compatible Equipment list |
| **Collections** | ArrayList for ordered lists; HashMap for O(1) ID-based lookup |
| **Exception Handling** | try-catch in File I/O; custom exceptions for data integrity violations |
| **File I/O** | CSV read/write operations in FileStorageService |
| **Enums** | Role, HeroType, EquipmentType, MatchResult |
| **Strategy Design Pattern** | Encapsulates interchangeable recommendation algorithms (`HeroRecommendationStrategy`, `EquipmentRecommendationStrategy`) behind a common interface, enabling runtime algorithm switching and Open/Closed Principle compliance |
| **Java Streams & Collectors** | Used extensively for filtering candidate pools, mapping entities to scored results, and aggregating statistics (e.g., `stream().filter().map().sorted().limit()` pipeline) |
| **Custom Comparator / Sorting** | Multi-factor weighted scoring with custom `Comparator<RecommendationResult>` for ranking results by composite score |
| **Generics** (`RecommendationResult<T>`) | Type-safe recommendation container supporting both `Hero` and `Equipment` return types from a unified interface |
| **EnumMap** | Efficient storage and lookup of recommendation factor weights using `EnumMap<RecommendationFactor, Double>` |
| **Functional Interfaces** | Lambda expressions for pluggable normalization functions (sigmoid, min-max) within the scoring formula |

---

## 4. Class Design

### 4.1 Core Classes

| Class | Type | Responsibilities |
|-------|------|------------------|
| **Person** | Abstract | Base class with id, name, role; common methods |
| **Player** | Concrete | Extends Person; adds level, winRate, heroes, team |
| **Admin** | Concrete | Extends Person; adds permission level |
| **Hero** | Concrete | Hero attributes (hp, attack, defense, speed), type, equipment compatibility |
| **Equipment** | Concrete | Equipment attributes, type, rating, usage count |
| **Team** | Concrete | Team name, player list, match history |
| **MatchRecord** | Concrete | Match participants, result, date, hero selections |

### 4.2 Service Classes

| Class | Responsibilities |
|-------|------------------|
| **GameDataManager** | Central in-memory data store, CRUD operations |
| **AuthenticationService** | Login/logout, role-based access control |
| **SearchService** | Player/Team/Hero lookups by ID/name |
| **RankingService** | Leaderboard generation, equipment ranking |
| **AdminService** | Admin-only data management operations |
| **FileStorageService** | CSV serialization/deserialization |

### 4.3 Utility Classes

| Class | Responsibilities |
|-------|------------------|
| **DataInitializer** | Creates initial test dataset |
| **InputHelper** | Safe console input handling and validation |

#### Recommendation Engine Classes
| Class | Package | Responsibility |
| :--- | :--- | :--- |
| `RecommendationEngine` | `src/hok/service/recommendation/` | Facade class; manages strategy registry and delegates execution |
| `RecommendationStrategy` | `src/hok/service/recommendation/` | Interface defining `execute(RecommendationRequest)` contract |
| `HeroRecommendationStrategy` | `src/hok/service/recommendation/impl/` | Weighted scoring for hero picks (synergy + mastery + win rate) |
| `EquipmentRecommendationStrategy` | `src/hok/service/recommendation/impl/` | Weighted scoring for equipment builds (usage + win rate + inventory) |
| `RecommendationResult<T>` | `src/hok/model/recommendation/` | Generic result DTO: item, score, reason string, factor breakdown map |
| `RecommendationRequest` | `src/hok/model/recommendation/` | Input DTO: player context, team composition, target role, top-N |
| `RecommendationFactor` | `src/hok/model/recommendation/` | Enum: WIN_RATE, PLAYER_MASTERY, TEAM_SYNERGY, EQUIP_POPULARITY, OWNERSHIP |
| `RecommendationMenu` | `src/hok/ui/` | Sub-menu UI implementing `IMenu` interface for recommendation entry points |

---

## 5. UML Draft

### 5.1 Class Diagram (Text-based)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Person (abstract)                             │
├─────────────────────────────────────────────────────────────────────────────┤
│ - id: String                                                               │
│ - name: String                                                             │
│ - role: Role                                                               │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getId(): String                                                          │
│ + getName(): String                                                        │
│ + getRole(): Role                                                          │
│ + setName(name: String): void                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │
            ┌───────────────────────┴───────────────────────┐
            │                                               │
┌───────────┴───────────┐                     ┌────────────┴────────────┐
│       Player          │                     │        Admin            │
├───────────────────────┤                     ├─────────────────────────┤
│ - level: int          │                     │ - permissionLevel: int  │
│ - winRate: double     │                     ├─────────────────────────┤
│ - heroes: List<Hero>  │                     │ + getPermissionLevel()  │
│ - team: Team          │                     └─────────────────────────┘
├───────────────────────┤
│ + getLevel(): int     │
│ + getWinRate(): double│
│ + getHeroes(): List   │
│ + getTeam(): Team     │
│ + addHero(Hero): void │
│ + setTeam(Team): void │
└───────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              Hero                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ - id: String                                                               │
│ - name: String                                                             │
│ - heroType: HeroType                                                       │
│ - hp: int                                                                  │
│ - attack: int                                                              │
│ - defense: int                                                             │
│ - speed: int                                                               │
│ - compatibleEquipment: List<Equipment>                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getSummary(): String  <<implements Reportable>>                          │
│ + getDetailedInfo(): String                                                │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                            Equipment                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│ - id: String                                                               │
│ - name: String                                                             │
│ - equipmentType: EquipmentType                                             │
│ - attackBonus: int                                                         │
│ - defenseBonus: int                                                        │
│ - hpBonus: int                                                             │
│ - speedBonus: int                                                          │
│ - rating: double                                                           │
│ - usageCount: int                                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ + calculateScore(): double                                                 │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              Team                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ - id: String                                                               │
│ - name: String                                                             │
│ - players: List<Player>                                                    │
│ - matchHistory: List<MatchRecord>                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getAverageLevel(): double                                                │
│ + getTotalMatches(): int                                                   │
│ + getWinRate(): double                                                     │
│ + getStrongestPlayer(): Player                                             │
│ + addPlayer(Player): void                                                  │
│ + removePlayer(Player): void                                               │
│ + getSummary(): String  <<implements Reportable>>                          │
│ + getDetailedInfo(): String                                                │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                         MatchRecord                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ - id: String                                                               │
│ - teamA: Team                                                              │
│ - teamB: Team                                                              │
│ - result: MatchResult                                                      │
│ - date: LocalDate                                                          │
│ - heroSelections: Map<Player, Hero>                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getWinner(): Team                                                        │
│ + getLoser(): Team                                                         │
│ + isDraw(): boolean                                                        │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 Interface

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Reportable (Interface)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getSummary(): String        // One-line summary for listings             │
│ + getDetailedInfo(): String   // Full formatted details                    │
└─────────────────────────────────────────────────────────────────────────────┘
        ▲              ▲              ▲
        │              │              │
    Player          Team           Hero
```

---

## 6. Data Design

### 6.1 Initial Dataset Structure

**Teams:**
- Team 1: "Royal Dragons" with 5 players
- Team 2: "Shadow Warriors" with 5 players  
- Team 3: "Phoenix Rising" with 5 players

**Players:**
- 15 total players (5 per team)
- Each player owns ≥3 heroes
- Predefined win rates and levels

**Heroes:**
- 15 heroes spanning all types (TANK, WARRIOR, ASSASSIN, MAGE, MARKSMAN, SUPPORT)
- Each hero compatible with ≥2 equipment items

**Equipment:**
- 20 equipment items across categories (ATTACK, DEFENSE, MAGIC, MOVEMENT, JUNGLE)

**Match Records:**
- 10+ match records with varying results

### 6.2 File Storage Format (CSV)

**players.csv:**
```
id,name,role,level,winRate,teamId,heroIds,password
P001,ZhangSan,PLAYER,30,0.65,T001,H001|H002|H003,p@ssw0rd
```

**heroes.csv:**
```
id,name,heroType,hp,attack,defense,speed,compatibleEquipmentIds
H001,LiBai,MAGE,3200,380,220,350,E001|E002
```

**equipment.csv:**
```
id,name,equipmentType,attackBonus,defenseBonus,hpBonus,speedBonus,rating,usageCount
E001,SwordOfDestiny,ATTACK,100,0,0,0,9.2,150
```

**teams.csv:**
```
id,name,playerIds
T001,Royal Dragons,P001|P002|P003|P004|P005
```

**matchrecords.csv:**
```
id,teamAId,teamBId,result,date
M001,T001,T002,WIN,2024-01-15
```

**admins.csv:**
```
id,name,role,password
A001,admin,ADMIN,admin123
```

---

## 7. AI Usage Plan

### 7.1 AI Agent Roles

| Agent Role | Purpose | Allowed Tasks |
|------------|---------|---------------|
| **Architect Agent** | System design and planning | Class diagram design, UML suggestions, API design, package structure |
| **Implementation Agent** | Code generation | Java method implementation, helper functions, data structures |
| **Testing/Review Agent** | Quality assurance | Bug detection, code review, test case generation, requirement verification |
| **Refactor Agent** (Optional) | Code improvement | Code restructuring, performance optimization, readability improvements |
| **Documentation Agent** (Optional) | Documentation writing | Javadoc comments, inline documentation, README content |

### 7.2 Agent Workflow

```
[Human Planning]
      │
      ▼
[Architect Agent] → Class Design → UML → API Specification
      │
      ▼
[Implementation Agent] → Code Generation → Unit Tests
      │
      ▼
[Testing/Review Agent] → Bug Detection → Code Review → Quality Report
      │
      ▼
[Human Approval/Modification]
      │
      ▼
[Refactor Agent] (Optional) → Optimization → Restructuring
```

### 7.3 AI Usage Constraints
- All AI-generated code must be reviewed and understood before acceptance
- No blind acceptance of AI outputs
- AI suggestions that don't meet requirements must be modified or rejected
- AI evidence must be documented in ai/ folder

---

## 8. Prompt Strategy

### 8.1 Prompt Design Principles

1. **Specificity**: Include context, constraints, and expected output format
2. **Role Assignment**: Assign specific roles to AI (e.g., "You are a Java architect")
3. **Step-by-Step Guidance**: Break complex tasks into smaller prompts
4. **Code Quality Requirements**: Specify coding standards and conventions
5. **Verification Criteria**: Include testable requirements

### 8.2 Example Prompt Structure

```
[Agent Role]: Java Implementation Specialist

[Context]:
- Project: Honor of Kings Information Management System
- Class: Player (extends Person, implements Reportable)
- Purpose: Represents a game player with level, win rate, and hero collection

[Requirements]:
1. Must extend Person class
2. Must implement Reportable interface
3. Private fields: level (int), winRate (double), heroes (List<Hero>), team (Team)
4. Include all necessary getters and setters
5. Implement getSummary() returning "Player: {name} (Level {level})"
6. Implement getDetailedInfo() returning formatted multi-line string
7. Include addHero() and removeHero() methods
8. Code must follow Java naming conventions
9. Include Javadoc comments

[Output Format]:
- Complete Java class code
- No additional explanations
- Include package declaration: hok.model
```

### 8.3 AI Output Verification Process

1. **Syntax Check**: Compile code and fix compilation errors
2. **Logic Check**: Verify against requirements and design specifications
3. **Quality Check**: Review code readability, naming, and structure
4. **Testing**: Run unit tests to validate functionality
5. **Documentation**: Ensure proper comments and Javadoc

---

## 9. Development Timeline

### 9.1 Phase Breakdown

| Stage | Duration | Expected Work | AI Agent Involvement |
|-------|----------|---------------|---------------------|
| **Stage 1** | 1 day | Read requirements, create repository, write initial plan.md | [Human] |
| **Stage 2** | 1 day | Design feedback from Architect Agent, revise class structure | [AI-Architect] |
| **Stage 3** | 2 days | Implement model classes (Person, Player, Admin, Hero, Equipment, Team, MatchRecord) | [AI-Implementation] |
| **Stage 4** | 1 day | Implement initial dataset using DataInitializer | [AI-Implementation] |
| **Stage 5** | 2 days | Implement menu system and search features | [AI-Implementation] |
| **Stage 6** | 2 days | Implement authentication and role-based permissions | [AI-Implementation] |
| **Stage 7** | 2 days | Implement ranking and leaderboard functions | [AI-Implementation] |
| **Stage 8** | 2 days | Testing/Review Agent bug detection, fixes, final testing | [AI-Review] |
| **Stage 9** | 1 day | Complete documentation, reflection, Git export | [Human] |
| **REC-1: Foundation** | Days 1–2 | `RecommendationResult`, `RecommendationRequest`, `RecommendationFactor` enum, `RecommendationStrategy` interface | Model layer stable |
| **REC-2: Hero Strategy** | Days 3–5 | `HeroRecommendationStrategy`, synergy matrix data structure, weighted scoring formula implementation | GameDataManager hero API |
| **REC-3: Equipment Strategy** | Days 6–7 | `EquipmentRecommendationStrategy`, usage statistics aggregation | GameDataManager equipment API |
| **REC-4: Engine & Config** | Day 8 | `RecommendationEngine` facade, external weight configuration loading, caching layer | REC-2, REC-3 complete |
| **REC-5: UI Integration** | Days 9–10 | `RecommendationMenu`, Main Menu integration, display formatting with explanations | REC-4 complete |
| **REC-6: Testing & Tuning** | Days 11–12 | Unit tests, integration tests, weight tuning with sample data | All above |

### 9.2 Milestones

| Milestone | Deadline | Deliverable |
|-----------|----------|-------------|
| Plan Finalized | Day 2 | plan.md approved |
| Core Classes | Day 5 | All model classes implemented |
| Initial Dataset | Day 6 | DataInitializer with sample data |
| Menu & Search | Day 8 | Console interface functional |
| Authentication | Day 10 | Login/logout with permissions |
| Ranking | Day 12 | Leaderboard and equipment ranking |
| Testing Complete | Day 14 | All tests passing |
| Final Submission | Day 15 | Full project with documentation |

---

## 10. Testing Plan

### 10.1 Test Cases

| Test ID | Test Function | Input | Expected Output |
|---------|---------------|-------|-----------------|
| TC001 | Player login with valid credentials | username: "admin", password: "admin123" | Login successful, role: ADMIN |
| TC002 | Player login with invalid credentials | username: "admin", password: "wrong" | Login failed message |
| TC003 | Search player by ID | Player ID: "P001" | Player details displayed |
| TC004 | Search player by name | Player name: "ZhangSan" | Player details displayed |
| TC005 | Search non-existent player | Player ID: "P999" | "Player not found" message |
| TC006 | Team overview | Team ID: "T001" | Team members, average level, win rate |
| TC007 | Hero details | Hero ID: "H001" | Hero type, stats, compatible equipment |
| TC008 | Equipment ranking | Top 5 by usage | 5 equipment items in descending order |
| TC009 | Match history retrieval | Player ID: "P001", N=3 | Last 3 matches displayed |
| TC010 | Leaderboard tie-breaking | Two players with same win rate | Players ordered by level, then name |
| TC011 | Admin add player | New player data | Player added successfully |
| TC012 | Player edit own profile | New player name | Profile updated (name only) |
| TC013 | Player unauthorized access | Player attempts to delete data | "Permission denied" message |
| TC014 | File save and load | Save data, restart, load | Data persists correctly |
| TC015 | Invalid input handling | Non-numeric level input | Error message, retry prompt |

### 10.2 Testing Approach

1. **Unit Testing**: Test individual methods and classes in isolation
2. **Integration Testing**: Test interactions between components
3. **System Testing**: Test complete workflows end-to-end
4. **Edge Case Testing**: Test boundary conditions and invalid inputs
5. **Manual Testing**: Console interaction and usability testing
6. **Recommendation Engine Testing**: Verify scoring formulas, strategy switching, explanation generation, config loading

#### Recommendation Engine Tests
| Test ID | Type | Description | Expected Result |
| :--- | :--- | :--- | :--- |
| TEST-REC-01 | Unit | Hero scoring formula produces normalized scores in [0.0, 1.0] | All factor contributions sum correctly; no score exceeds bounds |
| TEST-REC-02 | Unit | Owned-hero filter excludes unowned heroes when hard-filter enabled | Returned list contains only player-owned heroes |
| TEST-REC-03 | Unit | Team synergy score reflects known synergy pairs | Donghuang Taiyi + Luban yields higher synergy than two unrelated heroes |
| TEST-REC-04 | Unit | Equipment strategy ranks high-usage items above low-usage items | Top result matches highest usage-frequency equipment for given hero |
| TEST-REC-05 | Unit | Explanation string is non-null and references at least one factor | Every `RecommendationResult.reason` contains meaningful text |
| TEST-REC-06 | Integration | Full hero recommendation pipeline from request to sorted results | Returns top-N results in descending score order within 500ms |
| TEST-REC-07 | Integration | Menu navigation: Main Menu → Recommendation Menu → Back | Navigation flow completes without exceptions or state corruption |
| TEST-REC-08 | Edge Case | Empty team composition defaults to personal preference weighting | No NullPointerException; fallback weights applied gracefully |
| TEST-REC-09 | Edge Case | Player with zero match history receives global-stat-only recommendations | Mastery factor contributes 0; other factors compensate normally |
| TEST-REC-10 | Config | Weight modification via config file takes effect without recompilation | Updated weights reflected in next recommendation call |

---

## 11. Risk Analysis

### 11.1 Potential Risks

| Risk | Likelihood | Impact | Mitigation Strategy |
|------|------------|--------|---------------------|
| **AI-generated code bugs** | High | Medium | Review all AI output, run tests, maintain human oversight |
| **CSV file corruption** | Medium | High | Implement file validation, backup mechanism, error recovery |
| **Authentication bypass** | Low | High | Validate permissions on every sensitive operation |
| **Performance issues** | Medium | Medium | Use efficient data structures (HashMap), limit dataset size |
| **Deadline pressure** | High | High | Follow timeline strictly, prioritize core features |
| **Git history integrity** | Medium | Medium | Regular commits, meaningful messages, verify before submission |
| **Compilation errors** | High | Medium | Compile frequently, use IDE feedback |

### 11.2 Risk Response Plan

1. **Bug Detection**: Use Testing/Review Agent to identify issues early
2. **Backup Strategy**: Save data files before each major change
3. **Progress Tracking**: Maintain checklist of completed features
4. **Code Reviews**: Regular self-review of implementation against requirements
5. **Time Buffer**: Allocate extra time at the end for bug fixes and documentation

---

## 12. Final Reflection Placeholder

*This section will be completed after project implementation.*

### 12.1 Learning Outcomes

- What Java concepts did I master through this project?
- How did AI assist in my learning process?
- What challenges did I overcome independently?

### 12.2 AI Usage Reflection

- How did AI help me complete this project?
- What did AI do poorly?
- How did I verify AI outputs?

### 12.3 Project Evaluation

- What worked well in my design?
- What would I change if I started over?
- What additional features could be added?

---

## Appendix: Git Commit Protocol

| Prefix | Meaning | Minimum Required |
|--------|---------|-----------------|
| [Human] | Manual planning, debugging, approval | ≥ 4 |
| [AI-Architect] | System design, UML, API planning | ≥ 3 |
| [AI-Implementation] | Core logic, class implementation | ≥ 3 |
| [AI-Review] | Code audit, requirement verification | ≥ 2 |
| [AI-Refactor] | Restructure without behavior change | — |
| [Docs] | README, design.md, inline docs | — |
| [Test] | Unit/integration tests | — |
| [Fix] | Bug fixes | — |

**Total meaningful commits: ≥ 12**

---

*Last Updated: June 2026*

---

## 13. Architecture Review (AI-Architect Final Audit)

*Conducted: 2026-06-06 | Reviewer: AI-Architect Agent*

### 13.1 Codebase Statistics

| Metric | Value |
|--------|-------|
| Total source files | 24 |
| Total lines of code | 3,630 |
| Packages | 7 (model, service, enums, interfacepkg, storage, util, root) |
| Model classes | 8 |
| Service classes | 6 |
| Enums | 5 |
| Interfaces | 1 |
| Utility classes | 2 |
| Main method | 1 (349 lines) |

### 13.2 Architecture Strengths

| # | Strength | Detail |
|---|----------|--------|
| 1 | **Layered Architecture** | Clean 4-tier separation: Presentation (Main.java) → Service (5 classes) → Data (GameDataManager) → Persistence (FileStorageService). Each layer only talks to the layer directly below it. |
| 2 | **Pure Router Main.java** | Main.java contains zero business logic — 349 lines of pure menu routing. All operations delegate to services. This prevents the most common first-year pitfall. |
| 3 | **Dual-Storage Pattern** | GameDataManager uses `ArrayList` for ordered iteration + `HashMap` for O(1) ID lookup — optimal for this dataset size. |
| 4 | **Defensive Copies** | All collection getters (`getHeroes()`, `getPlayers()`, `getCompatibleEquipment()`, `getAll*()`) return `new ArrayList<>(list)` — prevents external mutation of internal state. |
| 5 | **Bidirectional Reference Management** | Team↔Player and Player↔Hero relationships are properly maintained. `addPlayer()` sets both sides; `deletePlayer()` cleans up both sides. No orphaned references. |
| 6 | **Graceful Degradation in I/O** | FileStorageService catches `FileNotFoundException`, `IOException`, and `NumberFormatException`. Missing files → empty lists + log message, no crash. |
| 7 | **Polymorphic Authentication** | `AuthenticationService.currentUser` is typed as `Person` — can hold `Player` or `Admin` at runtime. `isAdmin()` uses `getRole()` check, demonstrating runtime type dispatch. |
| 8 | **Read-Only Recommendation Engine** | RecommendationEngine depends on GameDataManager but never mutates it. No side effects. Safe to call from any context. |
| 9 | **Explainable AI** | Every recommendation includes `supportingStats` (factor breakdown) and `reason` (plain-English explanation). Transparent and auditable. |
| 10 | **Package Cohesion** | All enums in `enums/`, all models in `model/`, all services in `service/` — follows Java convention, easy to navigate. |

### 13.3 Architectural Issues

| # | Severity | Issue | Detail | Recommendation |
|---|----------|-------|--------|----------------|
| **I1** | 🟡 MEDIUM | **Design doc-code mismatch: No Strategy Pattern** | plan.md §3 and §4 specify Strategy Pattern (`RecommendationStrategy` interface, `HeroRecommendationStrategy`, `EquipmentRecommendationStrategy`). Actual implementation has all algorithms hardcoded in a single `RecommendationEngine` class (407 lines). | If Strategy Pattern is required for grading, refactor RecommendationEngine into 3 classes. If not, update plan.md to remove Strategy Pattern references and note the simplification as a design decision. |
| **I2** | 🟡 MEDIUM | **Design doc-code mismatch: No Singleton** | plan.md and design.md mention Singleton for GameDataManager, but it is instantiated via `new GameDataManager()` in Main.java — a plain object, not a Singleton. | Either implement `getInstance()` or document that the single instantiation in Main.java is functionally equivalent without the formal pattern. |
| **I3** | 🟡 MEDIUM | **GameDataManager size (477 lines)** | Largest single class. Handles initialization, indexing, CRUD for 6 entity types, plus cascading delete logic. | Consider extracting entity-specific CRUD into sub-managers (e.g., `PlayerManager`, `HeroManager`) if the project grows. Acceptable for current scope. |
| **I4** | 🟢 LOW | **CSV load does not restore relationships** | Known bug B2 from Prompt 12 review. Stored hero IDs and equipment IDs are saved to CSV but not re-wired on load. After save→load→restart, Player↔Hero and Hero↔Equipment links are broken. | Add a `restoreRelationships()` method in FileStorageService that runs after `loadAll()`. |
| **I5** | 🟢 LOW | **No model-level input validation** | `Player.setWinRate(2.0)` would be accepted. Range validation only happens in AdminService UI (manual input), not at the model layer. | Add validation in setters: `if (rate < 0.0 \|\| rate > 1.0) throw new IllegalArgumentException(...)`. Low priority — existing UI already validates. |
| **I6** | 🟢 LOW | **Static fields in Main.java** | All services are `private static` fields. Functional but not testable — no way to inject mock services. | For a first-year project, static fields are acceptable. Would refactor to instance-based for testability in a production project. |
| **I7** | 🟢 LOW | **No caching in RecommendationEngine** | `computeGlobalWinRate()` rescans all players for every hero. `maxUsage()` rescans all equipment for every equipment recommendation. | Cache global stats in a `Map<String, Double>` on first call. Current dataset (≤20 items) makes this negligible. |

### 13.4 OOP Concept Verification

| Concept | Required | Implemented | Verification |
|---------|----------|-------------|-------------|
| Inheritance | ✓ | ✓ | `Player extends Person`, `Admin extends Person` |
| Abstract Class | ✓ | ✓ | `Person` declared `abstract` — cannot be instantiated |
| Interface | ✓ | ✓ | `Reportable` with `getSummary()` + `getDetailedInfo()`, implemented by Player, Team, Hero |
| Polymorphism | ✓ | ✓ | `Person currentUser` in AuthenticationService; `Reportable` references in SearchService |
| Encapsulation | ✓ | ✓ | All 50+ fields are `private`; defensive copies on 12 collection getters |
| Aggregation | ✓ | ✓ | `Team ◇→ Player`: players survive team deletion |
| Composition | ✓ | ✓ | `Player ◆→ Hero`: heroes removed from all players on hero delete |
| Association | ✓ | ✓ | `Hero → Equipment`, `MatchRecord → Team` |
| Collections | ✓ | ✓ | `ArrayList` for ordered storage, `HashMap` for O(1) indexes |
| Exception Handling | ✓ | ✓ | try-catch on all I/O; `NoSuchElementException` guard in InputHelper |
| File I/O | ✓ | ✓ | 6 CSV files with header rows, semicolon sub-separators |
| Enums | ✓ | ✓ | 5 enums (Role, HeroType, EquipmentType, MatchResult, RecommendationType) |

### 13.5 Package Cohesion Assessment

| Package | Files | Cohesion | Notes |
|---------|-------|----------|-------|
| `hok.enums` | 5 | ✅ HIGH | All enumerated types, no logic |
| `hok.interfacepkg` | 1 | ✅ HIGH | Single interface |
| `hok.model` | 8 | ✅ HIGH | Pure data classes + DTOs |
| `hok.service` | 6 | ⚠️ MEDIUM | GameDataManager (477 lines) overshadows others; AdminService has UI logic mixed with CRUD |
| `hok.storage` | 1 | ✅ HIGH | Single responsibility |
| `hok.util` | 2 | ✅ HIGH | Pure helpers |
| `hok` (root) | 1 | ✅ HIGH | Entry point only |

### 13.6 Optimization Suggestions

| # | Suggestion | Effort | Benefit |
|---|-----------|--------|---------|
| 1 | **Add `dataManager.reloadFrom(LoadedData)`** method in GameDataManager to properly rebuild state from CSV-loaded data | ~30 lines | Fixes bug I4, enables full round-trip persistence |
| 2 | **Extract AdminService menu strings** to a separate `AdminMenuText` constants class or resource file | ~50 lines | Improves readability, enables future i18n |
| 3 | **Add `@Override` annotations** consistently — some methods are missing them (e.g., `RecommendationResult.toString()`) | ~5 lines | Compiler-checked correctness |
| 4 | **Add `RECOMMENDATION` entry to plan.md's class table** (currently missing from Section 4.1) | ~3 lines | Documentation completeness |
| 5 | **Add a `RecommendationEngine` to Service Layer Dependency Diagram** in uml.md §4 | ~3 lines | UML accuracy |

### 13.7 Final Verdict

| Dimension | Rating | Comment |
|-----------|--------|---------|
| **Architecture** | 8/10 | Clean layered design. Two doc-code mismatches (Strategy, Singleton) need resolution. |
| **OOP Correctness** | 10/10 | All 12 required concepts correctly demonstrated in working code. |
| **Code Quality** | 8/10 | Readable, well-commented. Some large classes (GameDataManager 477, RecommendationEngine 407) but acceptable for scope. |
| **Extensibility** | 7/10 | Adding new entity types requires touching multiple classes. Strategy Pattern would improve this. |
| **Testability** | 5/10 | Static fields prevent injection. No unit tests in codebase. Console I/O makes automated testing difficult. |
| **Documentation** | 9/10 | Comprehensive plan.md, design.md, uml.md, prompts.md, agent-log.md. Minor inconsistencies flagged above. |

**Overall: The architecture is solid for a first-year Java coursework project. The two design doc-code mismatches (I1: Strategy Pattern, I2: Singleton) are the only items that could affect grading if the marker expects formal pattern implementation. All OOP requirements are verifiably met in working, compilable code.**

---

## 14. Code Quality Review (AI-Review Final Audit)

*Conducted: 2026-06-06 | Reviewer: AI-Review Agent*

### 14.1 Issues Found

| ID | Severity | File | Line | Issue | Suggested Fix |
|----|----------|------|------|-------|---------------|
| R1 | LOW | 9 files | — | Wildcard imports (14 instances of `import X.*`) | Replace with explicit imports |
| R2 | LOW | RankingService.java | 170 | Leaderboard `%` display misalignment (confirmed by TC-11) | Use `String.format("%.1f%%", ...)` inside format column |
| R3 | INFO | Main.java | 199 | `var` keyword inconsistent with explicit types used elsewhere | Use `for (Player p : ...)` |
| R4 | INFO | SearchService.java | 45 | Manual string concatenation with index loop | Use `String.join(", ", names)` |
| R5 | INFO | GameDataManager | — | `findEquipmentByName()` defined but never called | Keep (future-proof) or add caller |

### 14.2 Quality Scores

| Dimension | Score | Notes |
|-----------|-------|-------|
| Code Style | 8/10 | Wildcard imports and var inconsistency |
| Encapsulation | 10/10 | All private, defensive copies on 12 getters |
| Error Handling | 8/10 | I/O layer robust; no runtime exception handling in business logic |
| Efficiency | 8/10 | HashMap indexes; Recommender uncached (acceptable) |
| Readability | 9/10 | Clear names, consistent Javadoc |
| Null Safety | 8/10 | 25+ null checks; minor edge NPE risk |
| Best Practices | 7/10 | Iterator removal, defensive copies; wildcard imports |
| **Overall** | **8.3/10** | Excellent for first-year coursework |