package com.example.applicationsecond.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.AdapterModalFollower;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersModalFragment extends BottomSheetDialogFragment {


    @BindView(R.id.modal_fragment_followers_recycler_view)
    RecyclerView recyclerView;
    //--------------------------------------------
    private AdapterModalFollower adapter;
    private List<User> followers;
    private String projectId;
    //------------------------------------------
    public static final String KEY_PROJECT_ID = "keyProjectId";

    public FollowersModalFragment() {
        // Required empty public constructor
    }

    public static FollowersModalFragment newInstance(String projectId) {
        FollowersModalFragment fragment = new FollowersModalFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PROJECT_ID, projectId);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.modal_fragment_followers,
                container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView();

    }

    private void configureRecyclerView() {
        followers = new ArrayList<>();
        getProjectInformation();
       /* adapter = new AdapterModalFollower(followers, Glide.with(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));*/

    }

    private void getProjectInformation() {
        projectId = getArguments().getString(KEY_PROJECT_ID, null);
        ProjectHelper.getProject(projectId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Project project = task.getResult().toObject(Project.class);
                    if (project.getUsersWhoSubscribed() != null) {
                        for (int i = 0; i < project.getUsersWhoSubscribed().size(); i++) {
                            UserHelper.getUser(project.getUsersWhoSubscribed().get(i)).addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                User user = task.getResult().toObject(User.class);
                                                followers.add(user);

                                                adapter = new AdapterModalFollower(followers, Glide.with(getActivity()));
                                                recyclerView.setAdapter(adapter);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
