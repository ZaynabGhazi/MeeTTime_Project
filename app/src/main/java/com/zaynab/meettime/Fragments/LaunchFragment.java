package com.zaynab.meettime.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.zaynab.meettime.R;

import org.w3c.dom.Text;

import static android.icu.util.Calendar.*;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LaunchFragment extends Fragment {

    public static final String TAG = "LAUNCH_FRAGMENT";
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private Calendar mCalendar;
    private Context mContext;


    private TextInputEditText mEtTitle;
    private SwitchMaterial mBtnInperson;
    private SwitchMaterial mBtnTime;
    private TextInputEditText mEtDateStart;
    private TextInputEditText mEtDateEnd;
    private TextInputEditText mEtTimeStart;
    private TextInputEditText mEtTimeEnd;
    private TextInputEditText mEtLocation;
    private MaterialButton mBtnRemoteLink;
    private TextInputEditText mEtDescription;
    private MaterialButton mBtnInvite;
    private SwitchMaterial mBtnPrivate;
    private MaterialButton mBtnLaunch;


    public LaunchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_launch, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = view.getContext();
        bindView(view);
        setDateTimePickers();
    }


    private void bindView(View view) {
        mEtTitle = view.findViewById(R.id.etTitle);
        mBtnInperson = view.findViewById(R.id.btnInperson);
        mBtnTime = view.findViewById(R.id.btnTime);
        mEtDateStart = view.findViewById(R.id.etDateStart);
        mEtDateEnd = view.findViewById(R.id.etDateEnd);
        mEtTimeStart = view.findViewById(R.id.etTimeStart);
        mEtTimeEnd = view.findViewById(R.id.etTimeEnd);
        mEtLocation = view.findViewById(R.id.etLocation);
        mBtnRemoteLink = view.findViewById(R.id.btnRemoteLink);
        mEtDescription = view.findViewById(R.id.etDescription);
        mBtnInvite = view.findViewById(R.id.btnInviteFriends);
        mBtnPrivate = view.findViewById(R.id.btnPrivate);
        mBtnLaunch = view.findViewById(R.id.btnLaunch);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDateTimePickers() {
        initializeCalendar();
        mEtDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(mEtDateStart);
            }
        });
        mEtDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(mEtDateEnd);
            }
        });
        mEtTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(mEtTimeStart);
            }
        });
        mEtTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(mEtTimeEnd);
            }
        });
    }

    private void showTimeDialog(final TextInputEditText et) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                et.setText(i + ":" + i1);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @SuppressLint("NewApi")
    private void showDateDialog(final TextInputEditText et) {
        @SuppressLint("RestrictedApi") MaterialStyledDatePickerDialog datePickerDialog = new MaterialStyledDatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                et.setText(i2 + "/" + i1 + "/" + i1);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeCalendar() {
        mYear = getInstance().get(YEAR);
        mMonth = getInstance().get(MONTH);
        mDay = getInstance().get(DAY_OF_MONTH);
        mHour = getInstance().get(HOUR_OF_DAY);
        mMinute = getInstance().get(MINUTE);
    }

}