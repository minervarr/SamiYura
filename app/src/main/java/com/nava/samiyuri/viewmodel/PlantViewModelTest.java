package com.nava.samiyuri.viewmodel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.nava.samiyuri.model.BuddyMessageEvent;
import com.nava.samiyuri.model.BuddyMessageType;
import com.nava.samiyuri.model.Plant;

import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * PlantViewModelTest - Unit tests for PlantViewModel's MVVM message system
 * <p>
 * These tests verify that the ViewModel properly emits structured message events
 * instead of formatted strings, ensuring clean MVVM separation and testability.
 * <p>
 * Tests cover:
 * - Correct message types are emitted for each action
 * - Message events contain appropriate data (buddy names, etc.)
 * - Business logic works independently of UI concerns
 * - ViewModel state management functions correctly
 */
@RunWith(JUnit4.class)
public class PlantViewModelTest {

    // Rule to make LiveData execute synchronously for testing
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Mock observers for LiveData
    @Mock
    private Observer<Plant> plantObserver;

    @Mock
    private Observer<Integer> avatarObserver;

    @Mock
    private Observer<BuddyMessageEvent> messageObserver;

    // The ViewModel we're testing
    private PlantViewModel viewModel;

    /**
     * Set up test environment before each test
     * <p>
     * Initializes mocks and creates a fresh ViewModel instance
     */
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        viewModel = new PlantViewModel();

