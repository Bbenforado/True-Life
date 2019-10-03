package com.example.applicationsecond.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Post;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderPosts extends RecyclerView.ViewHolder {

    @BindView(R.id.owner_of_post_post_list_item)
    TextView ownerOfPostTextView;
    @BindView(R.id.content_of_post_post_list_item) TextView contentOfNewsTextView;
    @BindView(R.id.title_of_post_post_list_item) TextView titleTextView;
    @BindView(R.id.date_post_list_item) TextView dateTextView;

    public ViewHolderPosts(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateUi(Post post) {
        contentOfNewsTextView.setText(post.getContent());
        titleTextView.setText(post.getTitle());
        if (post.getAuthorName() != null) {
            ownerOfPostTextView.setText(post.getAuthorName());
        }

        if (post.getDateOfPublication() != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy").format(post.getDateOfPublication());
            dateTextView.setText(date);
        }
    }
}
