package com.example.applicationsecond.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Project;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderUsersProjectsList extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_users_projects_item_creation_date)
    TextView creationDateTextView;
    @BindView(R.id.activity_users_projects_item_title) TextView titleTextView;

    public ViewHolderUsersProjectsList(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateUi(Project project) {
        titleTextView.setText(project.getTitle());
        if (project.getCreationDate() != null) {

            //formatter la date
            creationDateTextView.setText(project.getCreationDate().toString());
        }
    }
}
