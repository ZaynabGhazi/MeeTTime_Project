package com.zaynab.meettime.Fragments;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zaynab.meettime.models.Post;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileFragment extends TimelineFragment {
    public static final String TAG = "PROFILE_FRAGMENT";

    @Override
    protected void fetchOlderContent(Post last) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("owner");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.include("createdAt");
        query.whereLessThan("createdAt", last.getCreatedAt());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.");
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getCaption() + ", username: " + post.getOwner().getUsername());
                }
                mAdapter.addAll(posts);
            }
        });
    }

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("owner");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.");
                    return;
                }
                mProgressBar.setVisibility(View.GONE);
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getCaption() + ", username: " + post.getOwner().getUsername());
                }
                mAllPosts.addAll(posts);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void populateTimeline() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("owner");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts.");
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getCaption() + ", username: " + post.getOwner().getUsername());
                }
                mAdapter.clear();
                mAdapter.addAll(posts);
                mSwipeContainer.setRefreshing(false);
            }
        });
    }
}