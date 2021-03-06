package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.MessageHelper;
import com.example.applicationsecond.api.PostHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.fragments.MapFragment;
import com.example.applicationsecond.fragments.PostListFragment;
import com.example.applicationsecond.fragments.SearchFragment;
import com.example.applicationsecond.models.Message;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Map;

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
    private PostListFragment postListFragment;
    private MapFragment mapFragment;
    private boolean isCurrentUserAssociation;
    private SharedPreferences preferences;
    private ActionBar actionBar;
    private int counterUnreadMessages;
    private TextView badgeChatTextView;
    //---------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String USER_ID = "userId";
    public static final String KEY_EDIT_PROJECT = "keyEditproject";
    public static final String COLLECTION_NAME_POSTS = "posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        actualityListFragment = new ActualityListFragment("defaultScreen");
        searchFragment = new SearchFragment();
        postListFragment = new PostListFragment(false);
        mapFragment = new MapFragment();
        counterUnreadMessages = 0;
        badgeChatTextView = (TextView) navigationView.getMenu().findItem(R.id.users_chats).getActionView();

        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        preferences.edit().putInt(KEY_EDIT_PROJECT, -1).apply();

        if (!isCurrentUserLogged()) {
            Intent authenticationIntent = new Intent(this, AuthenticationActivity.class);
            startActivity(authenticationIntent);
            this.finish();
        } else {
            getDataFromCurrentUser();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser.getLastChatVisit() != null) {
                    getAllUnreadMessagesForAllChats(currentUser.getLastChatVisit());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (Utils.getCurrentUser() != null) {
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_search:
                showFragment(searchFragment);
                actionBar.setTitle(getResources().getString(R.string.action_bar_title_search));
                return true;
            case R.id.toolbar_add:
                if (isCurrentUserAssociation) {
                    displayPostOrProjectDialog();
                } else {
                    Intent addProject = new Intent(getApplicationContext(), AddProjectActivity.class);
                    startActivity(addProject);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.users_profile:
                launchActivity(ProfileActivity.class);
                break;
            case R.id.users_projects:
                launchActivity(UsersProjectsActivity.class);
                break;
            case R.id.users_posts:
                launchActivity(PostListActivity.class);
                break;
            case R.id.sign_out:
                signOut();
                break;
            case R.id.users_chats:
                launchActivity(UsersChatActivity.class);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        actionBar.setTitle(getResources().getString(R.string.action_bar_title_projects));
    }

    private void configureBottomNavigationViewListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_projects:
                        showFragment(actualityListFragment);
                        actionBar.setTitle(getResources().getString(R.string.action_bar_title_projects));
                        return true;
                    case R.id.navigation_posts:
                        showFragment(postListFragment);
                        actionBar.setTitle(getResources().getString(R.string.action_bar_title_posts));
                        return true;
                    case R.id.navigation_map:
                        showFragment(mapFragment);
                        actionBar.setTitle(getResources().getString(R.string.action_bar_title_map));
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
        if (isCurrentUserAssociation) {
            navigationView.getMenu().findItem(R.id.users_posts).setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    //-------------------------------------------------
    //GET DATA
    //-------------------------------------------------
    private void getAllUnreadMessagesForAllChats(Map<String, Long> lastChatVisit) {
        counterUnreadMessages = 0;
            for (Map.Entry<String, Long> entry : lastChatVisit.entrySet()) {
                MessageHelper.getUnreadMessage(entry.getKey(), entry.getValue()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Message message = document.toObject(Message.class);
                                if (!message.getUserSender().getId().equals(getCurrentUser().getUid())) {
                                    counterUnreadMessages = counterUnreadMessages + 1;
                                }
                                if (counterUnreadMessages == 0) {
                                    badgeChatTextView.setVisibility(View.GONE);
                                } else {
                                    initializeCountDrawer(counterUnreadMessages);
                                    badgeChatTextView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
                if (counterUnreadMessages == 0) {
                    badgeChatTextView.setVisibility(View.GONE);
                }
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

                doBasicConfiguration();
                showFragment(actualityListFragment);
            }
        });
    }

    //--------------------------------------------------
    //METHODS
    //------------------------------------------------------
    /**
     * set the params for the text view that displays the number of unread messages beside "chat" in navigation drawer
     */
    private void initializeCountDrawer(int unreadMessages) {
        badgeChatTextView.setGravity(Gravity.CENTER);
        badgeChatTextView.setTypeface(null, Typeface.BOLD);
        badgeChatTextView.setTextColor(getResources().getColor(R.color.white));
        badgeChatTextView.setTextSize(10);
        badgeChatTextView.setBackground(getResources().getDrawable(R.drawable.text_view_counter_style));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.gravity = Gravity.CENTER;
        badgeChatTextView.setLayoutParams(params);
        badgeChatTextView.setText(String.valueOf(unreadMessages));
    }

    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout_content_main_activity, fragment);
        transaction.commitAllowingStateLoss();
    }

    private void launchActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    //------------------------------------------
    //DIALOG METHODS
    //----------------------------------------------
    /**
     * diaplay a dialog that ask to the user if he wants to create a post or a project
     * available only for associations
     */
    private void displayPostOrProjectDialog() {
        String[] choices = {"Post", "Project"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialog_create_post_or_project_title))
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                displayCreatePostDialog();
                                break;
                            case 1:
                                Intent addProject = new Intent(getApplicationContext(), AddProjectActivity.class);
                                startActivity(addProject);
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
        builder.setTitle(getResources().getString(R.string.dialog_publish_post_title))
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.publish), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        CollectionReference ref = FirebaseFirestore.getInstance().collection(COLLECTION_NAME_POSTS);
                        String postId = ref.document().getId();

                        EditText title = view.findViewById(R.id.input_title);
                        EditText content = view.findViewById(R.id.input_description);

                        if (!TextUtils.isEmpty(title.getText()) && !TextUtils.isEmpty(content.getText())) {
                            String postTitle = title.getText().toString();
                            String postContent = content.getText().toString();
                            Date creationDate = new Date();
                            UserHelper.getUser(Utils.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        User currentUser = task.getResult().toObject(User.class);
                                        String authorId = currentUser.getId();
                                        PostHelper.createPost(postId, postTitle, postContent, authorId, creationDate);
                                        //save the id of the post in the user
                                        UserHelper.updatePublishedPostIdList(Utils.getCurrentUser().getUid(), postId);
                                        Toast.makeText(getApplication(), getResources().getString(R.string.post_published_toast), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.field_missing), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .show();
    }
}
