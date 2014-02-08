package com.sentulasia.enl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public class PortalDetailActivity extends ActionBarActivity {

    public static void show(Context context) {
	Intent intent = new Intent(context, PortalDetailActivity.class);
	context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	if (savedInstanceState == null) {
	    Fragment fragment = PortalDetailFragment.newInstance();

	    getSupportFragmentManager()
		    .beginTransaction()
		    .add(R.id.content_frame, fragment)
		    .commit();
	}
    }

}
