package com.example.applicationsecond.api;

import com.example.applicationsecond.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class PostHelper {

    private static final String COLLECTION_NAME = "posts";

    public static CollectionReference getPostsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //CREATE
    public static Task<Void> createPost(String title, String content, String authorName, Date dateOfPublication) {
        Post postToCreate = new Post(title, content, authorName, dateOfPublication);
        return PostHelper.getPostsCollection().document().set(postToCreate);
    }

    //GET
    public static Task<DocumentSnapshot> getPost(String id) {
        return PostHelper.getPostsCollection().document(id).get();
    }

}
