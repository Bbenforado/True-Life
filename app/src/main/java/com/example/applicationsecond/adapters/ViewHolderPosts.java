package com.example.applicationsecond.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.PostHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Post;
import com.example.applicationsecond.models.User;
import com.example.applicationsecond.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderPosts extends RecyclerView.ViewHolder {

    @BindView(R.id.owner_of_post_post_list_item)
    TextView ownerOfPostTextView;
    @BindView(R.id.content_of_post_post_list_item) TextView contentOfNewsTextView;
    @BindView(R.id.title_of_post_post_list_item) TextView titleTextView;
    @BindView(R.id.date_post_list_item) TextView dateTextView;
    @BindView(R.id.cardview_post_item)
    CardView cardView;
    private Context context;

    public ViewHolderPosts(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public void updateUi(Post post) {
        contentOfNewsTextView.setText(post.getContent());
        titleTextView.setText(post.getTitle());
        if (post.getAuthorId() != null) {
            UserHelper.getUser(post.getAuthorId()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        ownerOfPostTextView.setText(user.getUsername());
                    }
                }
            });

        }

        if (post.getDateOfPublication() != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(post.getDateOfPublication());
            dateTextView.setText(date);
        }
        if (post.getAuthorId().equals(Utils.getCurrentUser().getUid())) {
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    displayDialogToDeleteProject(post.getId());

                    return  true;
                }
            });
        }
    }

    private void displayDialogToDeleteProject(String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Do you want to delete this post?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PostHelper.deletePost(postId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }
}
