package com.example.applicationsecond.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatHelper {

    private static final String COLLECTION_NAME = "chats";

    public static CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }


}
