package com.djekgrif.alternativeradio.common;

import android.os.SystemClock;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.metadata.id3.GeobFrame;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.metadata.id3.TxxxFrame;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by djek-grif on 10/20/16.
 */

public class PlayerLogger implements AudioRendererEventListener,
        ExoPlayer.EventListener, MetadataRenderer.Output<List<Id3Frame>> {

    private static final int MAX_TIMELINE_ITEM_LINES = 3;
    private static final NumberFormat TIME_FORMAT;
    static {
        TIME_FORMAT = NumberFormat.getInstance(Locale.US);
        TIME_FORMAT.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
        TIME_FORMAT.setGroupingUsed(false);
    }

    private final long startTimeMs;
    private final Timeline.Window window;
    private final Timeline.Period period;

    public PlayerLogger() {
        window = new Timeline.Window();
        period = new Timeline.Period();
        startTimeMs = SystemClock.elapsedRealtime();
    }

    @Override
    public void onAudioEnabled(DecoderCounters counters) {}
    @Override
    public void onAudioSessionId(int audioSessionId) {}
    @Override
    public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {}
    @Override
    public void onAudioInputFormatChanged(Format format) {}
    @Override
    public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {}
    @Override
    public void onAudioDisabled(DecoderCounters counters) {}
    @Override
    public void onLoadingChanged(boolean isLoading) {}

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        String state;
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                state = "B";
                break;
            case ExoPlayer.STATE_ENDED:
                state = "E";
                break;
            case ExoPlayer.STATE_IDLE:
                state = "I";
                break;
            case ExoPlayer.STATE_READY:
                state = "R";
                break;
            default:
                state = "?";
        }
        Logger.d("Player state: " + getSessionTimeString() + ", " + playWhenReady + ", " + state + "]", Logger.PLAYER);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Logger.e(error, "Error play sessionTime:" + getSessionTimeString());
    }

    @Override
    public void onPositionDiscontinuity() {}


    @Override
    public void onMetadata(List<Id3Frame> metadata) {
        for (Id3Frame id3Frame : metadata) {
            if (id3Frame instanceof TxxxFrame) {
                TxxxFrame txxxFrame = (TxxxFrame) id3Frame;
                Logger.i(String.format("ID3 TimedMetadata %s: description=%s, value=%s", txxxFrame.id,
                        txxxFrame.description, txxxFrame.value), Logger.Level.DETAILS);
            } else if (id3Frame instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) id3Frame;
                Logger.i(String.format("ID3 TimedMetadata %s: owner=%s", privFrame.id, privFrame.owner), Logger.Level.DETAILS);
            } else if (id3Frame instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) id3Frame;
                Logger.i(String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description), Logger.Level.DETAILS);
            } else if (id3Frame instanceof ApicFrame) {
                ApicFrame apicFrame = (ApicFrame) id3Frame;
                Logger.i(String.format("ID3 TimedMetadata %s: mimeType=%s, description=%s",
                        apicFrame.id, apicFrame.mimeType, apicFrame.description), Logger.Level.DETAILS);
            } else if (id3Frame instanceof TextInformationFrame) {
                TextInformationFrame textInformationFrame = (TextInformationFrame) id3Frame;
                Logger.i(String.format("ID3 TimedMetadata %s: description=%s", textInformationFrame.id,
                        textInformationFrame.description), Logger.Level.DETAILS);
            } else {
                Logger.i(String.format("ID3 TimedMetadata %s", id3Frame.id), Logger.Level.DETAILS);
            }
        }
    }

    private String getSessionTimeString() {
        return getTimeString(SystemClock.elapsedRealtime() - startTimeMs);
    }

    private static String getTimeString(long timeMs) {
        return timeMs == C.TIME_UNSET ? "?" : TIME_FORMAT.format((timeMs) / 1000f);
    }

//    private static String getFormatString(Format format) {
//        if (format == null) {
//            return "null";
//        }
//        StringBuilder builder = new StringBuilder();
//        builder.append("id=").append(format.id).append(", mimeType=").append(format.sampleMimeType);
//        if (format.bitrate != Format.NO_VALUE) {
//            builder.append(", bitrate=").append(format.bitrate);
//        }
//        if (format.width != Format.NO_VALUE && format.height != Format.NO_VALUE) {
//            builder.append(", res=").append(format.width).append("x").append(format.height);
//        }
//        if (format.frameRate != Format.NO_VALUE) {
//            builder.append(", fps=").append(format.frameRate);
//        }
//        if (format.channelCount != Format.NO_VALUE) {
//            builder.append(", channels=").append(format.channelCount);
//        }
//        if (format.sampleRate != Format.NO_VALUE) {
//            builder.append(", sample_rate=").append(format.sampleRate);
//        }
//        if (format.language != null) {
//            builder.append(", language=").append(format.language);
//        }
//        return builder.toString();
//    }
}
