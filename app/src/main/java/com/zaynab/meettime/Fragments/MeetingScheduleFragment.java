package com.zaynab.meettime.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import java.util.Arrays;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.zaynab.meettime.Algorithms.Scheduler;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.UserTime;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import static com.zaynab.meettime.Fragments.JoinDialogFragment.DAY;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MeetingScheduleFragment extends Fragment {
    public static final String TAG = "MEETING_FRAGMENT";
    public static final int SEPARATION_INCREMENT = 60;
    private int mEventIndex;
    private int mEventSeparation = 0;

    private TextView mEventDate;
    private RelativeLayout mLayout;


    public MeetingScheduleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meeting, container, false);
        Bundle b = getArguments();
        Meeting meeting = (Meeting) b.getSerializable("MEETING");
        bindView(v);
        try {
            displayAvailability(meeting);
            displayBestHour(meeting);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return v;
    }

    private void bindView(View v) {
        mLayout = ((RelativeLayout) v.findViewById(R.id.left_event_column));
        mEventIndex = mLayout.getChildCount();
        mEventDate = ((TextView) v.findViewById(R.id.display_current_date));

    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.parse(date);
    }

    private int getEventTimeFrame(Date start, Date end) {
        long timeDifference = end.getTime() - start.getTime();
        return ((int) convertDpToPx(getContext(), timeDifference / 60000));
    }

    private void displayEventSection(Date eventDate, int height, String message, ParseUser user) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String displayValue = timeFormatter.format(eventDate);
        String[] hourMinutes = displayValue.split(":");
        int hours = Integer.parseInt(hourMinutes[0]);
        int minutes = Integer.parseInt(hourMinutes[1]);
        int topViewMargin = ((int) convertDpToPx(getContext(), hours * 60 + minutes));
        createEventView(topViewMargin, height, message, user);
    }

    private void createEventView(int topMargin, int height, String message, ParseUser user) {
        TextView mEventView = new TextView(getContext());
        RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = topMargin;
        lParam.leftMargin = mEventSeparation;
        mEventView.setLayoutParams(lParam);
        mEventView.setPadding(24, 0, 24, 0);
        mEventView.setHeight(height);
        mEventView.setGravity(0x11);
        mEventView.setTextColor(Color.parseColor("#ffffff"));

        if (user == null) mEventView.setText("meeTTime");
        else {
            try {
                mEventView.setText(user.fetchIfNeeded().getUsername().substring(0, 1));
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }
        RandomColors rand = new RandomColors();
        int color = rand.getColor();
        mEventView.setBackgroundColor(color);
        mEventSeparation += SEPARATION_INCREMENT;
        mLayout.addView(mEventView, mEventIndex - 1);
        if (user != null) {
            user.fetchInBackground();
            mEventView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    try {
                        Snackbar.make(view, user.fetchIfNeeded().getUsername(), BaseTransientBottomBar.LENGTH_SHORT).show();
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            });
        }
    }

    private void displayAvailability(Meeting meeting) throws ParseException {
        mEventDate.setText(meeting.getTimeStart().split(" ")[DAY]);
        ParseRelation<UserTime> attendees = meeting.getAttendanceData();
        final ParseQuery<UserTime> query = attendees.getQuery();
        query.findInBackground(new FindCallback<UserTime>() {
            @Override
            public void done(List<UserTime> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (int j = 0; j < objects.size(); j++) {
                        UserTime data = objects.get(j);
                        try {
                            Date availability_start = getDate(meeting.getTimeStart().split(" ")[DAY] + " " + data.getAvailabilityStart());
                            Date availability_end = getDate(meeting.getTimeStart().split(" ")[DAY] + " " + data.getAvailabilityEnd());
                            int availabilityBlockHeight = getEventTimeFrame(availability_start, availability_end);
                            displayEventSection(availability_start, availabilityBlockHeight, "availability", data.getUser());
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }//end_loop
                }//end_works
                else Log.e(TAG, "Error fetching userTime objects", e);
            }//end_done
        });

    }

    private void displayBestHour(Meeting meeting) throws ParseException {
        Scheduler.Interval best_hour = Scheduler.getBestHour(meeting);
        Date best_hour_start = getDate(meeting.getTimeStart().split(" ")[DAY] + " " + formatTime(String.valueOf(best_hour.getStart())));
        displayEventSection(best_hour_start, ((int) convertDpToPx(getContext(), 60)), "", null);

    }

    //Helper functions:
    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private String formatTime(String time) {
        double time_d = Double.parseDouble(time);
        int hours = (int) time_d;
        int minutes = (int) ((time_d - hours) * 60);
        String formatted = "" + hours + ":" + minutes;
        return formatted;
    }

    //generate random colors from list-palette:
    class RandomColors {
        private Stack<Integer> recycle, colors;

        public RandomColors() {
            colors = new Stack<>();
            recycle = new Stack<>();
            recycle.addAll(Arrays.asList(
                    0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7,
                    0xff3f51b5, 0xff2196f3, 0xff03a9f4, 0xff00bcd4,
                    0xff009688, 0xff4caf50, 0xff8bc34a, 0xffcddc39,
                    0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722,
                    0xff795548, 0xff9e9e9e, 0xff607d8b, 0xff333333
                    )
            );
        }

        public int getColor() {
            if (colors.size() == 0) {
                while (!recycle.isEmpty())
                    colors.push(recycle.pop());
                Collections.shuffle(colors);
            }
            Integer c = colors.pop();
            recycle.push(c);
            return c;
        }
    }
}