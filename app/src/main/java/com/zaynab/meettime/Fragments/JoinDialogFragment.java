package com.zaynab.meettime.Fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.Algorithms.Scheduler;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.UserTime;
import com.zaynab.meettime.support.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.icu.util.Calendar.HOUR_OF_DAY;
import static android.icu.util.Calendar.MINUTE;
import static android.icu.util.Calendar.getInstance;
import static android.text.TextUtils.isEmpty;


public class JoinDialogFragment extends DialogFragment {
    public static final int TIME = 1;
    public static final int DAY = 0;
    public static final int MIN_OVERLAP = 5; // in minutes

    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    public static final String TAG = "JOIN_DIALOG_FRAGMENT";
    private TextView mTvMeetingInfo;
    private TextInputEditText mEtStartTime;
    private TextInputEditText mEtEndTime;
    private MaterialButton mBtnConfirm;
    private int mHour;
    private int mMinute;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_join, container, false);
        Bundle b = getArguments();
        Meeting meeting = (Meeting) b.getSerializable("MEETING");
        bindView(v);
        try {
            showAvailability(meeting);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initializeDefaultData(meeting);
        setTimePickers();
        try {
            setupConfirm(meeting);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return v;
    }


    private void bindView(View v) {
        mTvMeetingInfo = v.findViewById(R.id.tvMeetingInfo);
        mEtStartTime = v.findViewById(R.id.etMonEnd);
        mEtEndTime = v.findViewById(R.id.etTimeEnd);
        mBtnConfirm = v.findViewById(R.id.btnConfirm);
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

    private void showAvailability(Meeting meeting) throws ParseException {
        //fetch day of week
        Calendar c = Calendar.getInstance();
        String meetingDate = meeting.getString("timeStart");
        c.setTime(getDate(meetingDate));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        //fetch corresponding availability data
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.fetchInBackground();
        String startTime = currentUser.getString("startAvailability");
        String endTime = currentUser.getString("endAvailability");
        if (!isEmpty(startTime) && !isEmpty(endTime)) {
            String[] start_fields = startTime.split(" ");
            String[] end_fields = endTime.split(" ");
            switch (dayOfWeek) {
                case 1: //SUNDAY
                    mEtStartTime.setText(start_fields[SUNDAY]);
                    mEtEndTime.setText(end_fields[SUNDAY]);
                    break;
                case 2:
                    mEtStartTime.setText(start_fields[MONDAY]);
                    mEtEndTime.setText(end_fields[MONDAY]);
                    break;
                case 3:
                    mEtStartTime.setText(start_fields[TUESDAY]);
                    mEtEndTime.setText(end_fields[TUESDAY]);
                    break;
                case 4:
                    mEtStartTime.setText(start_fields[WEDNESDAY]);
                    mEtEndTime.setText(end_fields[WEDNESDAY]);
                    break;
                case 5:
                    mEtStartTime.setText(start_fields[THURSDAY]);
                    mEtEndTime.setText(end_fields[THURSDAY]);
                    break;
                case 6:
                    mEtStartTime.setText(start_fields[FRIDAY]);
                    mEtEndTime.setText(end_fields[FRIDAY]);
                    break;
                case 7:
                    mEtStartTime.setText(start_fields[SATURDAY]);
                    mEtEndTime.setText(end_fields[SATURDAY]);
                    break;
            }//end_switch
        }
    }


    private void setupConfirm(Meeting meeting) throws ParseException {
        //TODO: Algorithm linking + new view
        final boolean[] overlap = {false};
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    overlap[0] = Scheduler.findIntersection(mEtStartTime.getText().toString(), mEtEndTime.getText().toString(), meeting);
                    double overlap_duration = Scheduler.getIntersection(mEtStartTime.getText().toString(), mEtEndTime.getText().toString(), meeting);
                    if (overlap[0] && overlap_duration >= MIN_OVERLAP) {
                        Logger.notify(TAG, "Overlap!", getContext(), null);
                        Logger.notify(TAG, "Intersection is " + Scheduler.getIntersection(mEtStartTime.getText().toString(), mEtEndTime.getText().toString(), meeting) + " minutes", getContext(), null);
                        enrollUser(meeting, ParseUser.getCurrentUser());
                    } else {
                        Logger.notify(TAG, "No overlap!", getContext(), null);
                        Snackbar.make(view, R.string.snackbar_cant_join, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void enrollUser(Meeting meeting, ParseUser currentUser) {
        UserTime attendance = new UserTime();
        attendance.setUser(currentUser);
        attendance.setAvailabilityStart(mEtStartTime.getText().toString());
        attendance.setAvailabilityEnd(mEtEndTime.getText().toString());
        attendance.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    meeting.addAttendanceData(attendance);
                    meeting.addUser(currentUser);
                    meeting.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e != null) {
                                Logger.notify(TAG, "Error enrolling user in the meeting!", getContext(), e);

                            } else {
                                Toast.makeText(getContext(), "joined successfully!", Toast.LENGTH_SHORT).show();
                                //add meeting to user object:
                                currentUser.fetchInBackground();
                                currentUser.getRelation("meetings").add(meeting);
                                //navigate to meeting details:
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(com.parse.ParseException e) {
                                        if (e == null) {
                                            Bundle b = new Bundle();
                                            b.putSerializable("MEETING", meeting);
                                            MeetingDetailsFragment meetingDetailsFragment = new MeetingDetailsFragment();
                                            meetingDetailsFragment.setArguments(b);
                                            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, meetingDetailsFragment).commit();
                                            getDialog().dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy HH:mm");
        return formatter.parse(date);
    }

}