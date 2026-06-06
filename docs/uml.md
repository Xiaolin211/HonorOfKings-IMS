# UML Diagrams: AI-Assisted Information Management System for Honor of Kings

---

## 1. Full Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              <<abstract>>                                           │
│                                Person                                               │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  - id: String                                                                       │
│  - name: String                                                                     │
│  - role: Role                                                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  + Person(id: String, name: String, role: Role)                                     │
│  + getId(): String                                                                  │
│  + getName(): String                                                                │
│  + getRole(): Role                                                                  │
│  + setName(name: String): void                                                      │
│  + setRole(role: Role): void                                                        │
│  + toString(): String                                                               │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                          △
                                          │  extends
                          ┌───────────────┴───────────────┐
                          │                               │
┌─────────────────────────────────────────┐  ┌─────────────────────────────────────────┐
│                Player                   │  │                 Admin                    │
├─────────────────────────────────────────┤  ├─────────────────────────────────────────┤
│  - level: int                           │  │  - permissionLevel: int                 │
│  - winRate: double                      │  │  - password: String                     │
│  - totalMatches: int                    │  ├─────────────────────────────────────────┤
│  - heroes: List<Hero>                   │  │  + Admin(id, name, password)            │
│  - team: Team                           │  │  + getPermissionLevel(): int            │
│  - password: String                     │  │  + getPassword(): String                │
├─────────────────────────────────────────┤  │  + setPermissionLevel(l: int): void     │
│  + Player(id, name, password)           │  │  + setPassword(pw: String): void        │
│  + getLevel(): int                      │  └─────────────────────────────────────────┘
│  + getWinRate(): double                 │
│  + getTotalMatches(): int               │
│  + getHeroes(): List<Hero>              │
│  + getTeam(): Team                      │
│  + getPassword(): String                │
│  + setLevel(l: int): void               │
│  + setWinRate(r: double): void          │
│  + setTotalMatches(n: int): void        │
│  + setTeam(t: Team): void               │
│  + setPassword(pw: String): void        │
│  + addHero(h: Hero): void               │
│  + removeHero(heroId: String): boolean  │
│  + getSummary(): String   «Reportable»  │
│  + getDetailedInfo(): String «Reportable│
└───────────────┬─────────────────────────┘
                │                          owns (composition)
                │ 1..*                     ◆
                ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                      Hero                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  - id: String                                                                       │
│  - name: String                                                                     │
│  - heroType: HeroType                                                               │
│  - hp: int                                                                          │
│  - attack: int                                                                      │
│  - defense: int                                                                     │
│  - speed: int                                                                       │
│  - compatibleEquipment: List<Equipment>                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  + Hero(id, name, heroType, hp, attack, defense, speed)                             │
│  + getId(): String                                                                  │
│  + getName(): String                                                                │
│  + getHeroType(): HeroType                                                          │
│  + getHp(): int                                                                     │
│  + getAttack(): int                                                                 │
│  + getDefense(): int                                                                │
│  + getSpeed(): int                                                                  │
│  + getCompatibleEquipment(): List<Equipment>                                        │
│  + addCompatibleEquipment(eq: Equipment): void                                      │
│  + removeCompatibleEquipment(eqId: String): boolean                                 │
│  + getSummary(): String         «Reportable»                                        │
│  + getDetailedInfo(): String    «Reportable»                                        │
└───────────────────────────────┬─────────────────────────────────────────────────────┘
                                │  compatible with (association)
                                │  0..*
                                ▽
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                   Equipment                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  - id: String                                                                       │
│  - name: String                                                                     │
│  - equipmentType: EquipmentType                                                     │
│  - attackBonus: int                                                                 │
│  - defenseBonus: int                                                                │
│  - hpBonus: int                                                                     │
│  - speedBonus: int                                                                  │
│  - rating: double                                                                   │
│  - usageCount: int                                                                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  + Equipment(id, name, type, aB, dB, hB, sB, rating)                                │
│  + getId(): String                                                                  │
│  + getName(): String                                                                │
│  + getEquipmentType(): EquipmentType                                                │
│  + getAttackBonus(): int                                                            │
│  + getDefenseBonus(): int                                                           │
│  + getHpBonus(): int                                                                │
│  + getSpeedBonus(): int                                                             │
│  + getRating(): double                                                              │
│  + getUsageCount(): int                                                             │
│  + setRating(r: double): void                                                       │
│  + incrementUsage(): void                                                           │
│  + getTotalBonus(): int                                                             │
└─────────────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                      Team                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  - id: String                                                                       │
│  - name: String                                                                     │
│  - players: List<Player>                                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  + Team(id: String, name: String)                                                   │
│  + getId(): String                                                                  │
│  + getName(): String                                                                │
│  + getPlayers(): List<Player>                                                       │
│  + setName(name: String): void                                                      │
│  + addPlayer(p: Player): void          // also sets p.team = this                   │
│  + removePlayer(playerId: String): boolean  // also sets p.team = null              │
│  + getAverageLevel(): double                                                         │
│  + getWinRate(): double                                                              │
│  + getStrongestPlayer(): Player                                                     │
│  + getSummary(): String          «Reportable»                                       │
│  + getDetailedInfo(): String     «Reportable»                                       │
└───────────────────────────────┬─────────────────────────────────────────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │ 1             │               │ 1
                ▽               │               ▽
