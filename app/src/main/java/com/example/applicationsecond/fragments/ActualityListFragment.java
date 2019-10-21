package com.example.applicationsecond.fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.ProjectDetailActivity;
import com.example.applicationsecond.adapters.AdapterRecyclerViewProjects;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.applicationsecond.utils.Utils.getCurrentUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActualityListFragment extends Fragment implements AdapterRecyclerViewProjects.Listener{
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
    private String codeLastActivity;
    @Nullable
    private String profileId;
    //-----------------------------------------------
    //-------------------------------------------------
    private static final String APP_PREFERENCES = "appPreferences";
    private static final String CLICKED_PROJECT = "clickedProject";
    private static final String ASSOCIATION_ID = "associationId";
    private static final String CITY = "city";

    public ActualityListFragment() {
        // Required empty public constructor
    }

    public ActualityListFragment(String codeLastActivity) {
        this.codeLastActivity = codeLastActivity;
    }

    public ActualityListFragment(String codeLastActivity, String profileId) {
        this.codeLastActivity = codeLastActivity;
        this.profileId = profileId;
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
    }

    /**
     * query the data depending on from where this fragment is launched.
     * if it s for default screen, we query projects that the user follows
     * if it s from profile activity, we query published projects the user is following
     * if it s from association profile, we query published projects by this association
     * if it s from the search activity, we query the projects depending on the city
     */
    private void configureRecyclerView() {
        switch (codeLastActivity) {
            case "profileActivity":
                adapter = new AdapterRecyclerViewProjects(generateOptionsForAdapter(ProjectHelper.getProjectsForOneUser(Utils.getCurrentUser().getUid())),
                Glide.with(this), this);
                break;
            case "notCurrentUsersProfile":
                adapter = new AdapterRecyclerViewProjects(generateOptionsForAdapter(ProjectHelper.getProjectsForOneUser(profileId)),
                        Glide.with(this), this);
                break;
            case "associationProfileActivity":
                String assoId = preferences.getString(ASSOCIATION_ID, null);
                adapter = new AdapterRecyclerViewProjects(generateOptionsForAdapter(ProjectHelper.getPublishedProjects(assoId)),
                Glide.with(this), this);
                break;
            case "searchResults":
                String city = preferences.getString(CITY, null).toLowerCase();
                adapter = new AdapterRecyclerViewProjects(generateOptionsForAdapter(ProjectHelper.getProjectsDependingOnCity(city)),
                        Glide.with(this), this);
                break;

            case "defaultScreen":
                Date todayDate = new Date();
                long dateInMilliseconds = todayDate.getTime();
                adapter = new AdapterRecyclerViewProjects(generateOptionsForAdapter(ProjectHelper.getFuturesProjectsForOneUser(getCurrentUser().getUid(), dateInMilliseconds)),
                        Glide.with(this), this);
                break;

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.actuality_list_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);

                        Project clickedProject = adapter.getItem(position);

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
    private FirestoreRecyclerOptions<Project> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {
        viewSwitcher.setDisplayedChild(adapter.getItemCount() == 0? 1 : 0);
    }
}
