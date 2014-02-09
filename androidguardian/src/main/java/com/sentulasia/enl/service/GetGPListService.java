package com.sentulasia.enl.service;

import com.google.gson.reflect.TypeToken;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.model.ScorePair;
import com.sentulasia.enl.util.Events;
import com.sentulasia.enl.util.FileUtil;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;

/**
 * Created by andhie on 2/1/14.
 */
public class GetGPListService extends IntentService {

    private static final String GET_LIVE_PORTALS = "live";

    private static final String NUM_OF_PAGE = "page";

    public static void execute(Context context, int pages, boolean live) {
	Log.i("service", "executing get portal list");
	context = context.getApplicationContext();
	Intent intent = new Intent(context, GetGPListService.class);
	intent.putExtra(GET_LIVE_PORTALS, live);
	intent.putExtra(NUM_OF_PAGE, pages);
	context.startService(intent);
    }

    public GetGPListService() {
	super(GetGPListService.class.getName());
    }

    private static final String GP_LIST_ENDPOINT
	    = "http://enl.sentulasia.com/portals.json?page=%s&status=%s";

    private boolean live;

    private int pages;

    @Override
    protected void onHandleIntent(Intent intent) {

	live = intent.getBooleanExtra(GET_LIVE_PORTALS, false);
	pages = intent.getIntExtra(NUM_OF_PAGE, 0);

	for (int i = 1; i <= pages; i++) {
	    String url;
	    if (live) {
		url = String.format(GP_LIST_ENDPOINT, i, "Live");
	    } else {
		url = String.format(GP_LIST_ENDPOINT, i, "Destroyed");
	    }

	    getData(url);
	}

    }

    private List<GuardianPortal> list = new LinkedList<GuardianPortal>();

    private Object group = new Object();

    private void getData(String url) {
	Ion.with(getApplicationContext(), url)
		.group(group)
		.as(new TypeToken<ArrayList<GuardianPortal>>() {
		})
		.setCallback(new FutureCallback<ArrayList<GuardianPortal>>() {
		    @Override
		    public void onCompleted(Exception e, ArrayList<GuardianPortal> result) {

			if (e != null) {
			    return;
			}

			int count = result.size() - 1;
			for (int i = count; i >= 0; i--) {
			    GuardianPortal portal = result.get(i);
			    portal.setIsLive(live);
			    if (!live) {
				addScore(portal.getDestroyed_by(), portal.getTotal_points());
			    }
			}

			list.addAll(result);
			onComplete();
		    }
		});
    }

    private void onComplete() {

	int pending = Ion.getDefault(getApplicationContext()).getPendingRequestCount(group);

	if (pending > 0) {
	    return;
	}

	if (live) {
	    FileUtil.savePortalList(getApplicationContext(), list, FileUtil.LIVE_PORTAL_FILE);
	    EventBus.getDefault().post(new Events.OnNewLivePortalListEvent(list));

	} else {
	    FileUtil.savePortalList(getApplicationContext(), list, FileUtil.DEAD_PORTAL_FILE);
	    EventBus.getDefault().post(new Events.OnNewDeadPortalListEvent(list));
	    List<ScorePair> list = prepScore();
	    FileUtil.saveLeaderboardList(getApplicationContext(), list);
	}

	list = null;
    }

    private Map<String, Integer> scoreList = new TreeMap<String, Integer>();

    private void addScore(String namex, int pts) {
	String[] names = namex.split(",");
	for (int i = 0; i < names.length; i++) {
	    String name = names[i].toUpperCase().trim();
	    Integer currScore = scoreList.get(name);
	    if (currScore == null) {
		currScore = 0;
	    }
	    currScore = currScore + pts;
	    scoreList.put(name, currScore);
	}
    }

    private List<ScorePair> prepScore() {
	List<ScorePair> scorePairs = new ArrayList<ScorePair>();
	for (String name : scoreList.keySet()) {
	    int pts = scoreList.get(name);
	    scorePairs.add(new ScorePair(name, pts));
	}
	Collections.sort(scorePairs);

	return scorePairs;
    }

}
