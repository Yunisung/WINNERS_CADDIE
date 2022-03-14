package com.bkwinners.ksnet.dpt.design.util;

import android.text.TextUtils;
import android.util.Log;


import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;


public class LOG {

    // ======================================================
    private static final String LOG_TAG = "CADDIE";
    private static final int STACK_TRACE_INDEX = 4;
    private static final int STACK_TRACE_PREVIOUS_INDEX = 5;
    // ======================================================

    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARNING = 2;
    public static final int LEVEL_ERROR = 3;
    public static final int LEVEL_VERBOSE = 5;

    private static boolean isRelease() {
        return !BuildConfig.DEBUG;
    }

    public static void d(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_DEBUG, args);
    }
    public static void e(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_ERROR, args);
    }

    public static void i(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_INFO, args);
    }

    public static void v(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_VERBOSE, args);
    }

    public static void w(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_WARNING, args);
    }

    public static void debug(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_DEBUG, args);
    }

    public static void debugForPreviousTrace(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_PREVIOUS_INDEX, LEVEL_DEBUG, args);
    }

    public static void error(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_ERROR, args);
    }

    public static void info(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_INFO, args);
    }

    public static void verbose(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_VERBOSE, args);
    }

    public static void warning(String... args) {
        if (isRelease()) return;
        printLog(STACK_TRACE_INDEX, LEVEL_WARNING, args);
    }

    private static void printLog(int callStackLevel, int level, String[] args) {
        String tag = "";
        String message = "";
        switch (args.length) {
            case 1:
                tag = LOG_TAG;
                message = args[0];
                break;
            case 2:
                tag = args[0];
                message = args[1];
                break;
        }

        StackTraceElement ste = Thread.currentThread().getStackTrace()[callStackLevel];
        String methodName = ste.getMethodName();
        String fileName = ste.getFileName();
        int lineNum = ste.getLineNumber();
        String strLog = String.format("(%s:%d) %s()  %s\n", fileName, lineNum, methodName, message);

        switch (level) {
            case LEVEL_DEBUG:
                Log.d(tag, strLog);
                break;
            case LEVEL_INFO:
                Log.i(tag, strLog);
                break;
            case LEVEL_WARNING:
                Log.w(tag, strLog);
                break;
            case LEVEL_ERROR:
                Log.e(tag, strLog);
                break;
            case LEVEL_VERBOSE:
                Log.v(tag, strLog);
                break;
        }
    }

    public static void logDump(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        int limit = 3000;
        int strLength = str.length();
        if (strLength < limit) {
            debugForPreviousTrace(str);
        } else {
            int count = strLength / limit;
            String sub = "";

            for (int i = 0; i <= count; i++) {
                if (i < count) {
                    sub = str.substring(i * limit, (i + 1) * limit);
                } else {
                    sub = str.substring(i * limit, strLength);
                }
                debugForPreviousTrace("[logDump][" + i + "] : " + sub);
            }
        }
    }

    // ======================================================
    // Write log to file
    // ======================================================
//    public static final int WRITE_TYPE_DEFAULT = 0;
//    public static final int WRITE_TYPE_ALARM = 1;
//
//    public static void writeLog(int writeType, String text) {
//        if (isRelease()) return;
//
//        String fileName = "log_default.log";
//
//        switch (writeType){
//            case WRITE_TYPE_DEFAULT:
//                fileName = "log_default.log";
//                break;
//            case WRITE_TYPE_ALARM:
//                fileName = "log_alarm.log";
//                break;
//        }
//
//        File logFile = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
//        if (!logFile.exists()) {
//            try {
//                logFile.createNewFile();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        try {
//            //BufferedWriter for performance, true to set append to file flag
//            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
//            buf.append(text);
//            buf.newLine();
//            buf.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}
