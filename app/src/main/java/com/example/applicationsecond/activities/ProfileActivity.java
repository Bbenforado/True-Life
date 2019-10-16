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
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.google.android.material.button.MaterialButton;
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

import static com.example.applicationsecond.utils.Utils.capitalizeFirstLetter;

public class ProfileActivity extends AppCompatActivity {


    @BindView(R.id.profile_activity_picture)
    ImageView imageView;
    @BindView(R.id.profile_activity_change_picture_image_view) ImageView imageViewChangeProfilePicture;
    @BindView(R.id.profile_activity_username_button)
    MaterialButton userNameButton;
    @BindView(R.id.profile_activity_text_view_user_name) TextView textViewUsername;
    @BindView(R.id.activity_profile_viewpager)
    ViewPager viewPager;
    @BindView(R.id.activity_profile_tabs)
    TabLayout tabLayout;
    @BindView(R.id.activity_profile_text_view_country_city)
    TextView textViewCountryCity;
    @BindView(R.id.activity_profile_image_view_add_country_city) ImageView imageViewAddCountryCity;
    //-----------------------------------
    private Uri uri;
    private boolean isCurrentUsersProfile;
    private String authorId;
    private int defaultColor;
    private User currentUser;
    //------------------------------------
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        defaultColor = textViewCountryCity.getTextColors().getDefaultColor();

        Bundle bundle = getIntent().getExtras();
        authorId = null;
        if (bundle != null) {
            isCurrentUsersProfile = false;
            authorId = bundle.getString("profileId");
            displayUserInformation(this, authorId);
            configurationIfProfileIsNotCurrentUsersProfile();
        } else {
            getUserFromFireBase(Utils.getCurrentUser().getUid());
            displayUserInformation(this , Utils.getCurrentUser().getUid());
            isCurrentUsersProfile = true;
        }
        configureToolbar();
        configureViewPager();
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
    }

    //------------------------------
    //CONFIGURATION
    //-----------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (isCurrentUsersProfile) {
            actionBar.setTitle("Profile");
        } else {
            UserHelper.getUser(authorId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        actionBar.setTitle(user.getUsername());
                    }
                }
            });

        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureViewPager() {
        if (isCurrentUsersProfile) {
            viewPager.setAdapter(new ViewPagerAdapterFollowedProjectsAndAssociations(getSupportFragmentManager(), true));
        } else {
            viewPager.setAdapter(new ViewPagerAdapterFollowedProjectsAndAssociations(getSupportFragmentManager(), false, authorId));
        }
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    private void configurationIfProfileIsNotCurrentUsersProfile() {
        userNameButton.setVisibility(View.GONE);
        textViewUsername.setVisibility(View.VISIBLE);
        textViewCountryCity.setEnabled(false);
        textViewCountryCity.setTextColor(defaultColor);
        imageViewChangeProfilePicture.setVisibility(View.GONE);
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

    @OnClick(R.id.activity_profile_text_view_country_city)
    public void addACountryAndACity() {
        displayDialogToAddCountryAndCity();
    }

    //-------------------------------
    //METHODS
    //---------------------------------

    private void getUserFromFireBase(String id) {
        UserHelper.getUser(id).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    currentUser = task.getResult().toObject(User.class);
                }
            }
        });
    }
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

    private void displayDialogToAddCountryAndCity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_country_and_city, null);
        final TextInputEditText cityInputEditText = view.findViewById(R.id.text_input_new_city);
        final TextInputEditText countryInputEditText = view.findViewById(R.id.text_input_new_country);
        if (currentUser.getCity() != null) {
            cityInputEditText.setText(capitalizeFirstLetter(currentUser.getCity()));
        }
        if (currentUser.getCountry() != null) {
            countryInputEditText.setText(capitalizeFirstLetter(currentUser.getCountry()));
        }
        builder.setTitle("Add some information:")
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!TextUtils.isEmpty(cityInputEditText.getText())) {
                            UserHelper.updateCity(Utils.getCurrentUser().getUid(), cityInputEditText.getText().toString());
                            textViewCountryCity.setText(cityInputEditText.getText());
                        }
                        if (!TextUtils.isEmpty(countryInputEditText.getText())) {
                            UserHelper.updateCountry(Utils.getCurrentUser().getUid(), countryInputEditText.getText().toString());
                            textViewCountryCity.setText(countryInputEditText.getText());
                        }
                        if (!TextUtils.isEmpty(cityInputEditText.getText()) && !TextUtils.isEmpty(countryInputEditText.getText())) {
                            String finalString = cityInputEditText.getText().toString() + ", " + countryInputEditText.getText().toString();
                            textViewCountryCity.setText(finalString);
                        }

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

    private void displayUserInformation(Context context, String id) {
        UserHelper.getUser(id).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                    if (isCurrentUsersProfile) {
                        userNameButton.setText(user.getUsername());
                    } else {
                        textViewUsername.setText(user.getUsername());
                    }

                    if (user.getCountry() != null && user.getCity() != null) {
                        textViewCountryCity.setText(user.getCity() + ", " + user.getCountry());
                    } else if (user.getCountry() != null && user.getCity() == null) {
                        textViewCountryCity.setText(user.getCountry());
                    } else if (user.getCountry() == null && user.getCity() != null) {
                        textViewCountryCity.setText(user.getCity());
                    } else if (user.getCountry() == null && user.getCity() == null) {
                        imageViewAddCountryCity.setVisibility(View.VISIBLE);
                        if (isCurrentUsersProfile) {
                            textViewCountryCity.setText("Add a country and a city");
                        } else {
                            textViewCountryCity.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }
}
