package app.crimera.patches.instagram.misc.reels

import app.crimera.patches.instagram.utils.Constants.COMPATIBILITY_INSTAGRAM
import app.crimera.patches.instagram.utils.Constants.PREF_CALL_DESCRIPTOR
import app.crimera.patches.instagram.utils.Constants.SCROLL_BLOCKER_DESCRIPTOR
import app.crimera.patches.instagram.utils.enableSettings
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.patch

private object ClipsViewPagerImplGetViewAtIndexFingerprint : Fingerprint(
    strings = listOf("ClipsViewPagerImpl_getViewAtIndex")
)

private object ClipsSwipeRefreshLayoutOnInterceptTouchEventFingerprint : Fingerprint (
    name = "onInterceptTouchEvent",
    parameters = listOf("Landroid/view/MotionEvent;"),
    definingClass = "Linstagram/features/clips/viewer/ui/ClipsSwipeRefreshLayout;"
)

@Suppress("unused")
val disableReelsScrollingPatch = patch(
    name = "Disable Reels and Feed swipe navigation",
    description = "Disables vertical swipe navigation between consecutive Reels and Feed/Explore items, preventing swiping to the next item while preserving screen controls, chat interactions, media playback, and back navigation."
) {
    compatibleWith(COMPATIBILITY_INSTAGRAM)

    execute {
        val viewPagerField = ClipsViewPagerImplGetViewAtIndexFingerprint.classDef.fields.first {
            it.type == "Landroidx/viewpager2/widget/ViewPager2;"
        }

        // Disable user input on the ViewPager2 to prevent scrolling across Reels, Explore clips, and chat-shared Reels.
        ClipsViewPagerImplGetViewAtIndexFingerprint.method.addInstructions(
            0,
            """
                ${PREF_CALL_DESCRIPTOR}->disableReelsScrolling()Z
                move-result v0
                if-eqz v0, :piko
                iget-object v0, p0, $viewPagerField
                const/4 v1, 0x0
                invoke-virtual { v0, v1 }, Landroidx/viewpager2/widget/ViewPager2;->setUserInputEnabled(Z)V
                invoke-static { v0 }, $SCROLL_BLOCKER_DESCRIPTOR->blockViewPager(Ljava/lang/Object;)V
                :piko
                nop
            """
        )

        // Return false in onInterceptTouchEvent to disable pull-to-refresh.
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

        enableSettings("disableReelsScrolling")
    }
}
