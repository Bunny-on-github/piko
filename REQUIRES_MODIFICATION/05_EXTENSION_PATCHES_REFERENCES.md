# Required Modification: Extension Patches References Cleanup

## 1. Overview
The retained core patch helpers in `extensions/instagram/src/main/java/app/morphe/extension/instagram/patches/` contain a few specific references to classes that have been removed (`Links`, `MoreOptionsOnPostPatch`, `CopyTextButton`, `CopyUsernameButton`, and `DebugButton`).

---

## 2. Target File 1: `DownloadUtils.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/patches/download/DownloadUtils.java`

### References to Update
- Lines 631 and 976:
  ```java
  String link = Links.generatePostLink(mediaObject, currentMediaIndex);
  ```
- **Reason:** `Links.java` was deleted because link sanitization and external browser handling are unrelated features.
- **Required Modification:** Replace `Links.generatePostLink(...)` with a standalone URL constructor:
  ```java
  public static String generatePostLink(MediaData mediaData, int index) {
      String code = mediaData.getShortCode();
      if (code == null || code.isEmpty()) return null;
      return "https://www.instagram.com/p/" + code + "/";
  }
  ```
- **Expected Behaviour:** "Copy media link" and embedding metadata inside downloaded MP4/JPEG files construct direct Instagram post links without relying on `Links.java`.

---

## 3. Target File 2: `HandleCommentButton.java`
- **Path:** `extensions/instagram/src/main/java/app/morphe/extension/instagram/patches/comment/HandleCommentButton.java`

### Unresolved Imports to Remove
```java
// Line 29:
import app.morphe.extension.instagram.patches.comment.copyTextButton.CopyTextButton;
// Line 30:
import app.morphe.extension.instagram.patches.comment.copyTextButton.CopyUsernameButton;
// Line 31:
import app.morphe.extension.instagram.patches.comment.debugButton.DebugButton;
```

### Required Modification in `addButtons(...)`
- Remove calls adding `DebugButton.A00`, `CopyTextButton.A00`, and `CopyUsernameButton.A00`.
- Retain solely:
  ```java
  if (commentData.hasMedia() && Pref.commentSaveMediaButton()) {
      list.add(SaveMediaButton.A00);
  }
  ```
- **Expected Behaviour:** The comment action buttons list only adds the `SaveMediaButton` for media comments (GIFs, images), preserving full media download capability while omitting deleted comment text-copy buttons.

---

## 4. Target File 3: `FeedButton.java` and `InfoButton.java`
- **Paths:**
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/patches/overflowMenuButton/FeedButton.java`
  - `extensions/instagram/src/main/java/app/morphe/extension/instagram/patches/overflowMenuButton/reels/buttons/InfoButton.java`

### Unresolved Import to Remove
- `import app.morphe.extension.instagram.patches.feed.MoreOptionsOnPostPatch;`
- In `FeedButton.java`: Remove checks for `MoreOptionsOnPostPatch.isEnabled()`. Retain download button insertion.
- In `InfoButton.java`: Remove `MoreOptionsOnPostPatch` reference or omit `InfoButton` from `AddReelButton.java`.
