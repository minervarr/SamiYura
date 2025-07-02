package com.nava.samiyuri.model;

/**
 * BuddyMessageEvent - Data class that carries message information from ViewModel to View
 * <p>
 * This class represents a single message event that the ViewModel wants to communicate
 * to the View layer. It contains the message type and any data needed for proper
 * string formatting (like the buddy's name for personalized messages).
 * <p>
 * This approach follows proper MVVM principles:
 * - ViewModel emits structured message events (business logic)
 * - View receives events and formats them using string resources (presentation logic)
 * - Clean separation allows for easy testing and localization
 * <p>
 * Usage example:
 * ```java
 * // ViewModel creates and emits event
 * BuddyMessageEvent event = new BuddyMessageEvent(BuddyMessageType.BUDDY_WATERED, "Roberto");
 * messageEvent.setValue(event);
 *
 * // View receives event and formats message
 * String message = getString(R.string.buddy_watered_message, event.getBuddyName());
 * ```
 */
public class BuddyMessageEvent {

    /**
     * The type of message being sent - determines which string resource to use
     */
    private final BuddyMessageType messageType;

    /**
     * The buddy's name for personalized message formatting
     * Used in string resources with %1$s placeholder
     * Can be null for messages that don't require buddy name
     */
    private final String buddyName;

    /**
     * Constructor for messages that require buddy name for formatting
     * <p>
     * Use this constructor for personalized messages like "Roberto loves fresh water!"
     *
     * @param messageType The type of message to display
     * @param buddyName The buddy's name for string formatting (e.g., "Roberto")
     */
    public BuddyMessageEvent(BuddyMessageType messageType, String buddyName) {
        this.messageType = messageType;
        this.buddyName = buddyName;
    }

    /**
     * Constructor for messages that don't require buddy name
     * <p>
     * Use this constructor for generic messages like "Coming soon!" or general info
     *
     * @param messageType The type of message to display
     */
    public BuddyMessageEvent(BuddyMessageType messageType) {
        this.messageType = messageType;
        this.buddyName = null;
    }

    /**
     * Gets the message type for determining which string resource to use
     *
     * @return The type of message being sent
     */
    public BuddyMessageType getMessageType() {
        return messageType;
    }

    /**
     * Gets the buddy name for string formatting
     * <p>
     * This value should be checked for null before using in string formatting.
     * Some message types don't require a buddy name.
     *
     * @return The buddy's name for formatting, or null if not needed
     */
    public String getBuddyName() {
        return buddyName;
    }

    /**
     * Checks if this message event includes a buddy name for formatting
     * <p>
     * Useful for determining whether to use string resources with or without
     * formatting placeholders.
     *
     * @return true if buddy name is available for formatting
     */
    public boolean hasBuddyName() {
        return buddyName != null && !buddyName.trim().isEmpty();
    }

    /**
     * String representation for debugging purposes
     *
     * @return Human-readable description of the message event
     */
    @Override
    public String toString() {
        return "BuddyMessageEvent{" +
                "messageType=" + messageType +
                ", buddyName='" + buddyName + '\'' +
                '}';
    }
}