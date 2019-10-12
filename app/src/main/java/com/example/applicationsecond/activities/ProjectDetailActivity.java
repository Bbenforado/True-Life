package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.applicationsecond.utils.Utils.getCurrentUser;
import static com.example.applicationsecond.utils.Utils.getLatLngOfPlace;
import static com.example.applicationsecond.utils.Utils.isNetworkAvailable;
import static java.security.AccessController.getContext;

public class ProjectDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    //---------------------------------
    //BIND VIEWS
    //------------------------------------
    @BindView(R.id.activity_detail_fab) FloatingActionButton buttonFollowProject;
    @BindView(R.id.activity_detail_title) TextView projectTitleTextView;
    @BindView(R.id.activity_detail_publish_date) TextView projectPublishedDateTextView;
    @BindView(R.id.activity_detail_event_date) TextView projectEventDateTextView;
    @BindView(R.id.activity_detail_end_date) TextView projectEndDateTextView;
    @BindView(R.id.activity_detail_description) TextView projectDescriptionTextView;
    @BindView(R.id.activity_detail_street_number_and_name) TextView projectStreetNameAndStreetNumberTextView;
    @BindView(R.id.activity_detail_complement) TextView projectComplementTextView;
    @BindView(R.id.activity_detail_postal_code_and_city) TextView projectPostalCodeAndCityTextView;
    @BindView(R.id.activity_detail_country) TextView projectCountryTextView;
    @BindView(R.id.project_detail_activity_author_button) ImageView authorPhotoImageView;
    @BindView(R.id.image_detail_activity) ImageView imageView;
    @BindView(R.id.map_view_detail_activity) MapView mapView;
    //-----------------------------------------
    //-------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    private static final String CLICKED_PROJECT = "clickedProject";
    public static final String KEY_EDIT_PROJECT = "keyEditproject";
    public static final String PROJECT_ID = "projectId";
    //---------------------------------------
    //------------------------------------------
    private SharedPreferences preferences;
    private boolean isButtonClicked;
    private Project clickedProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        clickedProject = retrieveClickedProject();
        if (clickedProject.getId() != null) {
            //getCurrentUserInfoToDisplayButtonState(clickedProject.getId());
            displayFollowButton();
        }

        configureToolbar();
        updateUi(clickedProject, this);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_project_detail, menu);

        //check if the user is the author of the project, if yes
        //if not, hide the item of the toolbar
        if (!clickedProject.getAuthorId().equals(Utils.getCurrentUser().getUid())) {
            menu.findItem(R.id.edit_item).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_item) {
            //launch add project activity with fields completed
            preferences.edit().putInt(KEY_EDIT_PROJECT, 1).apply();

            Intent editIntent = new Intent(getApplicationContext(), AddProjectActivity.class);
            String projectId = clickedProject.getId();
            Bundle bundle = new Bundle();
            bundle.putString(PROJECT_ID, projectId);
            editIntent.putExtras(bundle);
            startActivity(editIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(clickedProject.getTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.project_detail_activity_author_button)
    public void launchAuthorProfileActivity() {

        String authorId = clickedProject.getAuthorId();
        Intent authorProfileIntent = new Intent(getApplicationContext(), AssociationProfileActivity.class);
        authorProfileIntent.putExtra("authorId", authorId);
        startActivity(authorProfileIntent);
    }


    @OnClick(R.id.activity_detail_fab)
    public void followTheProject() {
        isButtonClicked = !isButtonClicked;
        //String userUid = getCurrentUser().getUid();
        if (isButtonClicked) {
            //change the color of the button
            buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            //create subscription, maybe list of projects Id in user, so, update a field in user
            String userId = Utils.getCurrentUser().getUid();
            if (clickedProject.getId() != null) {
                String projectId = clickedProject.getId();
                ProjectHelper.addSubscriptionToProject(projectId, userId);
            }
            Toast.makeText(this, "You are taking part in this project! You can contact the team through the chat!", Toast.LENGTH_SHORT).show();

        } else {
            //unclick button
            //delete the field with the projects id in the user tuple. delete the subscription
            String userId = Utils.getCurrentUser().getUid();
            if (clickedProject.getId() != null) {
                String projectId = clickedProject.getId();
                ProjectHelper.removeProjectSubscription(projectId, userId);
            }
            //change button color
            buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

            Toast.makeText(this, "You are not part of this project anymore", Toast.LENGTH_SHORT).show();
        }

    }

    private void displayColorButton(User user, String projectId) {

        //check if user subscribed to this project maybe
        if (user.getProjectsSubscribedId() != null) {
            if (user.getProjectsSubscribedId().contains(projectId)) {
                buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                isButtonClicked = true;
            } else {
                buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                isButtonClicked = false;
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void displayFollowButton() {
        if (clickedProject.getAuthorId().equals(Utils.getCurrentUser().getUid())) {
            buttonFollowProject.setVisibility(View.GONE);
        } else {
            getCurrentUserInfoToDisplayButtonState(clickedProject.getId());
        }
    }

    private void getCurrentUserInfoToDisplayButtonState(String projectId) {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                displayColorButton(currentUser, projectId);
            }
        });
    }

    private Project retrieveClickedProject() {
        Gson gson = new Gson();
        Project project = new Project();
        String json = preferences.getString(CLICKED_PROJECT, null);
        Type type = new TypeToken<Project>() {
        }.getType();

        project = gson.fromJson(json, type);
        return project;
    }

    private void updateUi(Project project, Context context) {
        projectTitleTextView.setText(project.getTitle());
        if (project.getCreationDate() != null) {
            projectPublishedDateTextView.setText(project.getCreationDate().toString());
        }
        if (project.getEventDate() != null) {
            projectEventDateTextView.setText(project.getEventDate());
        }
        if (project.getEndDate() != null) {
            projectEndDateTextView.setText(project.getEndDate().toString());
        }
        projectDescriptionTextView.setText(project.getDescription());
        if (project.getAuthorId() != null) {

            UserHelper.getUser(project.getAuthorId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        Glide.with(context)
                                .load(user.getUrlPhoto())
                                .apply(RequestOptions.circleCropTransform())
                                .into(authorPhotoImageView);
                    }
                }
            });
        }

        if (project.getUrlPhoto() != null) {
            Glide.with(this)
                    .load(project.getUrlPhoto())
                    .into(this.imageView);
        }

        projectStreetNameAndStreetNumberTextView.setText(project.getStreetNumber() + " " + project.getStreetName());
        if (project.getLocationComplement() != null) {
            projectComplementTextView.setText(project.getLocationComplement());
        } else {
            projectComplementTextView.setVisibility(View.GONE);
        }
        projectPostalCodeAndCityTextView.setText(project.getPostalCode() + " " + project.getCity());
        projectCountryTextView.setText(project.getCountry());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        if (isNetworkAvailable(getApplicationContext())) {

            if (clickedProject.getLatLng() != null) {

                String latLng = clickedProject.getLatLng();
                LatLng latLngOfAddress = getLatLngOfPlace(latLng);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngOfAddress, 16);
                googleMap.animateCamera(cameraUpdate);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLngOfAddress));

            } else {
                Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            //textViewNoInternet.setVisibility(View.VISIBLE);
            mapView.setVisibility(View.GONE);
            Toast.makeText(this, "You don't have internet, try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
