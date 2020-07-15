package com.zaynab.meettime.models;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Meeting")
public class Meeting extends ParseObject {

    public String getTitle() {
        String title = "";
        try {
            title = fetchIfNeeded().getString("title");

        } catch (ParseException e) {
            Log.v("MEETING", e.toString());
            e.printStackTrace();
        }
        return title;
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public ParseUser getChair() {
        return getParseUser("chairPerson");
    }

    public void setChair(ParseUser chair) {
        put("chairPerson", chair);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String desc) {
        put("description", desc);
    }

    public ParseRelation<ParseUser> getAttendees() {
        return getRelation("attendees");
    }

    public void inviteUser(ParseUser attendee) {
        getAttendees().add(attendee);
        saveInBackground();
    }

    public void uninviteUser(ParseUser attendee) {
        getAttendees().remove(attendee);
        saveInBackground();
    }

    public String getTimeStart() {
        return getString("timeStart");
    }

    public void setTimeStart(String time) {
        put("timeStart", time);
    }

    public String getTimeEnd() {
        return getString("timeEnd");
    }

    public void setTimeEnd(String time) {
        put("timeEnd", time);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("meetingLocation");
    }

    public void setLocation(ParseGeoPoint location) {
        put("meetingLocation", location);
    }

    /*
     * meeting.isOpen() -> Users can still join the meeting
     */
    public boolean isOpen() {
        return getBoolean("open");
    }

    public void makeOpen() {
        put("open", true);
    }

    public void makeClosed() {
        put("open", false);
    }

    public String getZoomUrl() {
        return getString("zoomUrl");
    }

    public void setZoomUrl(String url) {
        put("zoomUrl", url);
    }

    /*
     * meeting.isLocated() -> The location of the meeting has been set.
     */
    public boolean isLocated() {
        return getLocation() != null;
    }

    /*
     * meeting.isScheduled() -> The time of the meeting has been set.
     */
    public boolean isScheduled() {
        return getTimeStart() != null;
    }

}
