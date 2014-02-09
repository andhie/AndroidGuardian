package com.sentulasia.enl.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.koushikdutta.ion.Ion;
import com.sentulasia.enl.model.GPPageHash;
import com.sentulasia.enl.util.Events;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;

/**
 * Created by andhie on 2/1/14.
 */
public class GetHashListService extends IntentService {

    public static void execute(Context context) {
	Log.i("service", "executing get hash list");
	context = context.getApplicationContext();
	Intent intent = new Intent(context, GetHashListService.class);
	context.startService(intent);
    }

    public GetHashListService() {
	super(GetHashListService.class.getName());
    }

    private static final String GP_HASH_LIST_ENDPOINT
	    = "http://enl.sentulasia.com/hash/%s.json?status=%s";

    private static final String LIVE = "Live";

    private static final String DESTROYED = "Destroyed";

    private SparseArray<String> mLiveHash = new SparseArray<String>();

    private SparseArray<String> mDeadHash = new SparseArray<String>();

    @Override
    protected void onHandleIntent(Intent intent) {

	int totalPage = getData(LIVE, mLiveHash);
	boolean refreshLiveData = compareHash(LIVE, mLiveHash, totalPage);
	if (refreshLiveData) {
	    GetGPListService.execute(getApplicationContext(), totalPage, true);
	    writeHashData(LIVE, mLiveHash);
	} else {
	    Log.i("Hash", "NO NEW LIVE PORTAL DATA");
	    EventBus.getDefault().post(new Events.OnNoNewPortalData(LIVE));
	}

	totalPage = getData(DESTROYED, mDeadHash);
	boolean refreshDeadData = compareHash(DESTROYED, mDeadHash, totalPage);
	if (refreshDeadData) {
	    GetGPListService.execute(getApplicationContext(), totalPage, false);
	    writeHashData(DESTROYED, mDeadHash);
	} else {
	    Log.i("Hash", "NO NEW DEAD PORTAL DATA");
	    EventBus.getDefault().post(new Events.OnNoNewPortalData(DESTROYED));
	}

    }

    private int getData(String liveOrDead, SparseArray<String> array) {
	int currentPage = 1;
	int totalPage = 1;
	do {
	    String endpoint = String.format(GP_HASH_LIST_ENDPOINT, currentPage, liveOrDead);

	    try {
		List<GPPageHash> temp = Ion.with(getApplicationContext(), endpoint)
			.as(new TypeToken<ArrayList<GPPageHash>>() {
			})
			.get();

		GPPageHash item = temp.get(0);
		totalPage = item.getTotal_pages();
		array.append(currentPage, item.getHash());

	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } catch (ExecutionException e) {
		e.printStackTrace();
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    currentPage++;

	} while (currentPage <= totalPage);

	return totalPage;
    }

    private boolean compareHash(String liveOrDead, SparseArray<String> array, int totalPage) {
	SharedPreferences sp = getHashSharedPreference();
	String json = sp.getString(liveOrDead, null);

	if (TextUtils.isEmpty(json)) {
	    return true;
	}

	Gson gson = new Gson();
	SparseArray<String> oriHash = gson.fromJson(json, new TypeToken<SparseArray<String>>() {
	}.getType());

	if (oriHash.size() != totalPage) {
	    return true;
	} else {
	    for (int i = 1; i <= totalPage; i++) {
		if (!oriHash.get(i, "").equals(array.get(i))) {
		    return true;
		}
	    }
	}

	return false;

    }

    public void writeHashData(String liveOrDead, SparseArray<String> array) {
	Gson gson = new Gson();
	String json = gson.toJson(array);
	getHashSharedPreference()
		.edit()
		.putString(liveOrDead, json)
		.apply();
    }


    private SharedPreferences getHashSharedPreference() {
	return getSharedPreferences("hash", Context.MODE_PRIVATE);
    }

}
