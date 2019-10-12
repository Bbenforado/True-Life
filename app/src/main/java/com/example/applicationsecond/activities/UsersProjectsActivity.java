package com.example.applicationsecond.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.media.AudioAttributesCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersProjectsActivity extends AppCompatActivity {

    @BindView(R.id.activity_users_projects_viewpager)
    ViewPager viewPager;
    @BindView(R.id.activity_users_projects_tabs)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_projects);
        ButterKnife.bind(this);

        configureToolbar();
        configureViewPager();
    }

    private void configureViewPager() {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My projects");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
