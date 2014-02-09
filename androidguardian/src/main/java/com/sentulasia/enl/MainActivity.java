package com.sentulasia.enl;

import com.sentulasia.enl.service.GetHashListService;
import com.sentulasia.enl.util.PrefUtil;
import com.sentulasia.enl.util.Util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	if (savedInstanceState == null) {

	    pullDataIfRequired();

	    Fragment fragment = GPListFragment.newInstance();

	    getSupportFragmentManager()
		    .beginTransaction()
		    .add(R.id.content_frame, fragment)
		    .commit();
	}
    }

    /**
     * Currently it is set to pull data when data is over 24 hours old
     * at the moment the app runs
     */
    private void pullDataIfRequired() {
	boolean requireUpdate = Util.shouldUpdateData(this);
	if (requireUpdate) {
	    GetHashListService.execute(this);
	    PrefUtil.setLastUpdateTime(this, System.currentTimeMillis());
	}

    }

}
