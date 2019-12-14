package com.example.applicationsecond.api;

import com.example.applicationsecond.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String id, String username, boolean isAssociation, String city, String country) {
        User userToCreate = new User(id, username, isAssociation, city, country);
        return UserHelper.getUsersCollection().document(id).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String id) {
        return UserHelper.getUsersCollection().document(id).get();
    }

    public static Task<QuerySnapshot> getUserForSearchOnCity(String search) {
        return UserHelper.getUsersCollection()
                .whereEqualTo("association", true)
                .whereEqualTo("city", search).get();
    }

    public static Task<Void> addProjectsSubscriptions(String userId, String projectId) {
        return UserHelper.getUsersCollection().document(userId).update("projectsSubscribedId",
                FieldValue.arrayUnion(projectId));
    }

    public static Task<Void> updateLastChatVisit(String userId, Map<String, Long> lastChatVisit){
        return UserHelper.getUsersCollection().document(userId).update("lastChatVisit", lastChatVisit);
    }

    public static Task<Void> removeProjectSubscription(String userId, String projectId) {
        return UserHelper.getUsersCollection().document(userId).update("projectsSubscribedId",
                FieldValue.arrayRemove(projectId));
    }

    public static Task<Void> updateAssociationSubscriptions(String userId, String associationId) {
        return UserHelper.getUsersCollection().document(userId).update("associationSubscribedId",
                FieldValue.arrayUnion(associationId));
    }

    public static Task<Void> removeAssociationSubscription(String userId, String associationId) {
        return UserHelper.getUsersCollection().document(userId).update("associationSubscribedId",
                FieldValue.arrayRemove(associationId));
    }

    public static Task<Void> updatePublishedPostIdList(String userId, String postId) {
        return UserHelper.getUsersCollection().document(userId).update("publishedPostId",
                FieldValue.arrayUnion(postId));
    }
    public static Task<Void> removePublishedPostId(String userId, String postId) {
        return UserHelper.getUsersCollection().document(userId).update("publishedPostId",
                FieldValue.arrayRemove(postId));
    }

    public static Task<Void> updateUrlPhoto(String userId, String urlPhoto) {
        return UserHelper.getUsersCollection().document(userId).update("urlPhoto", urlPhoto);
    }

    public static Task<Void> updateUsername(String userId, String username) {
        return UserHelper.getUsersCollection().document(userId).update("username", username);
    }

    public static Task<Void> updateCountry(String userId, String country) {
        return UserHelper.getUsersCollection().document(userId).update("country", country);
    }

    public static Task<Void> updateCity(String userId, String city) {
        return UserHelper.getUsersCollection().document(userId).update("city", city);
    }
}
