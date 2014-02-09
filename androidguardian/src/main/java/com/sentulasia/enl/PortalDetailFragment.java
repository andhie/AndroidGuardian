package com.sentulasia.enl;


import com.koushikdutta.ion.Ion;
import com.sentulasia.enl.model.GuardianPortal;
import com.sentulasia.enl.util.Events;
import com.sentulasia.enl.util.Util;
import com.sentulasia.enl.widget.PortalCard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PortalDetailFragment extends Fragment {

    public static PortalDetailFragment newInstance() {
	PortalDetailFragment fragment = new PortalDetailFragment();
	return fragment;
    }

    private ImageView mMapImage;

    private PortalCard mPortalCard;

    private TextView mLastUpdated;

    private TextView mPoints;

    private TextView mBonusPoints;

    private TextView mBonusDetail;

    private TextView mNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {

	View v = inflater.inflate(R.layout.fragment_portal_detail, container, false);

	mMapImage = (ImageView) v.findViewById(R.id.image);
	mPortalCard = (PortalCard) v.findViewById(R.id.portal_card);
	mLastUpdated = (TextView) v.findViewById(R.id.last_updated);
	mPoints = (TextView) v.findViewById(R.id.points);
	mBonusPoints = (TextView) v.findViewById(R.id.bonus_points);
	mBonusDetail = (TextView) v.findViewById(R.id.bonus_detail);
	mNotes = (TextView) v.findViewById(R.id.notes);

	EventBus.getDefault().registerSticky(this);
	setHasOptionsMenu(true);

	return v;
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
	    ShareActionProvider shareProvider = (ShareActionProvider) MenuItemCompat
		    .getActionProvider(shareItem);

	    //workaround due to ProGuard broke it even when keep
	    if (shareProvider == null) {
		shareProvider = new ShareActionProvider(getActionBar().getThemedContext());
		MenuItemCompat.setActionProvider(shareItem, shareProvider);
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

    private GuardianPortal mPortal;

    public void onEventMainThread(Events.onRequestPortalDetail event) {
	mPortal = event.getPortal();
	setData(mPortal);
    }

    public void setData(GuardianPortal portal) {

	getActivity().supportInvalidateOptionsMenu();

	loadMap(portal.getLat_coordinate(), portal.getLng_coordinate());
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

    private void loadMap(final double lat, final double lng) {
	mMapImage.getViewTreeObserver()
		.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {

			String mapUrl = Util.getGMapImageUrl(lat, lng,
				mMapImage.getWidth(), mMapImage.getHeight());

			Ion.with(mMapImage).error(R.drawable.ingress_icon).load(mapUrl);

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			    mMapImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			} else {
			    mMapImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}

		    }
		});
    }

    private ActionBar getActionBar() {
	return ((ActionBarActivity) getActivity()).getSupportActionBar();
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
			})
		.show();
    }

}
