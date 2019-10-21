package com.example.applicationsecond.fragments;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.example.applicationsecond.adapters.AdapterUsersProjectsList;
import com.example.applicationsecond.api.ChatHelper;
import com.example.applicationsecond.api.MessageHelper;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Chat;
import com.example.applicationsecond.models.Post;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
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
public class UsersProjectsListFragment extends Fragment implements AdapterUsersProjectsList.Listener{
    //------------------------------
    //BIND VIEWS
    //-------------------------------
    @BindView(R.id.view_switcher_fragment_users_projects_list)
    ViewSwitcher viewSwitcher;
    @BindView(R.id.recycler_view_fragment_users_projects_list)
    RecyclerView recyclerView;
    //-----------------------------------
    //-------------------------------------
    private AdapterUsersProjectsList adapter;
    private Boolean isPublished;
    private SharedPreferences preferences;
    //-----------------------------------------------
    //-------------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    private static final String CLICKED_PROJECT = "clickedProject";

    public UsersProjectsListFragment() {
        // Required empty public constructor
    }

    public UsersProjectsListFragment(Boolean isPublished) {
        this.isPublished = isPublished;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_users_projects_list, container, false);
        ButterKnife.bind(this, result);
        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        doBasicConfiguration();
        return result;
    }

    //------------------------------------
    //CONFIGURATION
    //-----------------------------------------
    private void doBasicConfiguration() {
        configureRecyclerView();
        configureOnClickRecyclerView();
        configureOnLongClickRecyclerView();
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

    private void configureRecyclerView() {
        String currentUsersId = Utils.getCurrentUser().getUid();
        getDataToConfigureRecyclerView(currentUsersId);
    }

    /**
     * displays the detail of the project
     */
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_my_projects_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                        Project clickedProject = adapter.getItem(position);
                        Gson gson = new Gson();
                        preferences.edit().putString(CLICKED_PROJECT, gson.toJson(clickedProject)).apply();
                        startActivity(intent);
                    }
                });
    }

    /**
     * user can delete project if he clicks for long time on an item of the list
     */
    private void configureOnLongClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_my_projects_item)
                .setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        Project project = adapter.getItem(position);
                        displayDialogToDeleteProject(project.getId());
                        return true;
                    }
                });
    }

    //---------------------------------------
    //------------------------------------------
    private void displayDialogToDeleteProject(String projectId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.delete_project_dialog_title))
               .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       ProjectHelper.deleteProject(projectId);
                       ChatHelper.getChat(projectId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if (task.isSuccessful()) {
                                   Chat chat = task.getResult().toObject(Chat.class);
                                   if (chat.getMessagesId() != null) {
                                       for (int i = 0; i < chat.getMessagesId().size(); i++) {
                                           MessageHelper.deleteMessage(projectId, chat.getMessagesId().get(i));
                                       }
                                       ChatHelper.deleteChat(projectId);
                                   } else {
                                       ChatHelper.deleteChat(projectId);
                                   }
                                   ChatHelper.deleteChat(projectId);
                               }
                           }
                       });
                       UserHelper.removeProjectSubscription(Utils.getCurrentUser().getUid(), projectId);
                   }
               })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .create()
                .show();
    }

    private void getDataToConfigureRecyclerView(String userId) {
        if (isPublished) {
            adapter = new AdapterUsersProjectsList(generateOptionsForAdapter(ProjectHelper.getUsersPublishedProjects(userId)),
                    Glide.with(getContext()), this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        } else {
            adapter = new AdapterUsersProjectsList(generateOptionsForAdapter(ProjectHelper.getUsersNotPublishedProjects(userId)),
                    Glide.with(getContext()), this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    }

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
