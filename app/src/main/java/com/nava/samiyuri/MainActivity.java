package com.nava.samiyuri;

// Android framework imports for basic app functionality
import androidx.appcompat.app.AppCompatActivity;  // Base class for activities with modern features
import androidx.lifecycle.ViewModelProvider;       // For creating ViewModel instances
import android.os.Bundle;                        // For saving/restoring activity state
import android.view.View;                        // For handling click events
import android.widget.Toast;                     // For showing user feedback messages

// Project-specific imports
import com.nava.samiyuri.databinding.ActivityMainBinding;  // Auto-generated binding for layout
import com.nava.samiyuri.model.Plant;                     // Plant model for data types
import com.nava.samiyuri.model.BuddyMessageEvent;         // NEW: Message event data class
import com.nava.samiyuri.model.BuddyMessageType;          // NEW: Message type enum
import com.nava.samiyuri.viewmodel.PlantViewModel;         // Our ViewModel for business logic

/**
 * MainActivity - The "Buddy Dashboard" UI layer of the Samiyura app (MVVM Pattern)
 * <p>
 * This activity serves as the View in the MVVM architecture, focusing purely on UI display
 * and user interaction. All business logic has been moved to PlantViewModel, creating a
 * clean separation of concerns and making the code more maintainable and testable.
 * <p>
 * **NEW MVVM MESSAGE SYSTEM**: This activity now properly handles structured message events
 * from the ViewModel and formats them using string resources. This enables proper localization
 * support and maintains clean separation between business logic and presentation.
 * <p>
 * Key responsibilities:
 * - Display plant buddy information from ViewModel
 * - Handle user interactions (button clicks)
 * - Observe LiveData changes and update UI accordingly
 * - **NEW**: Format and display message events using string resources
 * - Provide user feedback through Toast messages
 * <p>
 * CURRENT PHASE: Full MVVM implementation with proper message event system
 * FUTURE PHASES: Database integration, multiple buddy support, advanced UI animations
 */
public class MainActivity extends AppCompatActivity {

    // View binding instance - provides type-safe access to all views in the layout
    // This replaces findViewById() calls and prevents null pointer exceptions
    private ActivityMainBinding binding;

    // ViewModel instance - handles all business logic and provides UI data through LiveData
    // The ViewModel survives configuration changes (like screen rotation)
    private PlantViewModel plantViewModel;

    /**
     * onCreate() - Android lifecycle method, called when activity is first created
     * <p>
     * This is where we:
     * 1. Set up the user interface using View Binding
     * 2. Initialize the ViewModel
     * 3. Set up LiveData observers for reactive UI updates
     * 4. Configure user interaction listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // STEP 1: Set up the user interface using View Binding
        // This inflates (creates) all UI components from the XML layout file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // STEP 2: Initialize ViewModel using ViewModelProvider
        // ViewModelProvider ensures the ViewModel survives configuration changes
        plantViewModel = new ViewModelProvider(this).get(PlantViewModel.class);

        // STEP 3: Set up LiveData observers for reactive UI updates
        setupLiveDataObservers();

        // STEP 4: Set up user interaction listeners
        setupBuddyCareListeners();
    }

    /**
     * Sets up LiveData observers for reactive UI updates
     * <p>
     * These observers automatically update the UI whenever the ViewModel's data changes.
     * This creates a reactive, data-driven UI that stays in sync with the business logic.
     * <p>
     * **NEW**: Now includes proper message event handling with string resource formatting.
     */
    private void setupLiveDataObservers() {

        // Observe current plant buddy data and update UI display
        plantViewModel.getCurrentPlant().observe(this, plant -> {
            if (plant != null) {
                updateBuddyDisplayInfo(plant);
            }
        });

        // Observe plant avatar resource changes and update buddy image
        plantViewModel.getPlantAvatarResource().observe(this, avatarResourceId -> {
            if (avatarResourceId != null) {
                binding.buddyAvatar.setImageResource(avatarResourceId);
            }
        });

        // **NEW**: Observe structured message events and format using string resources
        // This replaces the old feedbackMessage and analysisQuestion observers
        plantViewModel.getMessageEvent().observe(this, messageEvent -> {
            if (messageEvent != null) {
                handleMessageEvent(messageEvent);
            }
        });
    }

    /**
     * **NEW**: Handles structured message events and formats them using string resources
     * <p>
     * This method receives message events from the ViewModel and formats them into
     * user-friendly messages using the appropriate string resources. This approach
     * provides proper localization support and maintains clean MVVM separation.
     * <p>
     * Each message type corresponds to a specific string resource that may or may not
     * require formatting with the buddy's name.
     *
     * @param messageEvent The structured message event from the ViewModel
     */
    private void handleMessageEvent(BuddyMessageEvent messageEvent) {
        String formattedMessage = formatMessageFromEvent(messageEvent);

        if (formattedMessage != null && !formattedMessage.isEmpty()) {
            // Determine toast duration based on message type
            int duration = getToastDurationForMessageType(messageEvent.getMessageType());
            Toast.makeText(this, formattedMessage, duration).show();
        }
    }

