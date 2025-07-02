package com.nava.samiyuri;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.nava.samiyuri.model.BuddyMessageEvent;
import com.nava.samiyuri.model.BuddyMessageType;

import static org.junit.Assert.*;

/**
 * MessageFormattingTest - Instrumented tests for message formatting with string resources
 * <p>
 * These tests verify that the View layer correctly formats message events using
 * string resources. This ensures our localization system works properly and that
 * messages display correctly to users.
 * <p>
 * Note: These are instrumented tests that run on Android devices/emulators because
 * they need access to the Android Context to load string resources.
 * <p>
 * Tests cover:
 * - String resource formatting with buddy names
 * - Messages without formatting placeholders
 * - All message types have corresponding string resources
 * - String formatting handles null/empty buddy names gracefully
 */
@RunWith(AndroidJUnit4.class)
public class MessageFormattingTest {

    /**
     * Gets the app context for accessing string resources
     */
    private Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    /**
     * Helper method to format a message event like MainActivity does
     * <p>
     * This simulates the formatMessageFromEvent() method from MainActivity
     * so we can test the formatting logic in isolation.
     *
     * @param messageEvent The message event to format
     * @return Formatted message string
     */
    private String formatMessageFromEvent(BuddyMessageEvent messageEvent) {
        Context context = getContext();
        BuddyMessageType messageType = messageEvent.getMessageType();
        String buddyName = messageEvent.getBuddyName();

        switch (messageType) {
            // BUDDY CARE FEEDBACK MESSAGES
            case BUDDY_WATERED:
                return context.getString(R.string.buddy_watered_message, buddyName);

            case BUDDY_GOT_SUNLIGHT:
                return context.getString(R.string.buddy_sunlight_message, buddyName);

            case BUDDY_INFO_SHOWN:
                return context.getString(R.string.buddy_info_message);

            case FEATURE_COMING_SOON:
                return context.getString(R.string.feature_coming_soon);

            // PLANT ANALYSIS QUESTIONS
            case ANALYSIS_SEED_QUESTION:
                return context.getString(R.string.analysis_seed_question);

            case ANALYSIS_SPROUTING_QUESTION:
                return context.getString(R.string.analysis_sprouting_question, buddyName);

            case ANALYSIS_SEEDLING_QUESTION:
                return context.getString(R.string.analysis_seedling_question, buddyName);

            case ANALYSIS_GROWING_QUESTION:
                return context.getString(R.string.analysis_growing_question, buddyName);

            case ANALYSIS_MATURE_QUESTION:
                return context.getString(R.string.analysis_mature_question, buddyName);

            case ANALYSIS_HARVEST_QUESTION:
                return context.getString(R.string.analysis_harvest_question, buddyName);

            case ANALYSIS_DEFAULT_QUESTION:
                return context.getString(R.string.analysis_default_question, buddyName);

            default:
                return context.getString(R.string.buddy_info_message);
        }
    }

    /**
     * TEST: Buddy Care Messages Format Correctly
     * <p>
     * Verifies that care action messages include the buddy's name properly
     */
    @Test
    public void testBuddyCareMessages_FormatWithBuddyName() {
        String buddyName = "Roberto";

        // Test watering message
        BuddyMessageEvent waterEvent = new BuddyMessageEvent(BuddyMessageType.BUDDY_WATERED, buddyName);
        String waterMessage = formatMessageFromEvent(waterEvent);

        assertNotNull("Water message should not be null", waterMessage);
        assertTrue("Water message should contain buddy name", waterMessage.contains(buddyName));
        assertTrue("Water message should be encouraging", waterMessage.toLowerCase().contains("great"));

        // Test sunlight message
        BuddyMessageEvent sunlightEvent = new BuddyMessageEvent(BuddyMessageType.BUDDY_GOT_SUNLIGHT, buddyName);
        String sunlightMessage = formatMessageFromEvent(sunlightEvent);

        assertNotNull("Sunlight message should not be null", sunlightMessage);
        assertTrue("Sunlight message should contain buddy name", sunlightMessage.contains(buddyName));
        assertTrue("Sunlight message should mention sunlight", sunlightMessage.toLowerCase().contains("sunlight"));
    }

    /**
     * TEST: Info Messages Don't Require Buddy Name
     * <p>
     * Verifies that generic messages work without buddy name formatting
     */
    @Test
    public void testInfoMessages_WorkWithoutBuddyName() {
        // Test buddy info message (no buddy name needed)
        BuddyMessageEvent infoEvent = new BuddyMessageEvent(BuddyMessageType.BUDDY_INFO_SHOWN);
        String infoMessage = formatMessageFromEvent(infoEvent);

        assertNotNull("Info message should not be null", infoMessage);
        assertFalse("Info message should not be empty", infoMessage.trim().isEmpty());
        assertTrue("Info message should be encouraging", infoMessage.toLowerCase().contains("lucky"));

        // Test coming soon message (no buddy name needed)
        BuddyMessageEvent comingSoonEvent = new BuddyMessageEvent(BuddyMessageType.FEATURE_COMING_SOON);
        String comingSoonMessage = formatMessageFromEvent(comingSoonEvent);

        assertNotNull("Coming soon message should not be null", comingSoonMessage);
        assertFalse("Coming soon message should not be empty", comingSoonMessage.trim().isEmpty());
    }

