package com.nava.samiyuri.utils;

import java.util.Locale;

/**
 * LanguageInfo - A comprehensive data model for representing language options.
 * <p>
 * This class encapsulates all the information needed to display and select
 * a language option in the Samiyura app. It supports both standard ISO language
 * codes and completely custom language codes created specifically for the app.
 * <p>
 * The model handles everything from display formatting to locale creation,
 * making it easy to support unlimited languages including fictional ones,
 * regional variants, and accessibility-focused language modifications.
 * <p>
 * Key Features:
 * - Automatic locale creation from language codes
 * - Fallback display names for unknown languages
 * - Custom symbol/flag support for visual identification
 * - Sample text for language preview
 * - Regional variant support (es-rPE, zh-rCN, etc.)
 * - Custom language code support (gardenspeak, elvish, etc.)
 *
 * @author Neriou
 * @version 1.0
 * @since 2025-07-06
 */
public class LanguageInfo {

    /**
     * The raw language code extracted from the resource folder name.
     * <p>
     * Examples:
     * - "en" for values-en
     * - "es-rPE" for values-es-rPE
     * - "gardenspeak" for values-gardenspeak
     * - "kidlang" for values-kidlang
     */
    private final String languageCode;

    /**
     * The display name in the current system language.
     * <p>
     * This is what users see in the language selection interface.
     * For standard languages, this comes from the system. For custom
     * languages, this comes from our metadata or defaults to the code.
     */
    private final String displayName;

    /**
     * The native name of the language (how speakers of that language write it).
     * <p>
     * Examples:
     * - "English" for English
     * - "Español" for Spanish
     * - "Runasimi" for Quechua
     * - "Plantañol" for custom garden language
     */
    private final String nativeName;

    /**
     * A visual symbol representing the language (emoji flag, icon, etc.).
     * <p>
     * Used to provide quick visual identification in the UI.
     * Can be emoji flags, custom icons, or themed symbols.
     */
    private final String symbol;

    /**
     * Sample text in this language to give users a preview.
     * <p>
     * Shows users what the language "feels like" before they select it.
     * Should be encouraging and related to gardening when possible.
     */
    private final String sampleText;

    /**
     * The Android Locale object for this language.
     * <p>
     * Used by the system to apply the language formatting and
     * resource loading. Created automatically from the language code.
     */
    private final Locale locale;

    /**
     * Indicates whether this is a custom/fictional language.
     * <p>
     * Custom languages need special handling for display names
     * and may not have system-provided locale support.
     */
    private final boolean isCustomLanguage;

    /**
     * Creates a new LanguageInfo instance with all display properties.
     * <p>
     * This constructor provides complete control over how the language
     * appears in the selection interface, making it perfect for both
     * standard languages and completely custom language creations.
     *
     * @param languageCode The raw language code from the folder name
     * @param displayName The name shown in the current system language
     * @param nativeName The name in the target language itself
     * @param symbol Visual symbol (emoji, icon) for quick identification
     * @param sampleText Preview text to show the language character
     * @param isCustomLanguage Whether this is a standard or custom language
     */
    public LanguageInfo(String languageCode, String displayName, String nativeName,
                        String symbol, String sampleText, boolean isCustomLanguage) {
        this.languageCode = languageCode;
        this.displayName = displayName;
        this.nativeName = nativeName;
        this.symbol = symbol;
        this.sampleText = sampleText;
        this.isCustomLanguage = isCustomLanguage;
        this.locale = createLocaleFromCode(languageCode);
    }

    /**
     * Creates an Android Locale object from a language code string.
     * <p>
     * This method handles the complexity of parsing different language
     * code formats and creating appropriate Locale objects for the
     * Android system to use.
     * <p>
     * Supported formats:
     * - Simple: "en", "es", "fr"
     * - Regional: "es-rPE", "zh-rCN", "en-rUS"
     * - Custom: "gardenspeak", "elvish", "kidlang"
     *
     * @param languageCode The language code to parse
     * @return A Locale object for Android system use
     */
    private Locale createLocaleFromCode(String languageCode) {
        if (languageCode.contains("-r")) {
            // Handle regional variants like "es-rPE"
            String[] parts = languageCode.split("-r");
            if (parts.length == 2) {
                return new Locale(parts[0], parts[1]);
            }
        } else if (languageCode.contains("-")) {
            // Handle other variants like "zh-CN" (without 'r')
            String[] parts = languageCode.split("-");
            if (parts.length == 2) {
                return new Locale(parts[0], parts[1]);
            }
        }

        // Simple language code or custom language
        return new Locale(languageCode);
    }

    /**
     * Gets the raw language code extracted from the resource folder.
     * <p>
     * This is the identifier used internally by Android's resource
     * system and for saving user preferences.
     *
     * @return The language code string
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Gets the display name for showing in the current system language.
     * <p>
     * This is the primary text users see when selecting languages.
     * It's localized to the user's current system language when possible.
     *
     * @return The localized display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the native name of the language.
     * <p>
     * This shows how speakers of the target language would write
     * the language name, providing cultural authenticity and
     * helping users who recognize the native script.
     *
     * @return The native language name
     */
    public String getNativeName() {
        return nativeName;
    }

    /**
     * Gets the visual symbol representing this language.
     * <p>
     * Used in the UI for quick visual identification. Can be
     * emoji flags for countries, custom icons for themes,
     * or symbolic representations for fictional languages.
     *
     * @return The symbol string (emoji, icon reference, etc.)
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets sample text that previews the language character.
     * <p>
     * This gives users a feel for the language before selection,
     * showing vocabulary style, complexity level, and cultural tone.
     * Should be encouraging and garden-related when possible.
     *
     * @return Preview text in the target language
     */
    public String getSampleText() {
        return sampleText;
    }

    /**
     * Gets the Android Locale object for system integration.
     * <p>
     * This is used by Android's resource loading system to
     * apply the selected language throughout the app.
     *
     * @return The Locale object for Android system use
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Checks if this is a custom/fictional language.
     * <p>
     * Custom languages may need special handling for display
     * names and don't have standard system support for
     * locale-specific formatting.
     *
     * @return true if this is a custom language, false for standard languages
     */
    public boolean isCustomLanguage() {
        return isCustomLanguage;
    }

    /**
     * Gets the primary language code without regional variants.
     * <p>
     * Useful for grouping related language variants and for
     * fallback logic when region-specific resources aren't available.
     * <p>
     * Examples:
     * - "es-rPE" → "es"
     * - "zh-rCN" → "zh"
     * - "gardenspeak" → "gardenspeak"
     *
     * @return The base language code
     */
    public String getBaseLanguageCode() {
        if (languageCode.contains("-")) {
            return languageCode.split("-")[0];
        }
        return languageCode;
    }

    /**
     * Checks if this language has a regional variant.
     * <p>
     * Regional variants have specific cultural or geographical
     * adaptations beyond just language translation.
     *
     * @return true if this is a regional variant, false otherwise
     */
    public boolean hasRegionalVariant() {
        return languageCode.contains("-");
    }

    @Override
    public String toString() {
        return "LanguageInfo{" +
                "code='" + languageCode + '\'' +
                ", display='" + displayName + '\'' +
                ", native='" + nativeName + '\'' +
                ", symbol='" + symbol + '\'' +
                ", custom=" + isCustomLanguage +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LanguageInfo that = (LanguageInfo) obj;
        return languageCode.equals(that.languageCode);
    }

    @Override
    public int hashCode() {
        return languageCode.hashCode();
    }
}