# Required Modification: Swipe / Scroll Blocking Extension

## 1. Overview
The current implementation only blocks Reels scrolling inside the Reels tab viewer. Per the project requirements, this mechanism must be reused and extended across Instagram:
- **Blocked Surfaces:** Home / Feed, Explore, and Reels (including Reels opened directly from Direct Messages / chats).
- **Functional Surfaces (Must NOT be blocked):** User Profiles, Notifications, and Direct Messages / Chats.

---

## 2. Target File 1: `DisableReelsScrollingPatch.kt` (or refactored `DisableScrollingPatch.kt`)
- **Path:** `patches/src/main/kotlin/app/crimera/patches/instagram/misc/reels/DisableReelsScrollingPatch.kt`

### Existing Implementation
```kotlin
private object ClipsViewPagerImplGetViewAtIndexFingerprint : Fingerprint(
    strings = listOf("ClipsViewPagerImpl_getViewAtIndex")
)

private object ClipsSwipeRefreshLayoutOnInterceptTouchEventFingerprint : Fingerprint (
    name = "onInterceptTouchEvent",
    parameters = listOf("Landroid/view/MotionEvent;"),
    definingClass = "Linstagram/features/clips/viewer/ui/ClipsSwipeRefreshLayout;"
)

// In execute:
val viewPagerField = ClipsViewPagerImplGetViewAtIndexFingerprint.classDef.fields.first {
    it.type == "Landroidx/viewpager2/widget/ViewPager2;"
}

ClipsViewPagerImplGetViewAtIndexFingerprint.method.addInstructions(
    0,
    """
        ${PREF_CALL_DESCRIPTOR}->disableReelsScrolling()Z
        move-result v0
        if-eqz v0, :piko
        iget-object v0, p0, $viewPagerField
        const/4 v1, 0x0
        invoke-virtual { v0, v1 }, Landroidx/viewpager2/widget/ViewPager2;->setUserInputEnabled(Z)V
        :piko
        nop
    """
)

ClipsSwipeRefreshLayoutOnInterceptTouchEventFingerprint.method.addInstructions(
    0,
    """
        ${PREF_CALL_DESCRIPTOR}->disableReelsScrolling()Z
        move-result v0
        if-eqz v0, :piko
        const/4 v0, 0x0
        return v0
        :piko
        nop
    """
)
```

### Required Modifications

#### A. Reels opened from Direct Messages / Shared Links
- **Reason:** Clips opened from a message thread may instantiate a different entrypoint or launcher activity/fragment, but use the same `ClipsViewPagerImpl` / `ViewPager2`.
- **Expected Behaviour:** Ensure `ClipsViewPagerImplGetViewAtIndexFingerprint` also applies to clips opened via chat intents and direct message links, disabling vertical item-to-item swiping while keeping the media playback, audio control, back navigation, and comments accessible.

#### B. Home / Feed Navigation Gesture Interception
- **Target Components:** Instagram Main Feed container (`MainFeedFragment` / `FeedFragment` / `RecyclerView` / Litho `ComponentTree`).
- **Required Modification:**
  - Locate the root vertical scroll controller / gesture listener responsible for scrolling between consecutive posts.
  - Determine whether Instagram handles feed swiping via `RecyclerView.LayoutManager.canScrollVertically()`, `RecyclerView.onInterceptTouchEvent()`, or a custom touch dispatcher.
  - When `Pref.disableFeedScrolling()` (or global scroll blocking) is active, prevent vertical swipe fling gestures from advancing the feed to the next post, while preserving:
    - Tap events on Like, Comment, Share, and Overflow Menu buttons.
    - Media item clicks (carousel paging horizontally, opening media details).
    - Long presses and profile link taps.
- **Expected Behaviour:** Feed does not endlessly scroll down or swipe to subsequent items; user remains on the currently loaded post or current screen without vertical navigation swipe.

#### C. Explore Page Swipe Interception
- **Target Components:** `ExploreFragment` / Explore Grid / Explore Viewer.
- **Required Modification:**
  - Intercept vertical swipe gestures between explore posts and clips in the explore viewer.
  - Prevent swiping to the next explore recommendation.
  - Preserve tap interactions for opening an item, searching, and back navigation.
- **Expected Behaviour:** Explore screen does not allow vertical swipe browsing across content items.

#### D. Preserved Surfaces (Explicit Exclusions)
- **Surfaces:**
  - `UserProfileFragment` (Profile page)
  - `NotificationFragment` / Activity Feed (Notifications page)
  - `DirectThreadFragment` / Inbox (Direct Messages and Chat threads)
- **Required Modification:**
  - Under no circumstances should scroll or touch handlers on these classes/fragments be hooked or disabled.
  - Normal vertical list scrolling for reading chat history, checking notifications, and viewing profile grids must remain 100% operational.

---

## 3. Target File 2: `Pref.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/utils/Pref.java`

### Required Modification
- In method `disableReelsScrolling()`:
  - Currently: `return lockable(Settings.DISABLE_REELS_SCROLLING) && SettingsStatus.disableReelsScrolling;`
  - Required: Return `SharedPref.getBooleanPref(Settings.DISABLE_REELS_SCROLLING) && SettingsStatus.disableReelsScrolling;` (removing the deleted `FocusLock` dependency).
- Add methods for `disableFeedScrolling()` and `disableExploreScrolling()` or unify under `disableSwipeNavigation()`.

---

## 4. Target File 3: `Settings.java` and `SettingsStatus.java`
- **Path:**
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/Settings.java`
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/SettingsStatus.java`

### Required Modification
- Update `DISABLE_REELS_SCROLLING` description and preferences to reflect global scroll blocking covering Home/Feed, Reels, and Explore, with explicit exclusion of Profile, Notifications, and DMs.
