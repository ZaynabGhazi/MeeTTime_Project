package com.zaynab.meettime.utilities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.zaynab.meettime.Fragments.JoinDialogFragment;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.Post;

import java.util.List;

import static com.zaynab.meettime.Fragments.JoinDialogFragment.TIME;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.ViewHolder> {
    private Context mContext;
    private List<Meeting> mMeetings;
    private MeetingsAdapter.OnClickListener mClickListener;

    //communicate with fragment:
    public interface OnClickListener {
        void OnItemClicked(int position);
    }

    public MeetingsAdapter(Context context, List<Meeting> meetings, OnClickListener clickListener) {
        this.mContext = context;
        this.mMeetings = meetings;
        this.mClickListener = clickListener;

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvChairPerson;
        private TextView mTvEventTitle;
        private TextView mTvEventTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvChairPerson = itemView.findViewById(R.id.ivChairPerson);
            mTvEventTitle = itemView.findViewById(R.id.tvTitleEvent);
            mTvEventTime = itemView.findViewById(R.id.tvTime);
            itemView.setOnClickListener(this);
        }

        public void bind(final Meeting meeting) throws ParseException {
            ParseFile profile_image = meeting.getChair().fetchIfNeeded().getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvChairPerson);
            mTvEventTitle.setText(meeting.getTitle());
            mTvEventTime.setText(meeting.getTimeStart().split(" ")[TIME] + " - " + meeting.getTimeEnd().split(" ")[TIME]);
        }


        @Override
        public void onClick(View view) {
            mClickListener.OnItemClicked(getAdapterPosition());
        }
    }//end_VH_class

    @NonNull
    @Override
    public MeetingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.calendar_meeting_item, parent, false);
        return new MeetingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meeting meeting = mMeetings.get(position);
        try {
            holder.bind(meeting);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    //Helper function for refresh-on-swipe container
    public void clear() {
        mMeetings.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Meeting> list) {
        mMeetings.addAll(list);
        notifyDataSetChanged();
    }

    //quick fix to recycled icons
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
