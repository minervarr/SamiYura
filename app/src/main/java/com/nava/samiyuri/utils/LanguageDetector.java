package com.nava.samiyuri.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * LanguageDetector - Automatic discovery and metadata generation for app languages.
 * <p>
 * This utility class provides the core functionality for Samiyura's dynamic language
 * system. It automatically scans the APK at runtime to discover all available language
 * resources and generates appropriate metadata for display in the language selection UI.
 * <p>
 * The detector supports unlimited languages including:
 * - Standard ISO languages (en, es, fr, etc.)
 * - Regional variants (es-rPE, zh-rCN, etc.)
 * - Custom fictional languages (gardenspeak, elvish, etc.)
 * - Educational variants (kidlang, simple, advanced)
 * - Accessibility adaptations (dyslexic, minimal, pictographic)
 * <p>
 * Key Features:
 * - Zero-configuration language detection
 * - Automatic UI metadata generation
 * - Custom language metadata system
 * - Fallback display name generation
 * - Performance-optimized caching
 * - Comprehensive error handling
 * <p>
 * Architecture Philosophy:
 * This class embodies the "Add Folder, Get Language" principle - simply adding
 * a values-* folder automatically makes that language available throughout the app
 * with no code changes required.
 *
 * @author Neriou
 * @version 1.0
 * @since 2025-07-06
 */
public class LanguageDetector {

    private static final String TAG = "LanguageDetector";

    /**
     * Cache for discovered languages to avoid repeated APK scanning.
     * <p>
     * Language discovery involves file system operations which can be
     * expensive on low-spec devices. Caching ensures the detection
     * only happens once per app session.
     */
    private static List<LanguageInfo> cachedLanguages = null;

    /**
     * Metadata for custom languages that don't have system-provided information.
     * <p>
     * This map provides display names, symbols, and sample text for custom
     * language codes that won't be recognized by the Android system.
     * <p>
     * Developers can extend this map to add metadata for their custom languages,
     * making them appear professionally in the language selection interface.
     */
    private static final Map<String, CustomLanguageMetadata> CUSTOM_LANGUAGE_METADATA = new HashMap<>();

    static {
        // Initialize metadata for custom languages
        setupCustomLanguageMetadata();
    }

