package com.zaynab.meettime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zaynab.meettime.models.Comment;
import com.zaynab.meettime.models.Meeting;
import com.zaynab.meettime.models.Post;
import com.zaynab.meettime.utilities.CommentsAdapter;
import com.zaynab.meettime.utilities.PostsAdapter;
import com.zaynab.meettime.utilities.TimeFormatter;
import com.zaynab.meettime.utilities.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {
    public static final String TAG = "POST_DETAILS";
    protected RecyclerView mRvComments;
    protected CommentsAdapter mAdapter;
    protected List<Comment> mAllComments;

    private ImageView mIvProfile;
    private ImageView mIvCurrentUser;
    private TextView mTvCaption;
    private TextView mTvTitle;
    private ImageView mIvBackground;
    private ImageView mIvLike;
    private ImageView mIvShare;
    private TextView mTvTimestamp;
    private TextInputEditText mEtComment;
    private TextInputLayout mEtCommentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        Post post = (Post) bundle.getSerializable("post");
        bindView();
        populateView(post);
        setupList();
        loadComments(post);
        addComment(post);
    }


    private void bindView() {
        mRvComments = findViewById(R.id.rvComments);
        mIvProfile = findViewById(R.id.ivProfile);
        mTvCaption = findViewById(R.id.tvCaption);
        mTvTitle = findViewById(R.id.tvTitle);
        mIvBackground = findViewById(R.id.ivBackground);
        mIvLike = findViewById(R.id.ivLike);
        mIvShare = findViewById(R.id.ivShare);
        mTvTimestamp = findViewById(R.id.tvTimestamp);
        mIvCurrentUser = findViewById(R.id.ivCommenter);
        mEtComment = findViewById(R.id.etComment);
        mEtCommentLayout = findViewById(R.id.etCommentLayout);
        Glide.with(this).load(ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl()).placeholder(R.drawable.profile_icon).apply(RequestOptions.circleCropTransform()).into(mIvCurrentUser);

    }

    private void populateView(Post post) {
        String caption = (post.getOwner().getUsername().equals(ParseUser.getCurrentUser().getUsername())) ? "You" : post.getOwner().getUsername();
        if (post.isLaunched()) {
            caption += " launched a new event";
        } else {
            caption += " joined a new event";

        }
        mTvCaption.setText(caption);
        mTvTitle.setText(post.getMeeting().getTitle());
        mTvTimestamp.setText((TimeFormatter.getTimeDifference(post.getCreatedAt().toString()).equals("Just now")) ? TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) : TimeFormatter.getTimeDifference(post.getCreatedAt().toString()) + " ago");
        ParseFile profile_image = post.getOwner().getParseFile("profilePicture");
        if (profile_image != null)
            Glide.with(this).load(profile_image.getUrl()).placeholder(R.drawable.profile_icon).apply(RequestOptions.circleCropTransform()).into(mIvProfile);
        ParseFile bg_image = post.getMeeting().getParseFile("bgPicture");
        if (bg_image != null)
            Glide.with(this).load(bg_image.getUrl()).placeholder(R.drawable.placeholder).into(mIvBackground);
    }

    private void setupList() {
        mRvComments = findViewById(R.id.rvComments);
        mAllComments = new ArrayList<>();
        mAdapter = new CommentsAdapter(PostDetailsActivity.this, mAllComments);
        mRvComments.setAdapter(mAdapter);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        mRvComments.setLayoutManager(llManager);
    }

    private void loadComments(Post post) {
        ParseRelation comments = post.getComments();
        ParseQuery<Comment> query = comments.getQuery();
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error fetching comments!", e);
                    return;
                }
                mAllComments.addAll(objects);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addComment(Post post) {
        mEtCommentLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                comment.setOwner(ParseUser.getCurrentUser());
                comment.setText(mEtComment.getText().toString());
                comment.setPost(post);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error saving comment", e);
                            return;
                        }
                        post.getComments().add(comment);
                        post.setCommentsCount(post.getCommentsCount() + 1);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    mEtComment.setText("");
                                Toast.makeText(PostDetailsActivity.this, "comment added!", Toast.LENGTH_SHORT).show();
                                hideKeyboard(PostDetailsActivity.this);
                                mAllComments.add(comment);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}