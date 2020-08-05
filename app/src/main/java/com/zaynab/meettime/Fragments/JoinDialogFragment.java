package com.zaynab.meettime.Fragments;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.github.jinatonic.confetti.CommonConfetti;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.Algorithms.Scheduler;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.Post;
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
        String meetingInfo = "This meeting is scheduled to occur some time between " + make12Format(meeting.getTimeStart().split(" ")[TIME]) + " and " + make12Format(meeting.getTimeEnd().split(" ")[TIME]) + ", on " + meeting.getTimeEnd().split(" ")[DAY] + ".\nWhen would you be able to join?";
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
        //Calendar month counting starts from 0
        // c.setTime(getDate(meetingDate));
        c.set(Calendar.YEAR, Integer.parseInt(meetingDate.split(" ")[0].split("/")[2]));
        c.set(Calendar.MONTH, Integer.parseInt(meetingDate.split(" ")[0].split("/")[0]) - 1);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(meetingDate.split(" ")[0].split("/")[1]));


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
                    Scheduler.Interval hour_sun = Scheduler.suggestBestHour(meeting, start_fields[SUNDAY], end_fields[SUNDAY]);
                    mEtStartTime.setText(formatTime(String.valueOf(hour_sun.getStart())));
                    mEtEndTime.setText(formatTime(String.valueOf(hour_sun.getEnd())));
                    break;
                case 2:
                    Scheduler.Interval hour_mon = Scheduler.suggestBestHour(meeting, start_fields[MONDAY], end_fields[MONDAY]);
                    mEtStartTime.setText(formatTime(String.valueOf(hour_mon.getStart())));
                    mEtEndTime.setText(formatTime(String.valueOf(hour_mon.getEnd())));
                    break;
                case 3:
                    Scheduler.Interval hour_tue = Scheduler.suggestBestHour(meeting, start_fields[TUESDAY], end_fields[TUESDAY]);
                    mEtStartTime.setText(formatTime(String.valueOf(hour_tue.getStart())));
                    mEtEndTime.setText(formatTime(String.valueOf(hour_tue.getEnd())));
                    break;
                case 4:
                    Scheduler.Interval hour_wed = Scheduler.suggestBestHour(meeting, start_fields[WEDNESDAY], end_fields[WEDNESDAY]);
                    mEtStartTime.setText(formatTime(String.valueOf(hour_wed.getStart())));
                    mEtEndTime.setText(formatTime(String.valueOf(hour_wed.getEnd())));
                    break;
                case 5:
                    Scheduler.Interval hour_thu = Scheduler.suggestBestHour(meeting, start_fields[THURSDAY], end_fields[THURSDAY]);
                    mEtStartTime.setText(formatTime(String.valueOf(hour_thu.getStart())));
                    mEtEndTime.setText(formatTime(String.valueOf(hour_thu.getEnd())));
                    break;
                case 6:
                    Scheduler.Interval hour_fri = Scheduler.suggestBestHour(meeting, start_fields[FRIDAY], end_fields[FRIDAY]);
                    mEtStartTime.setText(formatTime(String.valueOf(hour_fri.getStart())));
                    mEtEndTime.setText(formatTime(String.valueOf(hour_fri.getEnd())));
                    break;
                case 7:
                    Scheduler.Interval hour_sat = Scheduler.suggestBestHour(meeting, start_fields[SATURDAY], end_fields[SATURDAY]);
                    mEtStartTime.setText(formatTime(String.valueOf(hour_sat.getStart())));
                    mEtEndTime.setText(formatTime(String.valueOf(hour_sat.getEnd())));
                    break;
            }//end_switch
        }
    }


    private void setupConfirm(Meeting meeting) throws ParseException {
        final boolean[] overlap = {false};
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    overlap[0] = Scheduler.findIntersection(mEtStartTime.getText().toString(), mEtEndTime.getText().toString(), meeting);
                    double overlap_duration = Scheduler.getIntersection(mEtStartTime.getText().toString(), mEtEndTime.getText().toString(), meeting);
                    if (overlap[0] && overlap_duration >= MIN_OVERLAP) {
                        enrollUser(meeting, ParseUser.getCurrentUser());
                    } else {
                        Snackbar.make(view, R.string.snackbar_cant_join, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void enrollUser(Meeting meeting, ParseUser currentUser) {
        //display confetti
        CommonConfetti.rainingConfetti(((AppCompatActivity) getContext()).findViewById(R.id.flContainer), new int[]{Color.BLUE, Color.MAGENTA, Color.CYAN, Color.CYAN, Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN})
                .stream(20).setEmissionRate(100000);

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
                                            makePost(meeting);
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

    private void makePost(Meeting meeting) {
        Post post = new Post();
        post.setOwner(ParseUser.getCurrentUser());
        post.setMeeting(meeting);
        post.setLaunched(false);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                Bundle b = new Bundle();
                b.putSerializable("MEETING", meeting);
                MeetingDetailsFragment meetingDetailsFragment = new MeetingDetailsFragment();
                meetingDetailsFragment.setArguments(b);
                getDialog().dismiss();
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, meetingDetailsFragment, "MDF").addToBackStack("MDF").commit();
            }
        });
    }


    private String formatTime(String time) {
        double time_d = Double.parseDouble(time);
        int hours = (int) time_d;
        int minutes = (int) ((time_d - hours) * 60);
        String formatted = "" + formatTimeZeros(hours) + ":" + formatTimeZeros(minutes);
        return formatted;
    }

    public String formatTimeZeros(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    public String make12Format(String time) {
        String result = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj = sdf.parse(time);
            result = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}