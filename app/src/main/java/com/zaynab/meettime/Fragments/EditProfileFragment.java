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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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
    private ParseUser mUsr;

    private Context mContext;
    private ImageView mIvProfileImage;
    private TextInputEditText mEtFirstName;
    private TextInputEditText mEtLastName;
    private TextInputEditText mEtUsername;
    private TextInputEditText mEtEmail;
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
        mBtnSave = view.findViewById(R.id.btnSave);
    }


    private void showCurrentData() {
        mEtFirstName.setText(mUsr.getString("firstName"));
        mEtLastName.setText(mUsr.getString("lastName"));
        mEtUsername.setText(mUsr.getUsername());
        mEtEmail.setText((mUsr.getEmail() == null) ? "" : mUsr.getEmail());
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
                // mUsr.put("startAvailability", mEtStartDate.getText().toString() + " " + mEtStartTime.getText().toString());
                // mUsr.put("endAvailability", mEtStartDate.getText().toString() + " " + mEtStartTime.getText().toString());
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