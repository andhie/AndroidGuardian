package com.virtualmanila.guardianportallister.sgmy;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuardianPortal {
	private String name;
	private String owner;
	private long capdate;
	private int level;
	private double longitude;
	private double lattitude;
	private String intelMapLink;
	private boolean live;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	private String location;
	private String city;
	private String note;

	public static final DecimalFormat df = new DecimalFormat("#0.000000");

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public long getCapdate() {
		return capdate;
	}

	public void setCapdate(long capdate) {
		this.capdate = capdate;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLattitude() {
		return lattitude;
	}

	public void setCoordinates(double lattitude, double longitude) {
		this.lattitude = lattitude;
		this.longitude = longitude;
	}

	private static final String REGEX = "ll=[0-9]*.[0-9]*,[0-9]*.[0-9]*";
	private static final Pattern pattern = Pattern.compile(REGEX);

	public void setCoordinates(String coords) throws InvalidParameterException {
		Matcher matcher = pattern.matcher(coords);
		if (!matcher.find()) {
			throw new InvalidParameterException(
					"Did not find portal coordinate [" + coords + "]");
		}
		String newString = matcher.group().substring(3);
		String[] split = newString.split(",");
		setCoordinates(Double.valueOf(split[0]), Double.valueOf(split[1]));

	}

	public int getAge() {
		long diff = System.currentTimeMillis() - capdate;
		int inDays = (int) (diff / (1000 * 60 * 60 * 24));
		return inDays;
	}

	public double getDistance(double latt, double longi) {
		return Math.sqrt(((latt - lattitude) * (latt - lattitude))
				+ ((longi - longitude) * (longi - longitude)));
	}

	public String getIntelMapLink() {
		return intelMapLink;
	}

	public void setIntelMapLink(String intelMapLink) {
		this.intelMapLink = intelMapLink;
	}

	public String getGeneratedIntelMapLink() {
		return "http://www.ingress.com/intel?ll=" + df.format(lattitude) + ","
				+ df.format(longitude) + "&z=17&pll=" + df.format(lattitude)
				+ "," + df.format(longitude);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}
}
