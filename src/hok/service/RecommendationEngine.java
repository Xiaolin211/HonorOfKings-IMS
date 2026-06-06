package hok.service;

import hok.enums.EquipmentType;
import hok.enums.HeroType;
import hok.enums.RecommendationType;
import hok.model.Equipment;
import hok.model.Hero;
import hok.model.Player;
import hok.model.RecommendationResult;
import hok.model.Team;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI-powered recommendation engine for heroes and equipment.
 * Uses weighted multi-factor scoring to produce ranked, explainable recommendations.
 *
 * Algorithm: Rank every candidate (hero not yet owned, or all equipment)
 * by a composite score, then return the top-N with supporting factor breakdowns.
 *
 * Read-only: never modifies GameDataManager state.
 */
public class RecommendationEngine {

    private GameDataManager dm;

    // ==================== Hero Recommendation Weights ====================
    private static final double W_TYPE_MATCH    = 0.30;
    private static final double W_WIN_RATE      = 0.25;
    private static final double W_POPULARITY    = 0.20;
    private static final double W_TEAM_SYNERGY  = 0.15;
    private static final double W_LEVEL_MATCH   = 0.10;

    // ==================== Equipment Recommendation Weights ====================
    private static final double EW_HERO_COMPAT  = 0.30;
    private static final double EW_USAGE        = 0.25;
    private static final double EW_RATING       = 0.25;
    private static final double EW_TYPE_SYNERGY = 0.20;

    public RecommendationEngine(GameDataManager dm) {
        this.dm = dm;
    }

    // ==================== PUBLIC API ====================

    /**
     * Recommends top-N heroes for a specific player.
     * Excludes heroes the player already owns.
     */
    public List<RecommendationResult> recommendHeroesForPlayer(String playerId, int count) {
        Player player = dm.findPlayerById(playerId);
        if (player == null) return Collections.emptyList();

        List<Hero> allHeroes = dm.getAllHeroes();
        List<Hero> owned = player.getHeroes();

        // Only consider not-yet-owned heroes
        List<RecommendationResult> results = new ArrayList<>();
        for (Hero hero : allHeroes) {
            if (owned.contains(hero)) continue;   // skip already owned
            double score = computeHeroScore(hero, player);
            Map<String, Double> stats = computeHeroStatBreakdown(hero, player);
            String reason = generateHeroReason(hero, score, stats, player);
            results.add(new RecommendationResult(
                    hero.getId(), hero.getName(),
                    RecommendationType.HERO, score, reason, stats));
        }

        return topN(results, count);
    }

    /**
     * Recommends top-N equipment for a specific player.
     */
    public List<RecommendationResult> recommendEquipmentForPlayer(String playerId, int count) {
        Player player = dm.findPlayerById(playerId);
        if (player == null) return Collections.emptyList();

        List<Equipment> allEq = dm.getAllEquipment();
        List<RecommendationResult> results = new ArrayList<>();

        for (Equipment eq : allEq) {
            double score = computeEquipmentScore(eq, player);
            Map<String, Double> stats = computeEquipmentStatBreakdown(eq, player);
            String reason = generateEquipmentReason(eq, score, stats, player);
            results.add(new RecommendationResult(
                    eq.getId(), eq.getName(),
                    RecommendationType.EQUIPMENT, score, reason, stats));
        }

        return topN(results, count);
    }

    /**
     * Recommends top-N heroes of a specific type (not player-specific).
     * Considers all players for win-rate and popularity.
     */
    public List<RecommendationResult> recommendHeroByType(HeroType heroType, int count) {
        List<Hero> allHeroes = dm.getAllHeroes();
        List<RecommendationResult> results = new ArrayList<>();

        for (Hero hero : allHeroes) {
            if (hero.getHeroType() != heroType) continue;

            // Use global stats (not player-specific)
            double typeBonus = 1.0;  // exact type match
            double winRate = computeGlobalWinRate(hero);
            double popularity = normalize(ownersOf(hero), 0, dm.getAllPlayers().size());
            // no team synergy or level match for general queries
            double score = typeBonus * W_TYPE_MATCH
                         + winRate * W_WIN_RATE
                         + popularity * W_POPULARITY;

            Map<String, Double> stats = new LinkedHashMap<>();
            stats.put("TypeMatch", typeBonus);
            stats.put("WinRate", winRate);
            stats.put("Popularity", popularity);

            String reason = String.format(
                    "%s hero. Win rate: %.0f%%. Used by %d players.",
                    hero.getHeroType(), winRate * 100, ownersOf(hero));

            results.add(new RecommendationResult(
                    hero.getId(), hero.getName(),
                    RecommendationType.HERO, score, reason, stats));
        }

        return topN(results, count);
    }

