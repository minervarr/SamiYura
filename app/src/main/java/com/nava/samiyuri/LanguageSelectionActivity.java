package com.nava.samiyuri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.nava.samiyuri.databinding.ActivityLanguageSelectionBinding;
import com.nava.samiyuri.utils.LanguageDetector;
import com.nava.samiyuri.utils.LanguageInfo;

import java.util.List;
import java.util.Locale;

/**
 * LanguageSelectionActivity - Dynamic, self-updating language selection interface.
 * <p>
 * This activity provides a completely flexible language selection system that
 * automatically adapts to any number of languages without requiring code changes.
 * Simply adding a values-* folder to the project makes that language available
 * in this interface with appropriate layout and visual adaptations.
 * <p>
 * The interface intelligently chooses the best layout based on language count:
 * - 2 languages: Horizontal card layout (optimal for phone screens)
 * - 3-6 languages: Grid layout (efficient use of screen space)
 * - 7+ languages: Vertical list with search capabilities
 * <p>
 * Key Features:
 * - Zero-configuration language addition (add folder → language appears)
 * - Automatic layout optimization based on language count
 * - Custom language support (fictional, themed, accessibility variants)
 * - Persistent language preference storage
 * - Smooth animations and visual feedback
 * - Accessibility-friendly navigation and selection
 * <p>
 * Architecture Philosophy:
 * This activity embodies the "Add Folder, Get Language" principle, making
 * internationalization as simple as adding translation files. The UI automatically
 * scales and adapts, requiring zero maintenance as languages are added.
 *
 * @author Neriou
 * @version 1.0
 * @since 2025-07-06
 */
public class LanguageSelectionActivity extends AppCompatActivity {

    /**
     * View binding instance for type-safe access to layout components.
     * <p>
     * Using ViewBinding provides compile-time safety and better performance
     * than findViewById(), which is especially important on low-spec target devices.
     */
    private ActivityLanguageSelectionBinding binding;

    /**
     * SharedPreferences configuration for persistent language storage.
     * <p>
     * These constants ensure consistent preference storage across the app
     * and provide a single source of truth for language preference management.
     */
    private static final String PREFS_NAME = "SamiyuraPrefs";
    private static final String LANGUAGE_KEY = "selected_language";

    /**
     * List of all discovered languages available in the app.
     * <p>
     * Populated by LanguageDetector during onCreate and used to build
     * the dynamic language selection interface.
     */
    private List<LanguageInfo> availableLanguages;

    /**
     * Adapter for the RecyclerView that displays language options.
     * <p>
     * This adapter handles the dynamic creation of language selection cards
     * and manages the selection interactions and visual feedback.
     */
    private LanguageAdapter languageAdapter;

    /**
     * Currently selected language during the selection process.
     * <p>
     * Used to track selection state during animations and to apply
     * the final language choice when selection completes.
     */
    private LanguageInfo selectedLanguage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if language has already been selected
        if (isLanguageAlreadySelected()) {
            // Skip language selection and go directly to welcome screen
            proceedToWelcome();
            return;
        }

        // Initialize view binding
        binding = ActivityLanguageSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Discover available languages and setup interface
        discoverLanguagesAndSetupUI();

