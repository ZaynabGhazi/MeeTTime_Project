package com.zaynab.meettime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zaynab.meettime.Fragments.LaunchFragment;
import com.zaynab.meettime.Fragments.ProfileFragment;
import com.zaynab.meettime.Fragments.TimelineFragment;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    final FragmentManager mFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_timeline:
                        Toast.makeText(MainActivity.this, "Timeline!", Toast.LENGTH_SHORT).show();
                        fragment = new TimelineFragment();
                        break;
                    case R.id.action_launch:
                        fragment = new LaunchFragment();
                        Toast.makeText(MainActivity.this, "Launch!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_calendar:
                        fragment = new Fragment();
                        Toast.makeText(MainActivity.this, "Calendar!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fragment = new TimelineFragment();
                        Toast.makeText(MainActivity.this, "Timeline!", Toast.LENGTH_SHORT).show();
                        break;
                }
                mFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        mBottomNavigationView.setSelectedItemId(R.id.action_timeline);
    }
}