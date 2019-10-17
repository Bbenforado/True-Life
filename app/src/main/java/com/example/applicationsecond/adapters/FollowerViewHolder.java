package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.User;
import com.google.firebase.firestore.CollectionReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.modal_follower_item_image)
    ImageView imageViewUser;
    @BindView(R.id.modal_follower_item_text_view)
    TextView textViewUsername;
    @BindView(R.id.modal_follower_item_image_association) ImageView imageViewBadge;

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
    }
}
