package hok.model;

import hok.enums.RecommendationType;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object for a single recommendation result.
 * Holds the recommended item, confidence score, human-readable reason,
 * and a breakdown of contributing factor scores for transparency.
 */
public class RecommendationResult {
    private String recommendedId;
    private String recommendedName;
    private RecommendationType type;
    private double confidence;
    private String reason;
    private Map<String, Double> supportingStats;

    public RecommendationResult(String recommendedId, String recommendedName,
                                RecommendationType type, double confidence,
                                String reason, Map<String, Double> supportingStats) {
        this.recommendedId = recommendedId;
        this.recommendedName = recommendedName;
        this.type = type;
        this.confidence = confidence;
        this.reason = reason;
        this.supportingStats = supportingStats;
    }

    public String getRecommendedId() {
        return recommendedId;
    }

    public String getRecommendedName() {
        return recommendedName;
    }

    public RecommendationType getType() {
        return type;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getReason() {
        return reason;
    }

    public Map<String, Double> getSupportingStats() {
        return new HashMap<>(supportingStats);
    }

    @Override
    public String toString() {
        return String.format("%-20s | Confidence: %5.1f%% | %s",
                recommendedName, confidence * 100, reason);
    }

    /**
     * Returns a detailed string with factor breakdown.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s Recommendation ===\n", type));
        sb.append(String.format("Name: %s (ID: %s)\n", recommendedName, recommendedId));
        sb.append(String.format("Confidence: %.1f%%\n", confidence * 100));
        sb.append(String.format("Reason: %s\n", reason));
        sb.append("Factor Breakdown:\n");
        for (Map.Entry<String, Double> entry : supportingStats.entrySet()) {
            sb.append(String.format("  %-25s: %.3f\n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
