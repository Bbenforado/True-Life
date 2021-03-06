package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.ChatAdapter;
import com.example.applicationsecond.api.ChatHelper;
import com.example.applicationsecond.api.MessageHelper;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Message;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.ItemClickSupport;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.applicationsecond.utils.Utils.formatLocation;
import static com.example.applicationsecond.utils.Utils.getCurrentUser;
import static com.example.applicationsecond.utils.Utils.isNetworkAvailable;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.Listener {

    //-------------------------------
    //BIND VIEWS
    //----------------------------------
    @BindView(R.id.activity_chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_chat_text_view_recycler_view_empty)
    TextView textViewRecyclerViewEmpty;
    @BindView(R.id.activity_chat_message_edit_text)
    TextInputEditText editTextMessage;
    @BindView(R.id.activity_chat_image_chosen_preview)
    ImageView imageViewPreview;
    //------------------------------------
    //
    //---------------------------------------
    private ChatAdapter adapter;
    @Nullable private User modelCurrentUser;
    private String currentChatName;
    //---------------------------------------------
    //---------------------------------------------
    public static final String COLLECTION_NAME_CHATS = "chats";
    public static final String COLLECTION_NAME_MESSAGES = "messages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        configureToolbar();
        String chatName = getIntent().getExtras().getString("chatName");
        configureRecyclerView(chatName);
        getCurrentUserFromFirestore();
    }

    /**
     * save the time (milliseconds) for the visit of the user on this chat.
     * used to get the unread messages for the user
     */
    private void saveTimeOfTheVisit() {
        if (isNetworkAvailable(this)) {
            Date date = new Date();
            long dateInMilliseconds = date.getTime();
            Map<String, Long> lastChatVisit = new HashMap<>();
            if (modelCurrentUser.getLastChatVisit() != null) {
                try {
                    lastChatVisit = modelCurrentUser.getLastChatVisit();
                    for (Map.Entry<String, Long> entry : lastChatVisit.entrySet()) {
                        if (entry.getKey().equals(currentChatName)) {
                            entry.setValue(dateInMilliseconds);
                            UserHelper.updateLastChatVisit(getCurrentUser().getUid(), lastChatVisit);
                        } else {
                            lastChatVisit.put(currentChatName, dateInMilliseconds);
                            UserHelper.updateLastChatVisit(getCurrentUser().getUid(), lastChatVisit);
                        }
                    }
                } catch (ConcurrentModificationException exception) {
                    System.out.println("error = " + exception.getMessage());
                }
            } else {
                lastChatVisit.put(currentChatName, dateInMilliseconds);
                UserHelper.updateLastChatVisit(getCurrentUser().getUid(), lastChatVisit);
            }
        }
    }

    //---------------------------------------
    //CONFIGURATION
    //------------------------------------------
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chat");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureRecyclerView(String chatName) {
        currentChatName = chatName;
        adapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(currentChatName)),
                Glide.with(this), this, getCurrentUser().getUid());
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    //-------------------------------
    //ACTIONS
    //---------------------------------
    @OnClick(R.id.activity_chat_send_button)
    public void onClickSendMessage() {
        if (isNetworkAvailable(this)) {
            if (!TextUtils.isEmpty(editTextMessage.getText()) &&
                    modelCurrentUser != null) {
                Date date = new Date();
                long dateInMillis = date.getTime();

                CollectionReference ref = FirebaseFirestore.getInstance().collection(COLLECTION_NAME_CHATS);
                String id = ref.document(currentChatName).collection(COLLECTION_NAME_MESSAGES).document().getId();

                MessageHelper.createMessageForChat(id, editTextMessage.getText().toString(),
                        currentChatName, modelCurrentUser, dateInMillis).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

                ChatHelper.addMessageId(currentChatName, id);
                MessageHelper.getLastMessageOfAChat(currentChatName).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Message message = document.toObject(Message.class);
                                ChatHelper.updateLastMessage(currentChatName, message);
                            }
                        }
                    }
                });

                ProjectHelper.getProject(currentChatName).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Project project = task.getResult().toObject(Project.class);
                            if (project.getUsersWhoSubscribed() != null) {
                                if (project.getUsersWhoSubscribed().size() > 0) {
                                    List<String> users = new ArrayList<>();
                                    for (int i = 0; i < project.getUsersWhoSubscribed().size(); i++) {
                                        users.add(project.getUsersWhoSubscribed().get(i));
                                        ChatHelper.updateInvolvedUsers(currentChatName, users);
                                    }
                                }
                            }
                        }
                    }
                });
                editTextMessage.setText("");
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_try_again_toast), Toast.LENGTH_SHORT).show();
        }
    }

    //---------------------------------
    //
    //-----------------------------------
    private void getCurrentUserFromFirestore() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    modelCurrentUser = task.getResult().toObject(User.class);
                    saveTimeOfTheVisit();
                }
            }
        });
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {
        textViewRecyclerViewEmpty.setVisibility(adapter.getItemCount() == 0 ?
                View.VISIBLE : View.GONE);
    }
}
