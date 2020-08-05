package com.zaynab.meettime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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
                Intent intent = new Intent(SearchableActivity.this, MainActivity.class);
                intent.putExtra("userId", mAllUsers.get(position).getObjectId());
                startActivity(intent);
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
            queryUsers(query);
        }
    }

    protected void queryUsers(String name) {
        ParseQuery<ParseUser> queryUserName = ParseQuery.getQuery(ParseUser.class);
        queryUserName.whereContains("username", name);
        ParseQuery<ParseUser> queryFirstName = ParseQuery.getQuery(ParseUser.class);
        queryFirstName.whereContains("firstName", name);
        ParseQuery<ParseUser> queryLastName = ParseQuery.getQuery(ParseUser.class);
        queryLastName.whereContains("lastName", name);
        //make compound query:
        List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
        queries.add(queryUserName);
        queries.add(queryFirstName);
        queries.add(queryLastName);
        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseUser>() {
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