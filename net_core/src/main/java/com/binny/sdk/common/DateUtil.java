package com.binny.sdk.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * author xander on  2017/9/11.
 * function
 */

public class DateUtil {
    public static String ms2Date(long _ms){
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }


}
