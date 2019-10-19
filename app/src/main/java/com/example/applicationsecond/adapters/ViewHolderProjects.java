package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.applicationsecond.utils.Utils.capitalizeFirstLetter;

public class ViewHolderProjects extends RecyclerView.ViewHolder {

    @BindView(R.id.owner_of_post)
    TextView ownerOfPostTextView;
    @BindView(R.id.content_of_news) TextView contentOfNewsTextView;
    @BindView(R.id.title_of_news) TextView titleTextView;
    @BindView(R.id.date_creation_project_item) TextView dateCreationProjectTextView;
    @BindView(R.id.image_view_verified_fragment_page_item)
    ImageView imageViewAssociationIcon;
    @BindView(R.id.fragment_page_item_image) ImageView imageView;
    private Context context;

    public ViewHolderProjects(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public void updateUi(Project project, RequestManager glide) {
        if (project.getDescription().length() > 100) {
            String newContent = capitalizeFirstLetter(project.getDescription().substring(0, 100) + "...");
            contentOfNewsTextView.setText(newContent);
        } else {
            contentOfNewsTextView.setText(capitalizeFirstLetter(project.getDescription()));
        }
        titleTextView.setText(capitalizeFirstLetter(project.getTitle()));
        if (project.getAuthorId() != null) {
            UserHelper.getUser(project.getAuthorId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        ownerOfPostTextView.setText(capitalizeFirstLetter(user.getUsername()));
                        if (user.isAssociation()) {
                            imageViewAssociationIcon.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

        }

        if (project.getCreationDate() != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(project.getCreationDate());
            dateCreationProjectTextView.setText(date);
        }

        if (project.getUrlPhoto() != null) {
            glide.load(project.getUrlPhoto())
                    .apply(RequestOptions.noTransformation())
                    .into(this.imageView);

        }

        Date todayDate = new Date();
        long dateInMilliseconds = todayDate.getTime();
        if (project.getEventDate() < dateInMilliseconds) {
            titleTextView.setTextColor(context.getResources().getColor(R.color.disabledColor));
            contentOfNewsTextView.setTextColor(context.getResources().getColor(R.color.disabledColor));
            dateCreationProjectTextView.setTextColor(context.getResources().getColor(R.color.disabledColor));
            ownerOfPostTextView.setTextColor(context.getResources().getColor(R.color.disabledColor));
        }

    }
}
