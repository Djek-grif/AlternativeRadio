package com.djekgrif.alternativeradio.manager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.utils.DeviceUtils;

import timber.log.Timber;

/**
 * Created by djek-grif on 11/15/16.
 */

public class NotificationManager {

    public static void initMediaSessionMetadata(MediaSessionCompat mediaSessionCompat, String title, String description) {
        Resources resources = App.getInstance().getResources();
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        //Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, description);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);

        mediaSessionCompat.setMetadata(metadataBuilder.build());
    }

    public static void showPlayingNotification(MediaSessionCompat mediaSessionCompat) {
        NotificationCompat.Builder builder = getBuilder(App.getInstance(), mediaSessionCompat);
        if (builder != null) {
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause,
                    App.getInstance().getString(R.string.pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(App.getInstance(), PlaybackStateCompat.ACTION_STOP)));
            builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSessionCompat.getSessionToken()));
            builder.setSmallIcon(DeviceUtils.isLollipopOrHigher() ? R.drawable.ic_service_transparent : R.drawable.ic_service);
            NotificationManagerCompat.from(App.getInstance()).notify(1, builder.build());
        }else{
            Timber.e("Notification builder is NULL");
        }
    }

    public static void showStopNotification(MediaSessionCompat mediaSessionCompat) {
        NotificationCompat.Builder builder = getBuilder(App.getInstance(), mediaSessionCompat);
        if (builder != null) {
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,
                    App.getInstance().getString(R.string.play),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(App.getInstance(), PlaybackStateCompat.ACTION_PLAY)));
            builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSessionCompat.getSessionToken()));
            builder.setSmallIcon(DeviceUtils.isLollipopOrHigher() ? R.drawable.ic_service_transparent : R.drawable.ic_service);
            NotificationManagerCompat.from(App.getInstance()).notify(1, builder.build());
        }
    }

    public static void removeNotifications(){
        NotificationManagerCompat.from(App.getInstance()).cancelAll();
    }

    private static NotificationCompat.Builder getBuilder(Context context, MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                .setShowWhen(false)
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        return builder;
    }
}
