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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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

public class AddProjectActivity extends AppCompatActivity {

    @BindView(R.id.input_title_add_project_activity) EditText titleEditText;
    @BindView(R.id.input_description_add_project_activity) EditText descriptionEditText;
    @BindView(R.id.button_publish_add_project_activity) Button buttonPublish;
    @BindView(R.id.button_save_for_later_add_project_activity) Button buttonSaveProject;
    @BindView(R.id.button_add_picture_add_project_activity) Button buttonAddPicture;
    @BindView(R.id.image_chosen_picture_add_project_activity) ImageView imageView;
    @BindView(R.id.text_edit_street_nbr_add_project_activity) TextInputEditText streetNumberEditText;
    @BindView(R.id.edit_text_street_name_add_project_activity) TextInputEditText streetNameEditText;
    @BindView(R.id.edit_text_complement_add_project_activity) TextInputEditText locationComplementEditText;
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
    private String currentDate;
    private String eventDate;
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
            updateUiWithProjectsData(projectId, this);
        } else {
            //display current date on the button to choose date project
            setDateOnButton(buttonEventDate, getCurrentDate());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            //finish();
            //launch main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            //boolean isLocationCompleted = checkIfLocationIsFilled();
            if (fieldsAreCorrectlyFilled()) {
                saveProjectInFireBase();
                Toast.makeText(this, "Project published!", Toast.LENGTH_SHORT).show();
                //finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You have to give a location for your project or to give a date for event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.button_save_for_later_add_project_activity)
    public void saveProjectForLater() {
        isPublished = false;
        if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
            updateProjectInFireBase();
        }
        saveProjectInFireBaseForNotPublishedProjects();
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

    @OnClick(R.id.spinner_button_event_date_add_project_activity)
    public void pickEventDate(View v) {
        createDatePickerDialog(v);
    }

    //--------------------------------------------------
    //
    //----------------------------------------------------
    private void handleResponseForGallery(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
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

    private boolean checkIfLocationIsFilled() {
        return  (!TextUtils.isEmpty(streetNumberEditText.getText()) && !TextUtils.isEmpty(streetNameEditText.getText()) &&
        !TextUtils.isEmpty(postalCodeEditText.getText()) && !TextUtils.isEmpty(cityEditText.getText()) &&
        !TextUtils.isEmpty(countryEditText.getText()));
    }

    private boolean fieldsAreCorrectlyFilled() {
        return  (!TextUtils.isEmpty(streetNumberEditText.getText()) && !TextUtils.isEmpty(streetNameEditText.getText()) &&
                !TextUtils.isEmpty(postalCodeEditText.getText()) && !TextUtils.isEmpty(cityEditText.getText()) &&
                !TextUtils.isEmpty(countryEditText.getText()) && eventDate != null);
    }

    private void displayWrongDateSelectedMessage(View v) {
        Toast.makeText(getApplicationContext(), "You have to select a current or future date...", Toast.LENGTH_SHORT).show();
        setDateOnButton((Button)v , getCurrentDate());
    }

    public void setDateOnButton(Button button, String date) {
        button.setText(date);
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        currentDate = dateFormat.format(calendar.getTime());
        return currentDate;
    }

    //------------------------------
    //DIALOGS METHODS
    //------------------------------------
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
                //check if selected date is passed or not
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

    private  void saveDates(View v, int dayOfMonth, int month, int year) {
        String strMonth = addZeroToDate(Integer.toString(month + 1));
        String strDay = addZeroToDate(Integer.toString(dayOfMonth));
        String strYear = Integer.toString(year);
        switch (v.getId()) {
            case R.id.spinner_button_event_date_add_project_activity:
                buttonEventDate.setText(strDay + "/" + strMonth + "/" + strYear);
                eventDate = strDay + "/" + strMonth + "/" + strYear;
                break;
            /*case R.id.spinner_button_end_date:
                if (beginDateButton.getText().toString().length() != 0) {
                    if (isBeginDateBeforeEndDate(dayOfMonth, month, year)) {
                        endDateButton.setText(strDay + "/" + strMonth + "/" + strYear);
                        preferences.edit().putString(END_DATE, strYear + strMonth + strDay).apply();
                    } else {
                        Toast.makeText(this, "You have to select a date after the begin date...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "You have to select a begin date first!", Toast.LENGTH_SHORT).show();
                }
                break;*/
            default:
                break;
        }
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
        String city = cityEditText.getText().toString();
        String country = countryEditText.getText().toString();
        String complement = null;
        if (!TextUtils.isEmpty(locationComplementEditText.getText())) {
            complement = locationComplementEditText.getText().toString();
        }
        String latLng = Utils.getLatLngOfProject(this, streetNbr, streetName, city, postalCode, country);

        if (imageView.getDrawable() == null) {
            //means there is no photo saved for the project
            CollectionReference ref = FirebaseFirestore.getInstance().collection("projects");
            String projectId = ref.document().getId();

            if (isPublished) {
                ProjectHelper.createProject(projectId, title, description, authorId, creation_date, eventDate, true, streetNbr, streetName,
                        complement, postalCode, city, country, latLng);
            } else {
                ProjectHelper.createProject(projectId, title, description, authorId, creation_date, eventDate,false, streetNbr, streetName,
                        complement, postalCode, city, country, latLng);
            }
            UserHelper.addProjectsSubscriptions(authorId, projectId);
        } else {
            uploadPhotoInFireBaseAndSaveProject(title, description, authorId, creation_date, eventDate, streetNbr, streetName, complement, postalCode,
                    city, country, latLng);
        }
    }

    private void saveProjectInFireBaseForNotPublishedProjects() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String authorId = Utils.getCurrentUser().getUid();
        Date creation_date = new Date();
        String streetNbr = null;
        String streetName = null;
        String postalCode = null;
        String city = null;
        String country = null;

        if (!TextUtils.isEmpty(streetNumberEditText.getText())) {
            streetNbr = streetNumberEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(streetNameEditText.getText())) {
            streetName = streetNameEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(postalCodeEditText.getText())) {
            postalCode = postalCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(cityEditText.getText())) {
            city = cityEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(countryEditText.getText())) {
            country = countryEditText.getText().toString();
        }
        String complement = null;
        if (!TextUtils.isEmpty(locationComplementEditText.getText())) {
            complement = locationComplementEditText.getText().toString();
        }
        String latLng = null;
        if (checkIfLocationIsFilled()) {
            latLng = Utils.getLatLngOfProject(this, streetNbr, streetName, city, postalCode, country);
        }
        if (imageView.getDrawable() == null) {
            //means there is no photo saved for the project
            CollectionReference ref = FirebaseFirestore.getInstance().collection("projects");
            String projectId = ref.document().getId();
            ProjectHelper.createProject(projectId, title, description, authorId, creation_date, eventDate, false, streetNbr, streetName,
                        complement, postalCode, city, country, latLng);
        } else {
            uploadPhotoInFireBaseAndSaveProject(title, description, authorId, creation_date, eventDate, streetNbr, streetName, complement, postalCode,
                    city, country, latLng);
        }
        UserHelper.addProjectsSubscriptions(authorId, projectId);
    }

    private void uploadPhotoInFireBaseAndSaveProject(final String title, final String description, final String authorId, final Date creation_date,
                                                     String eventDate, String streetNumber, String streetName, String complement, String postalCode,
                                                     String city, String country, String latLng) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference filePath = FirebaseStorage.getInstance().getReference(uuid);

        filePath.putFile(uriImageSelected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        CollectionReference ref = FirebaseFirestore.getInstance().collection("projects");
                        String idProject = ref.document().getId();
                        if (isPublished) {
                            ProjectHelper.createProjectWithImage(idProject, title, description, authorId, creation_date, eventDate, true, uri.toString(),
                                    streetNumber, streetName, complement, postalCode, city, country, latLng);
                        } else {
                            ProjectHelper.createProjectWithImage(idProject, title, description, authorId, creation_date, eventDate, false, uri.toString(),
                                    streetNumber, streetName, complement, postalCode, city, country, latLng);
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
        String complement = null;
        if (locationComplementEditText.getText() != null) {
            complement = locationComplementEditText.getText().toString();
        }
        String postalCode = postalCodeEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String country = countryEditText.getText().toString();

        ProjectHelper.updateTitle(projectId, title);
        ProjectHelper.updateDescription(projectId, description);
        ProjectHelper.updateStreetNumber(projectId, streetNumber);
        ProjectHelper.updateStreetName(projectId, streetName);
        ProjectHelper.updateComplement(projectId, complement);
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

            filePath.putFile(uriImageSelected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ProjectHelper.updateUrlPhoto(projectId, uri.toString());
                        }
                    });
                }
            });

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
                    if (project.getDescription() != null) {
                        descriptionEditText.setText(project.getDescription());
                    }
                    if (project.getEventDate() != null) {
                        buttonEventDate.setText(project.getEventDate());
                    }
                    if (project.getStreetNumber() != null) {
                        streetNumberEditText.setText(project.getStreetNumber());
                    }
                    if (project.getStreetName() != null) {
                        streetNameEditText.setText(project.getStreetName());
                    }
                    if (project.getLocationComplement() != null) {
                        locationComplementEditText.setText(project.getLocationComplement());
                    }
                    if (project.getPostalCode() != null) {
                        postalCodeEditText.setText(project.getPostalCode());
                    }
                    if (project.getCity() != null) {
                        cityEditText.setText(project.getCity());
                    }
                    if (project.getCountry() != null) {
                        countryEditText.setText(project.getCountry());
                    }
                    if (project.getUrlPhoto() != null) {

                        buttonAddPicture.setText("Change picture");

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
