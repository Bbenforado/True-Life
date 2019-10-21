package com.example.applicationsecond.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.AdapterRecyclerViewPosts;
import com.example.applicationsecond.api.PostHelper;
import com.example.applicationsecond.models.Post;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostListActivity extends AppCompatActivity implements AdapterRecyclerViewPosts.Listener{

    @BindView(R.id.recycler_view_post_list_activity)
    RecyclerView recyclerView;
    @BindView(R.id.view_switcher_post_list_activity)
    ViewSwitcher viewSwitcher;
    //-----------------------------------
    private AdapterRecyclerViewPosts adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        ButterKnife.bind(this);

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
        actionBar.setTitle(getResources().getString(R.string.action_bar_title_posts));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureRecyclerView() {
        adapter = new AdapterRecyclerViewPosts(generateOptionsForAdapter(PostHelper.getUsersPosts(Utils.getCurrentUser().getUid())), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

    }
    private FirestoreRecyclerOptions<Post> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {
        viewSwitcher.setDisplayedChild(adapter.getItemCount() == 0? 1 : 0);
    }
}
