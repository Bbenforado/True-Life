package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ChatHelper;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.fragments.FollowersModalFragment;
import com.example.applicationsecond.models.Chat;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.applicationsecond.utils.Utils.capitalizeFirstLetter;
import static com.example.applicationsecond.utils.Utils.getCurrentUser;
import static com.example.applicationsecond.utils.Utils.getLatLngOfPlace;
import static com.example.applicationsecond.utils.Utils.isNetworkAvailable;

public class ProjectDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    //---------------------------------
    //BIND VIEWS
    //------------------------------------
    @BindView(R.id.activity_detail_fab) FloatingActionButton buttonFollowProject;
    @BindView(R.id.activity_detail_title) TextView projectTitleTextView;
    @BindView(R.id.activity_detail_publish_date) TextView projectPublishedDateTextView;
    @BindView(R.id.activity_detail_event_date) TextView projectEventDateTextView;
    @BindView(R.id.activity_detail_description) TextView projectDescriptionTextView;
    @BindView(R.id.activity_detail_street_number_and_name) TextView projectStreetNameAndStreetNumberTextView;
    @BindView(R.id.activity_detail_postal_code_and_city) TextView projectPostalCodeAndCityTextView;
    @BindView(R.id.activity_detail_country) TextView projectCountryTextView;
    @BindView(R.id.project_detail_activity_author_button) ImageView authorPhotoImageView;
    @BindView(R.id.image_detail_activity) ImageView imageView;
    @BindView(R.id.map_view_detail_activity) MapView mapView;
    @BindView(R.id.activity_detail_text_view_no_internet) TextView textViewNoInternet;
    @BindView(R.id.activity_detail_image_view_followers) ImageView imageViewFollowers;
    @BindView(R.id.activity_detail_nbr_of_followers) TextView textViewNbrOfFollowers;
    @BindView(R.id.detail_activity_layout_nbr_follower)
    LinearLayout layoutNbrFollower;
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
    private boolean isProjectFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        isProjectFinished = false;
        clickedProject = retrieveClickedProject();
        if (clickedProject.getId() != null) {
            displayFollowButton();
        }
        Date todayDate = new Date();
        long dateInMilliseconds = todayDate.getTime();
        if (clickedProject.getEventDate() < dateInMilliseconds) {
            Toast.makeText(this, "This project is finished!", Toast.LENGTH_SHORT).show();
            isProjectFinished = true;
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
            menu.findItem(R.id.toolbar_detail_project_delete).setVisible(false);
        }
        if (isProjectFinished) {
            menu.findItem(R.id.edit_item).setVisible(false);
        }

        if (!clickedProject.isPublished()) {
            menu.findItem(R.id.toolbar_detail_project_chat).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                //launch add project activity with fields completed
                preferences.edit().putInt(KEY_EDIT_PROJECT, 1).apply();

                Intent editIntent = new Intent(getApplicationContext(), AddProjectActivity.class);
                String projectId = clickedProject.getId();
                Bundle bundle = new Bundle();
                bundle.putString(PROJECT_ID, projectId);
                editIntent.putExtras(bundle);
                startActivity(editIntent);
                return true;
            case R.id.toolbar_detail_project_chat:
                openChatForTheProject();
                return true;
            case R.id.toolbar_detail_project_delete:
                //display dialog to ask if user is really sure he wants to delete the project
                displayDialogToDeleteProject(clickedProject.getId());
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
    //--------------------------------------
    //CONFIGURATION
    //-----------------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(clickedProject.getTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //--------------------------------------
    //ACTIONS
    //----------------------------------------
    //@OnClick(R.id.activity_detail_image_button_chat)
    public void openChatForTheProject() {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("chatName", clickedProject.getId());
        chatIntent.putExtras(bundle);
        startActivity(chatIntent);
    }

    @OnClick(R.id.detail_activity_layout_nbr_follower)
    public void displayNumberOfFollower() {
        //display modal bottom sheet that will display a list of follower
        FollowersModalFragment.newInstance(clickedProject.getId())
                .show(this.getSupportFragmentManager(), "MODAL");
    }

    @OnClick(R.id.project_detail_activity_author_button)
    public void launchAuthorProfileActivity() {

        //check if it s an association or a user
        String authorId = clickedProject.getAuthorId();
        UserHelper.getUser(authorId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user.isAssociation()) {
                        Intent authorProfileIntent = new Intent(getApplicationContext(), AssociationProfileActivity.class);
                        authorProfileIntent.putExtra("authorId", authorId);
                        startActivity(authorProfileIntent);
                    } else {
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        profileIntent.putExtra("profileId", authorId);
                        startActivity(profileIntent);
                    }
                }
            }
        });
    }

    @OnClick(R.id.activity_detail_fab)
    public void followTheProject() {
        isButtonClicked = !isButtonClicked;
        //String userUid = getCurrentUser().getUid();
        if (isButtonClicked) {
            //change the color of the button
            buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkColor)));
            buttonFollowProject.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white));
            //create subscription, maybe list of projects Id in user, so, update a field in user
            String userId = Utils.getCurrentUser().getUid();
            if (clickedProject.getId() != null) {
                String projectId = clickedProject.getId();
                ProjectHelper.addSubscriptionToProject(projectId, userId);
                UserHelper.addProjectsSubscriptions(userId, projectId);
                ChatHelper.addInvolvedUser(projectId, userId);
            }
            int nbrFollower = Integer.parseInt(textViewNbrOfFollowers.getText().toString()) + 1;
            textViewNbrOfFollowers.setText(String.valueOf(nbrFollower));
            Toast.makeText(this, "You are taking part in this project! You can contact the team through the chat!", Toast.LENGTH_SHORT).show();

        } else {
            //unclick button
            //delete the field with the projects id in the user tuple. delete the subscription
            String userId = Utils.getCurrentUser().getUid();
            if (clickedProject.getId() != null) {
                String projectId = clickedProject.getId();
                ProjectHelper.removeProjectSubscription(projectId, userId);
                UserHelper.removeProjectSubscription(userId, projectId);
                ChatHelper.removeInvolvedUser(projectId, userId);
            }
            //change button color
            buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            buttonFollowProject.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
            int nbrFollower = Integer.parseInt(textViewNbrOfFollowers.getText().toString()) - 1;
            textViewNbrOfFollowers.setText(String.valueOf(nbrFollower));

            Toast.makeText(this, "You are not part of this project anymore", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.image_detail_activity)
    public void displayFullScreenImage() {
        if (clickedProject.getUrlPhoto() != null) {
            Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
            intent.putExtra("image_url", clickedProject.getUrlPhoto());
            startActivity(intent);
        }

    }

    //------------------------------------------
    //--------------------------------------------
    private void displayDialogToDeleteProject(String projectId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to delete this project?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProjectHelper.deleteProject(projectId);
                        ChatHelper.deleteChat(projectId);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void displayColorButton(User user, String projectId) {
        //check if user subscribed to this project maybe
        if (user.getProjectsSubscribedId() != null) {
            if (user.getProjectsSubscribedId().contains(projectId)) {
                buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkColor)));
                buttonFollowProject.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white));
                isButtonClicked = true;
            } else {
                buttonFollowProject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                buttonFollowProject.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
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
        if (project.getUsersWhoSubscribed().size() > 0) {
            String numberOfFollowers = String.valueOf(project.getUsersWhoSubscribed().size());
            textViewNbrOfFollowers.setText(numberOfFollowers);
        } else {
            imageViewFollowers.setVisibility(View.GONE);
            textViewNbrOfFollowers.setVisibility(View.GONE);
        }
        projectTitleTextView.setText(capitalizeFirstLetter(project.getTitle()));
        if (project.getCreationDate() != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(project.getCreationDate());
            projectPublishedDateTextView.setText(date);
        }
        if (project.getEventDate() != 0) {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(project.getCreationDate());
            projectEventDateTextView.setText(date);
        }
        projectDescriptionTextView.setText(project.getDescription());
        if (project.getAuthorId() != null) {

            UserHelper.getUser(project.getAuthorId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (user.getUrlPhoto() != null) {
                            Glide.with(context)
                                    .load(user.getUrlPhoto())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(authorPhotoImageView);
                        } else {
                            authorPhotoImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_recolored));
                        }
                    }
                }
            });
        }

        if (project.getUrlPhoto() != null) {
            Glide.with(this)
                    .load(project.getUrlPhoto())
                    .into(this.imageView);
        } else {
            imageView.setVisibility(View.GONE);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) buttonFollowProject.getLayoutParams();
            params.setAnchorId(R.id.layout_yellow_info);
            buttonFollowProject.setLayoutParams(params);
        }

        if (project.getStreetNumber() != null && project.getStreetName() != null) {
            projectStreetNameAndStreetNumberTextView.setText(capitalizeFirstLetter(project.getStreetNumber() + " " + project.getStreetName()));
        }
        if (project.getCity() != null) {
            projectPostalCodeAndCityTextView.setText(project.getPostalCode() + " " + capitalizeFirstLetter(project.getCity()));
        }
        if (project.getCountry() != null) {
            projectCountryTextView.setText(capitalizeFirstLetter(project.getCountry()));
        }
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
                //Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                textViewNoInternet.setText("Location not found");
                textViewNoInternet.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
            }
        } else {
            textViewNoInternet.setVisibility(View.VISIBLE);
            mapView.setVisibility(View.GONE);
            //Toast.makeText(this, "You don't have internet, try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
