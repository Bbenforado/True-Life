package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.ChatAdapter;
import com.example.applicationsecond.api.MessageHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Message;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.applicationsecond.utils.Utils.getCurrentUser;

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
    private SharedPreferences preferences;
    //---------------------------------------------
    //---------------------------------------------
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String CHAT_NAME = "chatName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        String chatName = preferences.getString(CHAT_NAME, null);
        configureRecyclerView(chatName);
        getCurrentUserFromFirestore();
    }

    //-------------------------------
    //ACTIONS
    //---------------------------------
    @OnClick(R.id.activity_chat_send_button)
    public void onClickSendMessage() {

    }

    @OnClick(R.id.activity_chat_add_file_button)
    public void addFile() {

    }

    //---------------------------------
    //
    //-----------------------------------
    private void getCurrentUserFromFirestore() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    private void configureRecyclerView(String chatName) {
        currentChatName = chatName;
        adapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(currentChatName)),
                Glide.with(this), this, getCurrentUser().getUid());

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                //super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
