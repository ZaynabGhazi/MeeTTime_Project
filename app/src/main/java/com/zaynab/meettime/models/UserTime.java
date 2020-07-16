package com.zaynab.meettime.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("UserTime")
public class UserTime extends ParseObject {
    public UserTime() {
        super();
    }

    public ParseUser getUser() {
        return getParseUser("attendee");
    }

    public String getAvailabilityStart() {
        return getString("availabilityStart");
    }

    public String getAvailabilityEnd() {
        return getString("availabilityEnd");
    }

    public void setUser(ParseUser usr) {
        put("attendee", usr);
    }

    public void setAvailabilityStart(String s) {
        put("availabilityStart", s);
    }

    public void setAvailabilityEnd(String s) {
        put("availabilityEnd", s);
    }
}
