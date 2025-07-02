package com.nava.samiyuri.model;

/**
 * BuddyMessageType - Enum defining all possible message types the ViewModel can emit
 * <p>
 * This enum represents different types of messages that can be sent from the ViewModel
 * to the View layer. Instead of the ViewModel creating formatted strings directly,
 * it emits message types that the View can interpret and format using string resources.
 * <p>
 * This approach supports proper MVVM separation and makes localization much easier,
 * as all user-facing text remains in strings.xml files.
 * <p>
 * Key benefits:
 * - Clean separation between business logic (ViewModel) and presentation (View)
 * - Full localization support through string resources
 * - Easier testing of ViewModel logic without UI dependencies
 * - Type-safe message handling
 */
public enum BuddyMessageType {

    // BUDDY CARE FEEDBACK MESSAGES
    // These are positive reinforcement messages when the child cares for their buddy

    /**
     * Emitted when the child successfully waters their plant buddy
     * Requires: buddy name for formatting
     * String resource: buddy_watered_message
     */
    BUDDY_WATERED,

    /**
     * Emitted when the child gives sunlight to their plant buddy
     * Requires: buddy name for formatting
     * String resource: buddy_sunlight_message
     */
    BUDDY_GOT_SUNLIGHT,

    /**
     * Emitted when the child requests information about their buddy
     * Requires: no additional data
     * String resource: buddy_info_message
     */
    BUDDY_INFO_SHOWN,

    /**
     * Emitted for features that are not yet implemented (navigation arrows)
     * Requires: no additional data
     * String resource: feature_coming_soon
     */
    FEATURE_COMING_SOON,

    // PLANT ANALYSIS QUESTIONS (Observer's Journal)
    // These are growth-stage specific questions that require real plant observation

    /**
     * Analysis question for seed stage - looking for first sprouts
     * Requires: no additional data (generic question)
     * String resource: analysis_seed_question
     */
    ANALYSIS_SEED_QUESTION,

    /**
     * Analysis question for sprouting stage - counting tiny leaves
     * Requires: buddy name for formatting
     * String resource: analysis_sprouting_question
     */
    ANALYSIS_SPROUTING_QUESTION,

    /**
     * Analysis question for seedling stage - measuring height
     * Requires: buddy name for formatting
     * String resource: analysis_seedling_question
     */
    ANALYSIS_SEEDLING_QUESTION,

    /**
     * Analysis question for growing stage - observing leaf growth
     * Requires: buddy name for formatting
     * String resource: analysis_growing_question
     */
    ANALYSIS_GROWING_QUESTION,

    /**
     * Analysis question for mature stage - checking harvest readiness
     * Requires: buddy name for formatting
     * String resource: analysis_mature_question
     */
    ANALYSIS_MATURE_QUESTION,

    /**
     * Analysis question for harvest stage - final observation
     * Requires: buddy name for formatting
     * String resource: analysis_harvest_question
     */
    ANALYSIS_HARVEST_QUESTION,

    /**
     * Default analysis question for any unhandled growth stages
     * Requires: buddy name for formatting
     * String resource: analysis_default_question
     */
    ANALYSIS_DEFAULT_QUESTION
}