    /**
     * Recommends top-N equipment compatible with heroes of a given type.
     */
    public List<RecommendationResult> recommendEquipmentByHeroType(HeroType heroType, int count) {
        List<Equipment> allEq = dm.getAllEquipment();
        List<Hero> allHeroes = dm.getAllHeroes();
        List<RecommendationResult> results = new ArrayList<>();

        // Count how many heroes of the given type are compatible with each equipment
        int typeHeroCount = 0;
        for (Hero h : allHeroes) {
            if (h.getHeroType() == heroType) typeHeroCount++;
        }
        if (typeHeroCount == 0) return results;

        for (Equipment eq : allEq) {
            int compatCount = 0;
            for (Hero h : allHeroes) {
                if (h.getHeroType() == heroType
                        && h.getCompatibleEquipment().contains(eq)) {
                    compatCount++;
                }
            }
            double compat = normalize(compatCount, 0, typeHeroCount);
            double usage = normalize(eq.getUsageCount(), 0, maxUsage());
            double rating = eq.getRating() / 10.0;
            double score = compat * EW_HERO_COMPAT
                         + usage * EW_USAGE
                         + rating * EW_RATING;

            Map<String, Double> stats = new LinkedHashMap<>();
            stats.put("HeroCompat", compat);
            stats.put("Usage", usage);
            stats.put("Rating", rating);

            String reason = String.format(
                    "Compatible with %d/%d %s heroes. Usage: %d. Rating: %.1f/10.",
                    compatCount, typeHeroCount, heroType, eq.getUsageCount(), eq.getRating());

            results.add(new RecommendationResult(
                    eq.getId(), eq.getName(),
                    RecommendationType.EQUIPMENT, score, reason, stats));
        }

        return topN(results, count);
    }

    // ==================== HERO SCORING ====================

    private double computeHeroScore(Hero hero, Player player) {
        double typeMatch   = computeTypeMatch(hero, player);
        double winRate     = computeGlobalWinRate(hero);
        double popularity  = normalize(ownersOf(hero), 0, dm.getAllPlayers().size());
        double teamSynergy = computeTeamSynergy(hero, player);
        double levelMatch  = computeLevelMatch(hero, player);

        return typeMatch * W_TYPE_MATCH
             + winRate * W_WIN_RATE
             + popularity * W_POPULARITY
             + teamSynergy * W_TEAM_SYNERGY
             + levelMatch * W_LEVEL_MATCH;
    }

    private Map<String, Double> computeHeroStatBreakdown(Hero hero, Player player) {
        Map<String, Double> stats = new LinkedHashMap<>();
        stats.put("TypeMatch", computeTypeMatch(hero, player));
        stats.put("WinRate", computeGlobalWinRate(hero));
        stats.put("Popularity", normalize(ownersOf(hero), 0, dm.getAllPlayers().size()));
        stats.put("TeamSynergy", computeTeamSynergy(hero, player));
        stats.put("LevelMatch", computeLevelMatch(hero, player));
        return stats;
    }

    /**
     * TypeMatch: how well the hero's type fills a gap in the player's hero pool.
     * 1.0 = type not yet owned → high recommendation
     * 0.5 = type partially covered (1 hero of that type)
     * 0.0 = type well-covered (2+ heroes of that type)
     */
    private double computeTypeMatch(Hero hero, Player player) {
        HeroType targetType = hero.getHeroType();
        int ownedOfType = 0;
        for (Hero h : player.getHeroes()) {
            if (h.getHeroType() == targetType) ownedOfType++;
        }
        if (ownedOfType == 0) return 1.0;
        if (ownedOfType == 1) return 0.5;
        return 0.0;
    }

