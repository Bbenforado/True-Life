package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Project;

import java.util.List;

import butterknife.BindView;

public class AdapterRecyclerViewProjects extends RecyclerView.Adapter<ViewHolderProjects>{

    private List<Project> projectList;

    public AdapterRecyclerViewProjects(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ViewHolderProjects onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.actuality_list_item, parent, false);
        return new ViewHolderProjects(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProjects holder, int position) {
        holder.updateUi(this.projectList.get(position));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}