┌───────────────────────────────┼─────────────────────────────────────────────────────┐
│                        MatchRecord                │                                  │
├───────────────────────────────────────────────────┼──────────────────────────────────┤
│  - id: String                                     │                                  │
│  - teamA: Team                                    │  association                     │
│  - teamB: Team                                    │                                  │
│  - result: MatchResult                            │                                  │
│  - matchDate: LocalDate                           │                                  │
│  - matchType: String                              │                                  │
├───────────────────────────────────────────────────┼──────────────────────────────────┤
│  + MatchRecord(id, teamA, teamB, result, date, type)                                 │
│  + getId(): String                                                                  │
│  + getTeamA(): Team                                                                  │
│  + getTeamB(): Team                                                                  │
│  + getResult(): MatchResult                                                          │
│  + getMatchDate(): LocalDate                                                        │
│  + getMatchDateString(): String                                                     │
│  + getMatchType(): String                                                            │
│  + getWinner(): Team          // null if draw                                       │
│  + getLoser(): Team           // null if draw                                       │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Interface Diagram

```
┌─────────────────────────────────┐
│       «interface»               │
│        Reportable               │
├─────────────────────────────────┤
│  + getSummary(): String         │
│  + getDetailedInfo(): String    │
└───────────────┬─────────────────┘
                │  implements
        ┌───────┼───────┐
        ▽       ▽       ▽
     Player   Team    Hero
```

---

## 3. Enum Diagram

```
┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│   <<enum>>       │   │   <<enum>>       │   │   <<enum>>       │   │   <<enum>>       │
│     Role         │   │   HeroType       │   │  EquipmentType   │   │  MatchResult     │
├──────────────────┤   ├──────────────────┤   ├──────────────────┤   ├──────────────────┤
│ PLAYER           │   │ TANK             │   │ ATTACK           │   │ WIN              │
│ ADMIN            │   │ WARRIOR          │   │ DEFENSE          │   │ LOSE             │
│                  │   │ ASSASSIN         │   │ MAGIC            │   │ DRAW             │
│                  │   │ MAGE             │   │ MOVEMENT         │   │                  │
│                  │   │ MARKSMAN         │   │ JUNGLE           │   │                  │
│                  │   │ SUPPORT          │   │                  │   │                  │
└──────────────────┘   └──────────────────┘   └──────────────────┘   └──────────────────┘
```

---

## 4. Service Layer Dependency Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                             Main.java                                │
│  (menu routing, no business logic)                                   │
└───┬──────────┬──────────┬──────────┬──────────┬─────────────────────┘
    │          │          │          │          │
    ▽          ▽          ▽          ▽          ▽
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────────────────┐
│ Auth   │ │ Search │ │Ranking │ │ Admin  │ │ FileStorage          │
│Service │ │Service │ │Service │ │Service │ │Service               │
└───┬────┘ └───┬────┘ └───┬────┘ └───┬────┘ └──────────┬───────────┘
    │          │          │          │                   │
    └──────────┴──────────┴──────────┴───────────────────┘
                         │
                         ▽
                ┌─────────────────┐
                │ GameDataManager │
                │  (data store)   │
                └────────┬────────┘
                         │
                         ▽
                ┌─────────────────┐
                │ DataInitializer │
                │  (startup only) │
                └─────────────────┘
```

**Dependency rule**: All services depend on GameDataManager. No service depends on another service.

---

## 5. Authentication Flow (Sequence Diagram)

```
  User          Main.java      AuthService     GameDataManager
   │                │               │                │
   │  select Login  │               │                │
   │───────────────>│               │                │
   │                │  login(id,pw) │                │
   │                │──────────────>│                │
   │                │               │ findPlayer(id) │
   │                │               │───────────────>│
   │                │               │   Player obj   │
   │                │               │<───────────────│
   │                │               │                │
   │                │               │ check password │
   │                │               │────┐           │
   │                │               │    │ compare   │
   │                │               │<───┘           │
   │                │               │                │
   │                │  true/false   │                │
   │                │<──────────────│                │
   │                │               │                │
   │  "Login OK"    │               │                │
   │  or "Failed"   │               │                │
   │<───────────────│               │                │
