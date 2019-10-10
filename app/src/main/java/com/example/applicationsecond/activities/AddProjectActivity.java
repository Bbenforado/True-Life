package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AddProjectActivity extends AppCompatActivity {

    @BindView(R.id.input_title_add_project_activity)
    EditText titleEditText;
    @BindView(R.id.input_description_add_project_activity) EditText descriptionEditText;
    @BindView(R.id.button_publish_add_project_activity)
    Button buttonPublish;
    @BindView(R.id.button_save_for_later_add_project_activity) Button buttonSaveProject;
    @BindView(R.id.button_add_picture_add_project_activity) Button buttonAddPicture;
    @BindView(R.id.image_chosen_picture_add_project_activity)
    ImageView imageView;
    //-------------------------------------------
    //--------------------------------------------
    private boolean isPublished;
    private SharedPreferences preferences;
    private String projectId;
    private Uri uriImageSelected;
    //----------------------------------------------
    //-----------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String KEY_EDIT_PROJECT = "keyEditproject";
    public static final String PROJECT_ID = "projectId";
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        configureToolbar();
        //check if it s for editing one existing project
        //if 0 it s not
        //if 1 it is
        if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
            //update ui with existing data on this project
            projectId = getIntent().getExtras().getString(PROJECT_ID);
            updateUiWithProjectsData(projectId);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        preferences.edit().putInt(KEY_EDIT_PROJECT, -1).apply();
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

    //-------------------------------------------------
    //CONFIGURATION
    //-------------------------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
            actionBar.setTitle("Edit your project");
        } else {
            actionBar.setTitle("Create a project");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //------------------------------------------------
    //ACTIONS
    //----------------------------------------------------
    @OnClick(R.id.button_publish_add_project_activity)
    public void publishProject() {
        isPublished = true;

        if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
            updateProjectInFireBase();
            Toast.makeText(this, "Project updated!", Toast.LENGTH_SHORT).show();
        } else {
            saveProjectInFireBase();
            Toast.makeText(this, "Project published!", Toast.LENGTH_SHORT).show();
        }

        finish();

    }

    @OnClick(R.id.button_save_for_later_add_project_activity)
    public void saveProjectForLater() {
        isPublished = false;
        saveProjectInFireBase();
        Toast.makeText(this, "Project saved for later!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.button_add_picture_add_project_activity)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void addPicture() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        String[] wayToGetPicture = {"Take from gallery", "Take from camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(wayToGetPicture, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        chooseImageFromPhone();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "nothing for the moment", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //--------------------------------------------------
    //
    //----------------------------------------------------
    private void handleResponseForGallery(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();

                System.out.println("here uri is = " + uriImageSelected);

                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageView);
            } else {
                Toast.makeText(this, "No image chosen", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //------------------------------------------
    //METHODS
    //-------------------------------------------
    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }


    //------------------------------------------
    //FIREBASE METHODS
    //------------------------------------------
    private void saveProjectInFireBase() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String authorId = Utils.getCurrentUser().getUid();
        Date creation_date = new Date();

        System.out.println("and here?");

        if (imageView.getDrawable() == null) {

            System.out.println("come here?");
            //means there is no photo saved for the project
            CollectionReference ref = FirebaseFirestore.getInstance().collection("projects");
            String projectId = ref.document().getId();

            if (isPublished) {
                ProjectHelper.createProject(projectId, title, description, authorId, creation_date, true);
            } else {
                ProjectHelper.createProject(projectId, title, description, authorId, creation_date, false);
            }
        } else {
            System.out.println("else here");
            uploadPhotoInFirebaseAndSaveproject(title, description, authorId, creation_date);
        }
    }

    private void uploadPhotoInFirebaseAndSaveproject(final String title, final String description, final String authorId, final Date creation_date) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference filePath = FirebaseStorage.getInstance().getReference(uuid);

        filePath.putFile(uriImageSelected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("LOG URI", "onSuccess: uri= "+ uri.toString());

                        CollectionReference ref = FirebaseFirestore.getInstance().collection("projects");
                        String idProject = ref.document().getId();

                        ProjectHelper.createprojectWithImage(idProject, title, description, authorId, creation_date, true, uri.toString());
                    }
                });
            }
        });
    }

    private void updateProjectInFireBase() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        ProjectHelper.updateTitle(projectId, title);
        ProjectHelper.updateDescription(projectId, description);
    }

    //---------------------------------------
    //UPDATE UI
    //-----------------------------------------
    private void updateUiWithProjectsData(String projectId) {
        ProjectHelper.getProject(projectId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Project project = task.getResult().toObject(Project.class);
                    titleEditText.setText(project.getTitle());
                    if (project.getDescription() != null) {
                        descriptionEditText.setText(project.getDescription());
                    }
                }
            }
        });
    }
}
