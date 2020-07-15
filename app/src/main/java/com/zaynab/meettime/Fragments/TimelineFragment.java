package com.zaynab.meettime.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Post;
import com.zaynab.meettime.utilities.EndlessRecyclerViewScrollListener;
import com.zaynab.meettime.utilities.PostsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {
    public static final String TAG = "TIMELINE_FRAGMENT";
    protected RecyclerView mRvPosts;
    protected PostsAdapter mAdapter;
    protected SwipeRefreshLayout mSwipeContainer;
    private EndlessRecyclerViewScrollListener mScrollListener;
    protected List<Post> mAllPosts;

    public TimelineFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        queryPosts();
        make_refreshOnSwipe();


    }

    private void bindView(View view) {
        mRvPosts = view.findViewById(R.id.rvPosts);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mAllPosts = new ArrayList<>();
        PostsAdapter.OnClickListener clickListener = new PostsAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Log.i(TAG, "Post clicked at position " + position);
            }
        };
        mAdapter = new PostsAdapter(getContext(), mAllPosts, clickListener);
        mRvPosts.setAdapter(mAdapter);
        LinearLayoutManager llManager = new LinearLayoutManager(getContext());
        mRvPosts.setLayoutManager(llManager);
        mScrollListener = new EndlessRecyclerViewScrollListener(llManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //last post
                Log.i(TAG, "Infinite pagination activated!");
                Post last = mAllPosts.get(mAllPosts.size() - 1);
                fetchOlderContent(last);
            }
        };
        mRvPosts.addOnScrollListener(mScrollListener);
    }

    protected void fetchOlderContent(Post last) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
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

    private void make_refreshOnSwipe() {
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(20);
            }
        });
    }

    protected void populateTimeline(int i) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("owner");
        query.setLimit(i);
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

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("owner");
        query.setLimit(20);
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
                mAllPosts.addAll(posts);
                mAdapter.notifyDataSetChanged();
            }
        });

    }
}