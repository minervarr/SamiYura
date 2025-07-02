package com.nava.samiyuri.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nava.samiyuri.R;
import com.nava.samiyuri.model.Plant;

/**
 * PlantViewModel - Business logic layer for plant buddy management (MVVM Pattern)
 * <p>
 * This ViewModel handles all business logic related to plant buddies, serving as the bridge
 * between the UI (MainActivity) and the data layer (Plant model). It uses LiveData to
 * provide reactive updates to the UI when plant buddy data changes.
 * <p>
 * Key responsibilities:
 * - Manage current plant buddy data
 * - Handle care actions (watering, analysis, sunlight)
 * - Calculate and update plant status
 * - Provide UI-ready data through LiveData
 * - Prepare for future database integration
 * <p>
 * Architecture: Follows Android MVVM pattern with LiveData for reactive UI updates
 */
public class PlantViewModel extends ViewModel {

    // LIVE DATA FOR REACTIVE UI UPDATES
    // These LiveData objects automatically notify the UI when plant buddy data changes

    /**
     * Current plant buddy being displayed and cared for
     * UI observes this to update buddy name, status, and growth information
     */
    private final MutableLiveData<Plant> currentPlant = new MutableLiveData<>();

    /**
     * Avatar resource ID based on plant type and current status
     * UI observes this to update the buddy's facial expression
     */
    private final MutableLiveData<Integer> plantAvatarResource = new MutableLiveData<>();

    /**
     * User feedback messages for care actions
     * UI observes this to show encouraging messages when child cares for buddy
     */
    private final MutableLiveData<String> feedbackMessage = new MutableLiveData<>();

    /**
     * Plant analysis questions for Observer's Journal
     * UI observes this to show growth-stage appropriate survey questions
     */
    private final MutableLiveData<String> analysisQuestion = new MutableLiveData<>();

    /**
     * Constructor - Initialize ViewModel with test plant buddy data
     * <p>
     * In future phases, this will load plant buddy data from database
     * or receive it from the naming ceremony flow.
     */
    public PlantViewModel() {
        // Initialize with test buddy data (future: load from database)
        initializeTestBuddy();
    }

    /**
     * PLANT BUDDY INITIALIZATION AND DATA MANAGEMENT
     */

    /**
     * Initialize ViewModel with test plant buddy for development
     * <p>
     * Future: Replace with loadBuddyFromDatabase() or createNewBuddy() methods
     */
    private void initializeTestBuddy() {
        Plant testBuddy = new Plant("radish", "Roberto");
        setCurrentPlant(testBuddy);
    }

    /**
     * Sets the current plant buddy and updates all dependent UI data
     * <p>
     * This method updates the plant data and triggers recalculation of status,
     * avatar, and other UI elements. Called when switching between buddies
     * or when plant buddy data changes.
     *
     * @param plant The plant buddy to display and manage
     */
    private void setCurrentPlant(Plant plant) {
        if (plant != null) {
            // Update plant status based on current time and care history
            plant.updateBuddyStatus();

            // Update LiveData to notify UI of changes
            currentPlant.setValue(plant);
            updatePlantAvatar();

            // Clear any previous feedback messages
            feedbackMessage.setValue(null);
        }
    }

    /**
     * PLANT BUDDY CARE ACTIONS
     * These methods handle the core interactions between child and plant buddy
     */

    /**
     * Records that the child watered their plant buddy
     * <p>
     * Updates care tracking, recalculates buddy status, refreshes UI data,
     * and provides positive feedback to encourage continued care.
     */
    public void waterCurrentBuddy() {
        Plant buddy = currentPlant.getValue();
        if (buddy != null) {
            // Record the care action in the plant model
            buddy.waterBuddy();

            // Update UI data to reflect new status
            currentPlant.setValue(buddy);
            updatePlantAvatar();

            // Provide encouraging feedback
            String message = "Great job! " + buddy.getBuddyName() + " loves the fresh water!";
            feedbackMessage.setValue(message);
        }
    }

    /**
     * Starts plant analysis survey - core Observer's Journal functionality
     * <p>
     * Generates growth-stage appropriate questions that require the child to
     * observe their real plant. This bridges the digital and physical experience.
     */
    public void startPlantAnalysis() {
        Plant buddy = currentPlant.getValue();
        if (buddy != null) {
            // Record that child is doing analysis
            buddy.observeBuddy();

            // Generate growth-stage specific question
            String question = generateAnalysisQuestion(buddy);
            analysisQuestion.setValue(question);

            // Update plant data
            currentPlant.setValue(buddy);
        }
    }

    /**
     * Records sunlight care action - positive reinforcement
     * <p>
     * Future: Could track sunlight exposure or location changes
     */
    public void giveSunlightToBuddy() {
        Plant buddy = currentPlant.getValue();
        if (buddy != null) {
            String message = buddy.getBuddyName() + " loves the warm sunlight!";
            feedbackMessage.setValue(message);
        }
    }

    /**
     * Shows buddy information and care tips
     * <p>
     * Future: Will provide detailed buddy profile and growth guidance
     */
    public void showBuddyInfo() {
        feedbackMessage.setValue("Your buddy is lucky to have you!");
    }

