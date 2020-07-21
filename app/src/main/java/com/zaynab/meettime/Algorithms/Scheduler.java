package com.zaynab.meettime.Algorithms;

import com.zaynab.meettime.models.Meeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    public static boolean findIntersection(String userTimeStart, String userTimeEnd, Meeting meeting) throws ParseException {
        String meetingTimeStart = meeting.getString("timeStart");
        String meetingTimeEnd = meeting.getString("timeEnd");
        String timeStart = meetingTimeStart.split(" ")[1];
        String timeEnd = meetingTimeEnd.split(" ")[1];
        Date mStart = getTime(timeStart);
        Date mEnd = getTime(timeEnd);
        Date uStart = getTime(userTimeStart);
        Date uEnd = getTime(userTimeEnd);
        //overlap when max(start1, start2) < min(end1, end2)
        Date maxStart = mStart.after(uStart) ? mStart : uStart;
        Date minEnd = mEnd.after(uEnd) ? uEnd : mEnd;
        return maxStart.before(minEnd);
    }

    public static double getIntersection(String userTimeStart, String userTimeEnd, Meeting meeting) throws ParseException {
        String meetingTimeStart = meeting.getString("timeStart");
        String meetingTimeEnd = meeting.getString("timeEnd");
        String timeStart = meetingTimeStart.split(" ")[1];
        String timeEnd = meetingTimeEnd.split(" ")[1];
        Date mStart = getTime(timeStart);
        Date mEnd = getTime(timeEnd);
        Date uStart = getTime(userTimeStart);
        Date uEnd = getTime(userTimeEnd);
        //possible intersections are ranges with different end/start:
        long diff1 = TimeUnit.MINUTES.convert(Math.abs(uStart.getTime() - mEnd.getTime()), TimeUnit.MILLISECONDS);
        long diff2 = TimeUnit.MINUTES.convert(Math.abs(mStart.getTime() - uEnd.getTime()), TimeUnit.MILLISECONDS);
        //or ranges with same end/start (case: range contained in the other):
        long diff3 = TimeUnit.MINUTES.convert(Math.abs(mStart.getTime() - mEnd.getTime()), TimeUnit.MILLISECONDS);
        long diff4 = TimeUnit.MINUTES.convert(Math.abs(uStart.getTime() - uEnd.getTime()), TimeUnit.MILLISECONDS);
        long min1 = (diff1 <= diff2) ? diff1 : diff2;
        long min2 = (diff3 <= diff4) ? diff3 : diff4;
        return ((min1 <= min2) ? min1 : min2);
    }

    private static Date getTime(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.parse(date);

    }

}
