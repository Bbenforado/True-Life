package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ChatHelper;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.applicationsecond.utils.Utils.addZeroToDate;
import static com.example.applicationsecond.utils.Utils.getCurrentDate;
import static com.example.applicationsecond.utils.Utils.getEditTextValue;
import static com.example.applicationsecond.utils.Utils.isNetworkAvailable;
import static com.example.applicationsecond.utils.Utils.setDateOnButton;
import static com.example.applicationsecond.utils.Utils.setValueToEditText;

public class AddProjectActivity extends AppCompatActivity {

    @BindView(R.id.input_title_add_project_activity) EditText titleEditText;
    @BindView(R.id.input_description_add_project_activity) EditText descriptionEditText;
    @BindView(R.id.button_publish_add_project_activity) MaterialButton buttonPublish;
    @BindView(R.id.button_save_for_later_add_project_activity) MaterialButton buttonSaveProject;
    @BindView(R.id.button_add_picture_add_project_activity)
    MaterialButton buttonAddPicture;
    @BindView(R.id.image_chosen_picture_add_project_activity) ImageView imageView;
    @BindView(R.id.text_edit_street_nbr_add_project_activity) TextInputEditText streetNumberEditText;
    @BindView(R.id.edit_text_street_name_add_project_activity) TextInputEditText streetNameEditText;
    @BindView(R.id.text_edit_postal_code_add_project_activity) TextInputEditText postalCodeEditText;
    @BindView(R.id.text_edit_city_add_project_activity) TextInputEditText cityEditText;
    @BindView(R.id.text_edit_country_add_project_activity) TextInputEditText countryEditText;
    @BindView(R.id.spinner_button_event_date_add_project_activity) Button buttonEventDate;
    //-------------------------------------------
    //--------------------------------------------
    private boolean isPublished;
    private SharedPreferences preferences;
    private String projectId;
    private Uri uriImageSelected;
    private long eventDate;
    //----------------------------------------------
    //-----------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String KEY_EDIT_PROJECT = "keyEditproject";
    public static final String PROJECT_ID = "projectId";
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    public static final String COLLECTION_NAME = "projects";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        configureToolbar();

