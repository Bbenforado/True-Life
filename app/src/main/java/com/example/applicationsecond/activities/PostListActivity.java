package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewSwitcher;

import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.AdapterRecyclerViewPosts;
import com.example.applicationsecond.api.PostHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Post;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostListActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_post_list_activity)
    RecyclerView recyclerView;
    @BindView(R.id.view_switcher_post_list_activity)
    ViewSwitcher viewSwitcher;

    private List<Post> posts;
    private AdapterRecyclerViewPosts adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        ButterKnife.bind(this);
        posts = new ArrayList<>();

        configureToolbar();
        configureRecyclerView();
    }

    //---------------------------------------
    //CONFIGURATION
    //------------------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Posts");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureRecyclerView() {
        UserHelper.getUser(Utils.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);

                    if (user.getPublishedPostId() != null) {
                        List<String> postsId = new ArrayList<>();
                        postsId.addAll(user.getPublishedPostId());

                        for (int i = 0; i < postsId.size(); i++) {
                            PostHelper.getPost(postsId.get(i)).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Post post = task.getResult().toObject(Post.class);
                                        posts.add(post);
                                        adapter = new AdapterRecyclerViewPosts(posts);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        recyclerView.setAdapter(adapter);
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
