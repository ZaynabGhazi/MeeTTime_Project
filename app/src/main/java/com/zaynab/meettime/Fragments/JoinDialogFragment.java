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
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseUser;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;

import static android.icu.util.Calendar.DAY_OF_MONTH;
import static android.icu.util.Calendar.HOUR_OF_DAY;
import static android.icu.util.Calendar.MINUTE;
import static android.icu.util.Calendar.MONTH;
import static android.icu.util.Calendar.YEAR;
import static android.icu.util.Calendar.getInstance;


public class JoinDialogFragment extends DialogFragment {

    public static final String TAG = "JOIN_DIALOG_FRAGMENT";
    private TextView mTvMeetingInfo;
    private TextInputEditText mEtStartDate;
    private TextInputEditText mEtEndDate;
    private TextInputEditText mEtStartTime;
    private TextInputEditText mEtEndTime;
    private MaterialButton mBtnConfirm;

    private int mYear;
    private int mMonth;
    private int mDay;
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
        setDateTimePickers();
        setupConfirm();
        return v;
    }


    private void bindView(View v) {
        mTvMeetingInfo = v.findViewById(R.id.tvMeetingInfo);
        mEtStartDate = v.findViewById(R.id.etMonStart);
        mEtEndDate = v.findViewById(R.id.etDateEnd);
        mEtStartTime = v.findViewById(R.id.etMonEnd);
        mEtEndTime = v.findViewById(R.id.etTimeEnd);
    }

    private void initializeDefaultData(Meeting meeting) {
        String meetingInfo = "This meeting is scheduled to occur some time between " + meeting.getTimeStart() + " and " + meeting.getTimeEnd() + ".\nWhen would you be able to join?";
        mTvMeetingInfo.setText(meetingInfo);


    }

    private void setDateTimePickers() {
        initializeCalendar();
        mEtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(mEtStartDate);
            }
        });
        mEtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(mEtEndDate);
            }
        });
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
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                et.setText(i + ":" + i1);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void showDateDialog(final TextInputEditText et) {
        @SuppressLint("RestrictedApi") MaterialStyledDatePickerDialog datePickerDialog = new MaterialStyledDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                et.setText(Integer.toString(month + 1) + "/" + day + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void initializeCalendar() {
        mYear = getInstance().get(YEAR);
        mMonth = getInstance().get(MONTH);
        mDay = getInstance().get(DAY_OF_MONTH);
        mHour = getInstance().get(HOUR_OF_DAY);
        mMinute = getInstance().get(MINUTE);
    }

    private void setupConfirm() {
        //TBD
    }

}