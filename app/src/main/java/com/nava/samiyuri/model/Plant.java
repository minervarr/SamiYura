package com.nava.samiyuri.model;

import java.util.Date;

/**
 * Plant - Core data model representing a child's plant "buddy" in the Samiyura app
 * <p>
 * This class represents a real plant that a child is growing and has given a personal name to,
 * creating an emotional bond. It tracks the plant's lifecycle from planting to harvest,
 * handling growth stage progression, care scheduling, and status monitoring.
 * <p>
 * The "buddy" concept is central - this isn't just "a plant," it's "Roberto the Radish"
 * or "Sunny the Lettuce" - a named companion that the child cares for daily.
 */
public class Plant {

    // PLANT IDENTITY & BUDDY RELATIONSHIP
    private long id;                    // Unique identifier for database storage
    private String plantType;           // "radish" or "lettuce"
    private String buddyName;           // Child's chosen name for their plant buddy (e.g., "Roberto")

    // LIFECYCLE TRACKING
    private Date plantingDate;          // When the buddy was planted
    private int growthStage;            // Current growth phase (see constants below)
    private Date expectedHarvestDate;   // Calculated expected harvest date

    // CARE TRACKING & BUDDY STATUS
    private Date lastWateredDate;       // Last time child watered their buddy
    private Date lastObservationDate;   // Last time child checked on their buddy
    private String currentStatus;       // "happy", "thirsty", "needs_attention"

    // GROWTH STAGE CONSTANTS - The buddy's life journey
    public static final int STAGE_SEED = 0;        // Just planted, buddy is sleeping
    public static final int STAGE_SPROUTING = 1;   // First signs of life!
    public static final int STAGE_SEEDLING = 2;    // Baby buddy with tiny leaves
    public static final int STAGE_GROWING = 3;     // Buddy is growing strong
    public static final int STAGE_MATURE = 4;      // Almost ready for harvest
    public static final int STAGE_HARVEST = 5;     // Buddy is ready to be harvested!

    /**
     * Constructor - Creates a new plant buddy with a personal name
     * <p>
     * Sets up a new plant buddy with default values and calculates initial schedule.
     * The child has chosen to grow this type of plant and given it a special name.
     *
     * @param plantType The type of plant ("radish" or "lettuce")
     * @param buddyName The special name the child chose for their plant buddy
     */
    public Plant(String plantType, String buddyName) {
        this.plantType = plantType;
        this.buddyName = buddyName;
        this.plantingDate = new Date(); // Current date/time
        this.growthStage = STAGE_SEED;  // Every buddy starts as a seed
        this.currentStatus = "happy";   // New buddies start happy!

        // Calculate when this buddy will be ready for harvest
        calculateExpectedHarvestDate();
    }

    /**
     * CORE BUDDY CARE METHODS
     * These methods handle the plant buddy's lifecycle and care needs
     */

    /**
     * Updates the buddy's current status based on care history and time passed
     * <p>
     * Checks if the buddy needs water, attention, or is doing well.
     * Called every time the app opens or user checks on their buddies.
     */
    public void updateBuddyStatus() {
        Date now = new Date();

        // Calculate days since last watering
        long daysSinceWater = calculateDaysBetween(lastWateredDate, now);

        // Simple buddy status logic (will expand this later)
        if (daysSinceWater > 4) {
            this.currentStatus = "needs_attention"; // Buddy is really thirsty!
        } else if (daysSinceWater > 2) {
            this.currentStatus = "thirsty";         // Buddy needs some water
        } else {
            this.currentStatus = "happy";           // Buddy is doing great!
        }

        // Update growth stage based on time since planting
        updateGrowthStage();
    }

    /**
     * Records that the child watered their plant buddy
     * <p>
     * Updates care tracking and recalculates buddy status to reflect the care given.
     */
    public void waterBuddy() {
        this.lastWateredDate = new Date();
        updateBuddyStatus(); // Recalculate status after watering
    }

    /**
     * Records that the child observed their plant buddy
     * <p>
     * Part of the "Observer's Journal" feature where kids check on their buddy's progress.
     */
    public void observeBuddy() {
        this.lastObservationDate = new Date();
    }

