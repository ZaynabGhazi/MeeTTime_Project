package com.zaynab.meettime.Fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zaynab.meettime.PostDetailsActivity;
import com.zaynab.meettime.R;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.Post;
import com.zaynab.meettime.utilities.PostsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    public static final String TAG = "USER_FRAGMENT";
    private Context mContext;
    private ImageView mIvProfilePicture;
    private TextView mTvUsername;
    private TextView mTvFullName;
    private MaterialButton mBtnAdd;
    private RecyclerView mRvPosts;
    private PostsAdapter mAdapter;
    protected HashSet<String> mPostTypes;
    protected List<Post> mAllPosts;
    protected ProgressBar mProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();
        Bundle b = getArguments();
        ParseUser user;
        if (b.containsKey("post")) {
            Post post = (Post) b.getSerializable("post");
            user = post.getOwner();
        } else {
            String userId = b.getString("userId");
            try {
                user = ParseQuery.getQuery(ParseUser.class).whereEqualTo("objectId", userId).getFirst();
            } catch (ParseException e) {
                user = ParseUser.getCurrentUser();
                e.printStackTrace();
            }
        }
        bindView(view, user);

    }

    private void bindView(View v, ParseUser user) {
        mProgressBar = v.findViewById(R.id.progressbar);
        mIvProfilePicture = v.findViewById(R.id.ivProfilePicture);
        mTvUsername = v.findViewById(R.id.tvUsername);
        mTvFullName = v.findViewById(R.id.tvFullName);
        mRvPosts = v.findViewById(R.id.rvUserPosts);
        mBtnAdd = v.findViewById(R.id.btnAddFriend);
        mPostTypes = new HashSet<>();
        PostsAdapter.OnClickListener clickListener = new PostsAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Log.i(TAG, "Post clicked at position " + position);
                enableCommenting(mAllPosts.get(position));
            }
        };
        ParseUser.getCurrentUser()
                .getRelation("meetings")
                .getQuery()
                .findInBackground(new FindCallback<ParseObject>() {
                                      @Override
                                      public void done(List<ParseObject> objects, ParseException e) {
                                          if (e == null) {
                                              if (objects.size() > 0) {
                                                  for (ParseObject object : objects) {
                                                      mPostTypes.add(object.getObjectId());
                                                  }
                                              }
                                          }
                                          populateRecycler(clickListener, user);
                                      }//end_done
                                  }//end query
                );

    }

    private void populateRecycler(PostsAdapter.OnClickListener clickListener, ParseUser user) {
        mAllPosts = new ArrayList<>();
        mAdapter = new PostsAdapter(getContext(), mAllPosts, clickListener, mPostTypes);
        mRvPosts.setAdapter(mAdapter);
        LinearLayoutManager llManager = new LinearLayoutManager(getContext());
        mRvPosts.setLayoutManager(llManager);
        mProgressBar.setVisibility(View.VISIBLE);
        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            mBtnAdd.setVisibility(View.GONE);

        }
        populateView(user);
        queryPosts(user);
    }


    private void populateView(ParseUser user) {
        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e != null || object == null) {
                    return;
                }
                ParseFile profile_image = user.getParseFile("profilePicture");
                if (profile_image != null)
                    Glide.with(mContext).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(mIvProfilePicture);
                mTvUsername.setText("@" + user.getUsername());
                if (user.getString("firstName") != null)
                    mTvFullName.setText(user.getString("firstName") + " " + user.getString("lastName"));
            }
        });
    }

    protected void queryPosts(ParseUser user) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("owner");
        query.whereEqualTo("owner", user);
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

    private void enableCommenting(Post post) {
        Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}