        // Attach observers to LiveData
        viewModel.getCurrentPlant().observeForever(plantObserver);
        viewModel.getPlantAvatarResource().observeForever(avatarObserver);
        viewModel.getMessageEvent().observeForever(messageObserver);
    }

    /**
     * TEST: Initial ViewModel State
     * <p>
     * Verifies that the ViewModel initializes correctly with test data
     */
    @Test
    public void testInitialState() {
        // Verify that ViewModel starts with a test buddy
        Plant currentPlant = viewModel.getCurrentPlant().getValue();

        assertNotNull("ViewModel should have a current plant", currentPlant);
        assertEquals("Initial plant should be radish", "radish", currentPlant.getPlantType());
        assertEquals("Initial buddy name should be Roberto", "Roberto", currentPlant.getBuddyName());

        // Verify that avatar resource is set
        Integer avatarResource = viewModel.getPlantAvatarResource().getValue();
        assertNotNull("Avatar resource should be set", avatarResource);
    }

    /**
     * TEST: Water Buddy Action
     * <p>
     * Verifies that watering emits correct message event with buddy name
     */
    @Test
    public void testWaterBuddy_EmitsCorrectMessageEvent() {
        // Action: Water the buddy
        viewModel.waterCurrentBuddy();

        // Verify: Correct message event was emitted
        verify(messageObserver, atLeastOnce()).onChanged(any(BuddyMessageEvent.class));

        // Get the actual message event that was emitted
        BuddyMessageEvent lastMessage = getLastEmittedMessage();

        assertNotNull("Message event should not be null", lastMessage);
        assertEquals("Should emit BUDDY_WATERED message type",
                BuddyMessageType.BUDDY_WATERED, lastMessage.getMessageType());
        assertEquals("Message should include buddy name",
                "Roberto", lastMessage.getBuddyName());
        assertTrue("Message should have buddy name for formatting",
                lastMessage.hasBuddyName());
    }

    /**
     * TEST: Give Sunlight Action
     * <p>
     * Verifies that sunlight action emits correct message event
     */
    @Test
    public void testGiveSunlight_EmitsCorrectMessageEvent() {
        // Action: Give sunlight to buddy
        viewModel.giveSunlightToBuddy();

        // Verify: Correct message event was emitted
        BuddyMessageEvent lastMessage = getLastEmittedMessage();

        assertNotNull("Message event should not be null", lastMessage);
        assertEquals("Should emit BUDDY_GOT_SUNLIGHT message type",
                BuddyMessageType.BUDDY_GOT_SUNLIGHT, lastMessage.getMessageType());
        assertEquals("Message should include buddy name",
                "Roberto", lastMessage.getBuddyName());
    }

    /**
     * TEST: Show Buddy Info Action
     * <p>
     * Verifies that info action emits message without buddy name
     */
    @Test
    public void testShowBuddyInfo_EmitsCorrectMessageEvent() {
        // Action: Show buddy info
        viewModel.showBuddyInfo();

        // Verify: Correct message event was emitted
        BuddyMessageEvent lastMessage = getLastEmittedMessage();

        assertNotNull("Message event should not be null", lastMessage);
        assertEquals("Should emit BUDDY_INFO_SHOWN message type",
                BuddyMessageType.BUDDY_INFO_SHOWN, lastMessage.getMessageType());
        assertFalse("Info message should not require buddy name",
                lastMessage.hasBuddyName());
    }

    /**
     * TEST: Plant Analysis Action
     * <p>
     * Verifies that analysis emits growth-stage appropriate question
     */
    @Test
    public void testStartPlantAnalysis_EmitsCorrectQuestionType() {
        // Action: Start plant analysis
        viewModel.startPlantAnalysis();

        // Verify: Correct analysis question was emitted
        BuddyMessageEvent lastMessage = getLastEmittedMessage();

        assertNotNull("Message event should not be null", lastMessage);

        // Since test buddy starts as seed stage, should get seed question
        assertEquals("Should emit ANALYSIS_SEED_QUESTION for new buddy",
                BuddyMessageType.ANALYSIS_SEED_QUESTION, lastMessage.getMessageType());
    }

    /**
     * TEST: Navigation Actions (Future Features)
     * <p>
     * Verifies that navigation buttons show "coming soon" message
     */
    @Test
    public void testNavigationActions_ShowComingSoonMessage() {
        // Test previous buddy navigation
        viewModel.showPreviousBuddy();
        BuddyMessageEvent previousMessage = getLastEmittedMessage();

        assertEquals("Previous buddy should show coming soon",
                BuddyMessageType.FEATURE_COMING_SOON, previousMessage.getMessageType());

        // Test next buddy navigation
        viewModel.showNextBuddy();
        BuddyMessageEvent nextMessage = getLastEmittedMessage();

        assertEquals("Next buddy should show coming soon",
                BuddyMessageType.FEATURE_COMING_SOON, nextMessage.getMessageType());
    }

    /**
     * TEST: Status Display Text Formatting
     * <p>
     * Verifies that status codes convert to user-friendly text
     */
    @Test
    public void testStatusDisplayText_FormatsCorrectly() {
        assertEquals("Happy status should format correctly",
                "happy and healthy", viewModel.getStatusDisplayText("happy"));
        assertEquals("Thirsty status should format correctly",
                "feeling a bit thirsty", viewModel.getStatusDisplayText("thirsty"));
        assertEquals("Needs attention status should format correctly",
                "really needs your care", viewModel.getStatusDisplayText("needs_attention"));
        assertEquals("Unknown status should default to happy",
                "happy and healthy", viewModel.getStatusDisplayText("unknown"));
    }

    /**
     * TEST: Multiple Actions Don't Interfere
     * <p>
     * Verifies that performing multiple actions works correctly
     */
    @Test
    public void testMultipleActions_WorkIndependently() {
        // Perform multiple actions
        viewModel.waterCurrentBuddy();
        viewModel.giveSunlightToBuddy();
        viewModel.showBuddyInfo();

        // Verify that each action emitted a message
        verify(messageObserver, atLeast(3)).onChanged(any(BuddyMessageEvent.class));

        // The last message should be from showBuddyInfo
        BuddyMessageEvent lastMessage = getLastEmittedMessage();
        assertEquals("Last action should be buddy info",
                BuddyMessageType.BUDDY_INFO_SHOWN, lastMessage.getMessageType());
    }

    /**
     * Helper method to get the last emitted message event
     * <p>
     * Uses Mockito to capture the last message that was emitted to observers
     *
     * @return The most recent BuddyMessageEvent, or null if none
     */
    private BuddyMessageEvent getLastEmittedMessage() {
        // This is a simplified approach - in a real test setup, you might use
        // ArgumentCaptor to capture all the messages that were emitted
        return viewModel.getMessageEvent().getValue();
    }

    /**
     * TEST: Message Event Data Integrity
     * <p>
     * Verifies that message events contain valid, non-null data
     */
    @Test
    public void testMessageEventDataIntegrity() {
        viewModel.waterCurrentBuddy();

        BuddyMessageEvent message = getLastEmittedMessage();

        assertNotNull("Message type should not be null", message.getMessageType());
        assertNotNull("Buddy name should not be null", message.getBuddyName());
        assertFalse("Buddy name should not be empty", message.getBuddyName().trim().isEmpty());
        assertTrue("toString should provide useful debug info",
                message.toString().contains("BUDDY_WATERED"));
    }
}