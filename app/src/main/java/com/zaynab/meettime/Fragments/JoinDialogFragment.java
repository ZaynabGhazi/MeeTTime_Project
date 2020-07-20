package com.zaynab.meettime.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;

import static android.icu.util.Calendar.HOUR_OF_DAY;
import static android.icu.util.Calendar.MINUTE;
import static android.icu.util.Calendar.getInstance;


public class JoinDialogFragment extends DialogFragment {
    public static final int TIME = 1;
    public static final int DAY = 0;

    public static final String TAG = "JOIN_DIALOG_FRAGMENT";
    private TextView mTvMeetingInfo;
    private TextInputEditText mEtStartTime;
    private TextInputEditText mEtEndTime;
    private MaterialButton mBtnConfirm;
    private int mHour;
    private int mMinute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_join, container, false);
        Bundle b = getArguments();
        Meeting meeting = (Meeting) b.getSerializable("MEETING");
        bindView(v);
        initializeDefaultData(meeting);
        setTimePickers();
        setupConfirm();
        return v;
    }


    private void bindView(View v) {
        mTvMeetingInfo = v.findViewById(R.id.tvMeetingInfo);

        mEtStartTime = v.findViewById(R.id.etMonEnd);
        mEtEndTime = v.findViewById(R.id.etTimeEnd);
    }

    private void initializeDefaultData(Meeting meeting) {
        String meetingInfo = "This meeting is scheduled to occur some time between " + meeting.getTimeStart().split(" ")[TIME] + " and " + meeting.getTimeEnd().split(" ")[TIME] + ", on " + meeting.getTimeEnd().split(" ")[DAY] + ".\nWhen would you be able to join?";
        mTvMeetingInfo.setText(meetingInfo);


    }

    private void setTimePickers() {
        initializeCalendar();
        mEtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(mEtStartTime);
            }
        });
        mEtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(mEtEndTime);
            }
        });
    }

    private void showTimeDialog(final TextInputEditText et) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                et.setText(hour + ":" + min);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    private void initializeCalendar() {
        mHour = getInstance().get(HOUR_OF_DAY);
        mMinute = getInstance().get(MINUTE);
    }

    private void setupConfirm() {
        //TODO: Algorithm linking + new view tbc
    }

}