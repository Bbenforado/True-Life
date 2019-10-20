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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.AdapterPostListFragment;
import com.example.applicationsecond.adapters.AdapterRecyclerViewPosts;
import com.example.applicationsecond.api.PostHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Post;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostListFragment extends Fragment {
    //------------------------------
    //BIND VIEWS
    //-------------------------------
    @BindView(R.id.view_switcher_post_list_fragment)
    ViewSwitcher viewSwitcher;
    @BindView(R.id.recycler_view_post_list_fragment)
    RecyclerView recyclerView;
    @BindView(R.id.text_view_post_list_fragment_no_post_to_display)
    TextView textViewNoPostToDisplay;
    //-----------------------------------
    //-------------------------------------
    private AdapterPostListFragment adapter;
    private List<Post> postList;
    private List<String> publishedPostsId;
    private Boolean isFromAssociationProfile;
    private SharedPreferences preferences;
    private int counterPost;
    //-----------------------------------------------
    //-------------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String ASSOCIATION_ID = "associationId";


    public PostListFragment() {
        // Required empty public constructor
    }

    public PostListFragment(Boolean isFromAssociationProfile) {
        this.isFromAssociationProfile = isFromAssociationProfile;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_post_list, container, false);
        ButterKnife.bind(this, result);

        publishedPostsId = new ArrayList<>();

        doBasicConfiguration();
        return result;
    }

    //-----------------------------
    //CONFIGURATION
    //-----------------------------
    private void doBasicConfiguration() {
        postList = new ArrayList<>();
        configureViewSwitcher();
        configureRecyclerView();
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
        if (isFromAssociationProfile) {
            counterPost = 0;
            preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            String associationId = preferences.getString(ASSOCIATION_ID, null);

            UserHelper.getUser(associationId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User asso = task.getResult().toObject(User.class);
                        if (asso.getPublishedPostId() != null) {
                            if (asso.getPublishedPostId().size() > 0) {
                                for (int i = 0; i < asso.getPublishedPostId().size(); i++) {
                                    counterPost = counterPost + 1;
                                    String id = asso.getPublishedPostId().get(i);
                                    PostHelper.getPost(id).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Post post = task.getResult().toObject(Post.class);
                                                postList.add(post);
                                                adapter = new AdapterPostListFragment(postList);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView.setAdapter(adapter);
                                                displayScreenDependingOfPostsAvailable();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });
            displayScreenDependingOfPostsAvailable();
        } else {
            getDataToConfigureRecyclerView();
        }
    }

    //-------------------------------------
    //-------------------------------------
    private void displayScreenDependingOfPostsAvailable() {
        if (counterPost > 0) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
            if (isFromAssociationProfile) {
                textViewNoPostToDisplay.setText("This association don't have post for the moment");
            }
        }
    }

    private boolean checkIfTheresNewsToDisplay() {
        return (postList.size() > 0);
    }

    private void launchActivity(Class activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }


    private void getDataToConfigureRecyclerView() {
        counterPost = 0;
        //get the current user
        UserHelper.getUser(Utils.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    //get the list of the followed associations
                    if (user.getAssociationSubscribedId() != null) {
                        if (user.getAssociationSubscribedId().size() > 0) {
                            for (int i = 0; i < user.getAssociationSubscribedId().size(); i++) {

                                UserHelper.getUser(user.getAssociationSubscribedId().get(i)).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User association = task.getResult().toObject(User.class);

                                            if (association.getPublishedPostId() != null) {
                                                if (association.getPublishedPostId().size() > 0) {
                                                    for (int j = 0; j < association.getPublishedPostId().size(); j++) {
                                                        String id = association.getPublishedPostId().get(j);
                                                        counterPost = counterPost + 1;
                                                        PostHelper.getPost(id).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    Post post = task.getResult().toObject(Post.class);
                                                                    postList.add(post);
                                                                    adapter = new AdapterPostListFragment(postList);
                                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                    recyclerView.setAdapter(adapter);
                                                                    displayScreenDependingOfPostsAvailable();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        displayScreenDependingOfPostsAvailable();
    }

}
