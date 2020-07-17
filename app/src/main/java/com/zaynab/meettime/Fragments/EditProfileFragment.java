package com.zaynab.meettime.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.MainActivity;
import com.zaynab.meettime.R;

import java.io.ByteArrayOutputStream;

import static android.icu.util.Calendar.DAY_OF_MONTH;
import static android.icu.util.Calendar.HOUR_OF_DAY;
import static android.icu.util.Calendar.MINUTE;
import static android.icu.util.Calendar.MONTH;
import static android.icu.util.Calendar.YEAR;
import static android.icu.util.Calendar.getInstance;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    public static final String TAG = "EDIT_PROFILE_FRAGMENT";
    public static final int DATE = 0;
    public static final int TIME = 1;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private ParseUser mUsr;

    private Context mContext;
    private ImageView mIvProfileImage;
    private TextInputEditText mEtFirstName;
    private TextInputEditText mEtLastName;
    private TextInputEditText mEtUsername;
    private TextInputEditText mEtEmail;
    private TextInputEditText mEtStartDate;
    private TextInputEditText mEtEndDate;
    private TextInputEditText mEtStartTime;
    private TextInputEditText mEtEndTime;
    private MaterialButton mBtnSave;

    public EditProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mContext = view.getContext();
        mUsr = ParseUser.getCurrentUser();
        bindView(view);
        setupProfilePicture();
        setDateTimePickers();
        showCurrentData();
        onSaveChanges();
        setupEditPicture();
    }

    private void setupEditPicture() {
        mIvProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfilePictureFragment editProfilePictureFragment = new EditProfilePictureFragment();
                FragmentTransaction ft = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                Fragment prev = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                editProfilePictureFragment.show(ft, "dialog");
            }
        });
    }


    private void setupProfilePicture() {
        ParseUser current = ParseUser.getCurrentUser();
        current.fetchInBackground();
        ParseFile profile_image = current.getParseFile("profilePicture");
        if (profile_image != null)
            Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvProfileImage);
    }

    private void bindView(View view) {
        mIvProfileImage = view.findViewById(R.id.ivProfileImage);
        mEtFirstName = view.findViewById(R.id.etFirstName);
        mEtLastName = view.findViewById(R.id.etLastName);
        mEtUsername = view.findViewById(R.id.etUsername);
        mEtEmail = view.findViewById(R.id.etEmail);
        mEtStartDate = view.findViewById(R.id.etDateStart);
        mEtEndDate = view.findViewById(R.id.etDateEnd);
        mEtStartTime = view.findViewById(R.id.etTimeStart);
        mEtEndTime = view.findViewById(R.id.etTimeEnd);
        mBtnSave = view.findViewById(R.id.btnSave);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                et.setText(i + ":" + i1);
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

    private void showCurrentData() {
        mEtFirstName.setText(mUsr.getString("firstName"));
        mEtLastName.setText(mUsr.getString("lastName"));
        mEtUsername.setText(mUsr.getUsername());
        mEtEmail.setText((mUsr.getEmail() == null) ? "" : mUsr.getEmail());
        if (mUsr.getString("startAvailability") != null) {
            String[] fields = mUsr.getString("startAvailability").split(" ");
            mEtStartDate.setText(fields[DATE]);
            mEtStartTime.setText(fields[TIME]);
        }
        if (mUsr.getString("endAvailability") != null) {
            String[] fields = mUsr.getString("endAvailability").split(" ");
            mEtEndDate.setText(fields[DATE]);
            mEtEndTime.setText(fields[TIME]);
        }
    }


    private void onSaveChanges() {
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add better handling of whether editText elements have changed
                mUsr.setUsername(mEtUsername.getText().toString());
                mUsr.setEmail(mEtEmail.getText().toString());
                mUsr.put("firstName", mEtFirstName.getText().toString());
                mUsr.put("lastName", mEtLastName.getText().toString());
                mUsr.put("startAvailability", mEtStartDate.getText().toString() + " " + mEtStartTime.getText().toString());
                mUsr.put("endAvailability", mEtStartDate.getText().toString() + " " + mEtStartTime.getText().toString());
                //save profile picture
                final BitmapDrawable drawable = (BitmapDrawable) mIvProfileImage.getDrawable();
                Bitmap image = drawable.getBitmap();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                ParseFile pictureFile = new ParseFile(outStream.toByteArray());
                mUsr.put("profilePicture", pictureFile);
                mUsr.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(mContext, "Error updating profile!", Toast.LENGTH_SHORT);
                            return;
                        }
                        Toast.makeText(mContext, "Error updating profile!", Toast.LENGTH_SHORT);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new ProfileFragment()).commit();
                        //update bottom navigation bar
                        ((BottomNavigationView) getActivity().findViewById(R.id.bottomNavigationView)).setSelectedItemId(R.id.action_profile);
                        //update Navigation Drawer instantly
                        if (drawable != null)
                            Glide.with(mContext).load(drawable).apply(RequestOptions.circleCropTransform()).into(((ImageView) getActivity().findViewById(R.id.drawerImage)));
                    }
                });
            }
        });
    }


}