package com.virtualmanila.guardianportallister.sgmy.model;

import com.google.gson.Gson;

import com.virtualmanila.guardianportallister.sgmy.util.Util;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class GuardianPortal {

    private int age_points;

    private String agent_name;

    private String bonus_details;

    private String bonus_points;

    private DateTime captured_date;

    private String city;

    private DateTime created_at;

    private DateTime day_of_150;

    private String destroyed_by;

    private DateTime destruction_date;

    private int id;

    private String lat_coordinate;

    private String link;

    private String lng_coordinate;

    private String location;

    private String note;

    private String portal_guid;

    private String portal_name;

    private String status_string;

    private int total_points;

    private DateTime updated_at;

    public int getAge_points() {
        return age_points;
    }

    public String getAgent_name() {
        return agent_name;
    }

    public String getBonus_details() {
        return bonus_details;
    }

    public String getBonus_points() {
        return bonus_points;
    }

    public DateTime getCaptured_date() {
        return captured_date;
    }

    public String getCity() {
        return city;
    }

    public DateTime getCreated_at() {
        return created_at;
    }

    public DateTime getDay_of_150() {
        return day_of_150;
    }

    public String getDestroyed_by() {
        return destroyed_by;
    }

    public DateTime getDestruction_date() {
        return destruction_date;
    }

    public int getId() {
        return id;
    }

    public String getLat_coordinate() {
        return lat_coordinate;
    }

    public String getLink() {
        return link;
    }

    public String getLng_coordinate() {
        return lng_coordinate;
    }

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public String getPortal_guid() {
        return portal_guid;
    }

    public String getPortal_name() {
        return portal_name;
    }

    public String getStatus_string() {
        return status_string;
    }

    public int getTotal_points() {
        return total_points;
    }

    public DateTime getUpdated_at() {
        return updated_at;
    }

    public boolean isLive() {
        return "Live".equalsIgnoreCase(status_string);
    }

    public int getPortalAge() {
        if (isLive()) {
            DateTime now = DateTime.now();
            return Days.daysBetween(captured_date.toLocalDate(), now.toLocalDate()).getDays();
        } else {
            return Days.daysBetween(captured_date.toLocalDate(), destruction_date.toLocalDate())
                    .getDays();
        }
    }

    public static GuardianPortal fromJson(String json) {
        Gson gson = Util.getGson();
        return gson.fromJson(json, GuardianPortal.class);
    }

    @Override
    public String toString() {
        Gson gson = Util.getGson();
        return gson.toJson(this);
    }
}