package com.djekgrif.alternativeradio.manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.core.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import androidx.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.common.StreamService;
import com.djekgrif.alternativeradio.ui.activity.HomeActivity;
import com.djekgrif.alternativeradio.ui.utils.DimensUtils;
import com.djekgrif.alternativeradio.utils.DeviceUtils;

/**
 * Created by djek-grif on 11/15/16.
 */

public class NotificationHelper {

    private static final int NOTIFICATION_ID = 232451;
    private static final String CHANNEL_ID = "media_playback_channel";
    private static final String CHANNEL_NAME = "radio_channel_256";

    public static void initMediaSessionMetadata(MediaSessionCompat mediaSessionCompat){
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);
        mediaSessionCompat.setMetadata(metadataBuilder.build());
    }

    public static void updateMediaSessionMetadata(MediaSessionCompat mediaSessionCompat, String title, String description, String imageUrl, ImageLoader imageLoader) {
        Resources resources = App.getInstance().getResources();
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, description);
        mediaSessionCompat.setMetadata(metadataBuilder.build());

        imageLoader.loadBitmap(imageUrl, DimensUtils.dpToPx(R.dimen.default_large), DimensUtils.dpToPx(R.dimen.default_large), bitmap -> {
            bitmap = bitmap == null ? BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher) : bitmap;
            //Notification icon in card
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
            //lock screen icon for pre lollipop
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap);

            mediaSessionCompat.setMetadata(metadataBuilder.build());

        });
    }

    public static void showPlayingNotification(MediaSessionCompat mediaSessionCompat, Service service) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) App.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = getBuilder(App.getInstance(), mediaSessionCompat);
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause,
                    App.getInstance().getString(R.string.pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(App.getInstance(), PlaybackStateCompat.ACTION_STOP)));
            builder.setSmallIcon(DeviceUtils.isLollipopOrHigher() ? R.drawable.ic_service_transparent : R.drawable.ic_service);
//            NotificationManagerCompat.from(App.getInstance()).notify(NOTIFICATION_ID, builder.build());
        service.startForeground(NOTIFICATION_ID, builder.build());
    }

    public static void showStopNotification(MediaSessionCompat mediaSessionCompat) {
        NotificationCompat.Builder builder = getBuilder(App.getInstance(), mediaSessionCompat);
        if (builder != null) {
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,
                    App.getInstance().getString(R.string.play),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(App.getInstance(), PlaybackStateCompat.ACTION_PLAY)));
            builder.setSmallIcon(DeviceUtils.isLollipopOrHigher() ? R.drawable.ic_service_transparent : R.drawable.ic_service);
            NotificationManagerCompat.from(App.getInstance()).notify(NOTIFICATION_ID, builder.build());
        }
    }

    public static void removeNotifications(Service service) {
        service.stopForeground(true);
//        NotificationManagerCompat.from(App.getInstance()).cancelAll();
    }

    private static NotificationCompat.Builder getBuilder(Context context, MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        Intent deleteIntent = new Intent(context, StreamService.class);
        deleteIntent.setAction(StreamService.CUSTOM_EXIT_ACTION);
        PendingIntent stopApp = PendingIntent.getService(context, 0, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        Intent openUI = new Intent(context, HomeActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        openUI.putExtra(MusicPlayerActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, mediaSession);
        PendingIntent openUIPendingIntent = PendingIntent.getActivity(context, 100, openUI, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder
                .setStyle(new MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()))
                .setShowWhen(false)
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(openUIPendingIntent)
                .setDeleteIntent(stopApp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        return builder;
    }
}
