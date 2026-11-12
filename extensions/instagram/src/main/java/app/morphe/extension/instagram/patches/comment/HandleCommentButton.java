/*
 * Copyright (C) 2026 piko <https://github.com/crimera/piko>
 *
 * See the included NOTICE file for GPLv3 §7(b) terms that apply to this code.
 */


package app.morphe.extension.instagram.patches.comment;

import static app.morphe.extension.instagram.utils.IgStr.str;

import java.util.List;
import android.content.Context;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.crimera.PikoUtils;
import app.morphe.extension.crimera.ObjectBrowser;
import app.morphe.extension.crimera.downloader.MediaType;

import app.morphe.extension.instagram.entity.CommentData;
import app.morphe.extension.instagram.entity.MediaData;
import app.morphe.extension.instagram.entity.UserData;
import app.morphe.extension.instagram.utils.Pref;
import app.morphe.extension.instagram.constants.Constants;
import app.morphe.extension.instagram.patches.download.DownloadUtils;
import app.morphe.extension.instagram.patches.comment.saveMediaButton.SaveMediaButton;

// Thanks to MyInsta.
@SuppressWarnings("unused")
public class HandleCommentButton {

    public static void addButtons(List list, Object commentObject) {
        try {
            CommentData commentData = new CommentData(commentObject);

            if(commentData.hasMedia() && Pref.commentSaveMediaButton()){
                list.add(SaveMediaButton.A00);
            }
        } catch (Exception e) {
            PikoUtils.logger(e);
        }
    }

    // return true = Piko button clicked no need to check Instagram's buttons.
    // return false = Piko button not clicked, check Instagram's buttons.
    public static boolean checkOnCommentButtonClick(Object button, List list) {
        try {
            if (list.isEmpty()) return false;
            Object commentObject = list.get(0);

            if (button.equals(SaveMediaButton.A00)) {
                CommentData commentData = new CommentData(commentObject);

                Context context = Utils.getContext();
                if (commentData.hasGifMedia()) {
                    String gifUrl = commentData.getGifUrl();
                    String fileName = commentData.getGifDownloadName();
                    DownloadUtils.downloadMediaUrl(context, gifUrl, Constants.DEFAULT_GIF_FOLDER, fileName);
                } else if (commentData.hasImageMedia()) {
                    MediaData imageData = commentData.getImageMedia();
                    UserData userData = commentData.getCommentUserData();

                    String userName = (userData != null) ? userData.getUsername() : "comment_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String mediaLink = imageData.getMediaLink();
                    String fileName = userName + "_" + imageData.getDownloadFilename(MediaType.ANY);

                    String subFolder = DownloadUtils.getSubfolderName(userName);

                    DownloadUtils.downloadMediaUrl(context, mediaLink, subFolder, fileName);
                }
                
                return true;
            }

        } catch (Exception e) {
            PikoUtils.logger(e);
            PikoUtils.toast(e.getMessage());
            // If exception happens, we need not check Instagram's button.
            return true;
        }
        return false;
    }

}
