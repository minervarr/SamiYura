package com.nava.samiyuri;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nava.samiyuri.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity - The heart of the Samiyura garden companion app.
 * <p>
 * This activity serves as the central hub where children interact with their virtual plant buddies.
 * It provides a carousel-style interface allowing users to navigate between different plants,
 * each with their own emotional states and care requirements.
 * <p>
 * Key Features:
 * - Dynamic mood cycling every 3 seconds for presentation purposes
 * - Smooth card-like sliding animations between plant buddies
 * - Interactive buttons with visual press feedback
 * - Localized strings for international accessibility
 * - Optimized for low-spec Android devices (API 30+)
 * <p>
 * The design philosophy follows "Less Screen, More Garden" - encouraging real-world
 * plant care through digital guidance and positive reinforcement.
 *
 * @author Neriou
 * @version 1.0
 * @since 2025-07-06
 */
public class MainActivity extends AppCompatActivity {

    /**
     * View binding instance for type-safe access to layout components.
     * <p>
     * Using ViewBinding instead of findViewById() for better performance
     * and compile-time safety, especially important on low-spec devices.
     */
    private ActivityMainBinding binding;

    /**
     * List containing all plant buddy instances that the user can interact with.
     * <p>
     * Includes named radish and lettuce plants, plus an "Add New Buddy" placeholder
     * for future expansion functionality.
     */
    private List<Plant> plantBuddies;

    /**
     * Current plant index in the carousel.
     * <p>
     * Used to track which plant buddy is currently being displayed
     * and to handle navigation between different plants.
     */
    private int currentPlantIndex = 0;

    /**
     * Handler for managing timed operations on the main UI thread.
     * <p>
     * Specifically used for the automatic mood cycling feature that
     * changes plant emotions every 3 seconds for presentation purposes.
     */
    private Handler moodCyclingHandler;

    /**
     * Runnable task for cycling through different plant moods.
     * <p>
     * This creates a presentation-friendly experience where plants
     * show all their emotional states automatically.
     */
    private Runnable moodCyclingRunnable;

    /**
     * Current mood index for the mood cycling feature.
     * <p>
     * Tracks which emotion is currently being displayed as part
     * of the automatic presentation cycling.
     */
    private int currentMoodIndex = 0;

