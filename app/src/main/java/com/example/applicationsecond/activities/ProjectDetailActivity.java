package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.PostHelper;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.fragments.AddProjectFragment;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
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

public class ProjectDetailActivity extends AppCompatActivity {

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
    @BindView(R.id.project_detail_activity_author_button)
    Button authorButton;
    //-----------------------------------------
    //-------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    private static final String CLICKED_PROJECT = "clickedProject";
    public static final String KEY_EDIT_PROJECT = "keyEditproject";
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
            getCurrentUserInfoToDisplayButtonState(clickedProject.getId());
        }

        configureToolbar();
        updateUi(clickedProject);

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
            Toast.makeText(this, "You ll soon be able to edit this project!", Toast.LENGTH_SHORT).show();
            //launch add project activity with fields completed
            preferences.edit().putInt(KEY_EDIT_PROJECT, 1).apply();
            //then display the fragment
            AddProjectFragment fragment = new AddProjectFragment();
            //showFragment(fragment);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout_content_detail_activity, fragment);
        transaction.commit();
    }*/

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

    private void updateUi(Project project) {
        projectTitleTextView.setText(project.getTitle());
        if (project.getCreationDate() != null) {
            projectPublishedDateTextView.setText(project.getCreationDate().toString());
        }
        if (project.getEventDate() != null) {
            projectEventDateTextView.setText(project.getEventDate().toString());
        }
        if (project.getEndDate() != null) {
            projectEndDateTextView.setText(project.getEndDate().toString());
        }
        projectDescriptionTextView.setText(project.getDescription());
        if (project.getAuthorId() != null) {

            String buttonText = "From : " + project.getAuthorId();
            authorButton.setText(buttonText);
        }
    }
}
