package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.ViewPagerAdapterFollowedProjectsAndAssociations;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfileActivity extends AppCompatActivity {


    @BindView(R.id.profile_activity_picture)
    ImageView imageView;
    @BindView(R.id.profile_activity_change_picture_image_view) ImageView imageViewChangeProfilePicture;
    @BindView(R.id.profile_activity_username_button)
    Button userNameButton;
    @BindView(R.id.activity_profile_viewpager)
    ViewPager viewPager;
    @BindView(R.id.activity_profile_tabs)
    TabLayout tabLayout;
    @BindView(R.id.activity_profile_text_view_country_city)
    TextView textViewCountryCity;
    //-----------------------------------
    private Uri uri;
    private SharedPreferences preferences;
    //------------------------------------
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String CODE_PROFILE_ACTIVITY = "codeProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        configureToolbar();
        configureViewPager();
        displayUserInformation(this );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseForGallery(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*preferences.edit().putInt(CODE_PROFILE_ACTIVITY, -1).apply();
        System.out.println("pref on stop  = " + preferences.getInt(CODE_PROFILE_ACTIVITY, -1));*/
    }

    //------------------------------
    //CONFIGURATION
    //-----------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureViewPager() {
        //preferences.edit().putInt(CODE_PROFILE_ACTIVITY, 1).apply();
        viewPager.setAdapter(new ViewPagerAdapterFollowedProjectsAndAssociations(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    //-------------------------------------------------
    //ACTIONS
    //---------------------------------------------------
    @OnClick(R.id.profile_activity_change_picture_image_view)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void changeProfilePicture() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        chooseImageFromPhone();

    }

    @OnClick(R.id.profile_activity_username_button)
    public void changeUsername() {
        displayDialogToChangeUsername();
    }

    //-------------------------------
    //METHODS
    //---------------------------------
    private void handleResponseForGallery(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                this.uri = data.getData();
                Glide.with(this)
                        .load(this.uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageView);

                uploadPhotoInFireBase();
            } else {
                Toast.makeText(this, "No image chosen", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void displayDialogToChangeUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile_activity_change_username, null);
        builder.setTitle("Change user name")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final TextInputEditText usernameInputEditText = view.findViewById(R.id.text_input_new_username);
                        userNameButton.setText(usernameInputEditText.getEditableText().toString());
                        UserHelper.updateUsername(Utils.getCurrentUser().getUid(), usernameInputEditText.getEditableText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    //------------------------------------
    //FIREBASE
    //--------------------------------------
    private void uploadPhotoInFireBase() {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference filePath = FirebaseStorage.getInstance().getReference(uuid);

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserHelper.updateUrlPhoto(Utils.getCurrentUser().getUid(), uri.toString());
                    }
                });
            }
        });
    }

    //-------------------------------
    //UPDATE UI
    //--------------------------------

    private void displayUserInformation(Context context) {
        UserHelper.getUser(Utils.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user.getUrlPhoto() != null) {
                        Glide.with(context)
                                .load(user.getUrlPhoto())
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageView);
                    }
                    userNameButton.setText(user.getUsername());

                    if (user.getCountry() != null && user.getCity() != null) {
                        textViewCountryCity.setText(user.getCity() + ", " + user.getCountry());
                    } else if (user.getCountry() != null && user.getCity() == null) {
                        textViewCountryCity.setText(user.getCountry());
                    } else if (user.getCountry() == null && user.getCity() != null) {
                        textViewCountryCity.setText(user.getCity());
                    } else if (user.getCountry() == null && user.getCity() == null) {
                        textViewCountryCity.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