    /**
     * NAVIGATION BETWEEN MULTIPLE BUDDIES (Future Feature)
     */

    /**
     * Navigate to previous buddy in collection
     * <p>
     * Future: Will load previous buddy from database/collection
     */
    public void showPreviousBuddy() {
        feedbackMessage.setValue("Previous buddy (coming soon!)");
    }

    /**
     * Navigate to next buddy in collection
     * <p>
     * Future: Will load next buddy from database/collection
     */
    public void showNextBuddy() {
        feedbackMessage.setValue("Next buddy (coming soon!)");
    }

    /**
     * HELPER METHODS FOR UI DATA PREPARATION
     */

    /**
     * Updates the plant avatar resource based on current plant type and status
     * <p>
     * Determines which drawable resource to display for the buddy's current mood
     * and growth state. Called whenever plant status changes.
     */
    private void updatePlantAvatar() {
        Plant buddy = currentPlant.getValue();
        if (buddy == null) return;

        int avatarResource;

        // Choose avatar based on plant type and current status
        if ("radish".equals(buddy.getPlantType())) {
            switch (buddy.getCurrentStatus()) {
                case "happy":
                    avatarResource = R.drawable.plant_radish_happy;
                    break;
                case "thirsty":
                    avatarResource = R.drawable.plant_radish_thirsty;
                    break;
                case "needs_attention":
                    avatarResource = R.drawable.plant_radish_sad;
                    break;
                default:
                    avatarResource = R.drawable.plant_radish;
                    break;
            }
        } else {
            // Lettuce avatars
            switch (buddy.getCurrentStatus()) {
                case "happy":
                    avatarResource = R.drawable.iceberg_lettuce_happy;
                    break;
                case "thirsty":
                    avatarResource = R.drawable.iceberg_lettuce_thirsty;
                    break;
                case "needs_attention":
                    avatarResource = R.drawable.iceberg_lettuce_sad;
                    break;
                default:
                    avatarResource = R.drawable.iceberg_lettuce;
                    break;
            }
        }

        plantAvatarResource.setValue(avatarResource);
    }

    /**
     * Generates growth-stage appropriate analysis questions for Observer's Journal
     * <p>
     * Creates questions that require the child to observe their real plant,
     * making the connection between digital buddy and physical garden.
     *
     * @param buddy The plant buddy to generate questions for
     * @return Question text that requires real plant observation
     */
    private String generateAnalysisQuestion(Plant buddy) {
        switch (buddy.getGrowthStage()) {
            case Plant.STAGE_SEED:
                return "Go check your real plant! Do you see any tiny sprouts coming up from the soil?";
            case Plant.STAGE_SPROUTING:
                return "Look closely at your real " + buddy.getBuddyName() + "! How many little leaves can you count?";
            case Plant.STAGE_SEEDLING:
                return "Check on your real plant! How tall is " + buddy.getBuddyName() + " now? Use your finger to measure!";
            case Plant.STAGE_GROWING:
                return "Time to analyze! Are " + buddy.getBuddyName() + "'s leaves getting bigger?";
            case Plant.STAGE_MATURE:
                return "Look carefully! Is " + buddy.getBuddyName() + " ready to harvest yet?";
            case Plant.STAGE_HARVEST:
                return "Amazing! " + buddy.getBuddyName() + " looks ready to harvest! How does it look?";
            default:
                return "Take a close look at " + buddy.getBuddyName() + ". Do you see any changes?";
        }
    }

    /**
     * PUBLIC LIVEDATA GETTERS FOR UI OBSERVATION
     * These provide read-only access to LiveData for the UI layer
     */

    /**
     * @return LiveData containing current plant buddy for UI display
     */
    public LiveData<Plant> getCurrentPlant() {
        return currentPlant;
    }

    /**
     * @return LiveData containing avatar resource ID for buddy image display
     */
    public LiveData<Integer> getPlantAvatarResource() {
        return plantAvatarResource;
    }

    /**
     * @return LiveData containing user feedback messages for Toast display
     */
    public LiveData<String> getFeedbackMessage() {
        return feedbackMessage;
    }

    /**
     * @return LiveData containing analysis questions for plant survey display
     */
    public LiveData<String> getAnalysisQuestion() {
        return analysisQuestion;
    }

    /**
     * UTILITY METHODS FOR UI DATA FORMATTING
     */

    /**
     * Converts internal status codes to child-friendly display text
     * <p>
     * Future: Will use string resources for proper localization
     *
     * @param status Internal status code from Plant model
     * @return Child-friendly status description
     */
    public String getStatusDisplayText(String status) {
        switch (status) {
            case "happy":
                return "happy and healthy";
            case "thirsty":
                return "feeling a bit thirsty";
            case "needs_attention":
                return "really needs your care";
            default:
                return "happy and healthy";
        }
    }

    /**
     * LIFECYCLE MANAGEMENT
     */

    /**
     * Called when ViewModel is cleared (Activity/Fragment destroyed)
     * <p>
     * Future: Will handle saving plant buddy data to database
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        // Future: Save current plant buddy data to database
        // Future: Clean up any background tasks or timers
    }
}