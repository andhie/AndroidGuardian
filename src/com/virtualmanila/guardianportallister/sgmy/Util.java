package com.virtualmanila.guardianportallister.sgmy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Util {

	public interface Properties {
		public static final String FILTER_OUT_DONE = "FILTER_OUT_DONE";
		public static final String TIME_LAST_DOWNLOAD = "TIME_LAST_DOWNLOAD";
	}

	public static final String FILENAME = "gp.csv";
	public static final String JSON_FILE = "gp";
	public static final String JSON_EXT = ".json";
	private static final String GP_LIST = "GP";

	public static final long REMINDER_TO_DOWNLOAD = 24 * 60 * 60 * 1000;

	public static final String DOWNLOAD_LINK = "https://docs.google.com/spreadsheet/fm?id=t5nAKyy_AupqqtKnNuUqoTw.09254443293239618166.2654919143921438143&fmcmd=5&gid=0";
	public static final String JEFF_LINK = "http://gphunt.jeffclarenz.com/";

	public static void expand(final View v) {
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targtetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
						: (int) (targtetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targtetHeight / v.getContext().getResources()
				.getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / v.getContext().getResources()
				.getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static File copyToFileToUserSpace(Context context,
			InputStream inputStream) {
		File desti = getFile(context);
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(inputStream);
			out = new BufferedOutputStream(new FileOutputStream(desti));
			byte[] buffer = new byte[1024];
			int readSize = in.read(buffer);
			while (readSize > 0) {
				out.write(buffer, 0, readSize);
				readSize = in.read(buffer);
			}
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return desti;
	}

	public static File getDestinationDirectory(Context context) {
		return context.getDir(GP_LIST, Context.MODE_PRIVATE);
	}

	public static File getFile(Context context) {
		return new File(getDestinationDirectory(context), FILENAME);
	}

	public static File getJsonFile(Context context, int page) {
		return new File(getDestinationDirectory(context), JSON_FILE + page + JSON_EXT);
	}

	public static boolean isFilterOut(SharedPreferences sp) {
		return sp.getBoolean(Util.Properties.FILTER_OUT_DONE, false);
	}

	public static void setFilterOut(SharedPreferences sp, boolean set) {
		Editor edit = sp.edit();
		edit.putBoolean(Util.Properties.FILTER_OUT_DONE, set);
		edit.commit();
	}

	public static long getLastTimeDownload(SharedPreferences sp) {
		return sp.getLong(Util.Properties.TIME_LAST_DOWNLOAD, -1L);
	}

	public static long getLastTimeDownload(Context context) {
		return getLastTimeDownload(PreferenceManager
				.getDefaultSharedPreferences(context));
	}

	public static void setLastTimeDownload(Context context, long set) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = sp.edit();
		edit.putLong(Util.Properties.TIME_LAST_DOWNLOAD, set);
		edit.commit();
	}

	public static void setLastTimeDownload(SharedPreferences sp, long set) {
		Editor edit = sp.edit();
		edit.putLong(Util.Properties.TIME_LAST_DOWNLOAD, set);
		edit.commit();
	}

	public static boolean remindToDownload(SharedPreferences sp) {
		return lastDownloadTimeDiff(sp) > REMINDER_TO_DOWNLOAD;
	}

	public static long lastDownloadTimeDiff(SharedPreferences sp) {
		return (System.currentTimeMillis() - getLastTimeDownload(sp));
	}
}
