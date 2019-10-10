package com.example.applicationsecond.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.utils.Utils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProjectFragment extends Fragment {

 /*   @BindView(R.id.input_title_add_project_activity)
    EditText editTextTitle;
    @BindView(R.id.input_description_add_project_activity)
    EditText editTextDescription;
    @BindView(R.id.button_publish_add_project_activity)
    Button saveButton;
    //-----------------------------------------------
    private SharedPreferences preferences;
    private boolean isPublished;
    //-------------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String KEY_EDIT_PROJECT = "keyEditproject";


    public AddProjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_add_project, container, false);
        ButterKnife.bind(this, result);
        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //check if it s for editing one existing project
        //if 0 it s not
        //if 1 it is
        if (preferences.getInt(KEY_EDIT_PROJECT, -1) == 1) {
            //update ui with existing data on this project

        }

        return result;
    }

    @OnClick(R.id.button_publish_add_project_activity)
    public void publishProject() {
        isPublished = true;
        saveProjectInFireBase();
        Toast.makeText(getActivity(), "Project published!", Toast.LENGTH_SHORT).show();
        ActualityListFragment fragment = new ActualityListFragment();
        showFragment(fragment);

    }

    @OnClick(R.id.button_save_for_later_add_project_activity)
    public void saveProjectForLater() {
        isPublished = false;
        saveProjectInFireBase();
        Toast.makeText(getActivity(), "Project saved for later!", Toast.LENGTH_SHORT).show();
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
