package com.virtualmanila.guardianportallister.sgmy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.virtualmanila.guardianportallister.sgmy.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class PortalListActivity extends ListActivity {

	GPListAdapter adapter;
	private double latitude = 0d;
	private double longitude = 0d;
	TextView currLoc;
	TextView refreshTime;
	MenuItem sortByDistanceMenu;
	MenuItem filterOutMenu;

	LocationManager mLocationManager;
	DownloadManager downloadManager;
	private int LOCATION_REFRESH_TIME = 60000;
	private int LOCATION_REFRESH_DISTANCE = 500;

	Sorter sorter = null;

	SharedPreferences sp;

	private final LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			if (currLoc != null)
				currLoc.setText(getString(R.string.label_location_found,
						GuardianPortal.df.format(latitude),
						GuardianPortal.df.format(longitude)));
			mLocationManager.removeUpdates(mLocationListener);
			sortByDistanceMenu.setEnabled(true);
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portal_list);
		// gp = new ArrayList<GuardianPortal>();
		adapter = new GPListAdapter(this, new ArrayList<GuardianPortal>());
		setListAdapter(adapter);
		new DownloadAsyncTask().execute(false);
		currLoc = (TextView) findViewById(R.id.currloc);
		refreshTime = (TextView) findViewById(R.id.refreshtime);

		sp = PreferenceManager.getDefaultSharedPreferences(this);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		requestLocation();
		// showWarning();
	}

	@Override
	protected void onStop() {
		mLocationManager.removeUpdates(mLocationListener);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.portal_list, menu);
		sortByDistanceMenu = menu.findItem(R.id.action_sort_closest);
		sortByDistanceMenu.setEnabled(false);
		filterOutMenu = menu.findItem(R.id.action_remove_captured);
		filterOutMenu.setChecked(Util.isFilterOut(sp));
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh_location:
			requestLocation();
			break;
		case R.id.action_sort_name:
			sorter = new SortByName();
			Collections.sort(adapter.getAll(), sorter);
			adapter.notifyDataSetChanged();
			break;
		case R.id.action_sort_age:
			sorter = new SortByAge();
			Collections.sort(adapter.getAll(), sorter);
			adapter.notifyDataSetChanged();
			break;
		case R.id.action_sort_owner:
			sorter = new SortByOwner();
			Collections.sort(adapter.getAll(), sorter);
			adapter.notifyDataSetChanged();
			break;
		case R.id.action_sort_closest:
			sorter = new SortByDistance();
			Collections.sort(adapter.getAll(), sorter);
			adapter.notifyDataSetChanged();
			break;
		/*
		 * case R.id.action_download_gp: downloadUpdate(); break;
		 */
		case R.id.action_reload_data:
			new DownloadAsyncTask().execute(true);
			break;
		case R.id.action_leaderboard:
			showLeaderBoard();
			break;
		case R.id.action_remove_captured:
			boolean isFilter = !Util.isFilterOut(sp);
			filterData(isFilter);
			break;
		case R.id.action_map:
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(Util.JEFF_LINK));
			startActivity(intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final GuardianPortal gp1 = adapter.getAll().get(position);
		String title = gp1.getLocation() + "\n" + gp1.getCity() + "\n"
				+ gp1.getNote();
		builder.setTitle(title);
		builder.setSingleChoiceItems(R.array.options_link, 0,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (which == 0) {
							String uri = "geo:" + gp1.getLattitude() + ","
									+ gp1.getLongitude() + "?q="
									+ gp1.getLattitude() + ","
									+ gp1.getLongitude();
							Intent intent = new Intent(
									android.content.Intent.ACTION_VIEW, Uri
											.parse(uri));

							intent.setClassName("com.google.android.apps.maps",
									"com.google.android.maps.MapsActivity");
							startActivity(intent);
						} else if (which == 1) {
							try {
								Intent intent = new Intent(
										android.content.Intent.ACTION_VIEW, Uri
												.parse(gp1.getIntelMapLink()));
								startActivity(intent);
							} catch (Exception e) {
								Intent intent = new Intent(
										android.content.Intent.ACTION_VIEW,
										Uri.parse(gp1
												.getGeneratedIntelMapLink()));
								startActivity(intent);
							}
						} else {
							String uri = "geo:" + gp1.getLattitude() + ","
									+ gp1.getLongitude() + "?q="
									+ gp1.getLattitude() + ","
									+ gp1.getLongitude();
							Intent intent = new Intent(
									android.content.Intent.ACTION_VIEW, Uri
											.parse(uri));

							startActivity(intent);
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void requestLocation() {
		mLocationManager.removeUpdates(mLocationListener);
		if (currLoc != null)
			currLoc.setText(getText(R.string.label_no_location));
		if (sortByDistanceMenu != null)
			sortByDistanceMenu.setEnabled(false);
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
				LOCATION_REFRESH_DISTANCE, mLocationListener);
	}

	private interface Sorter extends Comparator<GuardianPortal> {
		public int whichSort();

		public static final int NAME = 0;
		public static final int AGE = 1;
		public static final int OWNER = 2;
		public static final int DISTANCE = 3;
	}

	private class SortByName implements Sorter {

		@Override
		public int compare(GuardianPortal arg0, GuardianPortal arg1) {
			return arg0.getName().toUpperCase()
					.compareTo(arg1.getName().toUpperCase());
		}

		@Override
		public int whichSort() {
			return NAME;
		}
	}

	private class SortByAge implements Sorter {

		@Override
		public int compare(GuardianPortal arg0, GuardianPortal arg1) {
			return -Integer.valueOf(arg0.getAge()).compareTo(arg1.getAge());
		}

		@Override
		public int whichSort() {
			return AGE;
		}
	}

	private class SortByOwner implements Sorter {

		@Override
		public int compare(GuardianPortal arg0, GuardianPortal arg1) {
			return arg0.getOwner().toUpperCase()
					.compareTo(arg1.getOwner().toUpperCase());
		}

		@Override
		public int whichSort() {
			return OWNER;
		}
	}

	private class SortByDistance implements Sorter {

		@Override
		public int compare(GuardianPortal arg0, GuardianPortal arg1) {
			return Double.valueOf(arg0.getDistance(latitude, longitude))
					.compareTo(arg1.getDistance(latitude, longitude));
		}

		@Override
		public int whichSort() {
			return DISTANCE;
		}
	}

	/*
	 * private void downloadUpdate() { Uri Download_Uri =
	 * Uri.parse(Util.DOWNLOAD_LINK); DownloadManager.Request request = new
	 * DownloadManager.Request( Download_Uri); downloadId =
	 * downloadManager.enqueue(request); IntentFilter intentFilter = new
	 * IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE);
	 * registerReceiver(downloadReceiver, intentFilter);
	 * Toast.makeText(PortalListActivity.this, "Starting download...",
	 * Toast.LENGTH_LONG).show(); }
	 * 
	 * private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context arg0, Intent arg1) { // TODO
	 * Auto-generated method stub DownloadManager.Query query = new
	 * DownloadManager.Query(); query.setFilterById(downloadId); Cursor cursor =
	 * downloadManager.query(query);
	 * 
	 * if (cursor.moveToFirst()) { int columnIndex = cursor
	 * .getColumnIndex(DownloadManager.COLUMN_STATUS); int status =
	 * cursor.getInt(columnIndex); int columnReason = cursor
	 * .getColumnIndex(DownloadManager.COLUMN_REASON); int reason =
	 * cursor.getInt(columnReason);
	 * 
	 * if (status == DownloadManager.STATUS_SUCCESSFUL) { // Retrieve the saved
	 * download id Toast.makeText(PortalListActivity.this,
	 * "Download complete. Updating list...", Toast.LENGTH_LONG).show();
	 * ParcelFileDescriptor file; try { file =
	 * downloadManager.openDownloadedFile(downloadId);
	 * Util.copyToFileToUserSpace(PortalListActivity.this, new
	 * FileInputStream(file.getFileDescriptor())); GPLoader loader = GPLoader
	 * .getInstance(PortalListActivity.this); loader.reloadList(false);
	 * reloadPortalList(loader); Toast.makeText(PortalListActivity.this,
	 * "List updated.", Toast.LENGTH_LONG).show(); } catch (JSONException e) {
	 * // TODO Auto-generated catch block e.printStackTrace();
	 * Toast.makeText(PortalListActivity.this, e.toString(),
	 * Toast.LENGTH_LONG).show(); } catch (FileNotFoundException e) { // TODO
	 * Auto-generated catch block e.printStackTrace();
	 * Toast.makeText(PortalListActivity.this, e.toString(),
	 * Toast.LENGTH_LONG).show(); } unregisterReceiver(downloadReceiver);
	 * 
	 * } else if (status == DownloadManager.STATUS_FAILED) {
	 * Toast.makeText(PortalListActivity.this, "FAILED!\n" + "reason of " +
	 * reason, Toast.LENGTH_LONG).show(); } else if (status ==
	 * DownloadManager.STATUS_PAUSED) { Toast.makeText(PortalListActivity.this,
	 * "PAUSED!\n" + "reason of " + reason, Toast.LENGTH_LONG).show(); } else if
	 * (status == DownloadManager.STATUS_PENDING) {
	 * Toast.makeText(PortalListActivity.this, "PENDING!",
	 * Toast.LENGTH_LONG).show(); } else if (status ==
	 * DownloadManager.STATUS_RUNNING) { Toast.makeText(PortalListActivity.this,
	 * "RUNNING!", Toast.LENGTH_LONG).show(); } } }
	 * 
	 * };
	 */
	private void showLeaderBoard() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.action_leaderboard);
		Map<String, Integer> scores = GPLoader.getInstance(this).getScores();
		List<ScorePair> scorePairs = new ArrayList<ScorePair>();
		for (String name : scores.keySet()) {
			int pts = scores.get(name);
			scorePairs.add(new ScorePair(name, pts));
		}
		Collections.sort(scorePairs);
		CharSequence[] seqs = new CharSequence[scorePairs.size()];
		for (int i = 0; i < scorePairs.size(); i++) {
			ScorePair sp = scorePairs.get(i);
			seqs[i] = sp.score + " - [" + sp.name + "]";
		}
		builder.setItems(seqs, null);
		builder.show();
	}

	private class ScorePair implements Comparable<ScorePair> {
		public ScorePair(String name, int score) {
			this.name = name;
			this.score = score;
		}

		String name;
		int score;

		@Override
		public int compareTo(ScorePair another) {
			return -Integer.valueOf(score).compareTo(another.score);
		}
	}

	class DownloadAsyncTask extends AsyncTask<Boolean, Integer, Void> {

		private ProgressDialog progressDialog = new ProgressDialog(
				PortalListActivity.this);
		GPLoader loader = null;

		protected void onPreExecute() {
			progressDialog.setMessage(getString(R.string.dialog_download));
			progressDialog.setCancelable(false);
			progressDialog.show();
			progressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					DownloadAsyncTask.this.cancel(true);
				}
			});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progressDialog.setMessage(getString(R.string.dialog_download_page,
					values[0]));
			reloadPortalList(loader);
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Boolean... params) {
			boolean clear = params[0];
			int page = 1;
			boolean repeat = false;
			do {
				try {
					publishProgress(page);
					loader = GPLoader.getInstance(PortalListActivity.this);
					repeat = loader.reloadList(clear, page);
					page = page + 1;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (repeat);
			return null;
		} // protected Void doInBackground(String... params)

		protected void onPostExecute(Void v) {
			reloadPortalList(loader);
			setFilter();
			if (loader.isLastDataDownloaded()) {
				Util.setLastTimeDownload(sp, System.currentTimeMillis());
			}
			if (loader != null && loader.getTitle() != null) {
				setTitle(loader.getTitle());
			}
			if (loader != null && loader.getLastUpdate() != null) {
				refreshTime.setText(getString(R.string.label_last_update,
						loader.getLastUpdate()));
			} else {
				refreshTime.setText(getString(R.string.label_item_count, loader
						.getPortals().size()));
			}
			if (progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();

		} // protected void onPostExecute(Void v)

	} // class MyAsyncTask extends AsyncTask<String, String, Void>

	private void reloadPortalList(GPLoader loader) {
		if (loader != null && loader.getPortals().size() > 0) {
			adapter.getAll().clear();
			adapter.getAll().addAll(loader.getPortals());

		}
	}

	private void setFilter() {
		boolean isFilter = Util.isFilterOut(sp);
		adapter.getFilter().filter(String.valueOf(isFilter));
		adapter.notifyDataSetChanged();
	}

	private void filterData(boolean isFilter) {
		Util.setFilterOut(sp, isFilter);
		filterOutMenu.setChecked(isFilter);
		adapter.getFilter().filter(String.valueOf(isFilter));
		if (sorter != null) {
			Collections.sort(adapter.getAll(), sorter);
		}
		adapter.notifyDataSetChanged();
	}

}
