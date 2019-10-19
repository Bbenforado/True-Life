package com.example.applicationsecond.api;

import android.widget.Toast;

import com.example.applicationsecond.models.Project;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    public static Task<Void> createProject(String id, String title, String description, String idAuthor, Date creationDate, long eventDate,
                                           boolean isPublished, String streetNumber, String streetName, String postalCode,
                                           String city, String country, String latLng) {
        Project projectToCreate = new Project(id, title, description, idAuthor, creationDate, eventDate, isPublished, streetNumber, streetName,
                postalCode, city, country, latLng);
        return ProjectHelper.getProjectsCollection().document(id).set(projectToCreate);
    }

    public static Task<Void> createProjectWithImage(String id, String title, String description, String idAuthor, Date creationDate,
                                                    long eventDate, boolean isPublished, String urlPhoto,
                                                    String streetNumber, String streetName,String postalCode,
                                                    String city, String country, String latLng) {
        Project project = new Project(id, title, description, idAuthor, creationDate,
            eventDate, isPublished, urlPhoto, streetNumber, streetName, postalCode, city, country, latLng);
        return ProjectHelper.getProjectsCollection().document(id).set(project);
    }

    //GET
    public static Query getFuturesProjectsForOneUser(String userId, long date) {
        return ProjectHelper.getProjectsCollection()
                .whereEqualTo("published", true)
                .whereEqualTo("authorId", userId)
                .whereGreaterThanOrEqualTo("eventDate", date)
                .limit(50);
    }

    public static Query getPublishedProjects(String userId) {
        return ProjectHelper.getProjectsCollection()
                .whereEqualTo("published", true)
                .whereEqualTo("authorId", userId)
                .limit(50);
    }

    public static Query getProjectsForOneUser(String userId) {
        return ProjectHelper.getProjectsCollection()
                .whereEqualTo("published", true)
                .whereArrayContains("usersWhoSubscribed", userId)
                .limit(50);
    }

    public static Query getProjectsDependingOnCity(String city) {
        return ProjectHelper.getProjectsCollection()
                .whereEqualTo("published", true)
                .whereEqualTo("city", city)
                .limit(50);
    }

    public static Query getProjects() {
        return ProjectHelper.getProjectsCollection()
                .limit(50);
    }

    public static Task<QuerySnapshot> getProjectsOfACity(String city) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("city", city).get();
    }

    public static Task<QuerySnapshot> getProjectsDependingOnKeyWords(String keyword) {
        return ProjectHelper.getProjectsCollection().whereArrayContains("title", keyword).get();
    }


    public static Task<DocumentSnapshot> getProject(String id) {
        return ProjectHelper.getProjectsCollection().document(id).get();
    }

    /*public static Task<QuerySnapshot> getPublishedProjects() {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).get();
    }*/

    /*public static Task<QuerySnapshot> getUsersPublishedProjects(String currentUserId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).whereEqualTo("authorId", currentUserId).get();
    }*/

    public static Query getUsersPublishedProjects(String currentUserId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", true).whereEqualTo("authorId", currentUserId);
    }

   /* public static Task<QuerySnapshot> getUsersNotPublishedProjects(String userId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", false).whereEqualTo("authorId", userId).get();
    }*/

    public static Query getUsersNotPublishedProjects(String userId) {
        return ProjectHelper.getProjectsCollection().whereEqualTo("published", false).whereEqualTo("authorId", userId);
    }

    public static Task<Void> deleteProject(String id) {
        return ProjectHelper.getProjectsCollection().document(id).delete();
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
    public static Task<Void> updateTitle(String projectId, String title) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("title", title);
    }

    public static Task<Void> updateDescription(String projectId, String description) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("description", description);
    }

    public static Task<Void> updateStreetNumber(String projectId, String streetNumber) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("streetNumber", streetNumber);
    }
    public static Task<Void> updateStreetName(String projectId, String streetName) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("streetName", streetName);
    }
    public static Task<Void> updatePostalCode(String projectId, String postalCode) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("postalCode", postalCode);
    }
    public static Task<Void> updateCity(String projectId, String city) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("city", city);
    }
    public static Task<Void> updateCountry(String projectId, String country) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("country", country);
    }
    public static Task<Void> updateEventDate(String projectId, long eventDate) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("eventDate", eventDate);
    }
    public static Task<Void> updateUrlPhoto(String projectId, String urlPhoto) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("urlPhoto", urlPhoto);
    }
    public static Task<Void> updateLatLng(String projectid, String latLng) {
        return ProjectHelper.getProjectsCollection().document(projectid).update("latLng", latLng);
    }
    public static Task<Void> updateIsPublished(String projectId, boolean isPublished) {
        return ProjectHelper.getProjectsCollection().document(projectId).update("published", isPublished);
    }

}
