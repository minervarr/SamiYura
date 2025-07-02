package com.nava.samiyuri;

// Android framework imports for basic app functionality
import androidx.appcompat.app.AppCompatActivity;  // Base class for activities with modern features
import android.os.Bundle;                        // For saving/restoring activity state
import android.widget.TextView;                  // UI component for displaying text

// Project-specific imports
import com.nava.samiyuri.databinding.ActivityMainBinding;  // Auto-generated binding for layout
import com.nava.samiyuri.model.Plant;                     // Our custom Plant data model

/**
 * MainActivity - The main entry point and dashboard of the Samiyura app
 * <p>
 * This activity serves as the primary interface where children interact with their
 * virtual plant companions. It displays plant status, care buttons, and navigation
 * to other features.
 * <p>
 * CURRENT PHASE: Basic skeleton - testing Plant model integration
 * FUTURE PHASES: Will contain the full dashboard UI from design mockup
 */
public class MainActivity extends AppCompatActivity {

    // View binding instance - provides type-safe access to all views in the layout
    // This replaces findViewById() calls and prevents null pointer exceptions
    private ActivityMainBinding binding;

    /**
     * onCreate() - Android lifecycle method, called when activity is first created
     *
     * This is where we:
     * 1. Set up the user interface
     * 2. Initialize data models
     * 3. Connect UI components to data
     * 4. Prepare the app for user interaction
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // STEP 1: Set up the user interface using View Binding
        // This inflates (creates) all UI components from the XML layout file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // STEP 2: Initialize test data using our Plant model
        // In the final app, this will come from:
        // - User input (when child names their plants)
        // - Database storage (for persistent plant data)
        // - Schedule calculations (for growth stages and care needs)
        Plant testPlant = new Plant("radish", "Test Plant");

        // STEP 3: Connect data to the user interface
        // Get reference to the main text display component
        TextView tv = binding.sampleText;

        // Display plant information to verify our model works correctly
        // Format: "Your [plant_name] is [status]"
        // TODO: Replace with proper dashboard UI elements (plant cards, buttons, etc.)
        tv.setText(getString(R.string.plant_status_message, testPlant.getChildName(), testPlant.getStatus()));

        // FUTURE EXPANSION POINTS:
        // - Add click listeners for care buttons (water, light, analysis)
        // - Load plant data from database
        // - Calculate plant status based on care schedule
        // - Set up navigation to other screens (naming, analysis, learning)
        // - Implement proper localization using strings.xml
    }

    // FUTURE METHODS TO ADD:
    // - loadPlantData() - Load saved plants from database
    // - updatePlantStatus() - Calculate current plant needs based on schedule
    // - setupClickListeners() - Handle button presses for plant care actions
    // - navigateToAnalysis() - Open plant analysis screen
    // - showCareReminder() - Display gentle notification for plant care
}