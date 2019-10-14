package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.ViewPagerAdapterAssociationProfile;
import com.example.applicationsecond.adapters.ViewPagerAdapterFollowedProjectsAndAssociations;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AssociationProfileActivity extends AppCompatActivity {

    @BindView(R.id.follow_button)
    ImageButton followButton;
    //@BindView(R.id.textview_follow) TextView textViewFollow;
    @BindView(R.id.image_view_association_profile_activity)
    ImageView imageViewAssociationProfile;
    @BindView(R.id.text_view_association_name_association_profile_activity) TextView textViewAssociationName;
    @BindView(R.id.association_profile_activity_text_view_city_country) TextView textViewCityCountry;
    @BindView(R.id.activity_association_profile_tabs)
    TabLayout tabLayout;
    @BindView(R.id.activity_association_profile_viewpager)
    ViewPager viewPager;
    //--------------------------------
    private boolean isButtonClicked;
    private String authorId;
    private String currentUserId;
    private ColorStateList defaultColor;
    private ActionBar actionBar;
    private SharedPreferences preferences;
    //--------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String ASSOCIATION_ID = "associationId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_profile);

        System.out.println("on create asso prof");

        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        currentUserId = Utils.getCurrentUser().getUid();
        Bundle bundle = getIntent().getExtras();
        authorId = null;
        if (bundle != null) {
            authorId = bundle.getString("authorId");
            displayAssociationInformation(this);
            preferences.edit().putString(ASSOCIATION_ID, authorId).apply();
        }
        configureToolbar();
        configureViewPager();
        hideFollowButtonIfItsCurrentUserProfile();
        displayStateOfFollowButtonDependingOnIfTheUserFollowsOrNot();

    }

    //------------------------------
    //CONFIGURATION
    //-----------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        //actionBar.setTitle();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureViewPager() {
        viewPager.setAdapter(new ViewPagerAdapterAssociationProfile(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    //----------------------------------------
    //ACTIONS
    //--------------------------------------------
    @OnClick(R.id.follow_button)
    public void followThisAssociation() {
        isButtonClicked = !isButtonClicked;
        if (isButtonClicked) {
            //change the color of the button
            followButton.setImageResource(R.drawable.ic_heart_red);

            //create a subscription in user s tuple
            UserHelper.updateAssociationSubscriptions(currentUserId, authorId);

            //display toast message to tell the user he s now following this association
            Toast.makeText(this, "You are now following " + authorId, Toast.LENGTH_SHORT).show();
        } else {
            //unclick button
            UserHelper.removeAssociationSubscription(currentUserId, authorId);
            //change button color
            //followButton.setBackground(getResources().getDrawable(R.drawable.ic_heart));
            followButton.setImageResource(R.drawable.ic_heart);
            //textViewFollow.setTextColor(defaultColor);
            Toast.makeText(this, "You are not following " + authorId + " anymore", Toast.LENGTH_SHORT).show();
        }
    }

    //----------------------------------------
    //METHODS
    //----------------------------------------
    private void hideFollowButtonIfItsCurrentUserProfile() {
        //check if it s current user s profile
        if (currentUserId.equals(authorId)) {
            followButton.setEnabled(false);
            //followButton.setBackground(getResources().getDrawable(R.drawable.ic_heart_disabled));
            followButton.setImageResource(R.drawable.ic_heart_disabled);
            //textViewFollow.setTextColor(getResources().getColor(R.color.disabledColor));
        } else {
            followButton.setEnabled(true);
        }
    }

    private void displayStateOfFollowButtonDependingOnIfTheUserFollowsOrNot(){
        //check if current user is following the association
        UserHelper.getUser(currentUserId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                if (user.getAssociationSubscribedId().contains(authorId)) {
                    //set the color of the button to red
                    //followButton.setBackground(getResources().getDrawable(R.drawable.ic_heart_red));
                    followButton.setImageResource(R.drawable.ic_heart_red);
                    //textViewFollow.setTextColor(getResources().getColor(R.color.red));
                    isButtonClicked = true;
                } else {
                    //followButton.setBackground(getResources().getDrawable(R.drawable.ic_heart));
                    followButton.setImageResource(R.drawable.ic_heart);
                    //textViewFollow.setTextColor(defaultColor);
                }
            }
        });
    }

    //------------------------------
    //UPDATE UI
    //-------------------------------
    private void displayAssociationInformation(Context context) {
        if (authorId != null) {
            UserHelper.getUser(authorId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User association = task.getResult().toObject(User.class);
                        if (association.getUrlPhoto() != null) {
                            Glide.with(context) //SHOWING PREVIEW OF IMAGE
                                    .load(association.getUrlPhoto())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imageViewAssociationProfile);
                        }

                        textViewAssociationName.setText(association.getUsername());
                        actionBar.setTitle(association.getUsername());
                        if (association.getCountry() != null && association.getCity() != null) {
                            textViewCityCountry.setText(association.getCity() + ", " + association.getCountry());
                        } else if (association.getCountry() != null && association.getCity() == null) {
                            textViewCityCountry.setText(association.getCountry());
                        } else if (association.getCountry() == null && association.getCity() != null) {
                            textViewCityCountry.setText(association.getCity());
                        } else if (association.getCountry() == null && association.getCity() == null) {
                            textViewCityCountry.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }
}