```

---

## 6. Data Save/Load Sequence

```
  User       Main.java    FileStorageService    data/*.csv
   │             │                │                  │
   │ Save Data   │                │                  │
   │────────────>│                │                  │
   │             │ saveAll(dm)    │                  │
   │             │───────────────>│                  │
   │             │                │ write players    │
   │             │                │─────────────────>│
   │             │                │ write heroes     │
   │             │                │─────────────────>│
   │             │                │ write equipment  │
   │             │                │─────────────────>│
   │             │                │ write teams      │
   │             │                │─────────────────>│
   │             │                │ write matches    │
   │             │                │─────────────────>│
   │             │   success      │                  │
   │             │<───────────────│                  │
   │  "Saved!"   │                │                  │
   │<────────────│                │                  │
```

---

## 7. Relationship Legend

| Symbol | Meaning | Example |
|--------|---------|---------|
| `△` (solid triangle) | Inheritance (extends) | Player → Person |
| `◁` (dashed triangle) | Implementation (implements) | Player → Reportable |
| `◆` (filled diamond) | Composition (owns) | Player ◆→ Hero |
| `◇` (empty diamond) | Aggregation (has) | Team ◇→ Player |
| `→` (solid arrow) | Association (uses) | Hero → Equipment |
| `1` | Exactly one | Team 1 — Player 1..* |
| `0..*` | Zero or many | Hero 0..* — Equipment 0..* |
| `1..*` | One or many | Team 1..* — Player |

---

## 8. Recommendation Engine — New Classes

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                          <<enum>>                                                   │
│                      RecommendationType                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  HERO                                                                               │
│  EQUIPMENT                                                                          │
└─────────────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              RecommendationResult                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  - recommendedId: String                                                            │
│  - recommendedName: String                                                          │
│  - type: RecommendationType                                                         │
│  - confidence: double                                                               │
│  - reason: String                                                                   │
│  - supportingStats: Map<String, Double>                                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  + RecommendationResult(recId, recName, type, confidence, reason, stats)            │
│  + getRecommendedId(): String                                                       │
│  + getRecommendedName(): String                                                     │
│  + getType(): RecommendationType                                                    │
│  + getConfidence(): double                                                          │
│  + getReason(): String                                                              │
│  + getSupportingStats(): Map<String, Double>                                        │
│  + toString(): String                                                               │
└─────────────────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                            RecommendationEngine                                     │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  - dm: GameDataManager                                                              │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  + RecommendationEngine(dm: GameDataManager)                                        │
│  + recommendHeroesForPlayer(playerId: String, count: int): List<RecommendationResult│
│  + recommendEquipmentForPlayer(playerId: String, count: int): List<RecommendationRe.│
│  + recommendHeroByType(heroType: HeroType, count: int): List<RecommendationResult>  │
│  + recommendEquipmentByHeroType(heroType: HeroType, count: int): List<Recommendatio.│
│  - computeHeroScore(hero: Hero, player: Player): double                             │
│  - computeEquipmentScore(eq: Equipment, player: Player): double                     │
│  - generateHeroReason(hero: Hero, score: double, stats: Map): String                │
│  - generateEquipmentReason(eq: Equipment, score: double, stats: Map): String        │
│  - normalize(value: double, min: double, max: double): double                       │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 9. Recommendation Engine Dependency Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                             Main.java                                │
│  handleRecommendation() → RecommendationEngine                       │
└───┬──────────────────────────────────────────────────────────────────┘
    │
    ▽
┌──────────────────────────────────────────────────────────────────────┐
│                      RecommendationEngine                            │
│  Uses: GameDataManager (read-only)                                   │
│  Produces: List<RecommendationResult>                                │
└───┬──────────────────────────────────────────────────────────────────┘
    │
    │ reads
    ▽
┌──────────────────────────────────────────────────────────────────────┐
│                      GameDataManager                                 │
│  getAllPlayers(), getAllHeroes(), getAllEquipment()                  │
│  findPlayersOwningHero(), findHeroByName()                           │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 10. Recommendation Flow (Sequence Diagram)

```
  User          Main.java        RecEngine       GameDataManager
   │                │                │                │
   │ Recommend Hero │                │                │
   │───────────────>│                │                │
   │                │ recommendHero  │                │
   │                │───────────────>│                │
   │                │                │ getAllPlayers()│
   │                │                │───────────────>│
   │                │                │   players      │
   │                │                │<───────────────│
   │                │                │                │
   │                │                │ getAllHeroes() │
   │                │                │───────────────>│
   │                │                │   heroes       │
   │                │                │<───────────────│
   │                │                │                │
   │                │                │ [for each hero │
   │                │                │  not owned]:   │
   │                │                │ computeScore() │
   │                │                │ generateReason │
   │                │                │────┐           │
   │                │                │    │ sort by   │
   │                │                │    │ score desc│
   │                │                │<───┘           │
   │                │                │                │
   │                │  top N results │                │
   │                │<───────────────│                │
   │                │                │                │
   │  formatted     │                │                │
   │  recommendations                │                │
   │<───────────────│                │                │
```
