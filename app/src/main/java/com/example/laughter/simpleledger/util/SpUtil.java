package com.example.laughter.simpleledger.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 作者： 江浩
 * 创建时间： 2019/1/17
 * 版权： 江苏远大信息股份有限公司
 * 描述： com.example.laughter.simpleledger.util
 */
public class SpUtil {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private static void initSp(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static void initEditor(Context context) {
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    }

    public static void putString(Context context, String key, String value) {
        initEditor(context);
        editor.putString(key, value);
        editor.apply();
    }

    public static void putInt(Context context, String key, int value) {
        initEditor(context);
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        initEditor(context);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defaultVal) {
        initSp(context);
        return sp.getString(key, defaultVal);
    }

    public static int getInt(Context context, String key, int defaultVal) {
        initSp(context);
        return sp.getInt(key, defaultVal);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultVal) {
        initSp(context);
        return sp.getBoolean(key, defaultVal);
    }
}
