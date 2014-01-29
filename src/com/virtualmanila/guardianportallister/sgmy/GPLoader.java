package com.virtualmanila.guardianportallister.sgmy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class GPLoader {

	private static GPLoader loader;
	private static final String STATUS_LIVE = "LIVE";
	private static final String URL_SELECT = "http://enl.sentulasia.com/portals.json";
	private static final String PAGE_PARAM = "?page=";

	private List<GuardianPortal> portals;

	private Map<String, Integer> score;

	private String title;
	private String lastUpdate;

	private SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'hh:mm:ss'Z'");

	private Context context;
	private boolean lastDataDownloaded;

	private GPLoader(Context context) {
		portals = new ArrayList<GuardianPortal>();
		score = new TreeMap<String, Integer>();
		this.context = context;
		try {
			boolean repeat;
			int ctr = 1;
			do {
				repeat = reloadFromFile(ctr);
				ctr = ctr + 1;
			} while (repeat);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static GPLoader getInstance(Context context) {
		if (loader == null) {
			loader = new GPLoader(context);
		}
		return loader;
	}

	public boolean reloadList(boolean redownload, int page)
			throws JSONException {
		if (redownload) {
			return downloadFromURL(page);
		} else {
			return reloadFromFile(page);
		}
	}

	private boolean downloadFromURL(int page) throws JSONException {
		boolean retVal = false;
		new ArrayList<NameValuePair>();
		InputStream inputStream = null;

		URL url = null;
		HttpURLConnection urlCnxn = null;
		try {
			url = new URL(URL_SELECT + PAGE_PARAM + page);
			urlCnxn = (HttpURLConnection) url.openConnection();

			// Read content & Log
			inputStream = urlCnxn.getInputStream();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Convert response to string using String Builder
		BufferedWriter writer = null;
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					inputStream, "utf-8"), 8);
			writer = new BufferedWriter(new FileWriter(Util.getJsonFile(
					context, page)));
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				writer.write(line + "\n");
				sb.append(line).append("\n");
			}
			retVal = loadFromJsonString(sb.toString(), page);
			lastDataDownloaded = true;
		} catch (JSONException e) {
			Log.e("StringBuilding & BufferedReader", "Error converting result "
					+ e.toString());
			throw e;
		} catch (Exception e) {
			Log.e("StringBuilding & BufferedReader", "Error converting result "
					+ e.toString());
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}

	private boolean loadFromJsonString(String result, int page)
			throws JSONException {

		boolean retVal = false;
		if (page == 1) {
			portals.clear();
			score.clear();
		}
		// parse JSON data
		try {
			android.util.Log.d("ALAIN", "Result[" + page + "]: " + result);
			JSONArray jArray = new JSONArray(result);
			retVal = jArray != null && jArray.length() > 0;
			for (int i = 0; i < jArray.length(); i++) {

				JSONObject jObject = jArray.getJSONObject(i);

				GuardianPortal gp = new GuardianPortal();
				gp.setName(jObject.getString("portal_name"));
				gp.setCoordinates(jObject.getString("link"));
				gp.setOwner(jObject.getString("agent_name"));
				gp.setIntelMapLink(jObject.getString("link"));
				try {
					gp.setCapdate(sdf.parse(jObject.getString("captured_date"))
							.getTime());
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				gp.setLocation(jObject.getString("location"));
				gp.setCity(jObject.getString("city"));
				gp.setNote(jObject.getString("note"));
				boolean live = STATUS_LIVE.equalsIgnoreCase(jObject
						.getString("status_string"));
				gp.setLive(live);
				if (!live) {
					try {
						addScore(jObject.getString("destroyed_by"),
								Integer.valueOf(jObject
										.getString("total_points")));
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				portals.add(gp);
			} // End Loop

		} catch (JSONException e) {

			Log.e("JSONException", "Error: " + e.toString());
			throw e;
		} // catch (JSONException e)

		return retVal;
	}

	private boolean reloadFromFile(int page) throws JSONException {
		boolean retVal = false;
		File jsonFile = Util.getJsonFile(context, page);
		if (jsonFile.exists()) {
			BufferedReader bReader = null;
			try {
				bReader = new BufferedReader(new FileReader(jsonFile), 8);

				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = bReader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				retVal = loadFromJsonString(sb.toString(), page);
				lastDataDownloaded = false;
			} catch (JSONException e) {
				Log.e("StringBuilding & BufferedReader",
						"Error converting result " + e.toString());
				throw e;
			} catch (Exception e) {
				Log.e("StringBuilding & BufferedReader",
						"Error converting result " + e.toString());
			} finally {
				try {
					bReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			retVal = downloadFromURL(page);
		}
		return retVal;
	}

	public List<GuardianPortal> getPortals() {
		return portals;
	}

	public Map<String, Integer> getScores() {
		return score;
	}

	private void addScore(String namex, int pts) {
		String[] names = namex.split(",");
		for (int i = 0; i < names.length; i++) {
			String name = names[i].toUpperCase().trim();
			Integer currScore = score.get(name);
			if (currScore == null) {
				currScore = 0;
			}
			currScore = currScore + pts;
			score.put(name, currScore);
		}
	}

	public static File getFile(Context context, boolean clear) {
		File retVal = Util.getFile(context);

		if (clear || !retVal.exists()) {
			if (clear) {
				retVal.delete();
			}
			try {
				retVal = Util.copyToFileToUserSpace(context, context
						.getAssets().open(Util.FILENAME));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		android.util.Log.d("ALAIN", "Return file: " + retVal.getAbsolutePath());
		return retVal;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/*
	 * public boolean jsonFilesExists() { File jsonFile =
	 * Util.getJsonFile(context); return jsonFile.exists(); }
	 */

	public boolean isLastDataDownloaded() {
		return lastDataDownloaded;
	}

	public void setLastDataDownloaded(boolean lastDataDownloaded) {
		this.lastDataDownloaded = lastDataDownloaded;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
