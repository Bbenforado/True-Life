package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Project;

import java.util.List;

public class AdapterUsersProjectsList extends RecyclerView.Adapter<ViewHolderUsersProjectsList>{

    private List<Project> projectList;
    private RequestManager glide;

    public AdapterUsersProjectsList(List<Project> projectList, RequestManager glide) {
        this.projectList = projectList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ViewHolderUsersProjectsList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_my_projects_item, parent, false);
        return new ViewHolderUsersProjectsList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUsersProjectsList holder, int position) {
        holder.updateUi(projectList.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}
