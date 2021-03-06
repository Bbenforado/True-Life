package com.example.applicationsecond.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.ChatActivity;
import com.example.applicationsecond.api.MessageHelper;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Chat;
import com.example.applicationsecond.models.Message;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.applicationsecond.utils.Utils.convertDateToHour;

public class ViewHolderUsersChats extends RecyclerView.ViewHolder {

    @BindView(R.id.user_chats_activity_item_image)
    ImageView imageView;
    @BindView(R.id.user_chats_activity_item_last_message)
    TextView textViewLastMessage;
    @BindView(R.id.user_chats_activity_item_title) TextView textViewTitle;
    @BindView(R.id.user_chats_activity_item_time) TextView textViewTime;
    @BindView(R.id.user_chats_activity_item_unread_message_text_view) TextView textViewUnreadMessages;
    @BindView(R.id.layout_item)
    LinearLayout layout;

    private Context context;
    private int numberOfUnreadMessages;

    public ViewHolderUsersChats(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public void updateUi(Chat chat, RequestManager glide, Project project) {
        numberOfUnreadMessages = 0;
        textViewTitle.setText(project.getTitle());
        if (project.getUrlPhoto() != null) {
            glide.load(project.getUrlPhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
        if (chat.getLastMessage() != null) {
            Message lastMessage = chat.getLastMessage();
            textViewLastMessage.setText(giveNewSizeToTitle(lastMessage.getMessage()));
            textViewTime.setText(convertDateToHour(lastMessage.getDateCreated()));
            String userId = Utils.getCurrentUser().getUid();
            UserHelper.getUser(userId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (user.getLastChatVisit() != null) {
                            Long time = user.getLastChatVisit().get(chat.getId());
                            getUnreadMessages(chat.getId(), time);
                        }
                    }
                }
            });
        } else {
            textViewTime.setText("");
            textViewLastMessage.setText(context.getResources().getString(R.string.no_message_be_first_to_talk));
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchChatActivity(chat.getId());
            }
        });
    }

    private void launchChatActivity(String chatName) {
        Intent chatIntent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("chatName", chatName);
        chatIntent.putExtras(bundle);
        context.startActivity(chatIntent);
    }

    private void getUnreadMessages(String chatName, Long time) {
        numberOfUnreadMessages = 0;
        MessageHelper.getUnreadMessage(chatName, time).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Message message = document.toObject(Message.class);
                        if (!message.getUserSender().getId().equals(Utils.getCurrentUser().getUid())) {
                            numberOfUnreadMessages = numberOfUnreadMessages + 1;
                            textViewUnreadMessages.setVisibility(View.VISIBLE);
                            textViewUnreadMessages.setText(String.valueOf(numberOfUnreadMessages));
                        }
                    }
                }
            }
        });
        if (numberOfUnreadMessages == 0) {
            textViewUnreadMessages.setVisibility(View.GONE);
        }
    }

    private String giveNewSizeToTitle(String oldString) {
        if (oldString.length() > 10) {
            return oldString.substring(0, 10) + "...";
        } else {
            return oldString;
        }
    }
}
