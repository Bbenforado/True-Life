package com.example.applicationsecond.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.AssociationProfileActivity;
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
    private AdapterRecyclerViewUsers adapter;
    private List<User> users;
    private SharedPreferences preferences;
    private static final String APP_PREFERENCES = "appPreferences";
    private static final String RESULTS = "results";


    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, result);

        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //retrieveResults();
        //doBasicConfiguration();
        getDataForUserProfile();
        configureOnLongClickRecyclerView();
        configureOnClickRecyclerView();
        return result;
    }

    //----------------------------
    //CONFIGURATION
    //--------------------------------
    private void doBasicConfiguration() {
        configureRecyclerView();
    }

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
                    builder.setMessage("Do you want to unfollow this association?")
                            .setPositiveButton("Yes", (dialog, id) -> {
                                //unfollow the association

                                User association = adapter.getUser(position);
                                String currentUserId = Utils.getCurrentUser().getUid();

                                UserHelper.removeAssociationSubscription(currentUserId, association.getId());
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            })
                            .create()
                            .show();

                    return false;
                });
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

    private void getDataForUserProfile() {
        users = new ArrayList<>();

        UserHelper.getUser(Utils.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    User user = task.getResult().toObject(User.class);
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




                    }
                }
            }
        });
    }

}
