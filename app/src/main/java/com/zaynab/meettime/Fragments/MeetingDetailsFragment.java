package com.zaynab.meettime.Fragments;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.MapView;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.support.Logger;
import com.zaynab.meettime.utilities.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingDetailsFragment extends Fragment {
    public static final String TAG = "MEETING_DETAILS_FRAGMENT";

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
    protected List<ParseUser> mAllAttendees;


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
        mMvLocation = v.findViewById(R.id.mvLocation);
        mRvAttendees = v.findViewById(R.id.rvAttendees);
        mIvAddFriends = v.findViewById(R.id.ivAddFriends);
        mTvAttendeesText = v.findViewById(R.id.tvAttendeesText);
    }

    private void populateView(Meeting meeting) {
        meeting.fetchInBackground();
        mAllAttendees = new ArrayList<>();
        ParseFile bg_image = meeting.getParseFile("bgPicture");
        if (bg_image != null)
            Glide.with(mContext).load(bg_image.getUrl()).into(mIvBackground);
        mTvTitle.setText(meeting.getTitle());
        //TODO: EDIT TIME DISPLAY + Zoom Link + Maps View
        mTvTime.setText(meeting.getTimeStart());
        mTvDesc.setText(meeting.getDescription());
        hideSchedule(meeting);
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
    }

    private void hideSchedule(Meeting meeting) {
        ParseRelation<ParseUser> attendees = meeting.getAttendees();
        final ParseQuery<ParseUser> isMember = attendees.getQuery();
        isMember.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            mBtnViewSchedule.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
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

}