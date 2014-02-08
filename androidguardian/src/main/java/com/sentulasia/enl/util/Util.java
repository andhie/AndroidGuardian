package com.sentulasia.enl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.sentulasia.enl.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.widget.TextView;

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

    public static void setStrikeThru(TextView textView) {
	textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static String getGMapImageUrl(double lat, double lng, int width, int height) {
	return String
		.format("http://maps.google.com/maps/api/staticmap?markers=%s,%s&size=%sx%s&sensor=false",
			lat, lng, width, height);
    }

    public static int getBadge(int age) {
	if (age >= 150) {
	    return R.drawable.ic_guardian5;
	} else if (age >= 90) {
	    return R.drawable.ic_guardian4;
	} else if (age >= 20) {
	    return R.drawable.ic_guardian3;
	} else if (age >= 10) {
	    return R.drawable.ic_guardian2;
	} else if (age >= 3) {
	    return R.drawable.ic_guardian1;
	} else {
	    return 0;
	}
    }
}
