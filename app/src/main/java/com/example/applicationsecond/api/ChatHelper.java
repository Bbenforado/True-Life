package com.example.applicationsecond.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatHelper {

    private static final String COLLECTION_NAME = "chats";

    public static CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllChat() {
        return ChatHelper.getChatCollection()
                .orderBy("dateCreated")
                .limit(50);
    }


}
