package com.nava.samiyuri.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nava.samiyuri.R;
import com.nava.samiyuri.model.Plant;
import com.nava.samiyuri.model.BuddyMessageEvent;
import com.nava.samiyuri.model.BuddyMessageType;

/**
 * PlantViewModel - Business logic layer for plant buddy management (MVVM Pattern)
 * <p>
 * This ViewModel handles all business logic related to plant buddies, serving as the bridge
 * between the UI (MainActivity) and the data layer (Plant model). It uses LiveData to
 * provide reactive updates to the UI when plant buddy data changes.
 * <p>
 * **MVVM MESSAGE SYSTEM**: This ViewModel now properly follows MVVM principles by emitting
 * structured message events instead of formatted strings. The View layer handles all
 * string formatting using string resources, enabling proper localization support.
 * <p>
 * Key responsibilities:
 * - Manage current plant buddy data
 * - Handle care actions (watering, analysis, sunlight)
 * - Calculate and update plant status
 * - Emit message events for UI feedback
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
     * **NEW MVVM MESSAGE SYSTEM**: Structured message events instead of formatted strings
     * <p>
     * UI observes this to receive message events and format them using string resources.
     * This replaces the old feedbackMessage LiveData that contained hardcoded strings.
     * <p>
     * Benefits:
     * - Clean separation between business logic (ViewModel) and presentation (View)
     * - Full localization support through string resources
     * - Easier testing of ViewModel logic without UI dependencies
     */
    private final MutableLiveData<BuddyMessageEvent> messageEvent = new MutableLiveData<>();

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

            // Clear any previous message events
            messageEvent.setValue(null);
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
     * and emits a positive feedback message event to encourage continued care.
     */
    public void waterCurrentBuddy() {
        Plant buddy = currentPlant.getValue();
        if (buddy != null) {
            // Record the care action in the plant model
            buddy.waterBuddy();

            // Update UI data to reflect new status
            currentPlant.setValue(buddy);
            updatePlantAvatar();

            // **NEW**: Emit structured message event instead of hardcoded string
            // View will format this using buddy_watered_message string resource
            BuddyMessageEvent event = new BuddyMessageEvent(
                    BuddyMessageType.BUDDY_WATERED,
                    buddy.getBuddyName()
            );
            messageEvent.setValue(event);
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

            // **NEW**: Emit structured message event for growth-stage specific question
            // View will format this using appropriate analysis_*_question string resource
            BuddyMessageType questionType = getAnalysisQuestionType(buddy);
            BuddyMessageEvent event = new BuddyMessageEvent(questionType, buddy.getBuddyName());
            messageEvent.setValue(event);

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
            // **NEW**: Emit structured message event instead of hardcoded string
            // View will format this using buddy_sunlight_message string resource
            BuddyMessageEvent event = new BuddyMessageEvent(
                    BuddyMessageType.BUDDY_GOT_SUNLIGHT,
                    buddy.getBuddyName()
            );
            messageEvent.setValue(event);
        }
    }

    /**
     * Shows buddy information and care tips
     * <p>
     * Future: Will provide detailed buddy profile and growth guidance
     */
    public void showBuddyInfo() {
        // **NEW**: Emit structured message event instead of hardcoded string
        // View will format this using buddy_info_message string resource
        BuddyMessageEvent event = new BuddyMessageEvent(BuddyMessageType.BUDDY_INFO_SHOWN);
        messageEvent.setValue(event);
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
        // **NEW**: Emit structured message event instead of hardcoded string
        // View will format this using feature_coming_soon string resource
        BuddyMessageEvent event = new BuddyMessageEvent(BuddyMessageType.FEATURE_COMING_SOON);
        messageEvent.setValue(event);
    }

    /**
     * Navigate to next buddy in collection
     * <p>
     * Future: Will load next buddy from database/collection
     */
    public void showNextBuddy() {
        // **NEW**: Emit structured message event instead of hardcoded string
        // View will format this using feature_coming_soon string resource
        BuddyMessageEvent event = new BuddyMessageEvent(BuddyMessageType.FEATURE_COMING_SOON);
        messageEvent.setValue(event);
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
     * **NEW**: Determines which analysis question type to emit based on growth stage
     * <p>
     * Maps plant growth stages to appropriate message types that correspond to
     * specific string resources in strings.xml. This replaces the old method
     * that generated hardcoded question strings.
     *
     * @param buddy The plant buddy to generate question type for
     * @return Message type that corresponds to appropriate string resource
     */
    private BuddyMessageType getAnalysisQuestionType(Plant buddy) {
        switch (buddy.getGrowthStage()) {
            case Plant.STAGE_SEED:
                return BuddyMessageType.ANALYSIS_SEED_QUESTION;
            case Plant.STAGE_SPROUTING:
                return BuddyMessageType.ANALYSIS_SPROUTING_QUESTION;
            case Plant.STAGE_SEEDLING:
                return BuddyMessageType.ANALYSIS_SEEDLING_QUESTION;
            case Plant.STAGE_GROWING:
                return BuddyMessageType.ANALYSIS_GROWING_QUESTION;
            case Plant.STAGE_MATURE:
                return BuddyMessageType.ANALYSIS_MATURE_QUESTION;
            case Plant.STAGE_HARVEST:
                return BuddyMessageType.ANALYSIS_HARVEST_QUESTION;
            default:
                return BuddyMessageType.ANALYSIS_DEFAULT_QUESTION;
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
     * **NEW**: Gets the message event LiveData for structured UI feedback
     * <p>
     * Replaces the old getFeedbackMessage() and getAnalysisQuestion() methods.
     * UI observes this single LiveData stream and handles message formatting
     * based on the message type using appropriate string resources.
     *
     * @return LiveData containing structured message events for UI display
     */
    public LiveData<BuddyMessageEvent> getMessageEvent() {
        return messageEvent;
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