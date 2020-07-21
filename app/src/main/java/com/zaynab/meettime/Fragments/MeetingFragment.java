package com.zaynab.meettime.Fragments;

import android.content.Context;
import android.graphics.Color;

import java.util.Arrays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zaynab.meettime.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MeetingFragment extends Fragment {
    public static final String TAG = "MEETING_FRAGMENT";
    public static final int SEPARATION_INCREMENT = 100;

    private TextView mEventDate;
    private RelativeLayout mLayout;
    private int mEventIndex;

    private int eventSeparation = 0;


    public MeetingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meeting, container, false);
        bindView(v);
        try {
            displayOwnerAvailability();
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

    private String displayDateInString(Date mDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH);
        return formatter.format(mDate);
    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.parse(date);
    }

    private int getEventTimeFrame(Date start, Date end) {
        long timeDifference = end.getTime() - start.getTime();
        return ((int) convertDpToPx(getContext(), timeDifference / 60000));
    }

    private void displayEventSection(Date eventDate, int height, String message) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String displayValue = timeFormatter.format(eventDate);
        String[] hourMinutes = displayValue.split(":");
        int hours = Integer.parseInt(hourMinutes[0]);
        int minutes = Integer.parseInt(hourMinutes[1]);
        int topViewMargin = ((int) convertDpToPx(getContext(), hours * 60 + minutes));
        createEventView(topViewMargin, height, message);
    }

    private void createEventView(int topMargin, int height, String message) {
        TextView mEventView = new TextView(getContext());
        RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = topMargin;
        lParam.leftMargin = eventSeparation;
        mEventView.setLayoutParams(lParam);
        mEventView.setPadding(24, 0, 24, 0);
        mEventView.setHeight(height);
        mEventView.setGravity(0x11);
        mEventView.setTextColor(Color.parseColor("#ffffff"));
        mEventView.setText(message);
        RandomColors rand = new RandomColors();
        int color = rand.getColor();
        mEventView.setBackgroundColor(color);
        eventSeparation += SEPARATION_INCREMENT;
        mLayout.addView(mEventView, mEventIndex - 1);
    }

    private void displayOwnerAvailability() throws ParseException {
        //TODO: Implement this method
        //Code below for testing

        Date eventDate = getDate("21/07/2020 08:00");
        Date endDate = getDate("21/07/2020 11:00");
        String eventMessage = "test";
        int eventBlockHeight = getEventTimeFrame(eventDate, endDate);
        displayEventSection(eventDate, eventBlockHeight, eventMessage);

        Date eventDate1 = getDate("21/07/2020 10:30");
        Date endDate1 = getDate("21/07/2020 15:30");
        String eventMessage1 = "test";
        int eventBlockHeight1 = getEventTimeFrame(eventDate1, endDate1);
        displayEventSection(eventDate1, eventBlockHeight1, eventMessage1);

    }

    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


    //generate shiny random colors:
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