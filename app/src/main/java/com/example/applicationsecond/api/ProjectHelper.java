package com.example.applicationsecond.api;

import com.example.applicationsecond.models.Project;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class ProjectHelper {

    private static final String COLLECTION_NAME = "projects";

    public static CollectionReference getProjectsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //CREATE
    /*public static Task<Void> createProject(String id, String title, String description, String idAuthor, Date creationDate, boolean isPublished) {
        Project projectToCreate = new Project(id, title, description, idAuthor, creationDate, isPublished);
        return ProjectHelper.getProjectsCollection().document().set(projectToCreate);
    }*/

    public static Task<Void> createProject(String id, String title, String description, String idAuthor, Date creationDate, boolean isPublished) {
        Project projectToCreate = new Project(id, title, description, idAuthor, creationDate, isPublished);
        return ProjectHelper.getProjectsCollection().document(id).set(projectToCreate);
    }

    //GET
    public static Task<DocumentSnapshot> getProject(String id) {
        return ProjectHelper.getProjectsCollection().document(id).get();
    }

    public static Task<QuerySnapshot> getPublishedProjects() {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).get();
    }

    public static Task<QuerySnapshot> getUsersPublishedProjects(String currentUserId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).whereEqualTo("idAuthor", currentUserId).get();
    }

    public static Task<QuerySnapshot> getProjectsForOneUser(String userId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).whereArrayContains("usersWhoSubscribed", userId).get();
    }

    //UPDATE
    public static Task<Void> addSubscriptionToProject(String projectId, String userId) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("usersWhoSubscribed",
                FieldValue.arrayUnion(userId));
    }

    public static Task<Void> removeProjectSubscription(String projectId, String userId) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("usersWhoSubscribed",
                FieldValue.arrayRemove(userId));
    }

}
