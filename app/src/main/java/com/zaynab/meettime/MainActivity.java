package com.zaynab.meettime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zaynab.meettime.Fragments.AvailabilityFragment;
import com.zaynab.meettime.Fragments.CalendarFragment;
import com.zaynab.meettime.Fragments.EditProfileFragment;
import com.zaynab.meettime.Fragments.LaunchFragment;
import com.zaynab.meettime.Fragments.ProfileFragment;
import com.zaynab.meettime.Fragments.TimelineFragment;
import com.zaynab.meettime.Fragments.UserFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    final FragmentManager mFragmentManager = getSupportFragmentManager();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mAbDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationDrawer();
        setupBottomNavigation();
        if (getIntent().hasExtra("userId")) showUserFragment(getIntent().getStringExtra("userId"));
    }

    private void showUserFragment(String userId) {

        Bundle b = new Bundle();
        b.putSerializable("userId", userId);
        UserFragment userFragment = new UserFragment();
        userFragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, userFragment).addToBackStack(null).commit();
    }


    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        mAbDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mAbDrawerToggle);
        mAbDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigationView = (NavigationView) findViewById(R.id.nv);
        View hView = mNavigationView.inflateHeaderView(R.layout.nav_header);
        ImageView ivDrawer = hView.findViewById(R.id.drawerImage);
        TextView tvGreeting = hView.findViewById(R.id.tvGreeting);
        ParseUser current = ParseUser.getCurrentUser();
        current.fetchInBackground();
        ParseFile profile_image = current.getParseFile("profilePicture");
        if (profile_image != null)
            Glide.with(this.getApplicationContext()).load(profile_image.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivDrawer);
        tvGreeting.setText("Hi " + (current.getString("firstName") != null ? current.getString("firstName") : "") + "!");
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.editProfile:
                        mFragmentManager.beginTransaction().replace(R.id.flContainer, new EditProfileFragment()).addToBackStack(null).commit();
                        break;
                    case R.id.settings:
                        mFragmentManager.beginTransaction().replace(R.id.flContainer, new AvailabilityFragment()).addToBackStack(null).commit();
                        break;
                    case R.id.logOut:
                        ParseUser.logOut();
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });
    }

    private void setupBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_timeline:
                        fragment = new TimelineFragment();
                        break;
                    case R.id.action_launch:
                        fragment = new LaunchFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_calendar:
                        fragment = new CalendarFragment();
                        break;
                    default:
                        fragment = new TimelineFragment();
                        break;
                }
                mFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                return true;
            }
        });
        mBottomNavigationView.setSelectedItemId(R.id.action_timeline);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mAbDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName cn = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        searchView.setIconifiedByDefault(false);

        return true;
    }
}