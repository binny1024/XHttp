package com.bean.logger;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * ================================================
 * 描    述：日志的工具类
 * ================================================
 */
public class JJLogger {

    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

    private static final char D = 'D', E = 'E';

    private static String LINE_SEPARATOR = System.getProperty("line.separator"); //等价于"\n\r"
    private static int JSON_INDENT = 4;

    private static boolean isDebug = false;
    /**
     * 是否打印堆栈信息
     */
    private static boolean stackTrace = true;

    /**
     * isLogEnable 是否开启调试日志
     * true 开启；
     * false 关闭；
     */
    public static void debug(boolean isEnable) {
        isDebug = isEnable;
    }

    /**
     * 打印MAp
     */
    public static void map(String tag, Map map) {
        if (isDebug) {
            Set set = map.entrySet();
            if (set.size() < 1) {
                printLog(D, tag, "[]");
                return;
            }

            int i = 0;
            String[] s = new String[set.size()];
            for (Object aSet : set) {
                Map.Entry entry = (Map.Entry) aSet;
                s[i] = "key = " + entry.getKey() + " , " + "value = " + entry.getValue() + ",\n";
                i++;
            }
            printLog(D, tag, s);
        }
    }

    /**
     * 打印JSON
     *
     * @param tag     标志
     * @param jsonStr jsonString
     */
    public static void json(String tag, String jsonStr) {
        if (isDebug) {
            String message;
            try {
                if (jsonStr.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    message = jsonObject.toString(JSON_INDENT); //这个是核心方法
                } else if (jsonStr.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    message = jsonArray.toString(JSON_INDENT);
                } else {
                    message = jsonStr;
                }
            } catch (JSONException e) {
                message = jsonStr;
            }

            message = LINE_SEPARATOR + message;
            String[] lines = message.split(LINE_SEPARATOR);
            printLog(D, tag, lines);
        }
    }

    /**
     * 用于打印信息
     *
     * @param tag 级别
     * @param msg 信息
     */
    public static void logInfo(String tag, String msg) {
        if (isDebug) {
            printLog(D, tag, msg);
        }
    }

    /**
     * 用于打印错误信息
     *
     * @param errorCode 错误码
     * @param msg       错误码的伴随信息：描述信息错误码
     */
    public static void logError(String errorCode, String msg) {
        if (isDebug) {
            if (TextUtils.isEmpty(errorCode)) {
                printLog(E, "inner_error", "错误信息 : " + msg);
            } else {
                if (stackTrace) {
                    printLog(E, "inner_error", "错误码 ：" + errorCode + " 信息描述 ：" + msg);
                } else {
                    printLog(E, "inner_error", "错误码 ：" + errorCode);
                }
            }
        }
    }


    /**
     * @param type 打印类型
     * @param tag  筛选的tag
     * @param msg  要打印的信息
     */
    private static void printer(char type, String tag, String msg) {
        switch (type) {
            case D:
                Log.d(tag, msg);
                break;
            case E:
                Log.e(tag, msg);
                break;
        }
    }

    /**
     * 打印Log被调用的位置
     *
     * @param type 打印类型
     * @param tag  发音筛选的tag
     * @param msg  要打印的信息
     */
    private static void printLocation(char type, String tag, String... msg) {
        for (String str : msg) {
            printer(type, tag, HORIZONTAL_DOUBLE_LINE + "   " + str);
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        int i = 0;
        for (StackTraceElement e : stack) {
            String name = e.getClassName();
            if (!name.equals(JJLogger.class.getName())) {
                i++;
            } else {
                break;
            }
        }
        i += 3;
        String location = stack[i].toString();
        StringBuilder sb = new StringBuilder();
        sb.append(HORIZONTAL_DOUBLE_LINE).append("   线程名 :  ").append(Thread.currentThread().getName()).append("  调用位置:").append(location);
        printer(type, tag, sb.toString());
    }

    /**
     * 打印消息
     *
     * @param type 打印类型
     * @param tag  发音筛选的tag
     * @param msg  要打印的信息
     */
    private static void printMsg(char type, String tag, String... msg) {
        printer(type, tag, HORIZONTAL_DOUBLE_LINE + "   信息:");
        for (String str : msg) {
            printer(type, tag, HORIZONTAL_DOUBLE_LINE + "   " + str);
        }
    }

    /**
     * 打印log
     *
     * @param type 日志级别
     * @param tag  标志
     * @param msg  描述信息
     */
    private static void printLog(char type, String tag, String... msg) {
        if (msg == null || msg.length == 0) {
            return;
        }
        printer(type, tag, TOP_BORDER);
        if (stackTrace) {
            printLocation(type, tag, msg);
        } else {
            printMsg(type, tag, msg);
        }
        printer(type, tag, BOTTOM_BORDER);
    }
}
