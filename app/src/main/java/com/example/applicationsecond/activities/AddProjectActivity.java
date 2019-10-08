package com.example.applicationsecond.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.utils.Utils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import butterknife.OnClick;

public class AddProjectActivity extends AppCompatActivity {

   /* private boolean isPublished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
    }

    @OnClick(R.id.button_publish_add_project_activity)
    public void publishProject() {
        isPublished = true;
        saveProjectInFireBase();
        Toast.makeText(this, "Project published!", Toast.LENGTH_SHORT).show();
        ActualityListFragment fragment = new ActualityListFragment();
        showFragment(fragment);

    }

    @OnClick(R.id.button_save_for_later_add_project_activity)
    public void saveProjectForLater() {
        isPublished = false;
        saveProjectInFireBase();
        Toast.makeText(this, "Project saved for later!", Toast.LENGTH_SHORT).show();
        ActualityListFragment fragment = new ActualityListFragment();
        showFragment(fragment);
    }

    private void saveProjectInFireBase() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        //String idAuthor = preferences.getString(USER_ID, null);
        //String authorName = Utils.getCurrentUser().getDisplayName();
        String authorId = Utils.getCurrentUser().getUid();

        System.out.println("author id = " + authorId);

        Date creation_date = new Date();

        CollectionReference ref = FirebaseFirestore.getInstance().collection("projects");

        String projectId = ref.document().getId();
        if (isPublished) {
            ProjectHelper.createProject(projectId, title, description, authorId, creation_date, true);
        } else {
            ProjectHelper.createProject(projectId, title, description, authorId, creation_date, false);
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout_content_main_activity, fragment);
        transaction.commit();
    }*/
}
