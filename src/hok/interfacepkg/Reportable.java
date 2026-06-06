package hok.interfacepkg;

/**
 * Interface for entities that can produce formatted reports.
 * Implemented by Player, Team, and Hero.
 * Demonstrates polymorphism: the same method names produce different outputs
 * depending on the implementing class.
 */
public interface Reportable {

    /**
     * Returns a one-line summary suitable for list displays.
     * @return short descriptive string
     */
    String getSummary();

    /**
     * Returns full formatted details including all relevant fields.
     * @return multi-line detailed description
     */
    String getDetailedInfo();
}
