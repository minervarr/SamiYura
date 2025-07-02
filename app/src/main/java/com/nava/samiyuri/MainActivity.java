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
import com.nava.samiyuri.viewmodel.PlantViewModel;         // Our ViewModel for business logic

/**
 * MainActivity - The "Buddy Dashboard" UI layer of the Samiyura app (MVVM Pattern)
 * <p>
 * This activity serves as the View in the MVVM architecture, focusing purely on UI display
 * and user interaction. All business logic has been moved to PlantViewModel, creating a
 * clean separation of concerns and making the code more maintainable and testable.
 * <p>
 * Key responsibilities:
 * - Display plant buddy information from ViewModel
 * - Handle user interactions (button clicks)
 * - Observe LiveData changes and update UI accordingly
 * - Provide user feedback through Toast messages
 * <p>
 * CURRENT PHASE: Full MVVM implementation with LiveData observation
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

        // Observe feedback messages and show Toast notifications
        plantViewModel.getFeedbackMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe analysis questions and show plant survey prompts
        plantViewModel.getAnalysisQuestion().observe(this, question -> {
            if (question != null && !question.isEmpty()) {
                Toast.makeText(this, question, Toast.LENGTH_LONG).show();
            }
        });
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