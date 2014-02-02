package com.virtualmanila.guardianportallister.sgmy;

import com.virtualmanila.guardianportallister.sgmy.model.GuardianPortal;
import com.virtualmanila.guardianportallister.sgmy.util.Events;
import com.virtualmanila.guardianportallister.sgmy.util.FileUtil;
import com.virtualmanila.guardianportallister.sgmy.util.PortalSorter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class GPListFragment extends Fragment {


    public static Fragment newInstance() {
        return new GPListFragment();
    }

    private ListView mListView;

    private TextView mEmptyView;

    private TextView mHeaderTitle;

    private TextView mHeaderSubtitle;


    private ContentLoadingProgressBar mProgressBar;

    private LoadFromFileTask loadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_portal_list, container, false);

        mListView = (ListView) v.findViewById(R.id.list);
        mEmptyView = (TextView) v.findViewById(R.id.empty);

        mProgressBar = (ContentLoadingProgressBar) v.findViewById(R.id.progress_bar);
        mProgressBar.show();

        View header = inflater.inflate(R.layout.list_item_header, mListView, false);
        mHeaderTitle = (TextView) header.findViewById(R.id.title);
        mHeaderSubtitle = (TextView) header.findViewById(R.id.subtitle);

        mListView.addHeaderView(header, null, false);

        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);

        loadTask = new LoadFromFileTask(getActivity());
        loadTask.execute();

        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        Crouton.clearCroutonsForActivity(getActivity());
        if (loadTask != null) {
            loadTask.cancel(true);
        }
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.portal_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_sort_name:
                mHeaderTitle.setText(getString(R.string.header_name));
                Collections.sort(adapter.getAll(), new PortalSorter.Name());
                adapter.notifyDataSetChanged();
                return true;

            case R.id.action_sort_age:
                mHeaderTitle.setText(getString(R.string.header_age));
                Collections.sort(adapter.getAll(), new PortalSorter.Age());
                adapter.notifyDataSetChanged();
                return true;

            case R.id.action_sort_owner:
                mHeaderTitle.setText(getString(R.string.header_owner));
                Collections.sort(adapter.getAll(), new PortalSorter.Owner());
                adapter.notifyDataSetChanged();
                return true;

//            case R.id.action_sort_closest:
//                sorter = new SortByDistance();
//                Collections.sort(adapter.getAll(), sorter);
//                adapter.notifyDataSetChanged();
//                return true;

//            case R.id.action_leaderboard:
//                showLeaderBoard();
//                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(Events.OnPullServerListEvent event) {
        final List<GuardianPortal> list = event.getLiveList();

        final Crouton crouton = Crouton
                .makeText(getActivity(), getString(R.string.new_data), Style.INFO);
        crouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateAdapter(list);
                crouton.hide();
            }
        });
        crouton.show();

    }

    public void onEventMainThread(Events.onLoadFromFileEvent event) {
        populateAdapter(event.getList());
    }

    private GPListAdapter adapter;

    private void populateAdapter(List<GuardianPortal> list) {

        mProgressBar.hide();

        adapter = new GPListAdapter(getActivity(), list);
        mListView.setAdapter(adapter);
        mEmptyView.setText("No Result");
        mHeaderTitle.setText(getString(R.string.header_age));

    }

    private class LoadFromFileTask extends AsyncTask<Void, Void, List<GuardianPortal>> {

        private WeakReference<Activity> ref;

        public LoadFromFileTask(Activity activity) {
            ref = new WeakReference<Activity>(activity);
        }

        @Override
        protected List<GuardianPortal> doInBackground(Void... voids) {
            return FileUtil.getPortalList(ref.get(), FileUtil.LIVE_PORTAL_FILE);
        }

        @Override
        protected void onPostExecute(List<GuardianPortal> list) {
            EventBus.getDefault().post(new Events.onLoadFromFileEvent(list));
        }
    }

}