    /**
     * PLANT LIFECYCLE METHODS
     * Handle growth progression and harvest scheduling
     */

    /**
     * Calculates and updates growth stage based on days since planting
     * <p>
     * Uses realistic timelines for radish vs lettuce growth patterns.
     */
    public void updateGrowthStage() {
        int daysSincePlanting = (int) calculateDaysBetween(plantingDate, new Date());

        // Growth timeline depends on plant type
        if ("radish".equals(plantType)) {
            // Radish growth timeline (faster growing)
            if (daysSincePlanting >= 25) {
                this.growthStage = STAGE_HARVEST;
            } else if (daysSincePlanting >= 18) {
                this.growthStage = STAGE_MATURE;
            } else if (daysSincePlanting >= 10) {
                this.growthStage = STAGE_GROWING;
            } else if (daysSincePlanting >= 5) {
                this.growthStage = STAGE_SEEDLING;
            } else if (daysSincePlanting >= 2) {
                this.growthStage = STAGE_SPROUTING;
            }
        } else if ("lettuce".equals(plantType)) {
            // Lettuce growth timeline (slower growing)
            if (daysSincePlanting >= 45) {
                this.growthStage = STAGE_HARVEST;
            } else if (daysSincePlanting >= 30) {
                this.growthStage = STAGE_MATURE;
            } else if (daysSincePlanting >= 15) {
                this.growthStage = STAGE_GROWING;
            } else if (daysSincePlanting >= 7) {
                this.growthStage = STAGE_SEEDLING;
            } else if (daysSincePlanting >= 3) {
                this.growthStage = STAGE_SPROUTING;
            }
        }
    }

    /**
     * Calculates expected harvest date based on plant type
     */
    private void calculateExpectedHarvestDate() {
        long plantingTime = plantingDate.getTime();
        long daysToAdd;

        if ("radish".equals(plantType)) {
            daysToAdd = 25; // Radishes ready in ~25 days
        } else {
            daysToAdd = 45; // Lettuce ready in ~45 days
        }

        long harvestTime = plantingTime + (daysToAdd * 24 * 60 * 60 * 1000L);
        this.expectedHarvestDate = new Date(harvestTime);
    }

    /**
     * Checks if the buddy is ready for harvest
     *
     * @return true if the buddy has reached harvest stage
     */
    public boolean isBuddyReadyForHarvest() {
        return growthStage == STAGE_HARVEST;
    }

    /**
     * Gets a friendly description of the buddy's current growth stage
     *
     * @return Human-readable description of what's happening with the buddy
     */
    public String getGrowthStageDescription() {
        switch (growthStage) {
            case STAGE_SEED:
                return buddyName + " is sleeping as a seed";
            case STAGE_SPROUTING:
                return buddyName + " is starting to sprout!";
            case STAGE_SEEDLING:
                return buddyName + " has tiny leaves";
            case STAGE_GROWING:
                return buddyName + " is growing bigger";
            case STAGE_MATURE:
                return buddyName + " is almost ready";
            case STAGE_HARVEST:
                return buddyName + " is ready to harvest!";
            default:
                return buddyName + " is growing";
        }
    }

    // Helper method for date calculations
    private long calculateDaysBetween(Date start, Date end) {
        if (start == null) return 0;
        return (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
    }

    /**
     * GETTERS AND SETTERS
     * Provide access to buddy data for the UI and database
     */

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPlantType() { return plantType; }
    public void setPlantType(String plantType) { this.plantType = plantType; }

    public String getBuddyName() { return buddyName; }
    public void setBuddyName(String buddyName) { this.buddyName = buddyName; }

    public Date getPlantingDate() { return plantingDate; }
    public void setPlantingDate(Date plantingDate) { this.plantingDate = plantingDate; }

    public int getGrowthStage() { return growthStage; }
    public void setGrowthStage(int growthStage) { this.growthStage = growthStage; }

    public Date getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(Date expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }

    public Date getLastWateredDate() { return lastWateredDate; }
    public void setLastWateredDate(Date lastWateredDate) { this.lastWateredDate = lastWateredDate; }

    public Date getLastObservationDate() { return lastObservationDate; }
    public void setLastObservationDate(Date lastObservationDate) { this.lastObservationDate = lastObservationDate; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
}