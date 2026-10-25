package com.apps.miaowu.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Liang Jianxing
 */
public class MiaoWuUtil {
    public static String getDate(){
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = new Date();
        String tp = sd.format(date);
        return tp;
    }
}
