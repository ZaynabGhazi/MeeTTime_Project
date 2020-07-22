package com.zaynab.meettime.models;

import android.provider.CalendarContract;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.zaynab.meettime.support.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Meeting")
public class Meeting extends ParseObject implements Serializable {

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

    public void addUser(ParseUser attendee) {
        getAttendees().add(attendee);
        saveInBackground();
    }

    public void removeUser(ParseUser attendee) {
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

    public ParseRelation<UserTime> getAttendanceData() {
        return getRelation("attendanceData");
    }

    public void addAttendanceData(UserTime attendance) {
        getAttendanceData().add(attendance);
        saveInBackground();
    }

    public void removeAttendanceData(UserTime attendance) {
        getAttendanceData().remove(attendance);
        saveInBackground();
    }

    public boolean isAttendee(ParseUser usr) {
        final boolean[] isAttendee = {false};
        ParseRelation<ParseUser> attendees = this.getAttendees();
        final ParseQuery<ParseUser> isMember = attendees.getQuery();
        isMember.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).getObjectId().equals(usr.getObjectId())) {
                            isAttendee[0] = true;
                            break;
                        }
                    }
                }
            }
        });
        return isAttendee[0];
    }

    public void getAttendeesList(List<ParseUser> users) {
        ParseRelation<ParseUser> attendees = this.getAttendees();
        final ParseQuery<ParseUser> list = attendees.getQuery();
        list.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    users.addAll(objects);

                } else
                    Log.e("meeting", "Error fetching list of attendees", e);
            }
        });
    }
}
