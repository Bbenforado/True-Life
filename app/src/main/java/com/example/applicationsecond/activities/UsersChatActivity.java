package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.AdapterUsersChats;
import com.example.applicationsecond.api.ChatHelper;
import com.example.applicationsecond.api.MessageHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Chat;
import com.example.applicationsecond.models.Message;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<String> usersChats;
    private List<Message> messageList;
    private SharedPreferences preferences;
    private long userLastVisit;
    //---------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String LAST_VISIT_TIME = "lastVisitTime";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_chat);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (preferences.getString(LAST_VISIT_TIME, null) != null) {
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(preferences.getString(LAST_VISIT_TIME, null));
                userLastVisit = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        configureToolbar();
        configureRecyclerView();
        configureOnClickRecyclerView();
    }

    private void configureRecyclerView() {
        adapter = new AdapterUsersChats(generateOptionsForAdapter(ChatHelper.getChatWhereTheUserIsInvolved(Utils.getCurrentUser().getUid())),
                        Glide.with(this), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

  /*  private void configureRecyclerView() {
        UserHelper.getUser(Utils.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    messageList = new ArrayList<>();

                    if (user.getProjectsSubscribedId() != null) {
                        if (user.getProjectsSubscribedId().size() > 0) {
                            for (int i = 0; i < user.getProjectsSubscribedId().size(); i++) {
                                Query query = ChatHelper.getChatCollection().document(user.getProjectsSubscribedId().get(i)).collection("messages")
                                        .orderBy("dateCreated", Query.Direction.DESCENDING);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            int unreadMessages = task.getResult().getDocuments().size();

                                            System.out.println("unread messages = " + unreadMessages);

                                            if (task.getResult().getDocuments().size() > 0) {
                                                Message message = task.getResult().getDocuments().get(0).toObject(Message.class);
                                                messageList.add(message);
                                                adapter = new AdapterUsersChats(messageList, Glide.with(getApplicationContext()));
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                recyclerView.setAdapter(adapter);
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
    }*/

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.users_chats_activity_item)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        //launchChatActivity(message.getChatName());
                    }
                });
    }

    private void launchChatActivity(String chatName) {
        Intent chatIntent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("chatName", chatName);
        chatIntent.putExtras(bundle);
        startActivity(chatIntent);
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
