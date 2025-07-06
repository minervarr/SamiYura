package com.nava.samiyuri;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

/**
 * WelcomeActivity - The first impression and gateway to the Samiyura garden adventure.
 * <p>
 * This activity serves as the app's splash screen and intelligent routing system.
 * It performs essential startup tasks while presenting the app's branding to users.
 * The activity determines the user's journey based on their previous interactions:
 * first-time users are guided through language selection, while returning users
 * proceed directly to their personalized garden experience.
 * <p>
 * Key Responsibilities:
 * - Display app branding and create anticipation for the garden adventure
 * - Check for existing language preferences from previous app usage
 * - Apply saved language settings to ensure consistent user experience
 * - Route users to appropriate next screen based on their setup status
 * - Provide sufficient loading time for smooth transitions on low-spec devices
 * <p>
 * The activity embodies the app's "Seamless Experience" principle by eliminating
 * unnecessary steps for returning users while ensuring new users receive proper
 * onboarding. This creates a welcoming experience that respects both new and
 * returning users' time and preferences.
 * <p>
 * Technical Implementation:
 * - Uses Handler with postDelayed for non-blocking splash screen timing
 * - Integrates with SharedPreferences for language preference persistence
 * - Applies locale changes before UI navigation for consistent text rendering
 * - Optimized for low-memory devices with minimal resource usage
 *
 * @author Neriou
 * @version 1.0
 * @since 2025-07-06
 */
public class WelcomeActivity extends AppCompatActivity {

    /**
     * Duration of the splash screen display in milliseconds.
     * <p>
     * This timing is carefully chosen to balance several factors:
     * - Sufficient time for users to recognize the app's branding
     * - Allow for smooth language preference loading and application
     * - Provide buffer time for slower devices to complete initialization
     * - Avoid unnecessarily long delays that frustrate users
     * <p>
     * The 2-second duration provides a comfortable pacing that feels
     * intentional rather than sluggish, especially important for children
     * who may have shorter attention spans.
     */
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Check for existing language preference and apply it immediately
        // This ensures the subsequent screens display in the user's preferred language
        checkAndApplyLanguagePreference();

        // Use Handler with postDelayed to create a smooth, timed transition
        // This approach is more efficient than Thread.sleep() and doesn't block the UI
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextScreen();
        }, SPLASH_DELAY);
    }

    /**
     * Checks for saved language preference and applies it to the current session.
     * <p>
     * This method ensures that returning users see the app in their previously
     * selected language from the moment they interact with it. The language
     * application happens during the splash screen, making the transition
     * seamless and creating a consistent experience.
     * <p>
     * The method handles several scenarios gracefully:
     * - First-time users (no saved preference): continues with default system language
     * - Returning users: applies their saved language choice immediately
     * - Invalid/corrupted preferences: fails safely without crashing
     * <p>
     * This approach respects user preferences while maintaining app stability,
     * crucial for the trust and reliability expected in educational apps for children.
     */
    private void checkAndApplyLanguagePreference() {
        // Retrieve the saved language preference using the static utility method
        String savedLanguage = LanguageSelectionActivity.getSavedLanguagePreference(this);

        // If a language preference exists, apply it immediately
        if (savedLanguage != null && !savedLanguage.isEmpty()) {
            applyLanguageToApp(savedLanguage);
        }
        // If no preference exists, the app continues with the system default language
        // The user will be directed to language selection after the splash screen
    }

    /**
     * Applies the specified language locale to the current application context.
     * <p>
     * This method updates the app's locale configuration to display content
     * in the user's preferred language. The change takes effect immediately,
     * ensuring that all subsequent screens render text in the correct language.
     * <p>
     * The locale application process:
     * 1. Creates a Locale object from the language code
     * 2. Updates the JVM default locale for consistent formatting
     * 3. Modifies the app's Configuration object
     * 4. Applies the configuration to trigger UI language updates
     * <p>
     * Error handling ensures that even if locale application fails, the app
     * continues functioning with a sensible fallback, maintaining reliability
     * for users regardless of device capabilities or language support levels.
     *
     * @param languageCode The ISO language code to apply (e.g., "en", "es", "qu")
     */
    private void applyLanguageToApp(String languageCode) {
        try {
            // Create a Locale object for the specified language
            Locale locale = new Locale(languageCode);

            // Set as the default locale for the Java Virtual Machine
            // This affects number formatting, date formatting, and other locale-sensitive operations
            Locale.setDefault(locale);

            // Update the app's configuration with the new locale
            Configuration configuration = getResources().getConfiguration();
            configuration.setLocale(locale);

            // Apply the configuration change to trigger immediate UI updates
            // This ensures that any text rendered after this point uses the correct language
            getBaseContext().getResources().updateConfiguration(
                    configuration,
                    getBaseContext().getResources().getDisplayMetrics()
            );

        } catch (Exception e) {
            // If locale application fails, continue with current language
            // This prevents crashes while maintaining app functionality
            // The error is silently handled to avoid disrupting the user experience

            // In a production environment, you might want to log this for debugging:
            // Log.w("WelcomeActivity", "Failed to apply language: " + languageCode, e);
        }
    }

    /**
     * Determines and navigates to the appropriate next screen based on user setup status.
     * <p>
     * This method implements intelligent routing that creates a smooth onboarding
     * experience for new users while respecting the time of returning users.
     * The routing logic ensures users don't encounter unnecessary setup screens
     * they've already completed.
     * <p>
     * Navigation Logic:
     * - First-time users: Directed to language selection for proper setup
     * - Returning users: Skip directly to the story screen with their saved preferences
     * <p>
     * This approach follows the "Progressive Onboarding" principle, where users
     * only encounter setup steps they haven't completed, creating a respectful
     * and efficient user experience that adapts to individual user needs.
     */
    private void navigateToNextScreen() {
        // Check if the user has already selected a language preference
        String savedLanguage = LanguageSelectionActivity.getSavedLanguagePreference(this);

        Intent intent;

        if (savedLanguage == null || savedLanguage.isEmpty()) {
            // First-time user or language preference not set
            // Direct them to language selection for proper onboarding
            intent = new Intent(WelcomeActivity.this, LanguageSelectionActivity.class);
        } else {
            // Returning user with saved preferences
            // Skip language selection and proceed directly to the story
            intent = new Intent(WelcomeActivity.this, StoryActivity.class);
        }

        // Start the appropriate activity based on user setup status
        startActivity(intent);

        // Finish this activity to prevent users from navigating back to the splash screen
        // This creates a cleaner navigation flow and prevents confusion
        finish();
    }
}