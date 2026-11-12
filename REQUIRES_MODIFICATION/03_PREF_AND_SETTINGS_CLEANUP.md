# Required Modification: Pref.java, Settings.java, and SettingsStatus.java Cleanup

## 1. Overview
`Pref.java`, `Settings.java`, and `SettingsStatus.java` contain configuration flags, preference getters, and runtime toggles for numerous features that have been removed. Additionally, `Pref.java` contains unresolved imports for deleted classes (`FocusLock` and `MarkChatAsReadScope`).

---

## 2. Target File 1: `Pref.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/utils/Pref.java`

### Unresolved Imports to Remove
- Line 24: `import app.morphe.extension.instagram.patches.focusLock.FocusLock;`
- Line 28: `import app.morphe.extension.shared.MarkChatAsReadScope;`

### Methods Requiring Updates
1. `clearAllPreferences()` (lines 45–49):
   - **Current:**
     ```java
     public static boolean clearAllPreferences() {
         if (FocusLock.isActive()) return false;
         return SharedPref.clearAll();
     }
     ```
   - **Required:** Remove `FocusLock.isActive()` check and simply return `SharedPref.clearAll();`.
2. `lockable(BooleanSetting setting)` (lines 206–208):
   - **Current:**
     ```java
     private static boolean lockable(BooleanSetting setting) {
         return SharedPref.getBooleanPref(setting) || FocusLock.isForced(setting);
     }
     ```
   - **Required:** Remove `FocusLock.isForced(setting)` and return `SharedPref.getBooleanPref(setting)`.
3. `shouldBlockDmSeen(...)` (lines 132–141):
   - **Current:** References `MarkChatAsReadScope.isActive()`.
   - **Required:** Remove or replace reference with false.
4. FocusLock getters and setters (lines 210–259):
   - Remove `focusLockSelected`, `focusLockSelection`, `focusLockDurationMinutes`, `legacyFocusLockBlockReels`, `focusLockUntil`, `focusLockUnlockRequestedAt`, etc.
5. Obsolete getters for deleted features:
   - Remove methods related to Ads (`disableAds`, `hideSuggestedContent`), Ghost modes (`viewStoriesAnonymously`, `viewLiveAnonymously`, `viewDmAnonymously`, etc.), Link sanitization (`openLinksExternally`, `sanitizeShareLinks`), Story filters/flipping (`disableStoryFlipping`, `filterStoryByType`), Action bar custom buttons (`mainFeedActionBarButtons`, `userProfileActionBarButtons`, `chatActionBarButtons`), Custom fonts (`useSystemFont`), and Navigation bar tabs (`navigationTabs`).

---

## 3. Target File 2: `Settings.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/Settings.java`

### Required Modification
Remove obsolete `BooleanSetting` and `StringSetting` constants for deleted features. Retain only:
- **Core / Debug:** `PIKO_DEBUG`, `FIRST_TIME_PIKO`.
- **Media Downloads:** `ENABLE_DOWNLOAD`, `FEED_DOWNLOAD_BUTTON`, `REEL_DOWNLOAD_BUTTON`, `STORY_DOWNLOAD_BUTTON`, `ENABLE_DIRECT_DOWNLOAD`, `DOWNLOAD_USERNAME_FOLDER`, `EMBED_DOWNLOAD_METADATA`, `PREFER_DASH_FORMAT`, `DASH_AUDIO_AAC_LC`, `DOWNLOAD_FILE_NAME_TEMPLATE`, `CUSTOM_DOWNLOAD_PATH`, `EXTERNAL_DOWNLOADER_PACKAGE_NAME`, `DOWNLOAD_WITH_EXTERNAL_DOWNLOADER`, `COMMENT_SAVE_MEDIA_BUTTON`, `INSTANTS_DOWNLOAD`.
- **Scroll / Swipe Blocking:** `DISABLE_REELS_SCROLLING` (and extended `DISABLE_SCROLLING`).

---

## 4. Target File 3: `SettingsStatus.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/settings/SettingsStatus.java`

### Required Modification
- Remove flags and section methods for: `adsSection()`, `ghostSection()`, `dmSection()`, `storySection()`, `linksSection()`, `distractionFreeSection()` (except scroll blocking), `fontSection()`.
- Retain only:
  - `addSettings`, `downloadMedia`, `saveMediaCommentButton`, `feedDownloadButton`, `reelDownloadButton`, `storyDownloadButton`, `downloadVoiceMessage`, `downloadWithExternalDownloader`, `instantsDownload`, `disableReelsScrolling`.
- Update `loadStatusMap()` to register only the retained flags.
