package com.sentulasia.enl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.util.TypedValue;

public class Util {

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                .create();
    }

    public static int toPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp,
                context.getResources().getDisplayMetrics());
    }

    public static String printPrettyDate(DateTime dt) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("d MMMM, yyyy");
        return dt.toString(fmt);
    }
}
