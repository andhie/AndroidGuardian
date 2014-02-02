package com.sentulasia.enl.service;

import com.google.gson.reflect.TypeToken;

import com.koushikdutta.ion.Ion;
import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.util.Events;
import com.sentulasia.enl.util.FileUtil;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;

/**
 * Created by andhie on 2/1/14.
 */
public class GetGPListService extends IntentService {

    public static void execute(Context context) {
        Log.i("service", "executing get portal list");
        Intent intent = new Intent(context, GetGPListService.class);
        context.startService(intent);
    }

    public GetGPListService() {
        super(GetGPListService.class.getName());
    }

    private static final String GP_LIST_ENDPOINT = "http://enl.sentulasia.com/portals.json?page=";


    @Override
    protected void onHandleIntent(Intent intent) {

        boolean hasMorePages;
        int page = 1;
        do {

            String url = GP_LIST_ENDPOINT + page;
            hasMorePages = getData(url);
            page++;

        } while (hasMorePages);

        FileUtil.savePortalList(getApplicationContext(), liveList, FileUtil.LIVE_PORTAL_FILE);
        FileUtil.savePortalList(getApplicationContext(), deadList, FileUtil.DEAD_PORTAL_FILE);

        EventBus.getDefault().post(new Events.OnPullServerListEvent(liveList));

        liveList = null;
        deadList = null;

    }

    private List<GuardianPortal> liveList = new LinkedList<GuardianPortal>();

    private List<GuardianPortal> deadList = new LinkedList<GuardianPortal>();

    private boolean getData(String url) {
        try {
            List<GuardianPortal> list = Ion.with(getApplicationContext(), url)
                    .as(new TypeToken<ArrayList<GuardianPortal>>() {
                    })
                    .get();

            if (list.isEmpty()) {
                return false;
            }

            int count = list.size() - 1;
            for (int i = count; i >= 0; i--) {
                GuardianPortal portal = list.get(i);

                if (portal.isLive()) {
                    liveList.add(portal);
                } else {
                    deadList.add(portal);
                }
            }

            return true;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }
}
