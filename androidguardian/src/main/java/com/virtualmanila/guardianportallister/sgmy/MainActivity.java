package com.virtualmanila.guardianportallister.sgmy;

import com.virtualmanila.guardianportallister.sgmy.service.GetGPListService;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetGPListService.execute(this);

        if(savedInstanceState == null) {
            Fragment fragment = GPListFragment.newInstance();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

}