    /**
     * Global win rate: average win rate of all players who own this hero.
     */
    private double computeGlobalWinRate(Hero hero) {
        List<Player> owners = dm.findPlayersOwningHero(hero.getId());
        if (owners.isEmpty()) return 0.5; // neutral default
        double total = 0;
        for (Player p : owners) {
            total += p.getWinRate();
        }
        return total / owners.size();
    }

    /**
     * TeamSynergy: how well the hero's type complements the player's TEAM composition.
     * Scans all teammates' hero pools for type diversity.
     * 1.0 = this hero type is underrepresented across the entire team
     */
    private double computeTeamSynergy(Hero hero, Player player) {
        Team team = player.getTeam();
        if (team == null) return 0.5; // no team → neutral

        HeroType targetType = hero.getHeroType();
        int teamOwnedOfType = 0;
        int totalTeamHeroes = 0;

        for (Player teammate : team.getPlayers()) {
            for (Hero h : teammate.getHeroes()) {
                totalTeamHeroes++;
                if (h.getHeroType() == targetType) teamOwnedOfType++;
            }
        }

        if (totalTeamHeroes == 0) return 0.5;
        double ratio = (double) teamOwnedOfType / totalTeamHeroes;
        // Lower ratio = this type is underrepresented = higher synergy score
        return 1.0 - Math.min(ratio * 6.0, 1.0); // *6 because 6 hero types; cap at 1.0
    }

    /**
     * LevelMatch: how appropriate the hero is for the player's experience level.
     * Uses average owner level as a proxy for hero difficulty.
     * 1.0 = the hero's typical owner level matches the player's level.
     */
    private double computeLevelMatch(Hero hero, Player player) {
        List<Player> owners = dm.findPlayersOwningHero(hero.getId());
        if (owners.isEmpty()) return 0.5;
        double avgLevel = 0;
        for (Player p : owners) avgLevel += p.getLevel();
        avgLevel /= owners.size();
        return 1.0 - Math.min(Math.abs(player.getLevel() - avgLevel) / 30.0, 1.0);
    }

    // ==================== EQUIPMENT SCORING ====================

    private double computeEquipmentScore(Equipment eq, Player player) {
        double heroCompat  = computeHeroCompatibility(eq, player);
        double usage       = normalize(eq.getUsageCount(), 0, maxUsage());
        double rating      = eq.getRating() / 10.0;
        double typeSynergy = computeEquipmentTypeSynergy(eq, player);

        return heroCompat * EW_HERO_COMPAT
             + usage * EW_USAGE
             + rating * EW_RATING
             + typeSynergy * EW_TYPE_SYNERGY;
    }

    private Map<String, Double> computeEquipmentStatBreakdown(Equipment eq, Player player) {
        Map<String, Double> stats = new LinkedHashMap<>();
        stats.put("HeroCompat", computeHeroCompatibility(eq, player));
        stats.put("Usage", normalize(eq.getUsageCount(), 0, maxUsage()));
        stats.put("Rating", eq.getRating() / 10.0);
        stats.put("TypeSynergy", computeEquipmentTypeSynergy(eq, player));
        return stats;
    }

    /**
     * HeroCompatibility: what fraction of the player's heroes can use this equipment.
     */
    private double computeHeroCompatibility(Equipment eq, Player player) {
        List<Hero> playerHeroes = player.getHeroes();
        if (playerHeroes.isEmpty()) return 0.0;
        int compat = 0;
        for (Hero h : playerHeroes) {
            if (h.getCompatibleEquipment().contains(eq)) compat++;
        }
        return (double) compat / playerHeroes.size();
    }

