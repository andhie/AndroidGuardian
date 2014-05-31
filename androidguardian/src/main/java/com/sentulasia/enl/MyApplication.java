package com.sentulasia.enl;

import com.koushikdutta.ion.Ion;
import com.sentulasia.enl.util.Util;

import android.app.Application;

/**
 * Created by andhie on 2/1/14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Ion.getDefault(getApplicationContext()).configure().setGson(Util.getGson());
    }
}
