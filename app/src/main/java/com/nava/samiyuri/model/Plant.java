package com.nava.samiyuri.model;

import java.util.Date;

/**
 * Plant - Core data model for a single plant in the Samiyura app
 * <p>
 * This class represents a real plant that a child is growing, tracking its
 * lifecycle from planting to harvest. It handles:
 * - Growth stage progression based on time
 * - Care scheduling and status
 * - Plant-type specific behaviors (radish vs lettuce)
 */
public class Plant {

    // PLANT IDENTITY
    private long id;                    // Unique identifier for database storage
    private String plantType;           // "radish" or "lettuce"
    private String childName;           // Child's custom name for this plant

    // LIFECYCLE TRACKING
    private Date plantingDate;          // When the seed was planted
    private int growthStage;            // Current growth phase (see constants below)
    private Date harvestDate;           // Calculated expected harvest date

    // CARE TRACKING
    private Date lastWateredDate;       // Last time child watered the plant
    private Date lastAnalysisDate;      // Last time child did plant analysis
    private String currentStatus;       // "happy", "thirsty", "needs_attention"

    // GROWTH STAGE CONSTANTS - Makes code more readable
    public static final int STAGE_SEED = 0;        // Just planted
    public static final int STAGE_SPROUTING = 1;   // First signs of life
    public static final int STAGE_SEEDLING = 2;    // Small leaves visible
    public static final int STAGE_GROWING = 3;     // Actively growing
    public static final int STAGE_MATURE = 4;      // Almost ready
    public static final int STAGE_HARVEST = 5;     // Ready to harvest!

    // TODO: Add more detailed functionality here
    // Add this to your Plant.java after the constants:

    /**
     * Constructor - Creates a new plant with basic information
     * <p>
     * Sets up a new plant with default values and calculates initial schedule
     */
    public Plant(String plantType, String childName) {
        this.plantType = plantType;
        this.childName = childName;
        this.plantingDate = new Date(); // Current date/time
        this.growthStage = STAGE_SEED;  // Start as seed
        this.currentStatus = "happy";   // Start happy!

        // TODO: Calculate harvest date based on plant type
        // TODO: Set up care schedule
    }
    /**
     * CORE BEHAVIOR METHODS
     * These methods handle the plant's lifecycle and care needs
     */

    /**
     * Updates the plant's current status based on care history and time
     * <p>
     * Checks if plant needs water, attention, or is doing well
     * Called every time the app opens or user checks on plants
     */
    public void updateStatus() {
        Date now = new Date();

        // Calculate days since last watering
        long daysSinceWater = calculateDaysBetween(lastWateredDate, now);

        // Simple status logic (will expand this later)
        if (daysSinceWater > 2) {
            this.currentStatus = "thirsty";
        } else if (daysSinceWater > 4) {
            this.currentStatus = "needs_attention";
        } else {
            this.currentStatus = "happy";
        }

        // TODO: Add growth stage progression logic
        // TODO: Add plant-type specific care requirements
    }

    /**
     * Records that the child watered their plant
     * <p>
     * Updates care tracking and recalculates plant status
     */
    public void waterPlant() {
        this.lastWateredDate = new Date();
        updateStatus(); // Recalculate after care
    }

    // Helper method for date calculations
    private long calculateDaysBetween(Date start, Date end) {
        if (start == null) return 0;
        return (end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24);
    }

    /**
     * Basic getters for accessing plant data
     */
    public String getChildName() { return childName; }
    public String getStatus() { return currentStatus; }
    public String getPlantType() { return plantType; }
    public int getGrowthStage() { return growthStage; }

    /**
     * Basic setters for updating plant state
     */
    public void setStatus(String status) { this.currentStatus = status; }

    /**
     * PLANT LIFECYCLE METHODS
     * Handle growth progression and scheduling
     */

    /**
     * Calculates and updates growth stage based on days since planting
     */
    public void updateGrowthStage() {
        int daysSincePlanting = (int) calculateDaysBetween(plantingDate, new Date());

        // Basic timeline (will make this plant-specific later)
        if (daysSincePlanting >= 25 && plantType.equals("radish")) {
            this.growthStage = STAGE_HARVEST;
        } else if (daysSincePlanting >= 15) {
            this.growthStage = STAGE_MATURE;
        } else if (daysSincePlanting >= 7) {
            this.growthStage = STAGE_GROWING;
        } else if (daysSincePlanting >= 3) {
            this.growthStage = STAGE_SEEDLING;
        } else if (daysSincePlanting >= 1) {
            this.growthStage = STAGE_SPROUTING;
        }
    }

    /**
     * Records plant analysis activity
     */
    public void performAnalysis() {
        this.lastAnalysisDate = new Date();
    }

    /**
     * Checks if plant is ready for harvest
     */
    public boolean isReadyForHarvest() {
        return growthStage == STAGE_HARVEST;
    }
}