    /**
     * **NEW**: Formats a message event into a user-friendly string using string resources
     * <p>
     * This method maps message types to their corresponding string resources and
     * handles any necessary formatting (like inserting the buddy's name).
     * <p>
     * Benefits:
     * - All user-facing text comes from string resources (localization support)
     * - Clean separation between business logic and presentation
     * - Type-safe message handling
     *
     * @param messageEvent The message event to format
     * @return Formatted message string ready for display
     */
    private String formatMessageFromEvent(BuddyMessageEvent messageEvent) {
        BuddyMessageType messageType = messageEvent.getMessageType();
        String buddyName = messageEvent.getBuddyName();

        switch (messageType) {
            // BUDDY CARE FEEDBACK MESSAGES
            case BUDDY_WATERED:
                return getString(R.string.buddy_watered_message, buddyName);

            case BUDDY_GOT_SUNLIGHT:
                return getString(R.string.buddy_sunlight_message, buddyName);

            case BUDDY_INFO_SHOWN:
                return getString(R.string.buddy_info_message);

            case FEATURE_COMING_SOON:
                return getString(R.string.feature_coming_soon);

            // PLANT ANALYSIS QUESTIONS (Observer's Journal)
            case ANALYSIS_SEED_QUESTION:
                return getString(R.string.analysis_seed_question);

            case ANALYSIS_SPROUTING_QUESTION:
                return getString(R.string.analysis_sprouting_question, buddyName);

            case ANALYSIS_SEEDLING_QUESTION:
                return getString(R.string.analysis_seedling_question, buddyName);

            case ANALYSIS_GROWING_QUESTION:
                return getString(R.string.analysis_growing_question, buddyName);

            case ANALYSIS_MATURE_QUESTION:
                return getString(R.string.analysis_mature_question, buddyName);

            case ANALYSIS_HARVEST_QUESTION:
                return getString(R.string.analysis_harvest_question, buddyName);

            case ANALYSIS_DEFAULT_QUESTION:
                return getString(R.string.analysis_default_question, buddyName);

            default:
                // Fallback for unknown message types
                return getString(R.string.buddy_info_message);
        }
    }

    /**
     * **NEW**: Determines appropriate Toast duration based on message type
     * <p>
     * Analysis questions are shown longer since they require the child to
     * go observe their real plant. Quick feedback messages are shown briefly.
     *
     * @param messageType The type of message being displayed
     * @return Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    private int getToastDurationForMessageType(BuddyMessageType messageType) {
        switch (messageType) {
            // Analysis questions need more time to read
            case ANALYSIS_SEED_QUESTION:
            case ANALYSIS_SPROUTING_QUESTION:
            case ANALYSIS_SEEDLING_QUESTION:
            case ANALYSIS_GROWING_QUESTION:
            case ANALYSIS_MATURE_QUESTION:
            case ANALYSIS_HARVEST_QUESTION:
            case ANALYSIS_DEFAULT_QUESTION:
                return Toast.LENGTH_LONG;

            // Quick feedback messages
            default:
                return Toast.LENGTH_SHORT;
        }
    }

    /**
     * Sets up click listeners for all buddy care action buttons
     * <p>
     * Connects the UI buttons to ViewModel methods, maintaining clean separation
     * between UI interaction and business logic.
     */
    private void setupBuddyCareListeners() {

        // Water Buddy Button - Core care action
        binding.buttonWaterBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantViewModel.waterCurrentBuddy();
            }
        });

        // Plant Analysis Button - Interactive survey for Observer's Journal
        binding.buttonAnalyzePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantViewModel.startPlantAnalysis();
            }
        });

        // Sunlight Button - Positive reinforcement care action
        binding.buttonSunlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantViewModel.giveSunlightToBuddy();
            }
        });

        // Info Button - Shows buddy information and care tips
        binding.buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantViewModel.showBuddyInfo();
            }
        });

        // Navigation arrows - Future: Will switch between multiple buddies
        binding.arrowLeft.setOnClickListener(v -> plantViewModel.showPreviousBuddy());
        binding.arrowRight.setOnClickListener(v -> plantViewModel.showNextBuddy());
    }

    /**
     * Updates buddy display information in the UI
     * <p>
     * Called by LiveData observer when plant buddy data changes.
     * Focuses purely on UI updates without any business logic.
     *
     * @param plant The current plant buddy to display
     */
    private void updateBuddyDisplayInfo(Plant plant) {
        // Display buddy's personal name
        binding.buddyName.setText(plant.getBuddyName());

        // Display current buddy status with encouraging language
        String statusText = getString(R.string.buddy_status_message,
                plant.getBuddyName(),
                plantViewModel.getStatusDisplayText(plant.getCurrentStatus()));
        binding.buddyStatus.setText(statusText);

        // Display growth stage description using buddy's name
        binding.buddyGrowthStage.setText(plant.getGrowthStageDescription());
    }

    /**
     * LIFECYCLE MANAGEMENT
     */

    /**
     * onDestroy() - Called when activity is being destroyed
     * <p>
     * View binding cleanup to prevent memory leaks.
     * ViewModel is automatically retained and cleaned up by ViewModelProvider.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }
}