        // Start welcome animations
        startWelcomeAnimation();
    }

    /**
     * Discovers all available languages and configures the UI accordingly.
     * <p>
     * This method performs the core language discovery process and automatically
     * configures the interface layout based on the number of languages found.
     * It handles everything from language detection to layout optimization.
     */
    private void discoverLanguagesAndSetupUI() {
        // Discover all available languages using the detection system
        availableLanguages = LanguageDetector.getAvailableLanguages(this);

        // Configure the interface based on the number of languages found
        setupDynamicLayout();

        // Create and configure the language selection adapter
        setupLanguageAdapter();

        // Update informational text based on language count
        updateInformationText();
    }

    /**
     * Configures the optimal layout based on the number of available languages.
     * <p>
     * This method implements intelligent layout selection that provides the
     * best user experience regardless of how many languages are available.
     * The layout automatically adapts from horizontal cards to grids to lists.
     */
    private void setupDynamicLayout() {
        RecyclerView recyclerView = binding.languagesRecyclerView;

        int languageCount = availableLanguages.size();

        if (languageCount <= 2) {
            // Optimal for 1-2 languages: Horizontal card layout
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        } else if (languageCount <= 6) {
            // Optimal for 3-6 languages: Grid layout for efficient space usage
            int columns = (languageCount <= 4) ? 2 : 3;
            recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        } else {
            // Optimal for 7+ languages: Vertical list with potential search
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            // For many languages, show search hint
            binding.languageSubtitle.setText(getString(R.string.language_selection_many_subtitle));
        }
    }

    /**
     * Creates and configures the RecyclerView adapter for language selection.
     * <p>
     * This method sets up the adapter that dynamically creates language selection
     * cards based on the discovered languages. Each card includes appropriate
     * visual elements and interaction handlers.
     */
    private void setupLanguageAdapter() {
        languageAdapter = new LanguageAdapter(availableLanguages, this::onLanguageSelected);
        binding.languagesRecyclerView.setAdapter(languageAdapter);
    }

    /**
     * Updates informational text based on the number of available languages.
     * <p>
     * This method adjusts the instructional text to provide appropriate
     * guidance based on how many language options are available.
     */
    private void updateInformationText() {
        if (availableLanguages.size() == 1) {
            // Only default language available
            binding.languageSubtitle.setText(getString(R.string.language_selection_single_subtitle));
        } else if (availableLanguages.size() > 10) {
            // Many languages available
            binding.languageSubtitle.setText(getString(R.string.language_selection_many_subtitle));
        }
        // Default text is already set in XML for normal cases
    }

    /**
     * Handles language selection events from the adapter.
     * <p>
     * This method is called when a user selects a language option. It manages
     * the selection animation sequence and applies the language choice.
     *
     * @param languageInfo The selected language information
     */
    private void onLanguageSelected(LanguageInfo languageInfo) {
        selectedLanguage = languageInfo;

        // Animate selection feedback and apply language
        animateLanguageSelection(() -> {
            selectLanguage(languageInfo.getLanguageCode());
        });
    }

    /**
     * Checks if the user has previously selected a language preference.
     * <p>
     * This method prevents showing the language selection screen on every
     * app launch, only displaying it on the very first run or when explicitly
     * requested from settings.
     *
     * @return true if a language preference exists, false otherwise
     */
    private boolean isLanguageAlreadySelected() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedLanguage = prefs.getString(LANGUAGE_KEY, null);

        if (savedLanguage != null) {
            // Apply the saved language before proceeding
            applyLanguagePreference(savedLanguage);
            return true;
        }
        return false;
    }

    /**
     * Starts the welcome animation sequence for the language selection screen.
     * <p>
     * This method creates a delightful entrance animation that sets a positive
     * tone for the app experience. The animation is designed to be engaging
     * without being overwhelming for young users.
     */
    private void startWelcomeAnimation() {
        // Fade in the main content
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn.setDuration(800);
        binding.getRoot().startAnimation(fadeIn);

        // Stagger the RecyclerView animation for a polished entrance
        binding.languagesRecyclerView.setAlpha(0f);
        binding.languagesRecyclerView.animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(400)
                .start();
    }

    /**
     * Animates the language selection feedback and executes a callback.
     * <p>
     * This method provides visual confirmation of the user's choice through
     * a celebration-style animation sequence before applying the language change.
     *
     * @param onComplete Callback to execute when animation completes
     */
    private void animateLanguageSelection(Runnable onComplete) {
        // Fade out the interface gradually for a smooth transition
        binding.languagesRecyclerView.animate()
                .alpha(0.3f)
                .setDuration(400)
                .start();

        binding.languageSubtitle.animate()
                .alpha(0.5f)
                .setDuration(400)
                .withEndAction(() -> {
                    // Execute the completion callback after animation
                    if (onComplete != null) {
                        onComplete.run();
                    }
                })
                .start();
    }

    /**
     * Processes the user's language selection and updates app preferences.
     * <p>
     * This method handles the complete language selection workflow including
     * preference storage, immediate language application, and navigation to
     * the main app experience.
     *
     * @param languageCode The ISO language code to apply
     */
    private void selectLanguage(String languageCode) {
        // Save the language preference for future app launches
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply();

        // Apply the language preference immediately
        applyLanguagePreference(languageCode);

        // Proceed to the main app experience with transition delay
        binding.getRoot().postDelayed(this::proceedToWelcome, 600);
    }

    /**
     * Applies the selected language preference to the current app session.
     * <p>
     * This method updates the app's locale configuration to reflect the user's
     * choice. All string resources automatically switch to the selected language.
     *
     * @param languageCode The ISO language code to apply
     */
    private void applyLanguagePreference(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        // Update the app's configuration to use the new locale
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /**
     * Transitions to the welcome screen to begin the main app experience.
     * <p>
     * This method starts the welcome activity and finishes the language
     * selection activity to prevent users from returning through the back button.
     */
    private void proceedToWelcome() {
        Intent intent = new Intent(LanguageSelectionActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // Prevent returning to language selection
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up view binding reference to prevent memory leaks
        binding = null;
    }

    /**
     * LanguageAdapter - RecyclerView adapter for dynamic language selection cards.
     * <p>
     * This adapter creates language selection cards dynamically based on the
     * discovered languages. Each card is visually appealing and provides
     * appropriate feedback for user interactions.
     */
    private static class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

        /**
         * List of languages to display in the interface.
         */
        private final List<LanguageInfo> languages;

        /**
         * Callback interface for handling language selection events.
         */
        private final OnLanguageSelectedListener listener;

        /**
         * Interface for language selection event callbacks.
         */
        interface OnLanguageSelectedListener {
            void onLanguageSelected(LanguageInfo languageInfo);
        }

        /**
         * Creates a new LanguageAdapter with the specified languages and listener.
         *
         * @param languages List of languages to display
         * @param listener Callback for selection events
         */
        public LanguageAdapter(List<LanguageInfo> languages, OnLanguageSelectedListener listener) {
            this.languages = languages;
            this.listener = listener;
        }

        @Override
        public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_language_card, parent, false);
            return new LanguageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LanguageViewHolder holder, int position) {
            LanguageInfo language = languages.get(position);
            holder.bind(language, listener);
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }

        /**
         * ViewHolder for individual language selection cards.
         * <p>
         * This class manages the visual representation and interaction
         * handling for each language option in the selection interface.
         */
        static class LanguageViewHolder extends RecyclerView.ViewHolder {

            private final MaterialCardView cardView;
            private final TextView symbolText;
            private final TextView nameText;
            private final TextView sampleText;

            /**
             * Creates a new ViewHolder and initializes UI components.
             *
             * @param itemView The inflated card view
             */
            public LanguageViewHolder(View itemView) {
                super(itemView);
                cardView = (MaterialCardView) itemView;
                symbolText = itemView.findViewById(R.id.language_symbol);
                nameText = itemView.findViewById(R.id.language_name);
                sampleText = itemView.findViewById(R.id.language_sample);
            }

            /**
             * Binds language data to the card view and sets up interaction handling.
             *
             * @param language The language information to display
             * @param listener Callback for selection events
             */
            public void bind(LanguageInfo language, OnLanguageSelectedListener listener) {
                // Populate card with language information
                symbolText.setText(language.getSymbol());
                nameText.setText(language.getDisplayName());
                sampleText.setText(language.getSampleText());

                // Setup press animation for tactile feedback
                addPressAnimation(cardView);

                // Setup selection handling
                cardView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onLanguageSelected(language);
                    }
                });
            }

            /**
             * Adds visual press feedback animation to the card.
             * <p>
             * This method provides immediate tactile feedback when cards are
             * pressed, making the interface feel more responsive.
             *
             * @param view The card view to animate
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
        }
    }
}