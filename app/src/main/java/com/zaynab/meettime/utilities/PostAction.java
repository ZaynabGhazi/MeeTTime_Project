package com.zaynab.meettime.utilities;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Post;

public class PostAction {
    public void setupDoubleTapLike(View itemView, Post post, ImageView ivLikes) {
        itemView.setOnTouchListener(new OnDoubleTapListener(itemView.getContext()) {
            @Override
            public void onDoubleTap(MotionEvent e) {
                enableLiking(post, ivLikes);
            }
        });
    }

    private void enableLiking(Post post, ImageView ivLikes) {
        boolean liked = false;
        try {
            ParseUser usr = post.getLikes().getQuery().whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId()).getFirst();
            if (usr != null) liked = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!liked) {
            likePost(post, ivLikes);
        } else
            unlikePost(post, ivLikes);

    }

    private void likePost(Post post, ImageView ivLikes) {
        post.getLikes().add(ParseUser.getCurrentUser());
        post.setLikesCount(post.getLikesCount() + 1);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ivLikes.setImageResource(R.drawable.ufi_heart_active);
                }
            }
        });
    }

    private void unlikePost(Post post, ImageView ivLikes) {
        post.getLikes().remove(ParseUser.getCurrentUser());
        post.setLikesCount(post.getLikesCount() - 1);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ivLikes.setImageResource(R.drawable.ufi_heart);
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
}
