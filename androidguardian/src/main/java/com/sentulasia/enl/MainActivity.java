package com.sentulasia.enl;

import com.sentulasia.enl.service.GetHashListService;
import com.sentulasia.enl.util.PrefUtil;
import com.sentulasia.enl.util.Util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends FragmentActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase, R.attr.fontPath));
    }

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
