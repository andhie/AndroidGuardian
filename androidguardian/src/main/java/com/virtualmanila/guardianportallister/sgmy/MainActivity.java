package com.virtualmanila.guardianportallister.sgmy;

import java.util.List;

import org.json.JSONException;

import com.virtualmanila.guardianportallister.sgmy.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	List<GuardianPortal> gp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gp = GPLoader.getInstance(this).getPortals();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
