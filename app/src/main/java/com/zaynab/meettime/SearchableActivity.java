package com.zaynab.meettime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zaynab.meettime.models.Post;
import com.zaynab.meettime.utilities.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private static final String TAG = "SEARCHABLE_ACTIVITY";
    protected RecyclerView mRvUsers;
    protected UsersAdapter mAdapter;
    protected List<ParseUser> mAllUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        bindView();
        handleIntent(getIntent());

    }

    private void bindView() {
        mRvUsers = findViewById(R.id.rvUsers);
        mAllUsers = new ArrayList<>();
        UsersAdapter.OnClickListener clickListener = new UsersAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Log.i(TAG, "Post clicked at position " + position);
            }
        };
        mAdapter = new UsersAdapter(SearchableActivity.this, mAllUsers, clickListener);
        mRvUsers.setAdapter(mAdapter);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        mRvUsers.setLayoutManager(llManager);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Toast.makeText(SearchableActivity.this, "searching for " + query, Toast.LENGTH_LONG).show();
            queryUsers(query);
        }
    }

    protected void queryUsers(String name) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("username", name);
        //query.whereEqualTo("firstName",name);
        //query.whereEqualTo("lastName",name);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting users.");
                    return;
                }
                for (ParseUser usr : users) {
                    Log.i(TAG, " username: " + usr.getUsername());
                }
                mAllUsers.addAll(users);
                mAdapter.notifyDataSetChanged();
            }
        });

    }
}