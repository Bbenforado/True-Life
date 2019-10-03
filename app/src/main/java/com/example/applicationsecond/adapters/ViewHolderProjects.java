package com.example.applicationsecond.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Project;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderProjects extends RecyclerView.ViewHolder {

    @BindView(R.id.owner_of_post)
    TextView ownerOfPostTextView;
    @BindView(R.id.content_of_news) TextView contentOfNewsTextView;
    @BindView(R.id.title_of_news) TextView titleTextView;
    @BindView(R.id.text_view_view_more)
    TextView textViewViewMore;
    @BindView(R.id.date_creation_project_item) TextView dateCreationProjectTextView;

    public ViewHolderProjects(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateUi(Project project) {
        if (project.getDescription().length() > 100) {
            String newContent = project.getDescription().substring(0, 100) + "...";
            contentOfNewsTextView.setText(newContent);
        } else {
            contentOfNewsTextView.setText(project.getDescription());
        }
        titleTextView.setText(project.getTitle());
        if (project.getAuthorId() != null) {
            ownerOfPostTextView.setText(project.getAuthorId());
        }

        if (project.getCreationDate() != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(project.getCreationDate());
            dateCreationProjectTextView.setText(date);
        }

    }
}
