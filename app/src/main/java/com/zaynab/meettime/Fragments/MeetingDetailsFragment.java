package com.zaynab.meettime.Fragments;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.utilities.UsersAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingDetailsFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = "MEETING_DETAILS_FRAGMENT";
    public static final int TIME = 1;
    public static final int DAY = 0;

    private ImageView mIvBackground;
    private Context mContext;
    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvDesc;
    private MaterialButton mBtnViewSchedule;
    private TextView mTvZoomLink;
    private MapView mMvLocation;
    private TextView mTvAttendeesText;
    private RecyclerView mRvAttendees;
    private ImageView mIvAddFriends;
    private UsersAdapter mAdapter;
    private SupportMapFragment mMapFragment;
    protected List<ParseUser> mAllAttendees;
    private ParseGeoPoint mLocation;


    public MeetingDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meeting_details, container, false);
        mContext = getContext();
        Bundle b = getArguments();
        Meeting meeting = (Meeting) b.getSerializable("MEETING");
        bindView(v);
        populateView(meeting);
        return v;

    }


    private void bindView(View v) {
        mIvBackground = v.findViewById(R.id.ivBackground);
        mTvTitle = v.findViewById(R.id.tvTitle);
        mTvTime = v.findViewById(R.id.tvTime);
        mTvDesc = v.findViewById(R.id.tvDesc);
        mBtnViewSchedule = v.findViewById(R.id.btnViewSchedule);
        mTvZoomLink = v.findViewById(R.id.tvZoom);
        mRvAttendees = v.findViewById(R.id.rvAttendees);
        mIvAddFriends = v.findViewById(R.id.ivAddFriends);
        mTvAttendeesText = v.findViewById(R.id.tvAttendeesText);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

    }

    private void populateView(Meeting meeting) {
        meeting.fetchInBackground();
        mAllAttendees = new ArrayList<>();
        ParseFile bg_image = meeting.getParseFile("bgPicture");
        if (bg_image != null)
            Glide.with(mContext).load(bg_image.getUrl()).fitCenter().into(mIvBackground);
        mTvTitle.setText(meeting.getTitle());
        //TODO: Zoom Link + Maps View
        mTvTime.setText(getTimeString(meeting));
        mTvDesc.setText(meeting.getDescription());
        //edit:
        if (meeting.isScheduled()) showSchedule(meeting);
        if (meeting.getZoomUrl() != null) {
            mMvLocation.setVisibility(View.GONE);
        } else mTvZoomLink.setVisibility(View.GONE);
        UsersAdapter.OnClickListener clickListener = new UsersAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Log.i(TAG, "User clicked at position " + position);
            }
        };
        mAdapter = new UsersAdapter(mContext, mAllAttendees, clickListener);
        setupRecycler(meeting);
        if (meeting.getParseGeoPoint("meetingLocation") != null) {
            mLocation = meeting.getParseGeoPoint("meetingLocation");
            mMapFragment.getMapAsync(this);
        } else {
            getChildFragmentManager().beginTransaction().hide(mMapFragment).commit();

        }
    }


    private void showSchedule(Meeting meeting) {
        ParseRelation<ParseUser> attendees = meeting.getAttendees();
        final ParseQuery<ParseUser> isMember = attendees.getQuery();
        isMember.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        isMember.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    mBtnViewSchedule.setVisibility(View.VISIBLE);
                    setupSchedule(meeting);
                }

            }
        });
    }

    private void setupSchedule(Meeting meeting) {
        mBtnViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("MEETING", meeting);
                MeetingScheduleFragment meetingScheduleFragment = new MeetingScheduleFragment();
                meetingScheduleFragment.setArguments(b);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, meetingScheduleFragment).addToBackStack(null).commit();
            }
        });
    }

    private void setupRecycler(Meeting meeting) {
        ParseRelation<ParseUser> attendees = meeting.getAttendees();
        final ParseQuery<ParseUser> list = attendees.getQuery();
        list.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    mAllAttendees.addAll(objects);
                    mAdapter.notifyDataSetChanged();
                    mRvAttendees.setAdapter(mAdapter);
                    LinearLayoutManager llManager = new LinearLayoutManager(mContext);
                    mRvAttendees.setLayoutManager(llManager);
                } else
                    Log.e("MEETING", "Error showing attendees", e);
            }
        });
    }

    private String getTimeString(Meeting meeting) {
        String time = "";
        String date = meeting.getTimeStart().split(" ")[DAY];
        DateFormat num_date = new SimpleDateFormat("MM/dd/yyyy");
        try {

            DateFormat alpha_date = new SimpleDateFormat("MMMM dd, yyyy");
            Date date_ = num_date.parse(date);
            String dateString = alpha_date.format(date_);
            time += dateString;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        time += " from " + formatTime(meeting.getTimeStart().split(" ")[TIME].split(":")[0]) + ":" + formatTime(meeting.getTimeStart().split(" ")[TIME].split(":")[1]) + " to " + formatTime(meeting.getTimeEnd().split(" ")[TIME].split(":")[0]) + ":" + formatTime(meeting.getTimeEnd().split(" ")[TIME].split(":")[1]);
        return time;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(mLocation == null ? 0 : mLocation.getLatitude(), mLocation == null ? 0 : mLocation.getLongitude())).title("Here!"));
        if (mLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }
    }

    public String formatTime(String time) {
        int input = Integer.valueOf(time);
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }
}