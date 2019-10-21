package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Project;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderUsersProjectsList extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_users_projects_item_creation_date)
    TextView creationDateTextView;
    @BindView(R.id.activity_users_projects_item_title) TextView titleTextView;
    @BindView(R.id.activity_users_projects_item_image)
    ImageView imageView;
    private Context context;

    public ViewHolderUsersProjectsList(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public void updateUi(Project project, RequestManager glide) {
        titleTextView.setText(project.getTitle());
        if (project.getCreationDate() != null) {

            String date = new SimpleDateFormat("dd/MM/yyyy").format(project.getCreationDate());
            creationDateTextView.setText(date);
        }

        if (project.getUrlPhoto() != null) {
            glide.load(project.getUrlPhoto())
                    .into(imageView);
        }
        Date todayDate = new Date();
        long dateInMilliseconds = todayDate.getTime();

        if (project.getEventDate() < dateInMilliseconds) {
            titleTextView.setTextColor(context.getResources().getColor(R.color.disabledColor));
            creationDateTextView.setTextColor(context.getResources().getColor(R.color.disabledColor));
        }
    }
}
