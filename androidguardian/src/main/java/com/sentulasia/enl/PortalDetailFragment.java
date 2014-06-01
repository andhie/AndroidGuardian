package com.sentulasia.enl;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.util.Events;
import com.sentulasia.enl.util.PlayServicesUtils;
import com.sentulasia.enl.util.Util;
import com.sentulasia.enl.widget.NonInterceptingScrollView;
import com.sentulasia.enl.widget.PortalCard;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PortalDetailFragment extends Fragment {

    public static PortalDetailFragment newInstance() {
        PortalDetailFragment fragment = new PortalDetailFragment();
        return fragment;
    }

    private NonInterceptingScrollView mScrollView;

    private PortalCard mPortalCard;

    private TextView mLastUpdated;

    private TextView mPoints;

    private TextView mBonusPoints;

    private TextView mBonusDetail;

    private TextView mNotes;

    private GoogleMap mMap;

    private GuardianPortal mPortal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portal_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScrollView = (NonInterceptingScrollView) view.findViewById(R.id.scrollView);
        mPortalCard = (PortalCard) view.findViewById(R.id.portal_card);
        mLastUpdated = (TextView) view.findViewById(R.id.last_updated);
        mPoints = (TextView) view.findViewById(R.id.points);
        mBonusPoints = (TextView) view.findViewById(R.id.bonus_points);
        mBonusDetail = (TextView) view.findViewById(R.id.bonus_detail);
        mNotes = (TextView) view.findViewById(R.id.notes);

        EventBus.getDefault().registerSticky(this);
        setHasOptionsMenu(true);

        if (PlayServicesUtils.checkGooglePlayServices(getActivity())) {
            setUpMapIfNeeded();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PlayServicesUtils.checkGooglePlayServices(getActivity())) {
            setUpMapIfNeeded();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.portal_detail, menu);
        if (mPortal != null) {
            MenuItem shareItem = menu.findItem(R.id.action_share);
            ShareActionProvider shareProvider = (ShareActionProvider) shareItem.getActionProvider();

            //workaround due to ProGuard broke it even when keep
            if (shareProvider == null) {
                shareProvider = new ShareActionProvider(getActionBar().getThemedContext());
                shareItem.setActionProvider(shareProvider);
            }

            shareProvider.setShareIntent(getShareIntent());

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_navigate:
                displayMapChooser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(Events.onRequestPortalDetail event) {
        mPortal = event.getPortal();
        setData(mPortal);
    }

    public void setData(GuardianPortal portal) {

        getActivity().supportInvalidateOptionsMenu();

        loadMap(portal);
        mPortalCard.setData(portal);
        mLastUpdated.setText("Last updated on " + Util.printPrettyDate(portal.getUpdated_at()));
        mPoints.setText("Points for tears collected: " + portal.getAge_points());

        if (TextUtils.isEmpty(portal.getBonus_points())) {
            mBonusPoints.setText("Bonus Points: N/A");
        } else {
            mBonusPoints.setText("Bonus Points: " + portal.getBonus_points());
        }

        if (TextUtils.isEmpty(portal.getBonus_details())) {
            mBonusDetail.setText("Bonus Details: N/A");
        } else {
            mBonusDetail.setText("Bonus Details: " + portal.getBonus_details());
        }

        if (TextUtils.isEmpty(portal.getNote())) {
            mNotes.setText("Notes: N/A");
        } else {
            mNotes.setText("Notes: " + portal.getNote());
        }

        getActionBar().setTitle(portal.getPortal_name());

    }

    private Intent getShareIntent() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        StringBuilder sb = new StringBuilder();
        sb.append("Intel Map: ").append(mPortal.getLink())
                .append("\n\nGMap: http://maps.google.com/maps?q=")
                .append(mPortal.getLat_coordinate()).append(",")
                .append(mPortal.getLng_coordinate());

        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        return intent;

    }

    private void loadMap(GuardianPortal portal) {

        if (mMap != null) {
            mMap.clear();

            LatLng coords = new LatLng(portal.getLat_coordinate(), portal.getLng_coordinate());
            MarkerOptions options = new MarkerOptions()
                    .position(coords)
                    .title(portal.getPortal_name())
                    .snippet(portal.getLocation());

            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 14));

        }
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    private void displayMapChooser() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Select Options")
                .setSingleChoiceItems(R.array.options_link, -1,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String uri = null;
                                switch (which) {
                                    case 1:
                                        uri = mPortal.getLink();
                                        break;

                                    case 0:
                                        intent.setClassName("com.google.android.apps.maps",
                                                "com.google.android.maps.MapsActivity");
                                    case 2:
                                        uri = "geo:" + mPortal.getLat_coordinate() + ","
                                                + mPortal.getLng_coordinate() + "?q="
                                                + mPortal.getLat_coordinate() + ","
                                                + mPortal.getLng_coordinate();
                                        break;
                                }

                                intent.setData(Uri.parse(uri));
                                if (intent.resolveActivity(
                                        getActivity().getPackageManager()) != null) {
                                    startActivity(intent);

                                } else {
                                    Crouton.makeText(getActivity(),
                                            "No app available to handle",
                                            Style.INFO).show();
                                }
                                dialog.dismiss();
                            }
                        }
                )
                .show();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map))
                    .getMap();

            if (mMap != null) {
                mMap.setOnInfoWindowClickListener(mOnInfoWindowClickListener);
                mMap.setOnMapLoadedCallback(onMapLoaded);

                // if it is portrait, the map is within the scrollview, so we enlist
                //the frame container so that scrollview wont intercept our touch
                if (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {
                    mScrollView.addInterceptScrollView(getActivity().findViewById(R.id.frame_map));

                }

            }

        }
    }

    private GoogleMap.OnInfoWindowClickListener mOnInfoWindowClickListener
            = new GoogleMap.OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker marker) {
            displayMapChooser();
        }
    };

    private GoogleMap.OnMapLoadedCallback onMapLoaded = new GoogleMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {
            if (mPortal != null) {
                loadMap(mPortal);
            }
        }
    };

}
