package com.zaynab.meettime.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.support.Logger;
import com.zaynab.meettime.utilities.MeetingsAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    public static final String TAG = "CALENDAR_FRAGMENT";

    private CalendarView mCalendarView;
    private Context mContext;
    private RecyclerView mRvEvents;
    private List<Meeting> mAllMeetings;
    private MeetingsAdapter mAdapter;
    private ParseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mCalendarView = view.findViewById(R.id.calendarView);
        mRvEvents = view.findViewById(R.id.rvMeetings);
        mCurrentUser = ParseUser.getCurrentUser();
        setupRecycler();
        showCurrentEvents();
        setupDayAction();
    }

    private void setupRecycler() {
        mAllMeetings = new ArrayList<>();
        //setup recycler
        MeetingsAdapter.OnClickListener clickListener = new MeetingsAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Log.i(TAG, "Meeting clicked at position " + position);
            }
        };
        mAdapter = new MeetingsAdapter(mContext, mAllMeetings, clickListener);
        mRvEvents.setAdapter(mAdapter);
        LinearLayoutManager llManager = new LinearLayoutManager(mContext);
        mRvEvents.setLayoutManager(llManager);
    }

    private void showCurrentEvents() {
        Calendar today = mCalendarView.getFirstSelectedDate();
        String date = Integer.toString(Integer.valueOf(today.get(Calendar.MONTH)) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH) + "/" + today.get(Calendar.YEAR);
        displayEvents(date);

    }

    private void setupDayAction() {
        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String date = Integer.toString(Integer.valueOf(clickedDayCalendar.get(Calendar.MONTH)) + 1) + "/" + clickedDayCalendar.get(Calendar.DAY_OF_MONTH) + "/" + clickedDayCalendar.get(Calendar.YEAR);
                Logger.notify(TAG, "Clicked day " + date, mContext, null);
                //check if there are any events the user joined this day
                displayEvents(date);

            }
        });
    }

    private void displayEvents(String date) {
        mAllMeetings.clear();
        mCurrentUser.fetchInBackground();
        ParseRelation<Meeting> relation = mCurrentUser.getRelation("meetings");
        ParseQuery<Meeting> query = relation.getQuery();
        query.whereStartsWith("timeStart", date);
        query.findInBackground(new FindCallback<Meeting>() {
            @Override
            public void done(List<Meeting> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        Logger.notify(TAG, "There is an event today!", mContext, null);
                        mAllMeetings.addAll(objects);
                    } else {
                        mAllMeetings.clear();
                    }
                    mAdapter.notifyDataSetChanged();
                }//end_works
                else Log.e(TAG, "Error fetching meeting objects", e);
            }
        });
    }

}