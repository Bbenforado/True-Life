package com.example.applicationsecond.api;

import android.app.DownloadManager;

import com.example.applicationsecond.models.Message;
import com.example.applicationsecond.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MessageHelper {

    public static final String COLLECTION_NAME = "messages";

    public static Query getAllMessageForChat(String chat) {
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    /*public static Query getLastMessageOfChat(String chatName) {
        return ChatHelper.getChatCollection()
                .document(chatName)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(1);
    }*/

    public static Task<DocumentReference> createMessageForChat(String textMessage,
                                                               String chatName,
                                                               User userSender) {
        Message message = new Message(textMessage, userSender, chatName);
        return ChatHelper.getChatCollection().document(chatName).collection(COLLECTION_NAME)
                .add(message);
    }

    /*public static Task<QueryDocumentSnapshot> getLastMessageOfChat(String chatName) {
        return ChatHelper.getChatCollection().document(chatName).collection(COLLECTION_NAME).orderBy("dateCreated").limit(1).get();
    }*/
}