    /**
     * Discovers and returns all available languages in the app.
     * <p>
     * This is the main entry point for the language selection system.
     * It performs automatic APK scanning to find all values-* folders
     * and generates appropriate LanguageInfo objects for each discovered language.
     * <p>
     * The method includes intelligent caching to ensure performance on
     * low-spec devices, and comprehensive error handling to gracefully
     * handle APK access issues.
     *
     * @param context Android context for accessing app resources
     * @return List of all available languages with display metadata
     */
    public static List<LanguageInfo> getAvailableLanguages(Context context) {
        // Return cached results if available
        if (cachedLanguages != null) {
            Log.d(TAG, "Returning cached languages: " + cachedLanguages.size());
            return cachedLanguages;
        }

        Log.d(TAG, "Starting language discovery...");
        List<LanguageInfo> languages = new ArrayList<>();

        // Always include default English as the first option
        languages.add(createDefaultEnglishLanguage());

        // Scan APK for additional language folders
        try {
            AssetManager assets = context.getAssets();
            String[] allAssets = assets.list("");

            if (allAssets != null) {
                for (String assetName : allAssets) {
                    if (assetName.startsWith("values-") && !assetName.equals("values-night")) {
                        // Extract language code from folder name
                        String languageCode = assetName.substring(7); // Remove "values-"

                        // Skip if we already have this language
                        if (!containsLanguageCode(languages, languageCode)) {
                            LanguageInfo languageInfo = createLanguageInfo(languageCode, context);
                            if (languageInfo != null) {
                                languages.add(languageInfo);
                                Log.d(TAG, "Discovered language: " + languageCode);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error scanning assets for languages", e);
            // Continue with just default English
        }

        // Cache the results for future calls
        cachedLanguages = languages;
        Log.d(TAG, "Language discovery complete. Found " + languages.size() + " languages.");

        return languages;
    }

    /**
     * Creates the default English language entry.
     * <p>
     * English serves as the fallback language and is always available
     * even if no explicit values-en folder exists. This ensures the
     * app always has at least one language option.
     *
     * @return LanguageInfo object for default English
     */
    private static LanguageInfo createDefaultEnglishLanguage() {
        return new LanguageInfo(
                "en",
                "English",
                "English",
                "🇺🇸",
                "Let's grow together!",
                false
        );
    }

    /**
     * Creates a LanguageInfo object for a discovered language code.
     * <p>
     * This method handles the complexity of generating display metadata
     * for both standard and custom languages. It attempts to use system-provided
     * information when available, falling back to custom metadata or
     * intelligent defaults for unknown languages.
     *
     * @param languageCode The language code extracted from folder name
     * @param context Android context for locale operations
     * @return LanguageInfo object with complete metadata, or null if invalid
     */
    private static LanguageInfo createLanguageInfo(String languageCode, Context context) {
        try {
            // Check if this is a custom language with metadata
            if (CUSTOM_LANGUAGE_METADATA.containsKey(languageCode)) {
                return createCustomLanguageInfo(languageCode);
            }

            // Try to create a standard language using system information
            return createStandardLanguageInfo(languageCode);

        } catch (Exception e) {
            Log.w(TAG, "Could not create language info for: " + languageCode, e);
            return null;
        }
    }

    /**
     * Creates LanguageInfo for custom languages using predefined metadata.
     * <p>
     * Custom languages are completely defined by the developer and won't
     * have any system-provided information. This method uses the metadata
     * from CUSTOM_LANGUAGE_METADATA to create professional language entries.
     *
     * @param languageCode The custom language code
     * @return LanguageInfo object with custom metadata
     */
    private static LanguageInfo createCustomLanguageInfo(String languageCode) {
        CustomLanguageMetadata metadata = CUSTOM_LANGUAGE_METADATA.get(languageCode);
        return new LanguageInfo(
                languageCode,
                metadata.displayName,
                metadata.nativeName,
                metadata.symbol,
                metadata.sampleText,
                true
        );
    }

    /**
     * Creates LanguageInfo for standard languages using system information.
     * <p>
     * This method attempts to use Android's built-in locale system to
     * generate appropriate display names and metadata. It handles both
     * simple language codes (es, fr) and regional variants (es-rPE, zh-rCN).
     *
     * @param languageCode The standard language code
     * @return LanguageInfo object with system-generated metadata
     */
    private static LanguageInfo createStandardLanguageInfo(String languageCode) {
        Locale targetLocale = createLocaleFromCode(languageCode);

        // Get display names from the system
        String displayName = getDisplayName(targetLocale);
        String nativeName = getNativeName(targetLocale);
        String symbol = getLanguageSymbol(languageCode);
        String sampleText = getDefaultSampleText(languageCode);

        return new LanguageInfo(
                languageCode,
                displayName,
                nativeName,
                symbol,
                sampleText,
                false
        );
    }

    /**
     * Creates a Locale object from a language code string.
     * <p>
     * This method parses various language code formats and creates
     * appropriate Locale objects for Android system integration.
     *
     * @param languageCode The language code to parse
     * @return Locale object for system use
     */
    private static Locale createLocaleFromCode(String languageCode) {
        if (languageCode.contains("-r")) {
            // Regional variant like "es-rPE"
            String[] parts = languageCode.split("-r");
            return new Locale(parts[0], parts[1]);
        } else if (languageCode.contains("-")) {
            // Other variants like "zh-CN"
            String[] parts = languageCode.split("-");
            return new Locale(parts[0], parts[1]);
        }

        // Simple language code
        return new Locale(languageCode);
    }

    /**
     * Gets the display name for a locale in the current system language.
     * <p>
     * This provides localized language names that users will understand.
     * Falls back to the language code if no display name is available.
     *
     * @param locale The target locale
     * @return Human-readable display name
     */
    private static String getDisplayName(Locale locale) {
        String displayName = locale.getDisplayName();
        if (displayName != null && !displayName.equals(locale.toString())) {
            return capitalizeFirst(displayName);
        }
        return capitalizeFirst(locale.getLanguage());
    }

    /**
     * Gets the native name for a locale (how speakers write the language name).
     * <p>
     * This provides authentic language names that speakers of the target
     * language will recognize and appreciate.
     *
     * @param locale The target locale
     * @return Native language name
     */
    private static String getNativeName(Locale locale) {
        String nativeName = locale.getDisplayName(locale);
        if (nativeName != null && !nativeName.equals(locale.toString())) {
            return capitalizeFirst(nativeName);
        }
        return getDisplayName(locale);
    }

    /**
     * Gets an appropriate symbol (emoji flag, icon) for a language.
     * <p>
     * This method provides visual identification for languages in the UI.
     * It uses a combination of known mappings and intelligent defaults.
     *
     * @param languageCode The language code
     * @return Symbol string (emoji, icon reference, etc.)
     */
    private static String getLanguageSymbol(String languageCode) {
        // Map of language codes to symbols
        Map<String, String> symbols = new HashMap<>();
        symbols.put("es", "🇪🇸");
        symbols.put("es-rPE", "🇵🇪");
        symbols.put("es-rMX", "🇲🇽");
        symbols.put("es-rAR", "🇦🇷");
        symbols.put("fr", "🇫🇷");
        symbols.put("de", "🇩🇪");
        symbols.put("pt", "🇵🇹");
        symbols.put("pt-rBR", "🇧🇷");
        symbols.put("zh", "🇨🇳");
        symbols.put("zh-rCN", "🇨🇳");
        symbols.put("zh-rTW", "🇹🇼");
        symbols.put("ja", "🇯🇵");
        symbols.put("ko", "🇰🇷");
        symbols.put("qu", "🏔️"); // Quechua - mountain symbol
        symbols.put("ay", "🌄"); // Aymara - mountain symbol
        symbols.put("gn", "🇵🇾"); // Guaraní - Paraguay flag

        return symbols.getOrDefault(languageCode, "🌍");
    }

    /**
     * Generates default sample text for a language.
     * <p>
     * Sample text gives users a preview of the language character.
     * This method provides garden-themed defaults that fit the app's purpose.
     *
     * @param languageCode The language code
     * @return Sample text string
     */
    private static String getDefaultSampleText(String languageCode) {
        Map<String, String> sampleTexts = new HashMap<>();
        sampleTexts.put("es", "¡Vamos a crecer!");
        sampleTexts.put("fr", "Cultivons ensemble!");
        sampleTexts.put("de", "Lass uns wachsen!");
        sampleTexts.put("pt", "Vamos crescer!");
        sampleTexts.put("zh", "一起成长！");
        sampleTexts.put("ja", "一緒に育てよう！");
        sampleTexts.put("ko", "함께 키워요!");
        sampleTexts.put("qu", "Wiñasun!");

        return sampleTexts.getOrDefault(languageCode, "Let's grow!");
    }

    /**
     * Checks if a language code is already present in the languages list.
     * <p>
     * Used to prevent duplicate language entries during discovery.
     *
     * @param languages List of already discovered languages
     * @param languageCode Code to check for
     * @return true if language is already in the list
     */
    private static boolean containsLanguageCode(List<LanguageInfo> languages, String languageCode) {
        for (LanguageInfo lang : languages) {
            if (lang.getLanguageCode().equals(languageCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Capitalizes the first letter of a string.
     * <p>
     * Used to ensure consistent display formatting for language names.
     *
     * @param text Text to capitalize
     * @return Text with first letter capitalized
     */
    private static String capitalizeFirst(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Sets up metadata for custom languages.
     * <p>
     * This method initializes the CUSTOM_LANGUAGE_METADATA map with
     * information for custom/fictional languages that won't have
     * system-provided metadata.
     * <p>
     * Developers can extend this method to add metadata for their
     * own custom language creations.
     */
    private static void setupCustomLanguageMetadata() {
        // Example custom languages - developers can add their own here

        // Garden-themed language variant
        CUSTOM_LANGUAGE_METADATA.put("gardenspeak", new CustomLanguageMetadata(
                "Garden Speak",
                "Plantañol",
                "🌱",
                "Let's sprout-grow together!"
        ));

        // Simplified language for younger children
        CUSTOM_LANGUAGE_METADATA.put("kidlang", new CustomLanguageMetadata(
                "Kid Language",
                "Fun Talk",
                "😊",
                "Super easy words!"
        ));

        // Fantasy/fictional language example
        CUSTOM_LANGUAGE_METADATA.put("elvish", new CustomLanguageMetadata(
                "Elvish",
                "Eldarin",
                "🧝",
                "Lasto beth nin!"
        ));

        // Accessibility-focused simplified language
        CUSTOM_LANGUAGE_METADATA.put("simple", new CustomLanguageMetadata(
                "Simple Words",
                "Easy Talk",
                "📖",
                "Big plants grow!"
        ));

        // Pictographic/emoji-heavy language
        CUSTOM_LANGUAGE_METADATA.put("pictographic", new CustomLanguageMetadata(
                "Picture Language",
                "Emoji Talk",
                "🖼️",
                "🌱💧☀️ = 😊"
        ));
    }

    /**
     * Clears the language cache, forcing re-discovery on next call.
     * <p>
     * Useful for development and testing, or if languages are added
     * dynamically at runtime (though this is rare).
     */
    public static void clearCache() {
        cachedLanguages = null;
        Log.d(TAG, "Language cache cleared");
    }

    /**
     * CustomLanguageMetadata - Internal data class for custom language information.
     * <p>
     * This class holds the metadata needed to display custom languages
     * professionally in the language selection interface.
     */
    private static class CustomLanguageMetadata {
        final String displayName;
        final String nativeName;
        final String symbol;
        final String sampleText;

        CustomLanguageMetadata(String displayName, String nativeName, String symbol, String sampleText) {
            this.displayName = displayName;
            this.nativeName = nativeName;
            this.symbol = symbol;
            this.sampleText = sampleText;
        }
    }
}