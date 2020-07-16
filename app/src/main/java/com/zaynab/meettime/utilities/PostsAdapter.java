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
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.zaynab.meettime.Fragments.JoinDialogFragment;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private OnClickListener mClickListener;


    //communicate with fragment:
    public interface OnClickListener {
        void OnItemClicked(int position);
    }

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListener) {
        this.mContext = context;
        this.mPosts = posts;
        this.mClickListener = clickListener;

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvProfile;
        private TextView mTvCaption;
        private TextView mTvTitle;
        private ImageView mIvBackground;
        private ImageView mIvLike;
        private ImageView mIvComment;
        private ImageView mIvShare;
        private MaterialButton mBtnJoin;
        private TextView mTvTimestamp;

        public ViewHolder(@NonNull View itemView) {
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
            mTvTimestamp.setText(TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");
            //correct heart icon:
            ParseFile profile_image = post.getOwner().getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvProfile);
            ParseFile bg_image = post.getMeeting().getParseFile("bgPicture");
            if (bg_image != null)
                Glide.with(mContext).load(bg_image.getUrl()).into(mIvBackground);
            if (post.getOwner().getUsername().equals(ParseUser.getCurrentUser().getUsername()))
                mBtnJoin.setVisibility(View.GONE);
            else setupJoin();
        }

        private void setupJoin() {
            mBtnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putSerializable("MEETING", mPosts.get(getAdapterPosition()).getMeeting());
                    JoinDialogFragment joinDialogFragment = new JoinDialogFragment();
                    joinDialogFragment.setArguments(b);
                    //((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, joinDialogFragment).commit();

                    FragmentTransaction ft = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                    Fragment prev = ((AppCompatActivity) view.getContext()).getSupportFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    //JoinDialogFragment joinDialogFragment = new JoinDialogFragment();
                    joinDialogFragment.show(ft, "dialog");
                }
            });
        }

        @Override
        public void onClick(View view) {
            mClickListener.OnItemClicked(getAdapterPosition());
        }
    }//end_VH_class

    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
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
        return position;
    }
}
