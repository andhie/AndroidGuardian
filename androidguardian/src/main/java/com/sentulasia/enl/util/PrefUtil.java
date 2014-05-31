package com.sentulasia.enl.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by andhie on 2/9/14.
 */
public class PrefUtil {

    private static final String LAST_UPDATE = "last_download";

    public static long getLastUpdateTime(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getLong(LAST_UPDATE, -1L);
    }

    public static void setLastUpdateTime(Context context, long timeInMillis) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putLong(LAST_UPDATE, timeInMillis)
                .apply();
    }
}
