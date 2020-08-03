package com.zaynab.meettime.utilities;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Post;

public class PostAction {
    public void setupDoubleTapLike(View itemView, Post post, ImageView ivLikes, TextView tvLikes) {
        itemView.setOnTouchListener(new OnDoubleTapListener(itemView.getContext()) {
            @Override
            public void onDoubleTap(MotionEvent e) {
                enableLiking(post, ivLikes, tvLikes);
            }
        });
    }

    private void enableLiking(Post post, ImageView ivLikes, TextView tvLikes) {
        boolean liked = false;
        try {
            ParseUser usr = post.getLikes().getQuery().whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId()).getFirst();
            if (usr != null) liked = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!liked) {
            likePost(post, ivLikes, tvLikes);
        } else
            unlikePost(post, ivLikes, tvLikes);

    }

    private void likePost(Post post, ImageView ivLikes, TextView tvLikes) {
        tvLikes.setVisibility(View.VISIBLE);
        post.getLikes().add(ParseUser.getCurrentUser());
        post.setLikesCount(post.getLikesCount() + 1);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ivLikes.setImageResource(R.drawable.ufi_heart_active);
                    int new_count = post.getLikesCount();
                    tvLikes.setText(new_count > 1 ? new_count + " likes" : new_count + " like");
                }
            }
        });
    }

    private void unlikePost(Post post, ImageView ivLikes, TextView tvLikes) {
        post.getLikes().remove(ParseUser.getCurrentUser());
        post.setLikesCount(post.getLikesCount() - 1);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ivLikes.setImageResource(R.drawable.ufi_heart);
                    int new_count = post.getLikesCount();
                    if (new_count == 0) {
                        tvLikes.setVisibility(View.GONE);
                    } else {
                        tvLikes.setText(new_count > 1 ? new_count + " likes" : new_count + " like");
                    }
                }
            }
        });
    }

    public void setupLikeIcon(Post post, ImageView ivLikes) {
        try {
            ParseUser usr = post.getLikes().getQuery().whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId()).getFirst();
            if (usr != null) ivLikes.setImageResource(R.drawable.ufi_heart_active);

        } catch (ParseException e) {
            ivLikes.setImageResource(R.drawable.ufi_heart);
            e.printStackTrace();
        }
    }

    public void showCount(Post post, TextView tvLikes) {
        int count = post.getLikesCount();
        if (count > 0) {
            tvLikes.setVisibility(View.VISIBLE);
            tvLikes.setText(count > 1 ? count + " likes" : count + " like");
        }
    }
}
