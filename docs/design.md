# Design Document: AI-Assisted Information Management System for Honor of Kings

---

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                          │
│  Main.java (menu router)  +  InputHelper (input validation)         │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────────┐
│                          SERVICE LAYER                               │
│  AuthenticationService  SearchService  RankingService  AdminService │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────────┐
│                         DATA LAYER                                   │
│  GameDataManager (central in-memory store)                          │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────────┐
│                        PERSISTENCE LAYER                             │
│  FileStorageService (CSV read/write)  ←→  data/*.csv               │
└─────────────────────────────────────────────────────────────────────┘

                ┌──────────────────────────────┐
                │       DOMAIN MODEL LAYER      │
                │  Person Player Admin Hero     │
                │  Equipment Team MatchRecord   │
                │  Role HeroType EquipmentType  │
                │  MatchResult Reportable       │
                └──────────────────────────────┘
```

**Rule**: Each layer only talks to the layer directly below it. Main.java never touches model or storage directly.

---

## 2. Package Structure

```
src/hok/
├── Main.java
├── model/           // Domain entities
├── enums/           // Enumerated types
├── interfacepkg/    // Interfaces
├── service/         // Business logic
├── storage/         // File persistence
└── util/            // Helpers
```

---

## 3. Enum Definitions

### 3.1 Role.java
```java
package hok.enums;

public enum Role {
    PLAYER,
    ADMIN
}
```

### 3.2 HeroType.java
```java
package hok.enums;

public enum HeroType {
    TANK,       // 坦克 - high HP, high defense
    WARRIOR,    // 战士 - balanced attack and defense
    ASSASSIN,   // 刺客 - high attack, high speed, low HP
    MAGE,       // 法师 - high magic attack, low defense
    MARKSMAN,   // 射手 - high attack, moderate speed
    SUPPORT     // 辅助 - moderate stats, team utility
}
```

### 3.3 EquipmentType.java
```java
package hok.enums;

public enum EquipmentType {
    ATTACK,     // 攻击装
    DEFENSE,    // 防御装
    MAGIC,      // 法术装
    MOVEMENT,   // 移动装
    JUNGLE      // 打野装
}
```

### 3.4 MatchResult.java
```java
package hok.enums;

public enum MatchResult {
    WIN,
    LOSE,
    DRAW
}
```

---

## 4. Interface Specification

### 4.1 Reportable.java
```java
package hok.interfacepkg;

public interface Reportable {
    String getSummary();       // One-line summary for lists
    String getDetailedInfo();  // Full formatted details
}
```

**Implementing classes**: Player, Team, Hero

| Class | getSummary() returns | getDetailedInfo() returns |
|-------|---------------------|--------------------------|
| Player | "ID: p001 | Name: Alice | Level: 15" | All player fields + heroes + team |
| Team | "ID: t01 | Name: Dragons | 5 members" | All members + avg level + win rate |
| Hero | "ID: h01 | Name: Luban | Type: MARKSMAN" | All stats + compatible equipment |

---

## 5. Model Class Specifications

### 5.1 Person.java (abstract)
| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | abstract class |
| **Implements** | — |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| id | String | private | Unique identifier |
| name | String | private | Display name |
| role | Role | private | PLAYER or ADMIN |

| Method | Return | Description |
|--------|--------|-------------|
| Person(id, name, role) | — | Constructor |
| getId() | String | |
| getName() | String | |
| getRole() | Role | |
| setName(name) | void | |
| setRole(role) | void | |
| toString() | String | Override: "id - name (role)" |

### 5.2 Player.java (extends Person)
| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | concrete class |
| **Extends** | Person |
| **Implements** | Reportable |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| level | int | private | 1-30 |
| winRate | double | private | 0.0 - 1.0 |
| totalMatches | int | private | Total matches played |
| heroes | List\<Hero\> | private | Heroes owned (composition) |
| team | Team | private | Current team (aggregation, nullable) |
| password | String | private | For authentication |

| Method | Return | Description |
|--------|--------|-------------|
| Player(id, name, password) | — | Constructor |
| getLevel() | int | |
| getWinRate() | double | |
| getTotalMatches() | int | |
| getHeroes() | List\<Hero\> | Returns copy (defensive) |
| getTeam() | Team | |
| getPassword() | String | |
| setLevel(level) | void | |
| setWinRate(rate) | void | |
| setTotalMatches(n) | void | |
| setTeam(team) | void | |
| setPassword(pw) | void | |
| addHero(hero) | void | |
| removeHero(heroId) | boolean | |
| getSummary() | String | Reportable impl |
| getDetailedInfo() | String | Reportable impl |

### 5.3 Admin.java (extends Person)
| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | concrete class |
| **Extends** | Person |
| **Implements** | — |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| permissionLevel | int | private | 1 = basic, 2 = super |
| password | String | private | For authentication |

| Method | Return | Description |
|--------|--------|-------------|
| Admin(id, name, password) | — | Constructor |
| getPermissionLevel() | int | |
| getPassword() | String | |
| setPermissionLevel(level) | void | |
| setPassword(pw) | void | |

### 5.4 Hero.java
| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | concrete class |
| **Implements** | Reportable |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| id | String | private | Unique ID, e.g. "h01" |
| name | String | private | Display name |
| heroType | HeroType | private | Enum type |
| hp | int | private | Hit points |
| attack | int | private | Attack power |
| defense | int | private | Defense power |
| speed | int | private | Movement speed |
| compatibleEquipment | List\<Equipment\> | private | Association |

| Method | Return | Description |
|--------|--------|-------------|
| Hero(id, name, heroType, hp, attack, defense, speed) | — | Constructor |
| getId() / getName() / getHeroType() | — | Getters |
| getHp() / getAttack() / getDefense() / getSpeed() | int | Getters |
| getCompatibleEquipment() | List\<Equipment\> | Returns copy |
| addCompatibleEquipment(eq) | void | Add one |
| removeCompatibleEquipment(eqId) | boolean | |
| getSummary() | String | Reportable impl |
| getDetailedInfo() | String | Reportable impl |

### 5.5 Equipment.java
| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | concrete class |
| **Implements** | — |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| id | String | private | Unique ID, e.g. "e01" |
| name | String | private | Display name |
| equipmentType | EquipmentType | private | Enum type |
| attackBonus | int | private | |
| defenseBonus | int | private | |
| hpBonus | int | private | |
| speedBonus | int | private | |
| rating | double | private | 1.0-10.0 |
| usageCount | int | private | How many heroes use this |

| Method | Return | Description |
|--------|--------|-------------|
| Equipment(id, name, type, aB, dB, hB, sB, rating) | — | Constructor |
| All getters | — | Standard |
| All setters | — | Standard |
| incrementUsage() | void | usageCount++ |
| getTotalBonus() | int | attackBonus + defenseBonus + hpBonus + speedBonus |

### 5.6 Team.java
| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | concrete class |
| **Implements** | Reportable |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| id | String | private | Unique ID, e.g. "t01" |
| name | String | private | Team name |
| players | List\<Player\> | private | Aggregation |

| Method | Return | Description |
|--------|--------|-------------|
| Team(id, name) | — | Constructor |
| getId() / getName() | — | Getters |
| getPlayers() | List\<Player\> | Returns copy |
| setName(name) | void | |
| addPlayer(player) | void | Adds player + sets player.team = this |
| removePlayer(playerId) | boolean | Removes + sets player.team = null |
| getAverageLevel() | double | Computed |
| getWinRate() | double | Average of member win rates |
| getStrongestPlayer() | Player | Highest win rate |
| getSummary() | String | Reportable impl |
| getDetailedInfo() | String | Reportable impl |

### 5.7 MatchRecord.java
| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | concrete class |
| **Implements** | — |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| id | String | private | Unique ID, e.g. "m01" |
| teamA | Team | private | Association |
| teamB | Team | private | Association |
| result | MatchResult | private | WIN/LOSE/DRAW from teamA's perspective |
| matchDate | LocalDate | private | Match date |
| matchType | String | private | e.g. "Ranked", "Normal" |

| Method | Return | Description |
|--------|--------|-------------|
| MatchRecord(id, teamA, teamB, result, date, type) | — | Constructor |
| All getters | — | Standard |
| getWinner() | Team | Returns winning team (null if draw) |
| getLoser() | Team | Returns losing team (null if draw) |
| getMatchDateString() | String | Returns date in "YYYY-MM-DD" format |

---

## 6. Relationship Summary

```
                    ┌──────────────────┐
                    │     Person       │  abstract
                    │  (id, name, role)│
                    └────────┬─────────┘
                             │ inheritance
              ┌──────────────┼──────────────┐
              ▼                             ▼
     ┌────────────────┐           ┌────────────────┐
     │    Player      │           │     Admin      │
     │  level, winRate│           │ permissionLevel│
     │  heroes, team  │           │   password     │
     └───────┬────────┘           └────────────────┘
             │
             │ owns (composition: heroes die with player)
             ▼
     ┌────────────────┐           ┌────────────────┐
     │      Hero      │──────────▶│   Equipment    │
     │  hp, attack,   │ compatible│  attackBonus   │
     │  defense, speed│(assoc.)   │  defenseBonus  │
     └────────────────┘           │  rating        │
                                  └────────────────┘

     ┌────────────────┐
     │      Team      │
     │  name, players │◀────────── Player.team (aggregation)
     └───────┬────────┘
             │
             │ association
             ▼
     ┌────────────────┐
     │  MatchRecord   │
     │  teamA, teamB  │
     │  result, date  │
     └────────────────┘

     ┌────────────────┐
     │ «interface»    │
     │  Reportable    │
     │  getSummary()  │
     │  getDetailed() │
     └───────┬────────┘
             │ implements
      ┌──────┼──────┐
      ▼      ▼      ▼
   Player  Team   Hero
```

---

## 7. Service Layer Design

### 7.1 GameDataManager (Central Data Store)
```
Purpose: Single source of truth for all in-memory data

Fields:
  - List<Player> players
  - List<Admin> admins
  - List<Hero> heroes
  - List<Equipment> equipment
  - List<Team> teams
  - List<MatchRecord> matchRecords
  - Map<String, Player> playerById    // Fast lookup
  - Map<String, Hero> heroById
  - Map<String, Equipment> equipmentById
  - Map<String, Team> teamById

Key Methods:
  - initializeData()                    // Calls DataInitializer
  - getAllPlayers(): List<Player>
  - findPlayerById(id): Player
  - findPlayerByName(name): Player
  - CRUD methods for Admin: addPlayer(), deletePlayer(), updatePlayer(), etc.
```

### 7.2 SearchService
```
Purpose: Query operations visible to all users

Methods:
  - lookupPlayer(String query): String     // By ID or name, returns formatted info
  - lookupTeam(String query): String       // By ID or name
  - lookupHero(String name): String        // By name only
  - getMatchHistory(String teamOrPlayer, int limit): List<MatchRecord>
```

### 7.3 RankingService
```
Purpose: Statistics and leaderboards

Methods:
  - rankEquipment(): List<Equipment>       // By weighted formula
  - topPlayersByWinRate(int n): List<Player>
  - topPlayersByLevel(int n): List<Player>
  - topPlayersByMatches(int n): List<Player>
  - topPlayersByCustomScore(int n): List<Player>  // custom = winRate*0.5 + level*0.3 + totalMatches*0.2

Equipment Score Formula:
  score = usageCount * 0.4 + rating * 0.4 + compatibleHeroCount * 0.2

Tie-breaking (players):
  1. Main score (descending)
  2. Level (descending)
  3. Name (alphabetical ascending)
```

### 7.4 AuthenticationService
```
Purpose: Login/logout with role-based permissions

Fields:
  - Person currentUser   // null if not logged in; uses polymorphism
  - GameDataManager dataManager

Methods:
  - login(id, password): boolean
  - logout(): void
  - getCurrentUser(): Person
  - isLoggedIn(): boolean
  - isAdmin(): boolean
  - canEdit(): boolean      // true for Admin, limited for Player
  - canEditPlayer(pid): boolean  // Player can only edit self

Default accounts (created in DataInitializer):
  - Admin:  admin / admin123
  - Player: p001 / player123
```

### 7.5 AdminService
```
Purpose: Admin-only data management

Methods (all require admin authentication checked by caller):
  - addPlayer(Player): boolean
  - deletePlayer(String id): boolean
  - updatePlayer(String id, Player updated): boolean
  - addHero(Hero): boolean
  - deleteHero(String id): boolean
  - updateHero(String id, Hero updated): boolean
  - addEquipment(Equipment): boolean
  - deleteEquipment(String id): boolean
  - addTeam(Team): boolean
  - deleteTeam(String id): boolean
  - addMatchRecord(MatchRecord): boolean
  - deleteMatchRecord(String id): boolean

Rules:
  - Check for duplicate IDs before add
  - On delete Player: remove from team's player list
  - On delete Team: set all member players' team to null
  - On delete Hero: remove from all players' hero lists
  - Never crash on missing records; return false
```

---

## 8. Storage Layer Design

### 8.1 FileStorageService
```
Purpose: CSV persistence in data/ directory

Files created/read:
  - data/players.csv
  - data/admins.csv
  - data/heroes.csv
  - data/equipment.csv
  - data/teams.csv
  - data/matchrecords.csv

Methods:
  - saveAll(GameDataManager): boolean
  - loadAll(GameDataManager): boolean
  - savePlayers(List<Player>): void
  - loadPlayers(): List<Player>
  - (similar for all entity types)

CSV Format (see plan.md Section 7 for full field definitions):

  players.csv:
    id,name,role,level,winRate,totalMatches,teamId,heroIds,password

  heroes.csv:
    id,name,heroType,hp,attack,defense,speed,compatibleEquipmentIds

  equipment.csv:
    id,name,equipmentType,attackBonus,defenseBonus,hpBonus,speedBonus,rating,usageCount

  teams.csv:
    id,name,playerIds

  matchrecords.csv:
    id,teamAId,teamBId,result,matchDate,matchType

  admins.csv:
    id,name,role,permissionLevel,password

Error handling:
  - FileNotFoundException → log warning, return empty list, program continues
  - IOException → log error, return false, program continues
  - Never crash the application
```

---

## 9. Utility Classes

### 9.1 DataInitializer
```
Purpose: Create initial in-memory test dataset

Method:
  - initialize(GameDataManager): void

Data to create:
  - 3 Teams (e.g., "Dragon Warriors", "Phoenix Rise", "Shadow Reapers")
  - 15 Players (p001-p015), each with password = id + "123"
  - 1 Admin (admin / admin123)
  - 15 Heroes across all 6 HeroTypes
  - 20 Equipment items across all 5 EquipmentTypes
  - 10 MatchRecords between the 3 teams
  - Wire up: teams ↔ players, players ↔ heroes, heroes ↔ equipment
```

### 9.2 InputHelper
```
Purpose: Safe console input handling

Methods (all static):
  - readInt(prompt): int              // Loop until valid int
  - readInt(prompt, min, max): int    // With range check
  - readDouble(prompt): double
  - readString(prompt): String        // Non-empty string
  - readYesNo(prompt): boolean
  - pressEnterToContinue(): void

Behavior:
  - Never throws exceptions to caller
  - Prints error messages for invalid input
  - Loops until valid input received
```

---

## 10. Main.java Menu Structure

```
=== Honor of Kings Management System ===

After login, menu differs by role:

COMMON MENU (both roles):
  1. Player Lookup
  2. Team Overview
  3. Hero Details
  4. Equipment Statistics
  5. Match History
  6. Leaderboard
  7. Save Data
  8. Load Data
  9. Logout
  0. Exit

ADMIN-ONLY ADDITIONS (between 8 and 9):
  8. Admin Data Management → sub-menu for CRUD
  9. Save Data
  10. Load Data
  11. Logout
  0. Exit

PLAYER-ONLY:
  - Edit My Profile (in Player Lookup → self)

Main.java responsibilities:
  1. Display menu
  2. Read choice via InputHelper
  3. Route to appropriate Service method
  4. Display results

Main.java MUST NOT contain:
  - Any business logic
  - Any data manipulation
  - Any collection operations
  - Any file operations
```

---

## 11. Security & Constraints

| Rule | Enforcement |
|------|-------------|
| Admin-only CRUD | Checked in Main.java before calling AdminService |
| Player self-edit only | Checked in Main.java: "if editing self → allow; else → deny" |
| No duplicate IDs | Checked in GameDataManager before add |
| No crash on missing data | All find methods return null → caller checks |
| CSV load on startup | Optional; if file missing, use DataInitializer |
| No external libraries | JDK standard library only |

---

## 12. Compilation & Execution

```bash
# Compile all Java files
javac -d out src/hok/enums/*.java src/hok/interfacepkg/*.java src/hok/model/*.java src/hok/util/*.java src/hok/service/*.java src/hok/storage/*.java src/hok/Main.java

# Run
java -cp out hok.Main
```

Or if using a single command:
```bash
javac -d out $(find src -name "*.java") && java -cp out hok.Main
```

---

## 13. Recommendation Engine Design

### 13.1 Overview

The Recommendation Engine provides personalized hero and equipment suggestions based on multiple weighted factors. It analyzes existing game data (players, heroes, equipment, team compositions) to produce ranked, explainable recommendations.

**New files:**
- `src/hok/enums/RecommendationType.java`
- `src/hok/model/RecommendationResult.java`
- `src/hok/service/RecommendationEngine.java`

### 13.2 RecommendationType Enum

```java
package hok.enums;

public enum RecommendationType {
    HERO,       // Recommending a hero
    EQUIPMENT   // Recommending equipment
}
```

### 13.3 RecommendationResult (DTO)

| | |
|---|---|
| **Package** | `hok.model` |
| **Type** | concrete class (Data Transfer Object) |

| Field | Type | Access | Description |
|-------|------|--------|-------------|
| recommendedId | String | private | ID of the recommended item (hero ID or equipment ID) |
| recommendedName | String | private | Display name |
| type | RecommendationType | private | HERO or EQUIPMENT |
| confidence | double | private | Score 0.0–1.0, higher = stronger recommendation |
| reason | String | private | Human-readable explanation |
| supportingStats | Map\<String, Double\> | private | Raw factor scores for transparency |

| Method | Return | Description |
|--------|--------|-------------|
| RecommendationResult(id, name, type, confidence, reason, stats) | — | Constructor |
| getRecommendedId() | String | |
| getRecommendedName() | String | |
| getType() | RecommendationType | |
| getConfidence() | double | |
| getReason() | String | |
| getSupportingStats() | Map\<String, Double\> | |
| toString() | String | Formatted: "Name (Confidence: X%) — Reason" |

### 13.4 RecommendationEngine

| | |
|---|---|
| **Package** | `hok.service` |
| **Dependency** | GameDataManager (read-only) |

**Fields:**
- `dm: GameDataManager` — read-only data source

**Public Methods:**

| Method | Return | Description |
|--------|--------|-------------|
| RecommendationEngine(dm) | — | Constructor |
| recommendHeroesForPlayer(playerId, count) | List\<RecommendationResult\> | Top-N hero recommendations for a specific player |
| recommendEquipmentForPlayer(playerId, count) | List\<RecommendationResult\> | Top-N equipment recommendations for a player |
| recommendHeroByType(heroType, count) | List\<RecommendationResult\> | Top-N heroes of a specific type (general, not player-specific) |
| recommendEquipmentByHeroType(heroType, count) | List\<RecommendationResult\> | Top-N equipment compatible with a given hero type |

**Private Methods:**

| Method | Return | Description |
|--------|--------|-------------|
| computeHeroScore(hero, player) | double | Weighted score for hero recommendation |
| computeEquipmentScore(eq, player) | double | Weighted score for equipment recommendation |
| generateHeroReason(hero, score, stats) | String | Human-readable reason for hero rec |
| generateEquipmentReason(eq, score, stats) | String | Human-readable reason for equipment rec |
| normalize(value, min, max) | double | Min-max normalization to 0.0–1.0 |

### 13.5 Recommendation Algorithm — Hero

**Formula:**

```
heroScore = typeMatch      * 0.30
          + winRateFactor  * 0.25
          + popularity     * 0.20
          + teamSynergy    * 0.15
          + levelMatch     * 0.10
```

**Factor Definitions:**

| Factor | Weight | Computation | Rationale |
|--------|--------|-------------|-----------|
| **typeMatch** | 0.30 | 1.0 if the hero's type is NOT already well-represented in the player's hero pool; 0.5 if partially covered; 0.0 if fully covered | Encourages type diversity |
| **winRateFactor** | 0.25 | Average win rate of all players who own this hero, normalized to 0.0–1.0 | Favors strong-performing heroes |
| **popularity** | 0.20 | `countOfPlayersOwningHero / totalPlayers`, normalized | Favors commonly-used heroes |
| **teamSynergy** | 0.15 | 1.0 if the hero's type complements the player's team composition; lower if redundant | Favors team balance across HeroTypes |
| **levelMatch** | 0.10 | `1.0 - abs(playerLevel - avgOwnerLevel) / 30.0` | Avoids recommending heroes too far above/below the player's experience level |

**Tie-breaking:** confidence descending → hero name ascending

### 13.6 Recommendation Algorithm — Equipment

**Formula:**

```
equipmentScore = heroCompatibility * 0.30
               + usageFactor        * 0.25
               + ratingFactor       * 0.25
               + typeSynergy        * 0.20
```

**Factor Definitions:**

| Factor | Weight | Computation | Rationale |
|--------|--------|-------------|-----------|
| **heroCompatibility** | 0.30 | `countOfPlayerHeroesCompatibleWithThisEquipment / countOfPlayerHeroes` | How many of the player's heroes can use this equipment |
| **usageFactor** | 0.25 | `equipment.usageCount / maxUsageCount`, normalized | Favors popular, battle-tested equipment |
| **ratingFactor** | 0.25 | `equipment.rating / 10.0`, normalized | Favors highly-rated equipment |
| **typeSynergy** | 0.20 | 1.0 if the equipment type matches the dominant HeroType in the player's hero pool; 0.5 for neutral; 0.0 for poor match | E.g., ATTACK equipment for a player with mostly MARKSMAN heroes |

**Tie-breaking:** confidence descending → equipment name ascending

### 13.7 Reason Generation

Each recommendation includes a human-readable reason string. Examples:

- **Hero:** "Diao Chan (Confidence: 78%) — Adds MAGE diversity to your team (TANK-heavy). Strong win rate (65%) among owners. Recommended for level 22 players."
- **Equipment:** "Infinity Blade (Confidence: 85%) — Compatible with 3 of your 4 heroes. High usage (120 uses) and rating (9.5/10). Matches your ATTACK-focused hero pool."

### 13.8 Integration

**GameDataManager additions (no changes needed):**
- The engine uses existing `getAllPlayers()`, `getAllHeroes()`, `getAllEquipment()`, `findPlayersOwningHero()` — all already implemented.

**Main.java menu update:**
```
COMMON MENU (both roles):
  ...
  4.5  Recommendation Engine  ← NEW
       ├── 1. Recommend Heroes for Me
       ├── 2. Recommend Equipment for Me
       ├── 3. Recommend Heroes by Type
       └── 4. Recommend Equipment by Hero Type
  ...
```

**Call flow:**
1. Main.java → `recommendationEngine.recommendHeroesForPlayer(currentUserId, 5)`
2. Engine reads all players/heroes/equipment from GameDataManager
3. Scores each candidate hero (excluding already-owned)
4. Sorts by confidence descending, returns top-N
5. Main.java displays formatted results with reasons

### 13.9 Constraints

| Rule | Detail |
|------|--------|
| No self-recommendation | Never recommend a hero the player already owns |
| Read-only | Engine never modifies GameDataManager state |
| Graceful degradation | If player not found, return empty list (no crash) |
| Transparent | Every recommendation includes factor breakdown in supportingStats |
| Explainable | Every recommendation includes a plain-English reason string |
