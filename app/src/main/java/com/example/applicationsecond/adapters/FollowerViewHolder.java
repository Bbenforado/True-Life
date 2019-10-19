package com.example.applicationsecond.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.AssociationProfileActivity;
import com.example.applicationsecond.activities.ProfileActivity;
import com.example.applicationsecond.models.User;
import com.google.firebase.firestore.CollectionReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.modal_follower_item_image) ImageView imageViewUser;
    @BindView(R.id.modal_follower_item_text_view) TextView textViewUsername;
    @BindView(R.id.modal_follower_item_image_association) ImageView imageViewBadge;
    @BindView(R.id.layout_modal_sheet) LinearLayout layout;
    //------------------------------------------
    //-------------------------------------------
    private Context context;

    public FollowerViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public void updateUi(User user, RequestManager glide) {
        if (user.getUrlPhoto() != null) {
            glide.load(user.getUrlPhoto()).apply(RequestOptions.circleCropTransform())
                    .into(imageViewUser);
        } else {
           imageViewUser.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile));
        }
        textViewUsername.setText(user.getUsername());
        if (user.isAssociation()) {
            imageViewBadge.setVisibility(View.VISIBLE);
        } else {
            imageViewBadge.setVisibility(View.GONE);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isAssociation()) {
                    Intent authorProfileIntent = new Intent(context, AssociationProfileActivity.class);
                    authorProfileIntent.putExtra("authorId", user.getId());
                    context.startActivity(authorProfileIntent);
                } else {
                    Intent profileIntent = new Intent(context, ProfileActivity.class);
                    profileIntent.putExtra("profileId", user.getId());
                    context.startActivity(profileIntent);
                }
            }
        });
    }
}
