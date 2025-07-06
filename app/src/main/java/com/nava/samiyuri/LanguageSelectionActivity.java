package com.nava.samiyuri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.nava.samiyuri.databinding.ActivityLanguageSelectionBinding;
import java.util.Locale;

/**
 * LanguageSelectionActivity - The gateway to the Samiyura garden adventure.
 * <p>
 * This activity serves as the first meaningful interaction point where children
 * (with their parents) choose their preferred language for the entire app experience.
 * The selection is crucial for creating an inclusive, accessible experience that
 * honors the linguistic diversity of Peru and ensures children can learn in their
 * native or preferred language.
 * <p>
 * Key Features:
 * - Support for 5 languages: English, Spanish, Quechua, Aymara, and Ashaninka
 * - Persistent language preference storage using SharedPreferences
 * - Immediate locale application without requiring app restart
 * - Child-friendly interface with large, colorful buttons
 * - Cultural sensitivity in language presentation (native names + English clarification)
 * <p>
 * The design philosophy follows "Accessibility First" - ensuring that children
 * from diverse linguistic backgrounds can fully engage with the gardening experience
 * in a language that feels natural and comfortable to them.
 * <p>
 * Technical Implementation:
 * - Uses Android's built-in locale system for proper text rendering
 * - Stores language preference for future app launches
 * - Gracefully handles Right-to-Left (RTL) languages if needed in future
 * - Optimized for low-spec devices with minimal memory overhead
 *
 * @author Neriou
 * @version 1.0
 * @since 2025-07-06
 */
public class LanguageSelectionActivity extends AppCompatActivity {

    /**
     * View binding instance for type-safe access to layout components.
     * <p>
     * Using ViewBinding instead of findViewById() for better performance
     * and compile-time safety, especially important on low-spec devices
     * where every optimization matters for smooth user experience.
     */
    private ActivityLanguageSelectionBinding binding;

    /**
     * SharedPreferences key for storing the selected language code.
     * <p>
     * This constant ensures consistency across the app when retrieving
     * the user's language preference. The key is intentionally descriptive
     * to avoid conflicts with other app preferences.
     */
    private static final String LANGUAGE_PREFERENCE_KEY = "selected_language_code";

    /**
     * SharedPreferences file name for app-wide settings.
     * <p>
     * Using a dedicated preferences file for app settings ensures clean
     * separation from other potential SharedPreferences files and makes
     * data management more organized for future features.
     */
    private static final String PREFERENCES_FILE_NAME = "samiyura_settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding for type-safe access to UI components
        binding = ActivityLanguageSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up click listeners for all language selection buttons
        setupLanguageSelectionListeners();