    /**
     * Flag to track the direction of the last card swipe.
     * <p>
     * Used to determine which sliding animation to apply:
     * true = moving to next (slide right), false = moving to previous (slide left)
     */
    private boolean isMovingToNext = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding for type-safe access to UI components
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle system window insets for modern Android edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize core functionality
        setupPlantBuddies();
        updateUI();
        setupClickListeners();
        startMoodCycling();
    }

    /**
     * Initializes the plant buddies list with user-named plants and expansion placeholder.
     * <p>
     * Creates Plant objects for the radish and lettuce that were named during the
     * ceremony activity, plus adds an "Add New Buddy" option for future features.
     * The plants start with happy mood states as the default presentation state.
     */
    private void setupPlantBuddies() {
        plantBuddies = new ArrayList<>();

        // Retrieve plant names from the naming ceremony activity
        Intent intent = getIntent();
        String radishName = intent.getStringExtra("RADISH_NAME");
        String lettuceName = intent.getStringExtra("LETTUCE_NAME");

        // Create plant buddy instances with happy default states
        // Using happy variants as the starting point for mood cycling
        plantBuddies.add(new Plant(radishName, "radish", R.drawable.plant_radish_happy));
        plantBuddies.add(new Plant(lettuceName, "lettuce", R.drawable.iceberg_lettuce_happy));

        // Add expansion placeholder for future "add new plant" functionality
        plantBuddies.add(new Plant("Add New Buddy", "add", R.drawable.ic_add));
    }

    /**
     * Updates the user interface to reflect the currently selected plant buddy.
     * <p>
     * This method handles the dynamic content switching when users navigate between
     * different plant buddies. It shows/hides relevant UI elements based on whether
     * the current selection is an actual plant or the "add new buddy" placeholder.
     * <p>
     * For actual plants, it displays:
     * - Plant name and current status
     * - Growth stage information
     * - Care action buttons (water, sunlight, lab analysis)
     * - Information button for growing tips
     * <p>
     * For the "add new buddy" placeholder, it hides all plant-specific UI elements
     * to create a clean, focused interface for the expansion feature.
     */
    private void updateUI() {
        // Safety check to prevent crashes if plant list is empty
        if (plantBuddies.isEmpty()) return;

        Plant currentPlant = plantBuddies.get(currentPlantIndex);

        // Update plant name and avatar image
        binding.buddyName.setText(currentPlant.getName());
        binding.buddyAvatar.setImageResource(currentPlant.getImageResource());

        // Handle UI visibility based on plant type
        if (currentPlant.getType().equals("add")) {
            // Hide all plant-specific UI for the "add new buddy" placeholder
            hideAllPlantSpecificUI();
        } else {
            // Show all plant-specific UI for actual plant buddies
            showAllPlantSpecificUI();

            // Set plant-specific status and growth information
            binding.buddyStatus.setText(getString(R.string.status_happy));
            binding.buddyGrowthStage.setText(getString(R.string.stage_seed, currentPlant.getName()));
        }
    }

    /**
     * Hides all UI elements that are specific to plant care and monitoring.
     * <p>
     * This method is called when the "Add New Buddy" placeholder is selected,
     * creating a clean interface that focuses attention on the expansion feature
     * rather than confusing users with irrelevant plant care options.
     */
    private void hideAllPlantSpecificUI() {
        binding.buddyStatus.setVisibility(View.GONE);
        binding.buddyGrowthStage.setVisibility(View.GONE);
        binding.labelIWant.setVisibility(View.GONE);
        binding.buttonWaterBuddy.setVisibility(View.GONE);
        binding.buttonAnalyzePlant.setVisibility(View.GONE);
        binding.labelPlantAnalysis.setVisibility(View.GONE);
        binding.buttonSunlight.setVisibility(View.GONE);
        binding.buttonInfo.setVisibility(View.GONE);
    }

    /**
     * Shows all UI elements that are specific to plant care and monitoring.
     * <p>
     * This method is called when an actual plant buddy is selected,
     * providing the full interface for plant care, monitoring, and interaction.
     */
    private void showAllPlantSpecificUI() {
        binding.buddyStatus.setVisibility(View.VISIBLE);
        binding.buddyGrowthStage.setVisibility(View.VISIBLE);
        binding.labelIWant.setVisibility(View.VISIBLE);
        binding.buttonWaterBuddy.setVisibility(View.VISIBLE);
        binding.buttonAnalyzePlant.setVisibility(View.VISIBLE);
        binding.labelPlantAnalysis.setVisibility(View.VISIBLE);
        binding.buttonSunlight.setVisibility(View.VISIBLE);
        binding.buttonInfo.setVisibility(View.VISIBLE);
    }

    /**
     * Starts the automatic mood cycling feature for presentation purposes.
     * <p>
     * This creates a continuous loop that changes the plant's emotional state
     * every 3 seconds, cycling through happy, sad, and thirsty moods.
     * This feature is specifically designed for demonstration purposes to show
     * off all the different plant emotions and states.
     * <p>
     * The cycling only affects actual plant buddies, not the "add new buddy" placeholder.
     */
    private void startMoodCycling() {
        moodCyclingHandler = new Handler(Looper.getMainLooper());

        moodCyclingRunnable = new Runnable() {
            @Override
            public void run() {
                // Only cycle moods for actual plants, not the "add new buddy" placeholder
                if (currentPlantIndex < plantBuddies.size() - 1) {
                    cyclePlantMood();
                }

                // Schedule the next mood change in 3 seconds
                moodCyclingHandler.postDelayed(this, 3000);
            }
        };

        // Start the mood cycling with an initial delay
        moodCyclingHandler.postDelayed(moodCyclingRunnable, 3000);
    }

    /**
     * Cycles through different emotional states for the current plant buddy.
     * <p>
     * This method rotates through three emotional states:
     * 1. Happy - Plant is content and well-cared for
     * 2. Sad - Plant needs attention or care
     * 3. Thirsty - Plant specifically needs water
     * <p>
     * The cycling creates a dynamic presentation that showcases all the different
     * emotional feedback states that children can expect to see based on their
     * real-world plant care actions.
     */
    private void cyclePlantMood() {
        if (plantBuddies.isEmpty() || currentPlantIndex >= plantBuddies.size() - 1) {
            return;
        }

        Plant currentPlant = plantBuddies.get(currentPlantIndex);
        String plantType = currentPlant.getType();

        // Define mood resources for each plant type
        int[] radishMoods = {
                R.drawable.plant_radish_happy,
                R.drawable.plant_radish_sad,
                R.drawable.plant_radish_thirsty
        };

        int[] lettuceMoods = {
                R.drawable.iceberg_lettuce_happy,
                R.drawable.iceberg_lettuce_sad,
                R.drawable.iceberg_lettuce_thirsty
        };

        // Get the appropriate mood array based on plant type
        int[] currentMoods;
        if (plantType.equals("radish")) {
            currentMoods = radishMoods;
        } else if (plantType.equals("lettuce")) {
            currentMoods = lettuceMoods;
        } else {
            return; // Skip mood cycling for non-plant types
        }

        // Update to next mood in the cycle
        currentMoodIndex = (currentMoodIndex + 1) % currentMoods.length;
        int newImageResource = currentMoods[currentMoodIndex];

        // Update the plant's image resource and refresh the UI
        currentPlant.setImageResource(newImageResource);
        binding.buddyAvatar.setImageResource(newImageResource);

        // Update status text to match the current mood for better user feedback
        updateStatusForMood(currentMoodIndex);
    }

    /**
     * Updates the status text to match the current plant mood.
     * <p>
     * This provides contextual feedback that aligns with the visual
     * emotional state of the plant, creating a cohesive user experience.
     *
     * @param moodIndex The current mood index (0=happy, 1=sad, 2=thirsty)
     */
    private void updateStatusForMood(int moodIndex) {
        String statusText;
        switch (moodIndex) {
            case 0: // Happy
                statusText = getString(R.string.status_happy);
                break;
            case 1: // Sad
                statusText = getString(R.string.status_needs_attention);
                break;
            case 2: // Thirsty
                statusText = getString(R.string.status_thirsty);
                break;
            default:
                statusText = getString(R.string.status_happy);
        }
        binding.buddyStatus.setText(statusText);
    }

    /**
     * Animates the transition between plant buddies with realistic card-like movement.
     * <p>
     * This method creates a smooth, natural transition that mimics flipping through
     * physical cards. The entire card (including all content) moves as a single unit,
     * maintaining the visual integrity of the card metaphor.
     * <p>
     * The animation direction is determined by the navigation direction:
     * - Moving to next buddy: card slides out left, new card slides in from right
     * - Moving to previous buddy: card slides out right, new card slides in from left
     * <p>
     * Content is updated only when the card is completely off-screen to prevent
     * visual artifacts while maintaining the illusion of physical card movement.
     *
     * @param newIndex The index of the plant buddy to transition to
     */
    private void animateAndSwitch(final int newIndex) {
        // Determine slide out animation based on navigation direction
        Animation slideOut = isMovingToNext ?
                AnimationUtils.loadAnimation(this, R.anim.slide_out_left) :
                AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Disable navigation buttons to prevent rapid clicking during transition
                binding.arrowLeft.setEnabled(false);
                binding.arrowRight.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Card is now completely off-screen - safe to update content
                currentPlantIndex = newIndex;
                updateUI();

                // Start slide in animation with new content
                Animation slideIn = isMovingToNext ?
                        AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_right) :
                        AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_left);

                slideIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Slide in animation starting
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Animation complete - re-enable navigation
                        binding.arrowLeft.setEnabled(true);
                        binding.arrowRight.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Not used
                    }
                });

                binding.centralCard.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Not used in this implementation
            }
        });

        // Start the slide out animation
        binding.centralCard.startAnimation(slideOut);
    }

    /**
     * Adds visual press feedback animation to any view.
     * <p>
     * This method applies a subtle scale animation that provides immediate
     * visual feedback when buttons are pressed, making the interface feel
     * more responsive and tactile.
     *
     * @param view The view to animate when pressed
     */
    private void addPressAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    // Scale down slightly when pressed
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    // Scale back to normal when released
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                    break;
            }
            return false; // Allow other touch events to proceed
        });
    }

    /**
     * Sets up all click listeners for interactive UI elements.
     * <p>
     * This method configures the touch interactions for:
     * - Navigation arrows (left/right carousel movement)
     * - Menu icon (opens settings)
     * - Care action buttons (water, sunlight, lab analysis)
     * - Information button (growing tips and plant facts)
     * <p>
     * Each interaction provides appropriate feedback through localized toast messages
     * and triggers relevant animations or navigation actions.
     */
    private void setupClickListeners() {

        // Add press animations to interactive elements for better tactile feedback
        addPressAnimation(binding.menuIcon);
        addPressAnimation(binding.arrowLeft);
        addPressAnimation(binding.arrowRight);

        // Menu icon - opens settings activity
        binding.menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Left arrow - navigate to previous plant buddy
        binding.arrowLeft.setOnClickListener(v -> {
            isMovingToNext = false; // Set direction for animation
            int newIndex = (currentPlantIndex - 1 + plantBuddies.size()) % plantBuddies.size();
            animateAndSwitch(newIndex);
        });

        // Right arrow - navigate to next plant buddy
        binding.arrowRight.setOnClickListener(v -> {
            isMovingToNext = true; // Set direction for animation
            int newIndex = (currentPlantIndex + 1) % plantBuddies.size();
            animateAndSwitch(newIndex);
        });

        // Plant care action buttons - provide positive feedback for care actions
        setupCareActionButtons();

        // Information button - provides educational content about plant care
        binding.buttonInfo.setOnClickListener(v -> {
            Plant currentPlant = plantBuddies.get(currentPlantIndex);
            Toast.makeText(MainActivity.this,
                    getString(R.string.buddy_info_message),
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Sets up the plant care action buttons with appropriate feedback messages.
     * <p>
     * This method configures the three main care actions:
     * - Water buddy: Simulates watering the real plant
     * - Give sunlight: Simulates providing light to the plant
     * - Lab analysis: Encourages observation of the real plant
     * <p>
     * Each action provides encouraging feedback through localized toast messages
     * that reinforce positive plant care behaviors and guide children toward
     * real-world interaction with their physical plants.
     */
    private void setupCareActionButtons() {

        // Water buddy button - encourages real-world watering
        binding.buttonWaterBuddy.setOnClickListener(v -> {
            Plant currentPlant = plantBuddies.get(currentPlantIndex);
            String message = getString(R.string.buddy_watered_message, currentPlant.getName());
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        });

        // Give sunlight button - encourages proper plant positioning for light
        binding.buttonAnalyzePlant.setOnClickListener(v -> {
            Plant currentPlant = plantBuddies.get(currentPlantIndex);
            String message = getString(R.string.buddy_sunlight_message, currentPlant.getName());
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        });

        // Lab analysis button - encourages observation and scientific thinking
        binding.buttonSunlight.setOnClickListener(v -> {
            Plant currentPlant = plantBuddies.get(currentPlantIndex);
            String message = getString(R.string.buddy_lab_analysis_message, currentPlant.getName());
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up resources to prevent memory leaks
        if (moodCyclingHandler != null && moodCyclingRunnable != null) {
            moodCyclingHandler.removeCallbacks(moodCyclingRunnable);
        }

        // Release view binding reference
        binding = null;
    }

    /**
     * Plant - A simple data model representing a plant buddy in the app.
     * <p>
     * This lightweight class encapsulates the essential information needed
     * to represent a plant companion in the user interface. It follows the
     * principle of keeping the data model simple and focused.
     * <p>
     * The class is designed to be:
     * - Memory efficient for low-spec devices
     * - Easy to extend with additional properties in future versions
     * - Type-safe with private fields and public accessors
     * <p>
     * Plant types include:
     * - "radish" - A fast-growing root vegetable buddy
     * - "lettuce" - A leafy green companion plant
     * - "add" - A special placeholder for adding new plant buddies
     */
    private static class Plant {

        /**
         * The user-assigned name for this plant buddy.
         * <p>
         * This personal name creates an emotional connection between
         * the child and their plant, encouraging care and responsibility.
         */
        private final String name;

        /**
         * The botanical type of this plant.
         * <p>
         * Used to determine appropriate care instructions, growth timelines,
         * and visual representations. Current supported types are "radish",
         * "lettuce", and "add" (for the expansion placeholder).
         */
        private final String type;

        /**
         * The drawable resource ID for the current plant image.
         * <p>
         * This can change dynamically based on the plant's current mood
         * or growth stage, allowing for responsive visual feedback.
         */
        private int imageResource;

        /**
         * Creates a new Plant instance with the specified attributes.
         * <p>
         * This constructor initializes all the essential properties needed
         * to display and interact with a plant buddy in the user interface.
         *
         * @param name The user-assigned name for this plant buddy
         * @param type The botanical type ("radish", "lettuce", or "add")
         * @param imageResource The drawable resource ID for the plant's image
         */
        public Plant(String name, String type, int imageResource) {
            this.name = name;
            this.type = type;
            this.imageResource = imageResource;
        }

        /**
         * Returns the user-assigned name for this plant buddy.
         * <p>
         * This name was set during the naming ceremony and creates a personal
         * connection between the child and their plant companion.
         *
         * @return The plant's user-assigned name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the botanical type of this plant.
         * <p>
         * Used to determine care requirements, growth patterns, and
         * appropriate visual representations for the plant buddy.
         *
         * @return The plant type ("radish", "lettuce", or "add")
         */
        public String getType() {
            return type;
        }

        /**
         * Returns the current drawable resource ID for this plant's image.
         * <p>
         * This resource can change dynamically to reflect different moods
         * or growth stages, providing visual feedback to the user.
         *
         * @return The drawable resource ID for the plant's current image
         */
        public int getImageResource() {
            return imageResource;
        }

        /**
         * Updates the drawable resource ID for this plant's image.
         * <p>
         * This method allows for dynamic mood changes during the presentation
         * cycling feature, where plants automatically show different emotions.
         *
         * @param imageResource The new drawable resource ID to display
         */
        public void setImageResource(int imageResource) {
            this.imageResource = imageResource;
        }
    }
}