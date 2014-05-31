package com.sentulasia.enl.util;

import com.sentulasia.enl.model.GuardianPortal;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import de.greenrobot.event.EventBus;

public class LoadFromFileTask extends AsyncTask<Void, Void, List<GuardianPortal>> {

    private WeakReference<Activity> ref;

    private String filename;

    public LoadFromFileTask(Activity activity, String filename) {
        ref = new WeakReference<Activity>(activity);
        this.filename = filename;
    }

    @Override
    protected List<GuardianPortal> doInBackground(Void... voids) {
        return FileUtil.getPortalList(ref.get(), filename);
    }

    @Override
    protected void onPostExecute(List<GuardianPortal> list) {
        EventBus.getDefault().post(new Events.OnLoadFromFileEvent(list));
    }
}