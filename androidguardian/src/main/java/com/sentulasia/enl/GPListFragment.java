package com.sentulasia.enl;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.model.ScorePair;
import com.sentulasia.enl.util.Events;
import com.sentulasia.enl.util.FileUtil;
import com.sentulasia.enl.util.LoadFromFileTask;
import com.sentulasia.enl.util.PlayServicesUtils;
import com.sentulasia.enl.util.PortalSorter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class GPListFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,
        ActionBar.OnNavigationListener {

    public static Fragment newInstance() {
        return new GPListFragment();
    }

    private ListView mListView;

    private TextView mEmptyView;

    private TextView mHeaderTitle;

    private TextView mHeaderSubtitle;

    private ContentLoadingProgressBar mProgressBar;

    private LoadFromFileTask loadTask;

    private LocationClient mLocationClient;

    private LocationRequest mLocationRequest;

    private Location mCurrentLocation;

    private SearchView mSearchView;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 1069;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        initActionBar();

        View v = inflater.inflate(R.layout.fragment_portal_list, container, false);

        mListView = (ListView) v.findViewById(R.id.list);
        mEmptyView = (TextView) v.findViewById(R.id.empty);

        mProgressBar = (ContentLoadingProgressBar) v.findViewById(R.id.progress_bar);
        mProgressBar.show();

        View header = inflater.inflate(R.layout.list_item_header, mListView, false);
        mHeaderTitle = (TextView) header.findViewById(R.id.title);
        mHeaderSubtitle = (TextView) header.findViewById(R.id.subtitle);

        mListView.addHeaderView(header, null, false);
        mListView.setOnItemClickListener(onItemClick);

        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);

        mLocationClient = new LocationClient(getActivity(), this, this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setNumUpdates(1);

        return v;
    }

    private void initActionBar() {

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(
                actionBar.getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                getResources().getStringArray(R.array.ab_nav_array)
        ), this);

    }

    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        if (mLocationClient.isConnected()) {
            mLocationClient.removeLocationUpdates(this);
        }
        mLocationClient.disconnect();
        super.onStop();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    mLocationClient.connect();
                }

                break;
        }

    }

    private MenuItem mSortDistanceMenu;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.portal_list, menu);

        mSortDistanceMenu = menu.findItem(R.id.action_sort_distance);
        if (mCurrentLocation == null) {
            mSortDistanceMenu.setVisible(false);
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        mSearchView.setQueryHint(getString(R.string.search_hint));

        super.onCreateOptionsMenu(menu, inflater);
    }

    private PortalSorter.Sorter sortCriteria;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_sort_name:
                sortList(PortalSorter.SortType.NAME);
                return true;

            case R.id.action_sort_age:
                sortList(PortalSorter.SortType.AGE);
                return true;

            case R.id.action_sort_owner:
                sortList(PortalSorter.SortType.OWNER);
                return true;

            case R.id.action_sort_distance:
                sortList(PortalSorter.SortType.DISTANCE);
                return true;

            case R.id.action_leaderboard:
                showLeaderBoard();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(Events.OnNewLivePortalListEvent event) {
        int idx = getActionBar().getSelectedNavigationIndex();

        if (idx == 0) {
            if (adapter == null || adapter.isEmpty()) {
                populateAdapter(event.getList());
            } else {
                notifyNewData(event.getList());
            }
        }

    }

    public void onEventMainThread(Events.OnNewDeadPortalListEvent event) {
        int idx = getActionBar().getSelectedNavigationIndex();

        if (idx == 1) {
            if (adapter == null || adapter.isEmpty()) {
                populateAdapter(event.getList());
            } else {
                notifyNewData(event.getList());
            }
        }

    }

    public void onEventMainThread(Events.OnNoNewPortalData event) {
        Crouton.makeText(getActivity(),
                "No new " + event.getPortalDataType() + " Portal updates",
                Style.INFO)
                .show();
    }

    private String currentAddress;

    public void onEventMainThread(Events.OnAddressResolved event) {
        if (sortCriteria != null && PortalSorter.SortType.DISTANCE == sortCriteria.getSortType()) {
            mHeaderSubtitle.setVisibility(View.VISIBLE);
        } else {
            mHeaderSubtitle.setVisibility(View.GONE);
        }
        mHeaderSubtitle.setText(event.getAddress());
        currentAddress = event.getAddress();
    }

    public void onEventMainThread(Events.OnLoadFromFileEvent event) {
        populateAdapter(event.getList());
    }

    public void notifyNewData(final List<GuardianPortal> list) {
        Configuration config = new Configuration.Builder()
                .setDuration(10000)
                .build();

        Style style = new Style.Builder()
                .setBackgroundColorValue(Style.holoBlueLight)
                .setConfiguration(config).build();

        final Crouton crouton = Crouton
                .makeText(getActivity(), getString(R.string.new_data), style);

        crouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateAdapter(list);
                crouton.hide();
            }
        });
        crouton.show();
    }

    private GPListAdapter adapter;

    private void populateAdapter(List<GuardianPortal> list) {

        if (list.isEmpty() && adapter == null) {
            mProgressBar.show();
            return;
        }

        adapter = new GPListAdapter(getActivity(), list);
        mListView.setAdapter(adapter);
        mEmptyView.setText("No Result");

        if (sortCriteria == null) {
            sortCriteria = new PortalSorter.Age();
        }

        sortList(sortCriteria.getSortType());

        mProgressBar.hide();

    }

    private void sortList(PortalSorter.SortType sortType) {

        int textResId = R.string.header_age;

        switch (sortType) {

            case NAME:
                textResId = R.string.header_name;
                sortCriteria = new PortalSorter.Name();
                break;

            case AGE:
                textResId = R.string.header_age;
                sortCriteria = new PortalSorter.Age();
                break;

            case OWNER:
                textResId = R.string.header_owner;
                sortCriteria = new PortalSorter.Owner();
                break;

            case DISTANCE:
                textResId = R.string.header_nearby;
                sortCriteria = new PortalSorter.Distance(mCurrentLocation);

                break;
        }

        if (sortType == PortalSorter.SortType.DISTANCE && !TextUtils.isEmpty(currentAddress)) {
            mHeaderSubtitle.setText(currentAddress);
            mHeaderSubtitle.setVisibility(View.VISIBLE);
        } else {
            mHeaderSubtitle.setVisibility(View.GONE);
        }

        mHeaderTitle.setText(textResId);
        adapter.sort(sortCriteria);

    }

    private void showLeaderBoard() {
        List<ScorePair> list = FileUtil.getLeaderboardList(getActivity());
        CharSequence[] seqs = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ScorePair sp = list.get(i);
            seqs[i] = sp.getScore() + " - [" + sp.getName() + "]";
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_leaderboard)
                .setNeutralButton(android.R.string.ok, null)
                .setItems(seqs, null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {

        if (position == 0) {
            loadTask = new LoadFromFileTask(getActivity(), FileUtil.LIVE_PORTAL_FILE);
        } else {
            loadTask = new LoadFromFileTask(getActivity(), FileUtil.DEAD_PORTAL_FILE);
        }

        mProgressBar.show();
        loadTask.execute();

        return true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mCurrentLocation = mLocationClient.getLastLocation();
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mSortDistanceMenu.setVisible(true);
            mCurrentLocation = location;
            getAddress();
        }
    }

    @Override
    public void onDisconnected() {
        //do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            PlayServicesUtils.getErrorDialog(result.getErrorCode(), getActivity(), 0);
        }
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    private void getAddress() {
        double lat = mCurrentLocation.getLatitude();
        double lng = mCurrentLocation.getLongitude();

        String url = String.format(Locale.ENGLISH,
                "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                        + Locale.US, lat, lng
        );

        Ion.with(getActivity())
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject jsonObject) {
                        if (e != null) {
                            return;
                        }

                        if ("OK".equalsIgnoreCase(jsonObject.get("status").getAsString())) {
                            JsonArray resultArray = jsonObject.getAsJsonArray("results");
                            if (resultArray.size() > 0) {
                                JsonObject result = resultArray.get(0).getAsJsonObject();
                                String address = result.get("formatted_address").getAsString();

                                EventBus.getDefault()
                                        .post(new Events.OnAddressResolved(address));
                            }
                        }

                    }
                });
    }

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            final GuardianPortal portal = (GuardianPortal) adapterView.getItemAtPosition(position);

            EventBus.getDefault().postSticky(new Events.onRequestPortalDetail(portal));

            if (getResources().getBoolean(R.bool.isMultiPane)) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_frame, PortalDetailFragment.newInstance())
                        .commit();
            } else {
                PortalDetailActivity.show(getActivity());
            }

        }
    };

    private SearchView.OnQueryTextListener mOnQueryTextListener
            = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (adapter != null) {
                adapter.getFilter().filter(newText);
            }
            return true;
        }
    };

}
