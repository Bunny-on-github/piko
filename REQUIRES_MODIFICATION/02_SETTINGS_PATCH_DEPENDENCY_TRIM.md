# Required Modification: SettingsPatch.kt Dependency Trim

## 1. Overview
`SettingsPatch.kt` acts as the primary patch entrypoint that imports and bundles various feature patches into the Instagram patching lifecycle. Because unrelated feature patches have been conclusively removed during the cleanup, `SettingsPatch.kt` contains unresolved imports and dependencies that must be trimmed.

---

## 2. Target File: `SettingsPatch.kt`
- **Path:** `patches/src/main/kotlin/app/crimera/patches/instagram/misc/settings/SettingsPatch.kt`

### Unresolved Imports to Remove
```kotlin
// Line 14:
import app.crimera.patches.instagram.links.validateLinks.validateLinksPatch
// Line 15:
import app.crimera.patches.instagram.misc.actionBar.mainFeedActionBarButton.hideHomeActionButtonsPatch
// Line 16:
import app.crimera.patches.instagram.misc.actionBar.mainFeedActionBarButton.mainFeedActionBarButtonPatch
// Line 17:
import app.crimera.patches.instagram.misc.actionBar.userProfileActionBarButton.userProfileActionBarButtonPatch
// Line 18:
import app.crimera.patches.instagram.misc.buildExpiredPopup.removeBuildExpiredPopupPatch
// Line 19:
import app.crimera.patches.instagram.misc.dm.hideChatCallButtonsPatch
// Line 23:
import app.crimera.patches.instagram.misc.notification.fixDirectNotificationActionCrashPatch
// Line 24:
import app.crimera.patches.instagram.misc.notification.fixNotificationRegistrationCrashPatch
// Line 25:
import app.crimera.patches.instagram.misc.userProfile.preserveUnknownFriendshipStatusPatch
// Line 26:
import app.crimera.patches.instagram.misc.userProfile.userProfileButtonPatch
```

### Unresolved References in `dependsOn(...)` to Remove
Lines 110–122 currently specify:
```kotlin
mainFeedActionBarButtonPatch,
hideHomeActionButtonsPatch,
hideChatCallButtonsPatch,
userProfileActionBarButtonPatch,
userProfileButtonPatch,
fixNotificationRegistrationCrashPatch,
fixDirectNotificationActionCrashPatch,
validateLinksPatch,
removeBuildExpiredPopupPatch,
preserveUnknownFriendshipStatusPatch,
```

### Unresolved Code in `execute` Block to Remove
Lines 295–304:
```kotlin
// For welcome message.
MainFeedFragmentOnCreateFingerprint.let {
    val contextResultIndex = it.instructionMatches.last().index
    val contextRegister = it.instructionMatches.last().getInstruction<OneRegisterInstruction>().registerA

    it.method.addInstruction(
        contextResultIndex + 1,
        "invoke-static/range {v$contextRegister .. v$contextRegister}, " +
            "$PATCHES_DESCRIPTOR/WelcomeMessage;->openWelcomeMessage(Landroid/content/Context;)V",
    )
}
```
`WelcomeMessage.java` was deleted because the "Welcome to Piko" dialog is an obsolete/unrelated feature.

### Retained `dependsOn(...)` Specification
The modified `dependsOn` in `settingsPatch` should only contain:
```kotlin
sharedExtensionPatch,
settingsResourcePatch,
nativeSettingsSwitchStylePatch,
whitehatIndicatorPatch,
settingsTaskRemovalPatch,
userDataEntity,
hookFlagsPatch,
spoofSignaturePatch,
profileInfoEntity,
instagramDialogBoxEntity,
instagramButtonEntity,
developerOptionsEntity,
addResourcesPatch,
```
