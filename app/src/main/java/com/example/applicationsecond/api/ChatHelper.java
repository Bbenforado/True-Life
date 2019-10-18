package com.example.applicationsecond.api;

import com.example.applicationsecond.models.Chat;
import com.example.applicationsecond.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

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

    public static Query getChatWhereTheUserIsInvolved(String userId) {
        return ChatHelper.getChatCollection()
                .whereArrayContains("involvedUsers", userId)
                .limit(50);
    }

    //CREATE
    public static Task<Void> createChat(String id) {
        Chat chat = new Chat(id);
        return ChatHelper.getChatCollection().document(id).set(chat);
    }

    //GET
    public static Task<DocumentSnapshot> getChat(String id) {
        return ChatHelper.getChatCollection().document(id).get();
    }

    public static Task<QuerySnapshot> getChatsOfOneUser(String userId) {
        return ChatHelper.getChatCollection().whereArrayContains("involvedUsers", userId).get();
    }

    public static Task<Void> updateLastMessage(String chatName, Message message) {
        return ChatHelper.getChatCollection().document(chatName).update("lastMessage", message);
    }

    public static Task<Void> updateInvolvedUsers(String chatName, List<String> users) {
        return ChatHelper.getChatCollection().document(chatName).update("involvedUsers", users);
    }

    public static Task<Void> addInvolvedUser(String chatName, String userId) {
        return ChatHelper.getChatCollection().document(chatName).update("involvedUsers", FieldValue.arrayUnion(userId));
    }

    public static Task<Void> removeInvolvedUser(String chatName, String userId) {
        return ChatHelper.getChatCollection().document(chatName).update("involvedUsers", FieldValue.arrayRemove(userId));
    }



}
