package com.zaynab.meettime.utilities;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.Fragments.JoinDialogFragment;
import com.zaynab.meettime.Fragments.MeetingDetailsFragment;
import com.zaynab.meettime.Fragments.UserFragment;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.Post;
import com.zaynab.meettime.support.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.zaynab.meettime.Fragments.JoinDialogFragment.DAY;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_JOIN = 0;

    private Context mContext;
    private List<Post> mPosts;
    private HashSet<String> mTypes;
    private OnClickListener mClickListener;


    //communicate with fragment:
    public interface OnClickListener {
        void OnItemClicked(int position);
    }

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListener, HashSet<String> types) {
        this.mContext = context;
        this.mPosts = posts;
        this.mClickListener = clickListener;
        this.mTypes = types;

    }

    /*
     * Two ViewHolders: JoinViewHolder and ViewViewHolder
     *
     */
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
        private TextView mTvLikesCount;
        private View mItemView;

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
            mTvLikesCount = itemView.findViewById(R.id.likesCount);
            mItemView = itemView;
            itemView.setOnClickListener(this);
        }

        public void bind(final Post post) throws ParseException {
            post.fetchInBackground();
            String caption = (post.getOwner().fetchIfNeeded().getUsername().equals(ParseUser.getCurrentUser().fetchIfNeeded().getUsername())) ? "You" : post.getOwner().fetchIfNeeded().getUsername();
            if (post.isLaunched()) {
                caption += " launched a new event";
            } else {
                caption += " joined a new event";

            }
            mTvCaption.setText(caption);
            post.getMeeting().fetchInBackground();
            mTvTitle.setText(post.getMeeting().getTitle());
            mTvTimestamp.setText((TimeFormatter.getTimeDifference(post.getCreatedAt().toString()).equals("Just now")) ? TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) : TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");

            ParseFile profile_image = post.getOwner().fetchIfNeeded().getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).placeholder(R.drawable.profile_icon).apply(RequestOptions.circleCropTransform()).into(mIvProfile);
            ParseFile bg_image = post.getMeeting().fetchIfNeeded().getParseFile("bgPicture");
            if (bg_image != null)
                Glide.with(mContext).load(bg_image.getUrl()).placeholder(R.drawable.placeholder).into(mIvBackground);
            setupJoin();
            enableCommenting();
            setupDetailedView();
            PostAction postAction = new PostAction();
            postAction.showCount(post, mTvLikesCount);
            postAction.setupLikeIcon(post, mIvLike);
            postAction.setupCommentIcon(post, mIvComment);
            postAction.setupDoubleTapLike(mItemView, post, mIvLike, mTvLikesCount);
            postAction.showUser(mPosts.get(getAdapterPosition()), mIvProfile, mContext);
        }


        private void setupDetailedView() {
            mIvBackground.setOnClickListener(new View.OnClickListener() {
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

        private void enableCommenting() {
            mIvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.OnItemClicked(getAdapterPosition());
                }
            });
        }

        private void setupJoin() {
            mBtnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (checkIfOutdated(mPosts.get(getAdapterPosition()).getMeeting())) {
                        Snackbar.make(view, "Oops! This meeting has already happened!", BaseTransientBottomBar.LENGTH_SHORT).show();
                    } else {
                        Bundle b = new Bundle();
                        b.putSerializable("MEETING", mPosts.get(getAdapterPosition()).getMeeting());
                        JoinDialogFragment joinDialogFragment = new JoinDialogFragment();
                        joinDialogFragment.setArguments(b);
                        FragmentTransaction ft = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                        joinDialogFragment.show(ft, "dialog");
                    }
                }
            });
        }

        private boolean checkIfOutdated(Meeting meeting) {
            String day = meeting.getTimeEnd();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            try {
                Date meeting_day = sdf.parse(day);
                Date current = new Date();
                return meeting_day.before(current);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void onClick(View view) {
            Log.d("ADAPTER", " clicked");
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
        private View mItemView;
        private TextView mTvLikesCount;

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
            mTvLikesCount = itemView.findViewById(R.id.likesCount);
            mItemView = itemView;
            itemView.setOnClickListener(this);
        }

        public void bind(final Post post) throws ParseException {
            String caption = (post.getOwner().fetchIfNeeded().getUsername().equals(ParseUser.getCurrentUser().getUsername())) ? "You" : post.getOwner().getUsername();
            if (post.isLaunched()) {
                caption += " launched a new event";
            } else {
                caption += " joined a new event";

            }
            mTvCaption.setText(caption);
            mTvTitle.setText(post.getMeeting().getTitle());
            mTvTimestamp.setText((TimeFormatter.getTimeDifference(post.getCreatedAt().toString()).equals("Just now")) ? TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) : TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");
            ParseFile profile_image = post.getOwner().fetchIfNeeded().getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvProfile);
            ParseFile bg_image = post.getMeeting().getParseFile("bgPicture");
            if (bg_image != null)
                Glide.with(mContext).load(bg_image.getUrl()).into(mIvBackground);
            setupViewDetails();
            enableCommenting();
            PostAction postAction = new PostAction();
            postAction.setupLikeIcon(post, mIvLike);
            postAction.setupCommentIcon(post, mIvComment);
            postAction.showCount(post, mTvLikesCount);
            postAction.setupDoubleTapLike(mItemView, post, mIvLike, mTvLikesCount);
            postAction.showUser(mPosts.get(getAdapterPosition()), mIvProfile, mContext);
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

        private void enableCommenting() {
            mIvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.OnItemClicked(getAdapterPosition());
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
            try {
                ((JoinViewHolder) holder).bind(mPosts.get(position));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                ((ViewViewHolder) holder).bind(mPosts.get(position));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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

    @Override
    public int getItemViewType(int position) {
        return mTypes.contains(mPosts.get(position).getMeeting().getObjectId()) ? 1 : 0;
    }

}//end-class