    /**
     * TEST: Analysis Questions Format Correctly
     * <p>
     * Verifies that plant analysis questions include buddy names where appropriate
     */
    @Test
    public void testAnalysisQuestions_FormatCorrectly() {
        String buddyName = "Letticia";

        // Test seed question (no buddy name needed)
        BuddyMessageEvent seedEvent = new BuddyMessageEvent(BuddyMessageType.ANALYSIS_SEED_QUESTION);
        String seedQuestion = formatMessageFromEvent(seedEvent);

        assertNotNull("Seed question should not be null", seedQuestion);
        assertTrue("Seed question should mention sprouts", seedQuestion.toLowerCase().contains("sprout"));

        // Test sprouting question (buddy name needed)
        BuddyMessageEvent sproutingEvent = new BuddyMessageEvent(BuddyMessageType.ANALYSIS_SPROUTING_QUESTION, buddyName);
        String sproutingQuestion = formatMessageFromEvent(sproutingEvent);

        assertNotNull("Sprouting question should not be null", sproutingQuestion);
        assertTrue("Sprouting question should contain buddy name", sproutingQuestion.contains(buddyName));
        assertTrue("Sprouting question should mention leaves", sproutingQuestion.toLowerCase().contains("leaves"));

        // Test growing question (buddy name needed)
        BuddyMessageEvent growingEvent = new BuddyMessageEvent(BuddyMessageType.ANALYSIS_GROWING_QUESTION, buddyName);
        String growingQuestion = formatMessageFromEvent(growingEvent);

        assertNotNull("Growing question should not be null", growingQuestion);
        assertTrue("Growing question should contain buddy name", growingQuestion.contains(buddyName));
    }

    /**
     * TEST: All Message Types Have String Resources
     * <p>
     * Verifies that every message type can be formatted without crashing
     */
    @Test
    public void testAllMessageTypes_HaveStringResources() {
        String testBuddyName = "TestBuddy";

        // Test all message types to ensure string resources exist
        for (BuddyMessageType messageType : BuddyMessageType.values()) {
            BuddyMessageEvent event = new BuddyMessageEvent(messageType, testBuddyName);

            try {
                String formattedMessage = formatMessageFromEvent(event);
                assertNotNull("Message for " + messageType + " should not be null", formattedMessage);
                assertFalse("Message for " + messageType + " should not be empty",
                        formattedMessage.trim().isEmpty());
            } catch (Exception e) {
                fail("Failed to format message for " + messageType + ": " + e.getMessage());
            }
        }
    }

    /**
     * TEST: String Formatting Handles Edge Cases
     * <p>
     * Verifies that formatting works correctly with unusual buddy names
     */
    @Test
    public void testStringFormatting_HandlesEdgeCases() {
        // Test with empty buddy name
        BuddyMessageEvent emptyNameEvent = new BuddyMessageEvent(BuddyMessageType.BUDDY_WATERED, "");
        String emptyNameMessage = formatMessageFromEvent(emptyNameEvent);
        assertNotNull("Should handle empty buddy name", emptyNameMessage);

        // Test with special characters in buddy name
        BuddyMessageEvent specialCharEvent = new BuddyMessageEvent(BuddyMessageType.BUDDY_WATERED, "José María");
        String specialCharMessage = formatMessageFromEvent(specialCharEvent);
        assertNotNull("Should handle special characters", specialCharMessage);
        assertTrue("Should include special character name", specialCharMessage.contains("José María"));

        // Test with very long buddy name
        BuddyMessageEvent longNameEvent = new BuddyMessageEvent(BuddyMessageType.BUDDY_WATERED, "SuperLongBuddyNameThatGoesOnAndOn");
        String longNameMessage = formatMessageFromEvent(longNameEvent);
        assertNotNull("Should handle long buddy names", longNameMessage);
    }

    /**
     * TEST: Message Length Is Reasonable
     * <p>
     * Verifies that formatted messages are appropriate length for Toast display
     */
    @Test
    public void testMessageLength_IsReasonable() {
        String buddyName = "Roberto";

        // Test various message types for reasonable length
        BuddyMessageEvent waterEvent = new BuddyMessageEvent(BuddyMessageType.BUDDY_WATERED, buddyName);
        String waterMessage = formatMessageFromEvent(waterEvent);

        assertTrue("Water message should not be too short", waterMessage.length() > 10);
        assertTrue("Water message should not be too long for Toast", waterMessage.length() < 200);

        // Test analysis questions (can be longer)
        BuddyMessageEvent analysisEvent = new BuddyMessageEvent(BuddyMessageType.ANALYSIS_SPROUTING_QUESTION, buddyName);
        String analysisMessage = formatMessageFromEvent(analysisEvent);

        assertTrue("Analysis message should have meaningful content", analysisMessage.length() > 20);
        assertTrue("Analysis message should not be excessively long", analysisMessage.length() < 300);
    }
}