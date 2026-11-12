# Required Modification: Settings UI, Preference Widgets, and Helpers Cleanup

## 1. Overview
The settings UI system in `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/` contains activities, preference fragments, and preference screen builder methods that reference deleted features (Custom Font, Focus Lock, Navigation Bar Customization, Hidden Chats/Inbox Lock, Material You Theme, and Links).

---

## 2. Target File 1: `ScreenBuilder.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/ScreenBuilder.java`

### Unresolved Imports to Remove
- Line 29: `import app.morphe.extension.instagram.patches.Links;`
- Line 30: `import app.morphe.extension.instagram.patches.customise.font.FontStorage;`
- Line 31: `import app.morphe.extension.instagram.patches.focusLock.FocusLock;`
- Line 32: `import app.morphe.extension.instagram.patches.focusLock.FocusLockTargets;`
- Line 40: `import app.morphe.extension.instagram.theme.MaterialYouTheme;`

### Unresolved Methods / Sections to Remove
- `buildAdsSection()`
- `ghostSection()`
- `storySection()`
- `dmSection()`
- `linksSection()`
- `distractionFreeSection()` (convert to simple Scroll Blocking section)
- `buildFontSection()`
- References to `FocusLockDialogs`, `FontStorage`, `MaterialYouTheme`, and `Links`.

### Expected Behaviour
`ScreenBuilder` should only construct two primary preference screens/categories:
1. **Media Downloads:** Toggles and configurations for downloading Feed posts, Reels, Stories, Audio, and DASH format.
2. **Scroll & Swipe Blocking:** Toggle for blocking vertical navigation across Feed, Reels, and Explore.

---

## 3. Target File 2: `Helper.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/Helper.java`

### Unresolved References to Remove
- Line 20: `import app.morphe.extension.instagram.patches.ReelsPlayback;`
- Line 21: `import app.morphe.extension.instagram.patches.dm.InboxLock;`
- Line 34: `import app.morphe.extension.instagram.theme.MaterialYouTheme;`
- Line 244: `ReelsPlayback.onSettingChanged();`

---

## 4. Target File 3: `ButtonPref.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/widgets/ButtonPref.java`

### Unresolved References to Remove
- Line 23: `import app.morphe.extension.instagram.patches.customise.font.FontStorage;`
- Line 28: `import app.morphe.extension.instagram.patches.focusLock.FocusLockDialogs;`
- Line 29: `import app.morphe.extension.instagram.patches.dm.SavedMessagesHook;`
- Handlers in `onClick` referencing `FontStorage`, `FocusLockDialogs`, and `SavedMessagesHook`.

---

## 5. Target File 4: Navigation Bar Preference Classes
- **Files:**
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/widgets/NavigationStartupPreference.java`
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/navigation/NavigationBarAdapter.java`
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/navigation/NavigationBarEditorState.java`
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/navigation/NavigationSettingsAccessPolicy.java`
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/preference/widgets/MultiSelectListPref.java`
- **Reason:** These classes import `app.morphe.extension.instagram.patches.navigation.NavigationBarPatch`, which was deleted as an unrelated feature.
- **Required Modification:** Delete these navigation bar editor preference widgets or remove `NavigationBarPatch` references, as navigation bar customization is completely removed.

---

## 6. Target File 5: `ActivityHook.java` & `RestorePrefFragment.java`
- **Files:**
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/ActivityHook.java`
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/RestorePrefFragment.java`
- **Unresolved References:**
  - `ActivityHook.java` line 25: `import app.morphe.extension.instagram.patches.dm.DeletedMessagesActivity;`
  - `RestorePrefFragment.java` lines 38–40: imports of `FontStorage`, `InboxLock`, `FocusLock`.
- **Required Modification:** Remove references to `DeletedMessagesActivity`, `FontStorage`, `InboxLock`, and `FocusLock`.
