package com.example.applicationsecond.api;

import com.example.applicationsecond.adapters.AdapterRecyclerViewPosts;
import com.example.applicationsecond.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

public class PostHelper {

    private static final String COLLECTION_NAME = "posts";

    public static CollectionReference getPostsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createPost(String id, String title, String content, String authorId, Date dateOfPublication) {
        Post postToCreate = new Post(id, title, content, authorId, dateOfPublication);
        return PostHelper.getPostsCollection().document(id).set(postToCreate);
    }

    public static Task<DocumentSnapshot> getPost(String id) {
        return PostHelper.getPostsCollection().document(id).get();
    }

    public static Query getUsersPosts(String userId) {
        return PostHelper.getPostsCollection()
                .whereEqualTo("authorId", userId)
                .limit(50);
    }

    public static Task<Void> deletePost(String postId) {
        return PostHelper.getPostsCollection().document(postId).delete();
    }

}
