package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.PostHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.fragments.AddProjectFragment;
import com.example.applicationsecond.fragments.PostListFragment;
import com.example.applicationsecond.fragments.SearchFragment;
import com.example.applicationsecond.models.Post;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.applicationsecond.utils.Utils.getCurrentUser;
import static com.example.applicationsecond.utils.Utils.isCurrentUserLogged;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //BIND VIEWS
    //-------------------------------
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    //-----------------------------------
    //-----------------------------------
    private Toolbar toolbar;
    private ActualityListFragment actualityListFragment;
    private SearchFragment searchFragment;
    private AddProjectFragment addProjectFragment;
    private PostListFragment postListFragment;
    private User currentUser;
    private boolean isCurrentUserAssociation;
    private SharedPreferences preferences;
    //---------------------------------------
    public static final int RC_SIGN_IN = 123;
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String USER_ID = "userId";
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        actualityListFragment = new ActualityListFragment();
        searchFragment = new SearchFragment();
        addProjectFragment = new AddProjectFragment();
        postListFragment = new PostListFragment();

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (!isCurrentUserLogged()) {
            startSignInActivity();
        } else {
            getDataFromCurrentUser();
            //configure
            doBasicConfiguration();
            showFragment(actualityListFragment);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //------------------------------------
    //CONFIGURATION
    //--------------------------------------
    private void doBasicConfiguration() {
        configureToolbar();
        configureBottomNavigationViewListener();
        configureDrawerLayout();
        configureNavigationView();
    }

    private void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Projects");
    }

    private void configureBottomNavigationViewListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_projects:
                        showFragment(actualityListFragment);
                        actionBar.setTitle("Projects");
                        return true;
                    case R.id.navigation_posts:
                        showFragment(postListFragment);
                        actionBar.setTitle("Posts");
                        return true;
                    case R.id.navigation_add:
                        if (isCurrentUserAssociation) {
                            displayPostOrProjectDialog();
                        } else {
                            showFragment(addProjectFragment);
                            actionBar.setTitle("Add a project");
                        }
                        return true;
                    case R.id.navigation_map:
                        Toast.makeText(getApplicationContext(), "Clicked on map", Toast.LENGTH_SHORT).show();
                        actionBar.setTitle("Map");
                        return true;
                    case R.id.navigation_search:
                        showFragment(searchFragment);
                        actionBar.setTitle("Search");
                        return true;
                }
                return false;
            }
        });
    }

    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    //-------------------------------------------------
    //-------------------------------------------------
    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout_content_main_activity, fragment);
        transaction.commit();
    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_map)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {

                // check if user is already saved, if not ask if he s association
                displayDialogToAskIfUserIsAssociation();

            } else {
                if (response == null) {
                    finish();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.users_profile:
                launchActivity(ProfileActivity.class);
                break;
            case R.id.settings:
                Toast.makeText(this, "you clicked on settings!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.users_projects:
                launchActivity(UsersProjectsActivity.class);
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void launchActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void displayUsernameOnNavigationView() {
        if (isCurrentUserLogged()) {

        }
    }

    private void getDataFromCurrentUser() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                isCurrentUserAssociation = currentUser.isAssociation();
                String id = currentUser.getId();
                preferences.edit().putString(USER_ID, id).apply();
            }
        });
    }

    private void displayPostOrProjectDialog() {
        String[] choices = {"Post", "Project"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What do you want to create?")
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                displayCreatePostDialog();
                                break;
                            case 1:
                                showFragment(addProjectFragment);
                                actionBar.setTitle("Add a project");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void displayCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_post, null);
        builder.setTitle("Publish a post :")
                .setView(view)
                .setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText title = view.findViewById(R.id.input_title);
                        EditText content = view.findViewById(R.id.input_description);
                        String postTitle = title.getText().toString();
                        String postContent = content.getText().toString();
                        Date creationDate = new Date();
                        String authorName = Utils.getCurrentUser().getDisplayName();
                        PostHelper.createPost(postTitle, postContent, authorName, creationDate);

                        Toast.makeText(getApplication(), "Post published!", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNeutralButton("Save for later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Soon you'll be able to save your post to publish it later", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayDialogToAskIfUserIsAssociation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Are you an association?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //create user in firebase
                        UserHelper.createUser(getCurrentUser().getUid(), getCurrentUser().getDisplayName(), true);

                        isCurrentUserAssociation = true;
                        preferences.edit().putString(USER_ID, getCurrentUser().getUid()).apply();
                        doBasicConfiguration();
                        showFragment(actualityListFragment);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //create user in firebase
                        UserHelper.createUser(getCurrentUser().getUid(), getCurrentUser().getDisplayName(), false);

                        isCurrentUserAssociation = false;
                        preferences.edit().putString(USER_ID, getCurrentUser().getUid()).apply();
                        doBasicConfiguration();
                        showFragment(actualityListFragment);
                    }
                })
                .show();
    }

}
