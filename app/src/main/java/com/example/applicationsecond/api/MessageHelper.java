package com.example.applicationsecond.api;

import android.app.DownloadManager;

import com.example.applicationsecond.models.Message;
import com.example.applicationsecond.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public class MessageHelper {

    public static final String COLLECTION_NAME = "messages";

    public static Query getAllMessageForChat(String chat) {
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    public static Task<DocumentReference> createMessageForChat(String textMessage,
                                                               String chatName,
                                                               User userSender) {
        Message message = new Message(textMessage, userSender);
        return ChatHelper.getChatCollection().document(chatName).collection(COLLECTION_NAME)
                .add(message);
    }
}