    /**
     * TypeSynergy: does the equipment type match the dominant hero type in the player's pool?
     */
    private double computeEquipmentTypeSynergy(Equipment eq, Player player) {
        List<Hero> playerHeroes = player.getHeroes();
        if (playerHeroes.isEmpty()) return 0.5;

        // Find the most common HeroType in the player's pool
        Map<HeroType, Integer> typeCount = new HashMap<>();
        for (Hero h : playerHeroes) {
            typeCount.put(h.getHeroType(), typeCount.getOrDefault(h.getHeroType(), 0) + 1);
        }
        HeroType dominant = null;
        int maxCount = 0;
        for (Map.Entry<HeroType, Integer> e : typeCount.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                dominant = e.getKey();
            }
        }

        // Map equipment type to preferred hero types
        EquipmentType et = eq.getEquipmentType();
        if (dominant == null) return 0.5;

        switch (et) {
            case ATTACK:   return (dominant == HeroType.MARKSMAN || dominant == HeroType.ASSASSIN || dominant == HeroType.WARRIOR) ? 1.0 : 0.5;
            case DEFENSE:  return (dominant == HeroType.TANK || dominant == HeroType.WARRIOR) ? 1.0 : 0.5;
            case MAGIC:    return (dominant == HeroType.MAGE) ? 1.0 : 0.3;
            case MOVEMENT: return 0.7;  // universally useful
            case JUNGLE:   return (dominant == HeroType.ASSASSIN || dominant == HeroType.WARRIOR) ? 1.0 : 0.3;
            default:       return 0.5;
        }
    }

    // ==================== REASON GENERATION ====================

    private String generateHeroReason(Hero hero, double score, Map<String, Double> stats, Player player) {
        double typeMatch = stats.getOrDefault("TypeMatch", 0.0);
        double wr = stats.getOrDefault("WinRate", 0.0);

        String typeMsg;
        if (typeMatch >= 1.0) typeMsg = "adds new " + hero.getHeroType() + " type to your pool";
        else if (typeMatch >= 0.5) typeMsg = hero.getHeroType() + " type lightly covered";
        else typeMsg = hero.getHeroType() + " type already well-covered";

        return String.format("%s (%s). Win rate: %.0f%%. Good for level ~%d.",
                typeMsg, hero.getHeroType(),
                wr * 100, (int)avgOwnerLevel(hero));
    }

    private String generateEquipmentReason(Equipment eq, double score,
                                           Map<String, Double> stats, Player player) {
        double compat = stats.getOrDefault("HeroCompat", 0.0);
        return String.format("%s item. Compatible with %d/%d of your heroes. Rating: %.1f/10.",
                eq.getEquipmentType(),
                (int)(compat * player.getHeroes().size()),
                player.getHeroes().size(),
                eq.getRating());
    }

    // ==================== HELPERS ====================

    private int ownersOf(Hero hero) {
        return dm.findPlayersOwningHero(hero.getId()).size();
    }

    private int maxUsage() {
        int max = 1;
        for (Equipment e : dm.getAllEquipment()) {
            if (e.getUsageCount() > max) max = e.getUsageCount();
        }
        return max;
    }

    private double avgOwnerLevel(Hero hero) {
        List<Player> owners = dm.findPlayersOwningHero(hero.getId());
        if (owners.isEmpty()) return 1;
        double sum = 0;
        for (Player p : owners) sum += p.getLevel();
        return sum / owners.size();
    }

    /**
     * Min-max normalization to [0.0, 1.0].
     * If min == max, returns 0.5 (neutral).
     */
    private double normalize(double value, double min, double max) {
        if (max == min) return 0.5;
        return (value - min) / (max - min);
    }

    /**
     * Sorts results by confidence descending, then by name ascending.
     * Returns the top count.
     */
    private List<RecommendationResult> topN(List<RecommendationResult> results, int count) {
        results.sort((a, b) -> {
            int cmp = Double.compare(b.getConfidence(), a.getConfidence());
            if (cmp != 0) return cmp;
            return a.getRecommendedName().compareTo(b.getRecommendedName());
        });
        return results.subList(0, Math.min(count, results.size()));
    }
}
