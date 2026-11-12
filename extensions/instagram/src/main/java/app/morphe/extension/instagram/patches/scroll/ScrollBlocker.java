/*
 * Copyright (C) 2026 piko <https://github.com/crimera/piko>
 *
 * See the included NOTICE file for GPLv3 §7(b) terms that apply to this code.
 */

package app.morphe.extension.instagram.patches.scroll;

import android.view.MotionEvent;
import android.view.View;
import androidx.viewpager2.widget.ViewPager2;

import app.morphe.extension.instagram.utils.Pref;
import app.morphe.extension.instagram.settings.SettingsStatus;

/**
 * Intercepts and blocks vertical navigation swiping across Instagram surfaces
 * (Home / Feed, Explore, Reels, and chat-shared Reels) while preserving:
 * - Ordinary touch interactions (buttons, options, comments, likes, seekbars).
 * - Horizontal swipe gestures (e.g. multi-image carousels).
 * - Full functional scrolling on User Profiles, Notifications, and Direct Messages / Chats.
 */
public final class ScrollBlocker {

    public static final String SURFACE_FEED = "feed";
    public static final String SURFACE_EXPLORE = "explore";
    public static final String SURFACE_REELS = "reels";
    public static final String SURFACE_PROFILE = "profile";
    public static final String SURFACE_NOTIFICATIONS = "notifications";
    public static final String SURFACE_DIRECT = "direct";

    private ScrollBlocker() {}

    /**
     * Whether vertical swipe navigation is currently disabled by user preference.
     */
    public static boolean isScrollBlockingEnabled() {
        return Pref.disableReelsScrolling() && SettingsStatus.disableReelsScrolling;
    }

    /**
     * Checks whether the given screen surface should have vertical navigation swiping blocked.
     * Profiles, Notifications, and DMs/Chats are explicitly exempt and NEVER blocked.
     */
    public static boolean isSurfaceBlocked(String surface) {
        if (!isScrollBlockingEnabled() || surface == null) {
            return false;
        }

        // Explicitly preserved surfaces: Profile, Notifications, Direct Messages / Chats
        if (SURFACE_PROFILE.equalsIgnoreCase(surface)
                || SURFACE_NOTIFICATIONS.equalsIgnoreCase(surface)
                || SURFACE_DIRECT.equalsIgnoreCase(surface)
                || "chat".equalsIgnoreCase(surface)
                || "inbox".equalsIgnoreCase(surface)) {
            return false;
        }

        // Targeted surfaces: Home/Feed, Explore, Reels
        return SURFACE_FEED.equalsIgnoreCase(surface)
                || SURFACE_EXPLORE.equalsIgnoreCase(surface)
                || SURFACE_REELS.equalsIgnoreCase(surface)
                || "clips".equalsIgnoreCase(surface);
    }

    /**
     * Reuses the existing Reels ViewPager2 mechanism to disable swipe navigation on any ViewPager2.
     * Applied to Reels tab viewer, Explore video viewer, and Reels opened from chats/messages.
     */
    public static void blockViewPager(Object viewPagerObj) {
        if (viewPagerObj instanceof ViewPager2) {
            blockViewPager((ViewPager2) viewPagerObj, SURFACE_REELS);
        }
    }

    /**
     * Configures a ViewPager2 instance with surface-aware scroll blocking.
     */
    public static void blockViewPager(ViewPager2 viewPager, String surface) {
        if (viewPager != null && isSurfaceBlocked(surface)) {
            viewPager.setUserInputEnabled(false);
        }
    }

    /**
     * Evaluates a touch interception event for vertical navigation blocking.
     * Preserves taps (ACTION_DOWN) and horizontal carousels, but intercepts vertical drag gestures
     * that would navigate to the next or previous content item.
     *
     * @return true if the gesture is a vertical navigation swipe on a targeted surface; false otherwise.
     */
    public static boolean shouldInterceptVerticalSwipe(float deltaX, float deltaY, String surface) {
        if (!isSurfaceBlocked(surface)) {
            return false;
        }

        float absX = Math.abs(deltaX);
        float absY = Math.abs(deltaY);

        // If movement is predominantly vertical (swiping between posts/reels), block it.
        // Horizontal swipes (carousels) are allowed through (absX > absY).
        return absY > 12f && absY > absX;
    }

    /**
     * Used by pull-to-refresh layouts (e.g. ClipsSwipeRefreshLayout and Feed SwipeRefreshLayout)
     * to prevent pull-to-refresh swipe interception when scroll blocking is enabled.
     */
    public static boolean shouldDisablePullToRefresh(String surface) {
        return isSurfaceBlocked(surface);
    }
}
