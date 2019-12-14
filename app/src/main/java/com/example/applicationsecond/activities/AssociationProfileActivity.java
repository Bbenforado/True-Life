package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
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

import static com.example.applicationsecond.utils.Utils.capitalizeFirstLetter;
import static com.example.applicationsecond.utils.Utils.formatLocation;
import static com.example.applicationsecond.utils.Utils.isNetworkAvailable;

public class AssociationProfileActivity extends AppCompatActivity {

    @BindView(R.id.follow_button)
    ImageButton followButton;
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
    private ActionBar actionBar;
    //--------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String ASSOCIATION_ID = "associationId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_profile);

        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
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
        if (isNetworkAvailable(this)) {
            UserHelper.getUser(authorId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        String username = capitalizeFirstLetter(user.getUsername());
                        if (isButtonClicked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                followButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_white));
                                followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkColor)));
                            } else {
                                followButton.setImageResource(R.drawable.ic_heart_red);
                            }
                            UserHelper.updateAssociationSubscriptions(currentUserId, authorId);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.now_following_toast) + " " + username, Toast.LENGTH_SHORT).show();
                        } else {
                            //unclick button
                            UserHelper.removeAssociationSubscription(currentUserId, authorId);
                            followButton.setImageResource(R.drawable.ic_heart);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                            }
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_following_toast)
                                    + " " + username + " " + getResources().getString(R.string.anymore_toast), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_try_again_toast), Toast.LENGTH_SHORT).show();
        }
    }

    //----------------------------------------
    //METHODS
    //----------------------------------------
    /**
     * check if it s the current users profile that is displayed, if yes, remove follow button
     */
    private void hideFollowButtonIfItsCurrentUserProfile() {
        if (currentUserId.equals(authorId)) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setEnabled(true);
        }
    }

    /**
     * Check if user is following the association, and display the color of the follow button
     * depending of this
     */
    private void displayStateOfFollowButtonDependingOnIfTheUserFollowsOrNot(){
        if (isNetworkAvailable(this)) {
            UserHelper.getUser(currentUserId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user.getAssociationSubscribedId() != null) {
                        if (user.getAssociationSubscribedId().contains(authorId)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                followButton.setImageResource(R.drawable.ic_heart_white);
                                followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkColor)));
                            } else {
                                followButton.setImageResource(R.drawable.ic_heart_red);
                            }
                            isButtonClicked = true;
                        } else {
                            followButton.setImageResource(R.drawable.ic_heart);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                            }
                        }
                    }
                }
            });
        }
    }

    //------------------------------
    //UPDATE UI
    //-------------------------------
    private void displayAssociationInformation(Context context) {
        if (authorId != null) {
            if (isNetworkAvailable(this)) {
                UserHelper.getUser(authorId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User association = task.getResult().toObject(User.class);
                            if (association.getUrlPhoto() != null) {
                                Glide.with(context)
                                        .load(association.getUrlPhoto())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(imageViewAssociationProfile);
                            }
                            textViewAssociationName.setText(capitalizeFirstLetter(association.getUsername()));
                            actionBar.setTitle(capitalizeFirstLetter(association.getUsername()));
                            if (association.getCountry() != null && association.getCity() != null) {
                                textViewCityCountry.setText(capitalizeFirstLetter(association.getCity()) + ", " + capitalizeFirstLetter(association.getCountry()));
                            } else if (association.getCountry() != null && association.getCity() == null) {
                                textViewCityCountry.setText(capitalizeFirstLetter(association.getCountry()));
                            } else if (association.getCountry() == null && association.getCity() != null) {
                                textViewCityCountry.setText(capitalizeFirstLetter(association.getCity()));
                            } else if (association.getCountry() == null && association.getCity() == null) {
                                textViewCityCountry.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }
    }
}
