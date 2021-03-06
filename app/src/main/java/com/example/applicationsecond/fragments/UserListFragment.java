package com.example.applicationsecond.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.AssociationProfileActivity;
import com.example.applicationsecond.activities.ProfileActivity;
import com.example.applicationsecond.adapters.AdapterRecyclerViewUsers;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

    @BindView(R.id.recycler_view_user_list_fragment)
    RecyclerView recyclerView;
    @BindView(R.id.view_switcher_user_list_fragment)
    ViewSwitcher viewSwitcher;
    //---------------------------------
    //------------------------------------
    private AdapterRecyclerViewUsers adapter;
    private List<User> users;
    private SharedPreferences preferences;
    @Nullable
    private String profileId;
    private boolean isCurrentUsersProfile;
    private boolean isFromSearchFragment;
    //-----------------------------------------
    //------------------------------------------
    private static final String APP_PREFERENCES = "appPreferences";
    private static final String RESULTS = "results";


    public UserListFragment() {
        // Required empty public constructor
    }

    public UserListFragment(boolean isCurrentUsersProfile, String profileId, boolean isFromSearchFragment) {
        this.isCurrentUsersProfile = isCurrentUsersProfile;
        this.profileId = profileId;
        this.isFromSearchFragment = isFromSearchFragment;
    }
    public UserListFragment(boolean isCurrentUsersProfile, boolean isFromSearchFragment) {
        this.isCurrentUsersProfile = isCurrentUsersProfile;
        this.isFromSearchFragment = isFromSearchFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, result);

        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (isFromSearchFragment){
            retrieveResults();
            configureRecyclerView();

        } else {

            if (isCurrentUsersProfile) {
                getDataForUserProfile(Utils.getCurrentUser().getUid());
                configureOnLongClickRecyclerView();
            } else {
                getDataForUserProfile(profileId);
            }
        }

        configureOnClickRecyclerView();
        configureViewSwitcher();
        return result;
    }

    //----------------------------
    //CONFIGURATION
    //--------------------------------
    private void configureRecyclerView() {
        adapter = new AdapterRecyclerViewUsers(users, Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.user_list_fragment_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    //display association s profile

                    String associationId = adapter.getUser(position).getId();
                    Intent authorProfileIntent = new Intent(getContext(), AssociationProfileActivity.class);
                    authorProfileIntent.putExtra("authorId", associationId);
                    startActivity(authorProfileIntent);

                });
    }

    private void configureOnLongClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.user_list_fragment_item)
                .setOnItemLongClickListener((recyclerView, position, v) -> {
                    //display dialog which ask if user wants to unfollow the association
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getResources().getString(R.string.unfollow_association))
                            .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                                User association = adapter.getUser(position);
                                String currentUserId = Utils.getCurrentUser().getUid();
                                UserHelper.removeAssociationSubscription(currentUserId, association.getId());
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            })
                            .create()
                            .show();

                    return false;
                });
    }

    private void configureViewSwitcher() {
        // Declare in and out animations and load them using AnimationUtils class
        Animation newsAvailable = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.slide_in_left);
        Animation noNewsAvailable = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.slide_out_right);
        // set the animation type to ViewSwitcher
        viewSwitcher.setInAnimation(newsAvailable);
        viewSwitcher.setOutAnimation(noNewsAvailable);
    }

    //--------------------------------------
    //GET DATA
    //----------------------------------------
    private void retrieveResults() {
        Gson gson = new Gson();
        users = new ArrayList<>();
        String json = preferences.getString(RESULTS, null);
        Type type = new TypeToken<List<User>>() {
        }.getType();

        users = gson.fromJson(json, type);
    }

    /**
     * get the followed associations by the user
     */
    private void getDataForUserProfile(String id) {
        users = new ArrayList<>();
        UserHelper.getUser(id).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user.getAssociationSubscribedId() != null) {
                        if (user.getAssociationSubscribedId().size() > 0) {
                            for (int i = 0; i < user.getAssociationSubscribedId().size(); i++) {
                                UserHelper.getUser(user.getAssociationSubscribedId().get(i)).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            User association = task.getResult().toObject(User.class);
                                            users.add(association);
                                            configureRecyclerView();
                                        }
                                    }
                                });
                            }
                        } else {
                            displayScreenDependingOfDataAvailable(users);
                        }
                    } else {
                        displayScreenDependingOfDataAvailable(users);
                    }
                }
            }
        });
    }

    //---------------------------------
    //METHODS
    //------------------------------------
    private void displayScreenDependingOfDataAvailable(List<User> users) {
        if (checkIfThereIsDataToDisplay(users)) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
        }
    }

    private boolean checkIfThereIsDataToDisplay(List<User> users) {
        return users.size() > 0;
    }

}
