package com.sentulasia.enl.util;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;

public class PlayServicesUtils {

    public static boolean checkGooglePlaySevices(Activity activity) {
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        switch (errorCode) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                getErrorDialog(errorCode, activity, 0).show();
        }
        return false;
    }

    public static Dialog getErrorDialog(int errorCode, final Activity activity, int requestCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, requestCode);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                activity.finish();
            }
        });

        return dialog;
    }
}