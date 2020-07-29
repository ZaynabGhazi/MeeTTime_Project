package com.zaynab.meettime.utilities;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zaynab.meettime.Fragments.JoinDialogFragment;
import com.zaynab.meettime.Fragments.MeetingDetailsFragment;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.Post;
import com.zaynab.meettime.support.Logger;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_JOIN = 0;
    public static final int TYPE_VIEW = 1;

    private Context mContext;
    private List<Post> mPosts;
    private OnClickListener mClickListener;
    private Handler mHandler = new Handler();
    private int mType = 0;


    //communicate with fragment:
    public interface OnClickListener {
        void OnItemClicked(int position);
    }

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
        this.mContext = context;
        this.mPosts = posts;
        this.mClickListener = clickListener;

    }

    class JoinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvProfile;
        private TextView mTvCaption;
        private TextView mTvTitle;
        private ImageView mIvBackground;
        private ImageView mIvLike;
        private ImageView mIvComment;
        private ImageView mIvShare;
        private MaterialButton mBtnJoin;
        private TextView mTvTimestamp;

        public JoinViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvProfile = itemView.findViewById(R.id.ivProfile);
            mTvCaption = itemView.findViewById(R.id.tvCaption);
            mTvTitle = itemView.findViewById(R.id.tvTitle);
            mIvBackground = itemView.findViewById(R.id.ivBackground);
            mIvLike = itemView.findViewById(R.id.ivLike);
            mIvComment = itemView.findViewById(R.id.ivComment);
            mIvShare = itemView.findViewById(R.id.ivShare);
            mBtnJoin = itemView.findViewById(R.id.btnJoin);
            mTvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            itemView.setOnClickListener(this);
        }

        public void bind(final Post post) {
            String caption = (post.getOwner().getUsername().equals(ParseUser.getCurrentUser().getUsername())) ? "You" : post.getOwner().getUsername();
            if (post.isLaunched()) {
                caption += " launched a new event";
            } else {
                caption += " joined a new event";

            }
            mTvCaption.setText(caption);
            mTvTitle.setText(post.getMeeting().getTitle());
            mTvTimestamp.setText((TimeFormatter.getTimeDifference(post.getCreatedAt().toString()).equals("Just now")) ? TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) : TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");
            //correct heart icon:
            ParseFile profile_image = post.getOwner().getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvProfile);
            ParseFile bg_image = post.getMeeting().getParseFile("bgPicture");
            if (bg_image != null)
                Glide.with(mContext).load(bg_image.getUrl()).into(mIvBackground);
            setupJoin();
        }

        private void setupJoin() {
            mBtnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putSerializable("MEETING", mPosts.get(getAdapterPosition()).getMeeting());
                    JoinDialogFragment joinDialogFragment = new JoinDialogFragment();
                    joinDialogFragment.setArguments(b);
                    FragmentTransaction ft = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                    joinDialogFragment.show(ft, "dialog");
                }
            });
        }

        @Override
        public void onClick(View view) {
            mClickListener.OnItemClicked(getAdapterPosition());
        }


    }//end_VH_class_JOIN

    class ViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvProfile;
        private TextView mTvCaption;
        private TextView mTvTitle;
        private ImageView mIvBackground;
        private ImageView mIvLike;
        private ImageView mIvComment;
        private ImageView mIvShare;
        private MaterialButton mBtnView;
        private TextView mTvTimestamp;

        public ViewViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvProfile = itemView.findViewById(R.id.ivProfile);
            mTvCaption = itemView.findViewById(R.id.tvCaption);
            mTvTitle = itemView.findViewById(R.id.tvTitle);
            mIvBackground = itemView.findViewById(R.id.ivBackground);
            mIvLike = itemView.findViewById(R.id.ivLike);
            mIvComment = itemView.findViewById(R.id.ivComment);
            mIvShare = itemView.findViewById(R.id.ivShare);
            mBtnView = itemView.findViewById(R.id.btnViewDetails);
            mTvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            itemView.setOnClickListener(this);
        }

        public void bind(final Post post) {
            String caption = (post.getOwner().getUsername().equals(ParseUser.getCurrentUser().getUsername())) ? "You" : post.getOwner().getUsername();
            if (post.isLaunched()) {
                caption += " launched a new event";
            } else {
                caption += " joined a new event";

            }
            mTvCaption.setText(caption);
            mTvTitle.setText(post.getMeeting().getTitle());
            mTvTimestamp.setText((TimeFormatter.getTimeDifference(post.getCreatedAt().toString()).equals("Just now")) ? TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) : TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");
            //correct heart icon:
            ParseFile profile_image = post.getOwner().getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvProfile);
            ParseFile bg_image = post.getMeeting().getParseFile("bgPicture");
            if (bg_image != null)
                Glide.with(mContext).load(bg_image.getUrl()).into(mIvBackground);
            setupViewDetails();
        }

        private void setupViewDetails() {
            mBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putSerializable("MEETING", mPosts.get(getAdapterPosition()).getMeeting());
                    MeetingDetailsFragment meetingDetailsFragment = new MeetingDetailsFragment();
                    meetingDetailsFragment.setArguments(b);
                    ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, meetingDetailsFragment).addToBackStack(null).commit();
                }
            });
        }


        @Override
        public void onClick(View view) {
            mClickListener.OnItemClicked(getAdapterPosition());
        }
    }//end_VH_class_View

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_JOIN) {
            view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
            return new JoinViewHolder(view);

        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.post_item_joined, parent, false);
            return new ViewViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_JOIN) {
            ((JoinViewHolder) holder).bind(mPosts.get(position));
        } else {
            ((ViewViewHolder) holder).bind(mPosts.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    //Helper function for refresh-on-swipe container
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    //quick fix to recycled icons
    @Override
    public long getItemId(int position) {
        return position;
    }

    //ToDo: Do Parse query before fetching posts
    @Override
    public int getItemViewType(int position) {
        Meeting meeting = mPosts.get(position).getMeeting();
        meeting.fetchInBackground();
        ParseQuery<ParseUser> query = meeting.getAttendees().getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        try {
            query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

}//end-class

