package com.zaynab.meettime.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.Post;
import com.zaynab.meettime.models.UserTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import dagger.Reusable;

import static android.icu.util.Calendar.*;
import static android.text.TextUtils.isEmpty;
import static com.zaynab.meettime.Fragments.EditProfilePictureFragment.PICK_PHOTO_CODE;

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
    private Context mContext;
    private PlacesClient mPlacesClient;
    private boolean mDrawableSelected;

    private TextInputEditText mEtTitle;
    private SwitchMaterial mBtnInperson;
    private SwitchMaterial mBtnTime;
    private TextInputEditText mEtDate;
    private TextInputEditText mEtTimeStart;
    private TextInputEditText mEtTimeEnd;
    private MaterialButton mBtnRemoteLink;
    private TextInputEditText mEtDescription;
    private MaterialButton mBtnInvite;
    private SwitchMaterial mBtnPrivate;
    private MaterialButton mBtnLaunch;
    private ImageView mIvBackground;
    private AutocompleteSupportFragment mPlacesFragment;
    private ParseGeoPoint mLocation;
    private TextView mTvInfo;


    public LaunchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_launch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = view.getContext();
        bindView(view);
        setDateTimePickers();
        setRemoteView();
        launchMeeting();
        addBackgroundPhoto();
        initializePlacesSDK();
        setupPlacesFragment();
    }


    private void bindView(View view) {
        mEtTitle = view.findViewById(R.id.etTitle);
        mBtnInperson = view.findViewById(R.id.btnInperson);
        mBtnTime = view.findViewById(R.id.btnTime);
        mEtDate = view.findViewById(R.id.etDay);
        mEtTimeStart = view.findViewById(R.id.etTimeStart);
        mEtTimeEnd = view.findViewById(R.id.etTimeEnd);
        mBtnRemoteLink = view.findViewById(R.id.btnRemoteLink);
        mEtDescription = view.findViewById(R.id.etDescription);
        mBtnInvite = view.findViewById(R.id.btnInviteFriends);
        mBtnPrivate = view.findViewById(R.id.btnPrivate);
        mBtnLaunch = view.findViewById(R.id.btnLaunch);
        mIvBackground = view.findViewById(R.id.ivBackground);
        mTvInfo = view.findViewById(R.id.tvLocationHint);

    }

    private void setDateTimePickers() {
        initializeCalendar();
        mEtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(mEtDate);
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
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                String time = formatTimeZeros(hour) + ":" + formatTimeZeros(min);
                et.setText(make12Format(formatTimeZeros(hour) + ":" + formatTimeZeros(min)));
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void showDateDialog(final TextInputEditText et) {
        @SuppressLint("RestrictedApi") MaterialStyledDatePickerDialog datePickerDialog = new MaterialStyledDatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("RestrictedApi")
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

    private void launchMeeting() {
        mBtnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDrawableSelected) {
                    Toast.makeText(mContext, "Please select a picture for the meeting!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (errorField(mEtTimeStart) || errorField(mEtTimeEnd) || errorField(mEtDate) || errorField(mEtTitle) || errorField(mEtDescription)) {
                    return;
                }
                final Meeting meeting = createMeeting();
                //ToDo: implement all other features/attributes of a meeting
                saveAttendance(meeting);
            }
        });
    }

    private boolean errorField(TextInputEditText text) {
        if (isEmpty(text.getText().toString())) {
            text.setError("Field Required to launch event!");
            return true;
        }
        return false;
    }

    private void saveAttendance(Meeting meeting) {
        UserTime attendance = new UserTime();
        attendance.setUser(ParseUser.getCurrentUser());
        attendance.setAvailabilityStart(make24Format(mEtTimeStart.getText().toString()));
        attendance.setAvailabilityEnd(make24Format(mEtTimeEnd.getText().toString()));
        attendance.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error saving attendance", e);
                    return;
                }
                saveMeeting(meeting, attendance);
            }
        });
    }

    private void saveMeeting(Meeting meeting, UserTime attendance) {
        meeting.addAttendanceData(attendance);
        meeting.addUser(ParseUser.getCurrentUser());
        if (mLocation != null) {
            meeting.put("meetingLocation", mLocation);
        }
        meeting.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser current = ParseUser.getCurrentUser();
                    current.fetchInBackground();
                    current.getRelation("meetings").add(meeting);
                    current.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error adding meeting to user object", e);
                                return;
                            }
                            makePost(meeting);

                        }
                    });

                } else Log.e(TAG, "Error saving meeting upon launch", e);
            }
        });
    }

    private void makePost(Meeting meeting) {
        Post post = new Post();
        post.setOwner(ParseUser.getCurrentUser());
        post.setMeeting(meeting);
        post.setLaunched(true);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //redirect
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new TimelineFragment()).commit();
            }
        });
    }

    private void addBackgroundPhoto() {
        mIvBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto(view);
            }
        });
    }

    private void onPickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            if (selectedImage != null) {
                Glide.with(getContext()).load(photoUri).centerCrop().into(mIvBackground);
                mDrawableSelected = true;
            }
        } else { // Result was a failure
            Toast.makeText(getContext(), "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();
            mDrawableSelected = false;
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if (Build.VERSION.SDK_INT > 27) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
                mDrawableSelected = true;
            } else {
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                mDrawableSelected = true;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public Meeting createMeeting() {
        Meeting meeting = new Meeting();
        meeting.setTitle(mEtTitle.getText().toString());
        meeting.setChair(ParseUser.getCurrentUser());
        meeting.setDescription(mEtDescription.getText().toString());
        //DateTime format: mm/dd/yyyy hh:mm
        String timeStart = mEtDate.getText().toString() + " " + make24Format(mEtTimeStart.getText().toString());
        String timeEnd = mEtDate.getText().toString() + " " + make24Format(mEtTimeEnd.getText().toString());
        meeting.setTimeStart(timeStart);
        meeting.setTimeEnd(timeEnd);
        //save background picture
        Drawable image_dr = mIvBackground.getDrawable();
        if (mDrawableSelected) {
            final BitmapDrawable drawable = (BitmapDrawable) image_dr;

            if (drawable != null) {
                Bitmap image = drawable.getBitmap();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                ParseFile pictureFile = new ParseFile(outStream.toByteArray());
                meeting.put("bgPicture", pictureFile);
            }
        }
        return meeting;
    }

    private void setRemoteView() {
        mBtnInperson.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    mTvInfo.setVisibility(View.GONE);
                    getChildFragmentManager().beginTransaction().hide(mPlacesFragment).commit();
                    mBtnRemoteLink.setVisibility(View.VISIBLE);
                } else {
                    mTvInfo.setVisibility(View.VISIBLE);
                    getChildFragmentManager().beginTransaction().show(mPlacesFragment).commit();
                    mBtnRemoteLink.setVisibility(View.GONE);
                }
            }
        });
    }


    private void initializePlacesSDK() {
        Places.initialize(mContext.getApplicationContext(), getString(R.string.places_API_KEY));
        mPlacesClient = Places.createClient(mContext);
    }

    private void setupPlacesFragment() {
        mPlacesFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        mPlacesFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        mPlacesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mLocation = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
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

    public String make12Format(String time) {
        String result = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj = sdf.parse(time);
            result = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String make24Format(String time) {
        String result = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            final Date dateObj = sdf.parse(time);
            result = new SimpleDateFormat("HH:mm").format(dateObj);
        } catch (final java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}