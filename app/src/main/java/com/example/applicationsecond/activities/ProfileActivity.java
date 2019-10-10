package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
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
    //-----------------------------------
    private Uri uri;
    //------------------------------------
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

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

    private void handleResponseForGallery(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uri = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageView);

                uploadPhotoInFireBase();
            } else {
                Toast.makeText(this, "No image chosen", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //-------------------------------------------------
    //ACTIONS
    //---------------------------------------------------
    @OnClick(R.id.profile_activity_sign_out_button)
    public void signOut() {
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

    @OnClick(R.id.profile_activity_change_picture_image_view)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void changeProfilePicture() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        chooseImageFromPhone();

    }

    //-------------------------------
    //METHODS
    //---------------------------------
    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
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
                        Log.d("LOG URI", "onSuccess: uri= "+ uri.toString());
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

                }
            }
        });
    }
}
