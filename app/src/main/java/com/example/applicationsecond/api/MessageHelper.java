package com.example.applicationsecond.api;

import android.app.DownloadManager;

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
}
