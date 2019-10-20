package com.example.applicationsecond.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.AdapterUsersChats;
import com.example.applicationsecond.api.ChatHelper;
import com.example.applicationsecond.models.Chat;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersChatActivity extends AppCompatActivity implements AdapterUsersChats.Listener{

    //----------------------------
    //BINDVIEWS
    //---------------------------------
    @BindView(R.id.recycler_view_users_chats_activity)
    RecyclerView recyclerView;
    @BindView(R.id.view_switcher_users_chats_activity)
    ViewSwitcher viewSwitcher;
    //------------------------------------
    private AdapterUsersChats adapter;
    //---------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_chat);
        ButterKnife.bind(this);
        configureToolbar();
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        adapter = new AdapterUsersChats(generateOptionsForAdapter(ChatHelper.getChatWhereTheUserIsInvolved(Utils.getCurrentUser().getUid())),
                Glide.with(getApplicationContext()), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    //---------------------------------------
    //CONFIGURATION
    //------------------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Messages");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private FirestoreRecyclerOptions<Chat> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {
        viewSwitcher.setDisplayedChild(adapter.getItemCount() == 0? 1 : 0);
    }
}
