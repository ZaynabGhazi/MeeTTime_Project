package com.zaynab.meettime.Algorithms;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.UserTime;
import com.zaynab.meettime.support.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zaynab.meettime.Algorithms.Scheduler.HOUR;
import static com.zaynab.meettime.Algorithms.Scheduler.MIN;
import static com.zaynab.meettime.Fragments.JoinDialogFragment.DAY;

public class Scheduler {
    public static final String TAG = "SCHEDULER_ALGORITHM";
    public static final int HOUR = 0;
    public static final int MIN = 1;

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

    public static Interval getBestHour(Meeting meeting) throws ParseException {
        List<Interval> list = new ArrayList<>();
        List<UserTime> ut_list = new ArrayList<>();
        //get chairPerson's availability:
        String meetingTimeStart = meeting.getString("timeStart");
        String meetingTimeEnd = meeting.getString("timeEnd");
        String timeStart = meetingTimeStart.split(" ")[1];
        String timeEnd = meetingTimeEnd.split(" ")[1];
        Date mStart = getTime(timeStart);
        Date mEnd = getTime(timeEnd);
        //parse userTime data

        return computeBestHour(meeting, list, ut_list);
    }

    public static Interval suggestBestHour(Meeting meeting, String userTimeStart, String userTimeEnd) throws ParseException {
        List<Interval> list = new ArrayList<>();
        List<UserTime> ut_list = new ArrayList<>();
        //add day-specific availability to list
        double hours_start = Double.parseDouble(userTimeStart.split(":")[HOUR]);
        double hours_end = Double.parseDouble(userTimeEnd.toString().split(":")[HOUR]);
        double min_start = Double.parseDouble(userTimeEnd.toString().split(":")[MIN]);
        double min_end = Double.parseDouble(userTimeEnd.toString().split(":")[MIN]);
        list.add(new Interval((hours_start + min_start / 60), (hours_end + min_end / 60)));
        return computeBestHour(meeting, list, ut_list);
    }

    public static Interval computeBestHour(Meeting meeting, List<Interval> list, List<UserTime> ut_list) {
        ParseRelation<UserTime> attendees = meeting.getAttendanceData();
        final ParseQuery<UserTime> query = attendees.getQuery();
        try {
            ut_list.addAll(query.find());
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        for (int j = 0; j < ut_list.size(); j++) {
            UserTime data = ut_list.get(j);
            String availability_start = data.getAvailabilityStart();
            String availability_end = data.getAvailabilityEnd();
            double hours_start_ = Double.parseDouble(availability_start.split(":")[HOUR]);
            double hours_end_ = Double.parseDouble(availability_end.toString().split(":")[HOUR]);
            double min_start_ = Double.parseDouble(availability_start.toString().split(":")[MIN]);
            double min_end_ = Double.parseDouble(availability_end.toString().split(":")[MIN]);
            list.add(new Interval((hours_start_ + min_start_ / 60), (hours_end_ + min_end_ / 60)));
        }//end_loop
        Interval result = getBestInterval(list);
        double result_start = result.getStart();
        double result_end = result.getEnd();
        if (result.getEnd() - result.getStart() != 1) {
            int midpoint = (int) Math.round((result.getEnd() + result.getStart()) / 2);
            result_start = midpoint - 0.5;
            result_end = midpoint + 0.5;
        }
        Interval best_hour = new Interval(result_start, result_end);
        return best_hour;
    }

    /* Implementation of Marzullo's algorithm
     * Adapted from: "https://github.com/adrian219/marzullo-alogrithm"
     */
    public static Interval getBestInterval(List<Interval> intervalList) {
        List<Pair> pairList = new ArrayList<>();
        for (Interval interval : intervalList) {
            pairList.add(new Pair(interval.getStart(), -1));
            pairList.add(new Pair(interval.getEnd(), +1));
        }
        Collections.sort(pairList);
        Integer best = 0;
        Integer current = 0;
        Pair currentPair;
        Double bestStart = 0.0;
        Double bestEnd = 0.0;
        for (Integer i = 0; i < pairList.size(); i++) {
            currentPair = pairList.get(i);

            current -= currentPair.getType();

            if (current > best) {
                best = current;
                bestStart = currentPair.getOffset();
                bestEnd = pairList.get(i + 1).getOffset();
            }
        }
        return new Interval(bestStart, bestEnd);

    }


    //Helper classes
    static class Pair implements Comparable<Pair> {
        private Double offset;
        private Integer type;

        public Pair(Double offset, Integer type) {
            this.offset = offset;
            this.type = type;
        }

        public Double getOffset() {
            return offset;
        }

        public void setOffset(Double offset) {
            this.offset = offset;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        @Override
        public int compareTo(Pair o) {
            if (o.getOffset().equals(this.getOffset())) return o.getType() - this.getType();
            return (this.getOffset() - o.getOffset()) > 0 ? 1 : -1;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "offset=" + offset +
                    ", type=" + type +
                    '}';
        }
    }

    public static class Interval {
        private Double start;
        private Double end;

        public Interval(Double start, Double end) {
            this.start = start;
            this.end = end;
        }

        public Double getStart() {
            return start;
        }

        public void setStart(Double start) {
            this.start = start;
        }

        public Double getEnd() {
            return end;
        }

        public void setEnd(Double end) {
            this.end = end;
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}

