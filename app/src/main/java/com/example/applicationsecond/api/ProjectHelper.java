package com.example.applicationsecond.api;

import android.widget.Toast;

import com.example.applicationsecond.models.Project;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Map;

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

    public static Task<Void> createprojectWithImage(String id, String title, String description, String idAuthor, Date creationDate, boolean isPublished, String urlPhoto) {
        Project project = new Project(id, title, description, idAuthor, creationDate, isPublished, urlPhoto);
        return ProjectHelper.getProjectsCollection().document(id).set(project);
    }

    //GET
    public static Task<DocumentSnapshot> getProject(String id) {
        return ProjectHelper.getProjectsCollection().document(id).get();
    }

    public static Task<QuerySnapshot> getPublishedProjects() {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).get();
    }

    public static Task<QuerySnapshot> getUsersPublishedProjects(String currentUserId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).whereEqualTo("authorId", currentUserId).get();
    }

    public static Task<QuerySnapshot> getUsersNotPublishedProjects(String userId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", false).whereEqualTo("authorId", userId).get();
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

   /* public static Task<Void> updateProject(String projectId, Map<String, Object> data) {
        return ProjectHelper.getProjectsCollection().document(projectId).set(data);
    }*/

    public static Task<Void> updateTitle(String projectId, String title) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("title", title);
    }

    public static Task<Void> updateDescription(String projectId, String description) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("description", description);
    }

}
