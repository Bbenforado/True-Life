package com.example.applicationsecond.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Project;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdapterRecyclerViewProjects extends FirestoreRecyclerAdapter<Project, ViewHolderProjects> {

    public interface Listener {
        void onDataChanged();
    }

    private RequestManager glide;
    private Listener callback;

    public AdapterRecyclerViewProjects(@NonNull FirestoreRecyclerOptions<Project> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolderProjects onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderProjects(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.actuality_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProjects holder, int position, @NonNull Project project) {
        holder.updateUi(project, this.glide);
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}
