package com.zaynab.meettime.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.zaynab.meettime.MainActivity;
import com.zaynab.meettime.R;
import com.zaynab.meettime.SearchableActivity;
import com.zaynab.meettime.models.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComments;


    //communicate with activity
    public interface OnClickListener {
        void OnItemClicked(int position);
    }

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.mContext = context;
        this.mComments = comments;

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvOwnerPicture;
        private TextView mTvOwnerFullName;
        private TextView mTvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvOwnerPicture = itemView.findViewById(R.id.ivUserPicture);
            mTvOwnerFullName = itemView.findViewById(R.id.tvFullName);
            mTvComment = itemView.findViewById(R.id.tvComment);
        }

        public void bind(final Comment comment) throws ParseException {
            ParseFile profile_image = comment.getOwner().fetchIfNeeded().getParseFile("profilePicture");
            if (profile_image != null)
                Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvOwnerPicture);
            if (comment.getOwner().fetchIfNeeded().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                mTvOwnerFullName.setText("You");

            } else {
                if (comment.getOwner().getString("firstName") != null)
                    mTvOwnerFullName.setText(comment.getOwner().fetchIfNeeded().getString("firstName") + " " + comment.getOwner().fetchIfNeeded().getString("lastName"))
                            ;
            }
            mTvComment.setText(comment.getText());
            mIvOwnerPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("userId", comment.getOwner().getObjectId());
                    mContext.startActivity(intent);
                }
            });
        }
    }//end_VH_class

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        try {
            holder.bind(comment);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    //Helper function for refresh-on-swipe container
    public void clear() {
        mComments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Comment> list) {
        mComments.addAll(list);
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
