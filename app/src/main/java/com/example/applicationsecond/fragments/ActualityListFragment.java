package com.example.applicationsecond.fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.ProjectDetailActivity;
import com.example.applicationsecond.adapters.AdapterRecyclerViewProjects;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.callbacks.MyCallback;
import com.example.applicationsecond.models.Post;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActualityListFragment extends Fragment {
    //------------------------------
    //BIND VIEWS
    //-------------------------------
    @BindView(R.id.view_switcher_main_activity)
    ViewSwitcher viewSwitcher;
    @BindView(R.id.recycler_view_main_activity)
    RecyclerView recyclerView;
    //-----------------------------------
    //-------------------------------------
    private AdapterRecyclerViewProjects adapter;
    private SharedPreferences preferences;
    private boolean isNewsToDisplay;
    private List<Project> projectList;
    //-----------------------------------------------
    //-------------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    private static final String CLICKED_PROJECT = "clickedProject";

    public ActualityListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_actuality_list, container, false);
        ButterKnife.bind(this, result);

        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        doBasicConfiguration();
        return result;
    }

    //-----------------------------
    //CONFIGURATION
    //-----------------------------
    private void doBasicConfiguration() {
        projectList = new ArrayList<>();

        configureRecyclerView();
        configureOnClickRecyclerView();
        configureViewSwitcher();

    }

    private void configureViewSwitcher() {
        // Declare in and out animations and load them using AnimationUtils class
        Animation newsAvailable = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.slide_in_left);
        Animation noNewsAvailable = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.slide_out_right);
        // set the animation type to ViewSwitcher
        viewSwitcher.setInAnimation(newsAvailable);
        viewSwitcher.setOutAnimation(noNewsAvailable);

        displayScreenDependingOfNewsAvailable();
    }

    private void configureRecyclerView() {
        getDataToConfigureRecyclerView();
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.actuality_list_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);

                        Project clickedProject = projectList.get(position);

                        Gson gson = new Gson();
                        preferences.edit().putString(CLICKED_PROJECT, gson.toJson(clickedProject)).apply();

                        // Start Animation
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), v, getString(R.string.animation_main_to_detail));
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }

                    }
                });
    }

    //-------------------------------------
    //-------------------------------------
    private void displayScreenDependingOfNewsAvailable() {
        isNewsToDisplay = checkIfTheresNewsToDisplay();
        if (isNewsToDisplay) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
        }
    }

    private boolean checkIfTheresNewsToDisplay() {
        return true;
    }

    private void launchActivity(Class activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }


    private void getDataToConfigureRecyclerView() {
        ProjectHelper.getProjectsForOneUser(Utils.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Project> projects = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Project project = document.toObject(Project.class);
                        projects.add(project);
                    }

                    projectList.addAll(projects);

                    adapter = new AdapterRecyclerViewProjects(projects);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.e("TAG", "Error");
                }
            }
        });
        /*ProjectHelper.getPublishedProjects().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Project> projects = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Project project = document.toObject(Project.class);
                        projects.add(project);
                    }

                    projectList.addAll(projects);

                    adapter = new AdapterRecyclerViewProjects(projects);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.e("TAG", "Error");
                }
            }
        });*/
    }

}
