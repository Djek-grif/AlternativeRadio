package com.djekgrif.alternativeradio.common;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by djek-grif on 6/9/17.
 */

public class Logger {

    public interface LogsHolder {

        String DEBUG = "D";
        String INFO = "I";
        String WARNING = "W";
        String ERROR = "E";

        void addLog(String tag, String time, String priority, String module, String message);
        List<String> getLogs();

    }

    // Modules
    public static final String SONG_INFO = "SONG_INFO";
    public static final String LIFECYCLE = "LIFE_CYCLE";
    public static final String DB_REMOTE = "DB_REMOTE";
    public static final String STREAM = "STREAM";
    public static final String PLAYER = "PLAYER";

    public static final String REPORT_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final String LOG_SEPARATOR = "\t";
    private static final int CALL_STACK_INDEX = 3;
    private static final int MAX_LOG_LENGTH = 4000;
    private static final int MAX_TAG_LENGTH = 25;

    private static final int D = 1;
    private static final int I = 2;
    private static final int W = 3;
    private static final int E = 4;

    public enum Level {DEFAULT, DETAILS}

    private static LogsHolder logsHolder;
    private static Level currentLevel = Level.DEFAULT;
    private static SimpleDateFormat formatter = new SimpleDateFormat(REPORT_TIME_FORMAT_PATTERN, Locale.US);
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    private static final ThreadLocal<String> explicitTag = new ThreadLocal<>();

    public static void setCurrentLevel(Level currentLevel) {
        Logger.currentLevel = currentLevel;
    }

    public static void setLogsHolder(LogsHolder logsHolder) {
        Logger.logsHolder = logsHolder;
    }

    public static void removeLogsHolder() {
        Logger.logsHolder = null;
    }

    public static void d(String message) {
        Logger.d(message, null, Level.DEFAULT);
    }

    public static void d(String message, String module) {
        Logger.d(message, module, Level.DEFAULT);
    }

    public static void d(String message, Level level) {
        Logger.d(message, null, level);
    }

    public static void d(String message, String module, Level level) {
        String tag = getTag();
        addLog(tag, message, module, level, D);
        addLogToHolder(tag, message, module, level, LogsHolder.DEBUG);
    }


    public static void i(String message) {
        Logger.i(message, null, Level.DEFAULT);
    }

    public static void i(String message, String module) {
        Logger.i(message, module, Level.DEFAULT);
    }

    public static void i(String message, Level level) {
        Logger.i(message, null, level);
    }

    public static void i(String message, String module, Level level) {
        String tag = getTag();
        addLog(tag, message, module, level, I);
        addLogToHolder(tag, message, module, level, LogsHolder.INFO);
    }

    public static void w(String message) {
        Logger.w(message, null, Level.DEFAULT);
    }

    public static void w(String message, String module) {
        Logger.w(message, module, Level.DEFAULT);
    }

    public static void w(String message, Level level) {
        Logger.w(message, null, level);
    }

    public static void w(String message, String module, Level level) {
        String tag = getTag();
        addLog(tag, message, module, level, W);
        addLogToHolder(tag, message, module, level, LogsHolder.WARNING);
    }


    public static void e(String message) {
        Logger.e(null, message, null, Level.DEFAULT);
    }

    public static void e(Throwable t, String message) {
        Logger.e(t, message, null, Level.DEFAULT);
    }

    public static void e(String message, String module) {
        Logger.e(null, message, module, Level.DEFAULT);
    }

    public static void e(Throwable t, String message, String module) {
        Logger.e(t, message, module, Level.DEFAULT);
    }

    public static void e(String message, Level level) {
        Logger.e(null, message, null, level);
    }

    public static void e(Throwable t, String message, Level level) {
        Logger.e(t, message, null, level);
    }

    public static void e(String message, String module, Level level) {
        Logger.e(null, message, null, level);
    }

    public static void e(Throwable t, String message, String module, Level level) {
        String tag = getTag();
        addLog(t, tag, message, module, level, E);
        addLogToHolder(tag, message, module, level, LogsHolder.ERROR);
    }

    private static void addLog(String tag, String message, String module, Level level, int priority) {
        addLog(null, tag, message, module, level, priority);
    }

    private static void addLog(Throwable t, String tag, String message, String module, Level level, int priority) {
        if (currentLevel == level || level == Level.DEFAULT) {
            message = message.length() > MAX_LOG_LENGTH ? message.substring(0, MAX_LOG_LENGTH) : message;
            String log = buildLog(module, message);
            switch (priority) {
                case D:
                    Log.d(tag, log);
                    break;
                case I:
                    Log.i(tag, log);
                    break;
                case W:
                    Log.w(tag, log);
                    break;
                case E:
                    if(t != null) {
                        Log.e(tag, log, t);
                    }else{
                        Log.e(tag, log);
                    }
                    break;
            }
        }
    }

    private static void addLogToHolder(String tag, String message, String module, Level level, String priority) {
        if (logsHolder != null && (currentLevel == level || level == Level.DEFAULT)) {
            tag = tag != null ? tag : getTag();
            String time = formatter.format(System.currentTimeMillis());
            logsHolder.addLog(tag, time, priority, module, message);
        }
    }

    private static String getTag() {
        String tag = explicitTag.get();
        if (tag != null) {
            explicitTag.remove();
            return tag;
        }
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            Log.e("Logger", "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        tag = stackTrace[CALL_STACK_INDEX].getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1);
        return tag.length() > MAX_TAG_LENGTH ? tag.substring(0, MAX_TAG_LENGTH) : tag;
    }

    private static String buildLog(String module, String message) {
        return Thread.currentThread().getName() +
                (module != null ? LOG_SEPARATOR + module : "") +
                LOG_SEPARATOR + message;
    }
}