        System.out.println("pref = " + preferences.getInt(KEY_EDIT_PROJECT, -1));
        //check if it s for editing one existing project
        //if 0 it s not
        //if 1 it is
        if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
            //update ui with existing data on this project
            projectId = getIntent().getExtras().getString(PROJECT_ID);
            updateUiWithProjectsData(projectId, this);
            buttonSaveProject.setVisibility(View.GONE);
        } else {
            //display current date on the button to choose date project
            setDateOnButton(buttonEventDate, getCurrentDate());
        }
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
        preferences.edit().putInt(KEY_EDIT_PROJECT, -1).apply();
    }

    //-------------------------------------------------
    //CONFIGURATION
    //-------------------------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
            actionBar.setTitle(getResources().getString(R.string.toolbar_title_edit));
        } else {
            actionBar.setTitle(getResources().getString(R.string.toolbar_title_create_project));
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //------------------------------------------------
    //ACTIONS
    //----------------------------------------------------
    @OnClick(R.id.button_publish_add_project_activity)
    public void publishProject() {
        isPublished = true;
        if (isNetworkAvailable(this)) {
            if (fieldsAreCorrectlyFilled()) {
                if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
                    updateProjectInFireBase();
                    Toast.makeText(this, getResources().getString(R.string.project_updated_toast), Toast.LENGTH_SHORT).show();
                    launchActivity(MainActivity.class);
                } else {
                    saveProjectInFireBase();
                    launchActivity(MainActivity.class);
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.field_missing), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_try_again_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_save_for_later_add_project_activity)
    public void saveProjectForLater() {
        isPublished = false;
        if (isNetworkAvailable(this)) {
            if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
                if (fieldsAreCorrectlyFilled()) {
                    updateProjectInFireBase();
                    Toast.makeText(this, getResources().getString(R.string.project_updated_toast), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.field_missing), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (!TextUtils.isEmpty(titleEditText.getText()) && !TextUtils.isEmpty(descriptionEditText.getText())) {
                    saveProjectInFireBaseForNotPublishedProjects();
                    Toast.makeText(this, getResources().getString(R.string.project_saved_for_later_toast), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.field_missing), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_try_again_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_add_picture_add_project_activity)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void addPicture() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        chooseImageFromPhone();
    }

    @OnClick(R.id.spinner_button_event_date_add_project_activity)
    public void pickEventDate(View v) {
        createDatePickerDialog(v);
    }

    //--------------------------------------------------
    //
    //----------------------------------------------------
    private void handleResponseForGallery(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                this.uriImageSelected = data.getData();
                Glide.with(this)
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.imageView);
            }
        }
    }

    //------------------------------------------
    //METHODS
    //-------------------------------------------
    private void launchActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void chooseImageFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private boolean checkIfLocationIsFilled() {
        return  (!TextUtils.isEmpty(streetNumberEditText.getText()) && !TextUtils.isEmpty(streetNameEditText.getText()) &&
        !TextUtils.isEmpty(postalCodeEditText.getText()) && !TextUtils.isEmpty(cityEditText.getText()) &&
        !TextUtils.isEmpty(countryEditText.getText()));
    }

    private boolean fieldsAreCorrectlyFilled() {
        return  (!TextUtils.isEmpty(streetNumberEditText.getText()) && !TextUtils.isEmpty(streetNameEditText.getText()) &&
                !TextUtils.isEmpty(postalCodeEditText.getText()) && !TextUtils.isEmpty(cityEditText.getText()) &&
                !TextUtils.isEmpty(countryEditText.getText()) && eventDate != 0 && !TextUtils.isEmpty(titleEditText.getText())
                && !TextUtils.isEmpty(descriptionEditText.getText()));
    }

    private void displayWrongDateSelectedMessage(View v) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_date_toast), Toast.LENGTH_SHORT).show();
        setDateOnButton((Button)v , getCurrentDate());
    }

    /**
     * save the event date as long (milliseconds since 1 j 1970)
     */
    private  void saveDates(View v, int dayOfMonth, int month, int year){
        String strMonth = addZeroToDate(Integer.toString(month + 1));
        String strDay = addZeroToDate(Integer.toString(dayOfMonth));
        String strYear = Integer.toString(year);
        String dateStr = strDay + "/" + strMonth + "/" + strYear;
        buttonEventDate.setText(dateStr);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        eventDate = date.getTime();
    }

    //------------------------------
    //DIALOGS METHODS
    //------------------------------------
    /**
     * show dialog to pick date. Check if dates are current or futures. Save them
     */
    private void createDatePickerDialog(final View v) {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int currentMonth = calendar.get(Calendar.MONTH)+1;
                int currentYear = calendar.get(Calendar.YEAR);
                if (year> currentYear) {
                    saveDates(v, dayOfMonth, month, year);
                } else if(year == currentYear) {
                    if ((month+1) > currentMonth) {
                        saveDates(v, dayOfMonth, month, year);
                    } else if ((month+1) == currentMonth) {
                        if (dayOfMonth >= currentDay) {
                            saveDates(v, dayOfMonth, month, year);
                        } else {
                            displayWrongDateSelectedMessage(v);
                        }
                    } else {
                        displayWrongDateSelectedMessage(v);
                    }
                } else {
                    displayWrongDateSelectedMessage(v);
                }
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    //------------------------------------------
    //FIREBASE METHODS
    //------------------------------------------
    private void saveProjectInFireBase() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String authorId = Utils.getCurrentUser().getUid();
        Date creation_date = new Date();
        String streetNbr = streetNumberEditText.getText().toString();
        String streetName = streetNameEditText.getText().toString();
        String postalCode = postalCodeEditText.getText().toString();
        String city = cityEditText.getText().toString().toLowerCase();
        String country = countryEditText.getText().toString();

        String latLng = Utils.getLatLngOfProject(this, streetNbr, streetName, city, postalCode, country);
        CollectionReference ref = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
        String projectId = ref.document().getId();
        if (imageView.getDrawable() == null) {
            if (isPublished) {
                ProjectHelper.createProject(projectId, title, description, authorId, creation_date, eventDate, true, streetNbr, streetName,
                        postalCode, city, country, latLng);
                ChatHelper.createChat(projectId);
                ChatHelper.addInvolvedUser(projectId, authorId);
                UserHelper.addProjectsSubscriptions(authorId, projectId);
            }
        } else {
            uploadPhotoInFireBaseAndSaveProject(title, description, authorId, creation_date, eventDate, streetNbr, streetName, postalCode,
                    city, country, latLng);
        }
    }

    private void saveProjectInFireBaseForNotPublishedProjects() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String authorId = Utils.getCurrentUser().getUid();
        Date creation_date = new Date();
        String streetNbr = getEditTextValue(streetNumberEditText, false);
        String streetName = getEditTextValue(streetNameEditText, false);
        String postalCode = getEditTextValue(postalCodeEditText, false);
        String city = getEditTextValue(cityEditText, true);
        String country = getEditTextValue(countryEditText, false);

        String latLng = null;
        if (checkIfLocationIsFilled()) {
            latLng = Utils.getLatLngOfProject(this, streetNbr, streetName, city, postalCode, country);
        }
        if (imageView.getDrawable() == null) {
            CollectionReference ref = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
            String projectId = ref.document().getId();
            ProjectHelper.createProject(projectId, title, description, authorId, creation_date, eventDate, false, streetNbr, streetName,
                        postalCode, city, country, latLng);
            UserHelper.addProjectsSubscriptions(authorId, projectId);
        } else {
            uploadPhotoInFireBaseAndSaveProject(title, description, authorId, creation_date, eventDate, streetNbr, streetName, postalCode,
                    city, country, latLng);
        }

    }
    /**
     * save the photo in storage firebase, then create a project in firebase with the download url
     */
    private void uploadPhotoInFireBaseAndSaveProject(final String title, final String description, final String authorId, final Date creation_date,
                                                     long eventDate, String streetNumber, String streetName, String postalCode,
                                                     String city, String country, String latLng) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference filePath = FirebaseStorage.getInstance().getReference(uuid);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.project_is_saving_toast), Toast.LENGTH_SHORT).show();

        filePath.putFile(uriImageSelected).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            CollectionReference ref = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
                            String idProject = ref.document().getId();
                            if (isPublished) {
                                ProjectHelper.createProjectWithImage(idProject, title, description, authorId,
                                        creation_date, eventDate, true, uri.toString(),
                                        streetNumber, streetName, postalCode, city, country, latLng);
                                ChatHelper.createChat(idProject);
                                ChatHelper.addInvolvedUser(idProject, authorId);
                                UserHelper.addProjectsSubscriptions(authorId, idProject);
                            } else {
                                ProjectHelper.createProjectWithImage(idProject, title, description, authorId,
                                        creation_date, eventDate, false, uri.toString(),
                                        streetNumber, streetName, postalCode, city, country, latLng);
                                UserHelper.addProjectsSubscriptions(authorId, idProject);
                            }
                        }
                    }
                });
            }
        });
    }

    private void updateProjectInFireBase() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String streetNumber = streetNumberEditText.getText().toString();
        String streetName = streetNameEditText.getText().toString();
        String postalCode = postalCodeEditText.getText().toString();
        String city = cityEditText.getText().toString().toLowerCase();
        String country = countryEditText.getText().toString();

        ProjectHelper.updateTitle(projectId, title);
        ProjectHelper.updateDescription(projectId, description);
        ProjectHelper.updateStreetNumber(projectId, streetNumber);
        ProjectHelper.updateStreetName(projectId, streetName);
        ProjectHelper.updatePostalCode(projectId, postalCode);
        ProjectHelper.updateCity(projectId, city);
        ProjectHelper.updateCountry(projectId, country);
        ProjectHelper.updateEventDate(projectId, eventDate);
        if (isPublished) {
            ProjectHelper.updateIsPublished(projectId, true);
        }
        if (uriImageSelected != null) {
            String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
            StorageReference filePath = FirebaseStorage.getInstance().getReference(uuid);

            filePath.putFile(uriImageSelected).addOnSuccessListener(taskSnapshot ->
                    filePath.getDownloadUrl().addOnSuccessListener(uri ->
                            ProjectHelper.updateUrlPhoto(projectId, uri.toString())));
        }
    }

    //---------------------------------------
    //UPDATE UI
    //-----------------------------------------
    private void updateUiWithProjectsData(String projectId, Context context) {
        ProjectHelper.getProject(projectId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Project project = task.getResult().toObject(Project.class);
                    titleEditText.setText(project.getTitle());
                    if (project.getEventDate() != 0) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        buttonEventDate.setText(formatter.format(project.getEventDate()));
                    }
                    if (project.getDescription() != null) {
                        descriptionEditText.setText(project.getDescription());
                    }

                    setValueToEditText(project.getStreetNumber(), streetNumberEditText);
                    setValueToEditText(project.getStreetName(), streetNameEditText);
                    setValueToEditText(project.getPostalCode(), postalCodeEditText);
                    setValueToEditText(project.getCity(), cityEditText);
                    setValueToEditText(project.getCountry(), countryEditText);

                    if (project.getUrlPhoto() != null) {

                        buttonAddPicture.setText(getResources().getString(R.string.change_picture));

                        Glide.with(context)
                                .load(project.getUrlPhoto())
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageView);
                    }
                }
            }
        });
    }


}
