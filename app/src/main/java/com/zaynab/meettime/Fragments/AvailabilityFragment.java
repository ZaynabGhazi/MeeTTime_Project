package com.zaynab.meettime.Fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.R;

import static android.icu.util.Calendar.DAY_OF_MONTH;
import static android.icu.util.Calendar.HOUR_OF_DAY;
import static android.icu.util.Calendar.MINUTE;
import static android.icu.util.Calendar.MONTH;
import static android.icu.util.Calendar.YEAR;
import static android.icu.util.Calendar.getInstance;
import static android.text.TextUtils.isEmpty;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AvailabilityFragment extends Fragment {
    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    private TextInputEditText mEtStartMon;
    private TextInputEditText mEtEndMon;
    private TextInputEditText mEtStartTue;
    private TextInputEditText mEtEndTue;
    private TextInputEditText mEtStartWed;
    private TextInputEditText mEtEndWed;
    private TextInputEditText mEtStartThu;
    private TextInputEditText mEtEndThu;
    private TextInputEditText mEtStartFri;
    private TextInputEditText mEtEndFri;
    private TextInputEditText mEtStartSat;
    private TextInputEditText mEtEndSat;
    private TextInputEditText mEtStartSun;
    private TextInputEditText mEtEndSun;
    private MaterialButton mBtnSave;


    private Context mContext;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private ParseUser mUsr;

    public AvailabilityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_availability, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = view.getContext();
        mUsr = ParseUser.getCurrentUser();
        bindView(view);
        showCurrentData();
        setTimePickers();

    }


    private void bindView(View view) {
        mEtStartMon = view.findViewById(R.id.etMonStart);
        mEtEndMon = view.findViewById(R.id.etMonEnd);
        mEtStartTue = view.findViewById(R.id.etTueStart);
        mEtEndTue = view.findViewById(R.id.etTueEnd);
        mEtStartWed = view.findViewById(R.id.etWedStart);
        mEtEndWed = view.findViewById(R.id.etWedEnd);
        mEtStartThu = view.findViewById(R.id.etThuStart);
        mEtEndThu = view.findViewById(R.id.etThuEnd);
        mEtStartFri = view.findViewById(R.id.etFriStart);
        mEtEndFri = view.findViewById(R.id.etFriEnd);
        mEtStartSat = view.findViewById(R.id.etSatStart);
        mEtEndSat = view.findViewById(R.id.etSatEnd);
        mEtStartSun = view.findViewById(R.id.etSunStart);
        mEtEndSun = view.findViewById(R.id.etSunEnd);
        mBtnSave = view.findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUpdates();
            }
        });
    }

    private void showCurrentData() {
        mUsr.fetchInBackground();
        String startTime = mUsr.getString("startAvailability");
        String endTime = mUsr.getString("endAvailability");
        if (!isEmpty(startTime) && !isEmpty(endTime)) {
            String[] start_fields = startTime.split(" ");
            String[] end_fields = endTime.split(" ");
            mEtStartMon.setText(formatTimeZeros(Integer.parseInt(start_fields[MONDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(start_fields[MONDAY].split(":")[1])));
            mEtEndMon.setText(formatTimeZeros(Integer.parseInt(end_fields[MONDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(end_fields[MONDAY].split(":")[1])));
            mEtStartTue.setText(formatTimeZeros(Integer.parseInt(start_fields[TUESDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(start_fields[TUESDAY].split(":")[1])));
            mEtEndTue.setText(formatTimeZeros(Integer.parseInt(end_fields[TUESDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(end_fields[TUESDAY].split(":")[1])));
            mEtStartWed.setText(formatTimeZeros(Integer.parseInt(start_fields[WEDNESDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(start_fields[WEDNESDAY].split(":")[1])));
            mEtEndWed.setText(formatTimeZeros(Integer.parseInt(end_fields[WEDNESDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(end_fields[WEDNESDAY].split(":")[1])));
            mEtStartThu.setText(formatTimeZeros(Integer.parseInt(start_fields[THURSDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(start_fields[THURSDAY].split(":")[1])));
            mEtEndThu.setText(formatTimeZeros(Integer.parseInt(end_fields[THURSDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(end_fields[THURSDAY].split(":")[1])));
            mEtStartFri.setText(formatTimeZeros(Integer.parseInt(start_fields[FRIDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(start_fields[FRIDAY].split(":")[1])));
            mEtEndFri.setText(formatTimeZeros(Integer.parseInt(end_fields[FRIDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(end_fields[FRIDAY].split(":")[1])));
            mEtStartSat.setText(formatTimeZeros(Integer.parseInt(start_fields[SATURDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(start_fields[SATURDAY].split(":")[1])));
            mEtEndSat.setText(formatTimeZeros(Integer.parseInt(end_fields[SATURDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(end_fields[SATURDAY].split(":")[1])));
            mEtStartSun.setText(formatTimeZeros(Integer.parseInt(start_fields[SUNDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(start_fields[SUNDAY].split(":")[1])));
            mEtEndSun.setText(formatTimeZeros(Integer.parseInt(end_fields[SUNDAY].split(":")[0])) + ":" + formatTimeZeros(Integer.parseInt(end_fields[SUNDAY].split(":")[1])));
        }

    }


    private void showTimeDialog(final TextInputEditText et) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                et.setText(formatTimeZeros(hour) + ":" + formatTimeZeros(min));
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void initializeCalendar() {
        mYear = getInstance().get(YEAR);
        mMonth = getInstance().get(MONTH);
        mDay = getInstance().get(DAY_OF_MONTH);
        mHour = getInstance().get(HOUR_OF_DAY);
        mMinute = getInstance().get(MINUTE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTimePickers() {
        initializeCalendar();
        timeListener(mEtStartMon);
        timeListener(mEtStartTue);
        timeListener(mEtStartWed);
        timeListener(mEtStartThu);
        timeListener(mEtStartFri);
        timeListener(mEtStartSat);
        timeListener(mEtStartSun);
        timeListener(mEtEndMon);
        timeListener(mEtEndTue);
        timeListener(mEtEndWed);
        timeListener(mEtEndThu);
        timeListener(mEtEndFri);
        timeListener(mEtEndSat);
        timeListener(mEtEndSun);

    }


    private void timeListener(TextInputEditText et) {
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(et);
            }
        });
    }

    private void saveUpdates() {
        String sMon = mEtStartMon.getText().toString();
        String sTue = mEtStartTue.getText().toString();
        String sWed = mEtStartWed.getText().toString();
        String sThu = mEtStartThu.getText().toString();
        String sFri = mEtStartFri.getText().toString();
        String sSat = mEtStartSat.getText().toString();
        String sSun = mEtStartSun.getText().toString();
        String startTime = "";
        if (!isEmpty(sMon) && !isEmpty(sTue) && !isEmpty(sWed) && !isEmpty(sThu) && !isEmpty(sFri) && !isEmpty(sSat) && !isEmpty(sSun)) {
            startTime += sMon + " " + sTue + " " + sWed + " " + sThu + " " + sFri + " " + sSat + " " + sSun;
        }
        String eMon = mEtEndMon.getText().toString();
        String eTue = mEtEndTue.getText().toString();
        String eWed = mEtEndWed.getText().toString();
        String eThu = mEtEndThu.getText().toString();
        String eFri = mEtEndFri.getText().toString();
        String eSat = mEtEndSat.getText().toString();
        String eSun = mEtEndSun.getText().toString();
        String endTime = "";
        if (!isEmpty(eMon) && !isEmpty(eTue) && !isEmpty(eWed) && !isEmpty(eThu) && !isEmpty(eFri) && !isEmpty(eSat) && !isEmpty(eSun)) {
            endTime += eMon + " " + eTue + " " + eWed + " " + eThu + " " + eFri + " " + eSat + " " + eSun;
        }
        mUsr.fetchInBackground();
        mUsr.put("startAvailability", startTime);
        mUsr.put("endAvailability", endTime);
        mUsr.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Toast.makeText(getContext(), "Availability updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String formatTimeZeros(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

}