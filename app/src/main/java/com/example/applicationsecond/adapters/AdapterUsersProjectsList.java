package com.example.applicationsecond.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Project;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdapterUsersProjectsList extends FirestoreRecyclerAdapter<Project, ViewHolderUsersProjectsList> {
    public interface Listener {
        void onDataChanged();
    }

    private RequestManager glide;
    private Listener callback;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterUsersProjectsList(@NonNull FirestoreRecyclerOptions<Project> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderUsersProjectsList viewHolderUsersProjectsList, int i, @NonNull Project project) {
        viewHolderUsersProjectsList.updateUi(project, glide);
    }

    @NonNull
    @Override
    public ViewHolderUsersProjectsList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderUsersProjectsList(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_my_projects_item, parent, false));
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}