        // Add visual press feedback to enhance tactile experience
        addPressAnimationToButtons();
    }

    /**
     * Sets up click listeners for all language selection buttons.
     * <p>
     * Each button triggers the language selection process with the appropriate
     * locale code. The locale codes follow ISO 639-1 standard where possible,
     * with appropriate fallbacks for indigenous languages that may not have
     * standardized codes in Android's locale system.
     * <p>
     * Language Codes:
     * - English: "en" (ISO 639-1)
     * - Spanish: "es" (ISO 639-1)
     * - Quechua: "qu" (ISO 639-1)
     * - Aymara: "ay" (ISO 639-1)
     * - Ashaninka: "cni" (ISO 639-3, fallback to Spanish if not supported)
     */
    private void setupLanguageSelectionListeners() {

        // English selection - International standard language
        binding.buttonLanguageEnglish.setOnClickListener(v ->
                selectLanguageAndProceed("en"));

        // Spanish selection - Primary official language of Peru
        binding.buttonLanguageSpanish.setOnClickListener(v ->
                selectLanguageAndProceed("es"));

        // Quechua selection - Indigenous language family of the Andes
        // Most widely spoken indigenous language in Peru
        binding.buttonLanguageQuechua.setOnClickListener(v ->
                selectLanguageAndProceed("qu"));

        // Aymara selection - Indigenous language of the Altiplano region
        // Spoken primarily in southern Peru, Bolivia, and northern Chile
        binding.buttonLanguageAymara.setOnClickListener(v ->
                selectLanguageAndProceed("ay"));

        // Ashaninka selection - Indigenous language of the Amazon region
        // Represents the linguistic diversity of Peru's rainforest communities
        // Note: May fallback to Spanish on devices without full Ashaninka support
        binding.buttonLanguageAshaninka.setOnClickListener(v ->
                selectLanguageAndProceed("cni"));
    }

    /**
     * Applies the selected language to the app and navigates to the story screen.
     * <p>
     * This method handles the complete language selection workflow:
     * 1. Updates the app's locale configuration
     * 2. Saves the preference for future app launches
     * 3. Navigates to the story activity with the new language active
     * 4. Finishes this activity to prevent back navigation to language selection
     * <p>
     * The language change takes effect immediately, demonstrating the selection
     * to users and providing instant feedback. This is especially important for
     * children who need clear, immediate responses to their actions.
     *
     * @param languageCode The ISO language code (e.g., "en", "es", "qu")
     */
    private void selectLanguageAndProceed(String languageCode) {

        // Apply the selected locale to the current app session
        setAppLocale(languageCode);

        // Persist the language choice for future app launches
        saveLanguagePreference(languageCode);

        // Navigate to the story activity with the new language active
        Intent intent = new Intent(LanguageSelectionActivity.this, StoryActivity.class);
        startActivity(intent);

        // Finish this activity to prevent users from navigating back
        // Once a language is selected, they should proceed with the story
        finish();
    }

    /**
     * Applies the selected locale to the current application context.
     * <p>
     * This method immediately changes the app's language without requiring
     * a restart. It updates the Configuration object which controls how
     * Android renders text, formats numbers, and handles other locale-specific
     * elements throughout the app.
     * <p>
     * The locale change affects:
     * - All text loaded from strings.xml resources
     * - Date and time formatting
     * - Number formatting
     * - Text direction (important for potential RTL language support)
     * <p>
     * For indigenous languages that may not be fully supported by Android's
     * built-in locale system, the method gracefully falls back to Spanish
     * to ensure the app remains functional while preserving as much linguistic
     * authenticity as possible.
     *
     * @param languageCode The ISO language code to apply (e.g., "en", "es", "qu")
     */
    private void setAppLocale(String languageCode) {
        try {
            // Create a new Locale object for the selected language
            Locale locale = new Locale(languageCode);

            // Set this as the default locale for the JVM
            // This affects formatting and other locale-sensitive operations
            Locale.setDefault(locale);

            // Get the current configuration and update it with the new locale
            Configuration configuration = getResources().getConfiguration();
            configuration.setLocale(locale);

            // Apply the new configuration to the app context
            // This triggers immediate UI updates with the new language
            getBaseContext().getResources().updateConfiguration(
                    configuration,
                    getBaseContext().getResources().getDisplayMetrics()
            );

        } catch (Exception e) {
            // If there's any issue with the locale change, fall back to Spanish
            // This ensures the app remains functional even with unsupported language codes
            Locale fallbackLocale = new Locale("es");
            Locale.setDefault(fallbackLocale);

            Configuration configuration = getResources().getConfiguration();
            configuration.setLocale(fallbackLocale);
            getBaseContext().getResources().updateConfiguration(
                    configuration,
                    getBaseContext().getResources().getDisplayMetrics()
            );
        }
    }

    /**
     * Saves the selected language preference to persistent storage.
     * <p>
     * This method stores the user's language choice in SharedPreferences,
     * ensuring that the selected language persists across app launches.
     * The preference is saved immediately to prevent data loss and to
     * provide a seamless experience when the app is reopened.
     * <p>
     * The language preference is stored as a simple string code that can
     * be easily retrieved and applied when the app starts up. This approach
     * is lightweight and efficient, crucial for optimal performance on
     * low-specification devices.
     *
     * @param languageCode The ISO language code to save (e.g., "en", "es", "qu")
     */
    private void saveLanguagePreference(String languageCode) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Store the language code for retrieval on future app launches
        editor.putString(LANGUAGE_PREFERENCE_KEY, languageCode);

        // Apply changes immediately to ensure persistence
        editor.apply();
    }

    /**
     * Retrieves the previously selected language preference from storage.
     * <p>
     * This static method can be called from other activities (particularly
     * the WelcomeActivity) to check if a language has already been selected.
     * If no language preference exists, it returns null, indicating that
     * the language selection screen should be shown.
     * <p>
     * This method supports the app's goal of showing the language selection
     * only once per installation, creating a smooth onboarding experience
     * that doesn't burden users with repeated language selection.
     *
     * @param context The context to access SharedPreferences
     * @return The saved language code, or null if no preference exists
     */
    public static String getSavedLanguagePreference(android.content.Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        return preferences.getString(LANGUAGE_PREFERENCE_KEY, null);
    }

    /**
     * Adds visual press feedback animation to all language selection buttons.
     * <p>
     * This method enhances the tactile experience by providing immediate
     * visual feedback when buttons are pressed. The subtle scale animation
     * makes the interface feel more responsive and engaging, which is
     * particularly important for child users who benefit from clear,
     * immediate feedback on their interactions.
     * <p>
     * The animations are lightweight and designed to work smoothly even
     * on low-specification devices, maintaining the app's commitment to
     * accessibility across all hardware tiers.
     */
    private void addPressAnimationToButtons() {
        addPressAnimation(binding.buttonLanguageEnglish);
        addPressAnimation(binding.buttonLanguageSpanish);
        addPressAnimation(binding.buttonLanguageQuechua);
        addPressAnimation(binding.buttonLanguageAymara);
        addPressAnimation(binding.buttonLanguageAshaninka);
    }

    /**
     * Applies press animation effect to a specific view.
     * <p>
     * This utility method creates a subtle scale effect that provides
     * immediate visual feedback when the view is touched. The animation
     * scales the view down slightly when pressed and returns it to normal
     * size when released, creating a satisfying "button press" feeling.
     * <p>
     * The animation parameters are optimized for:
     * - Quick response time (100ms duration)
     * - Subtle effect (95% scale) that's noticeable but not jarring
     * - Smooth performance on low-specification devices
     *
     * @param view The view to apply the press animation effect to
     */
    private void addPressAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    // Scale down slightly when pressed for immediate feedback
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    // Scale back to normal when released
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
                    break;
            }

            // Return false to allow other touch events (like onClick) to proceed
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up view binding reference to prevent memory leaks
        // This is particularly important on low-spec devices where memory management
        // is crucial for maintaining smooth performance
        binding = null;
    }
}