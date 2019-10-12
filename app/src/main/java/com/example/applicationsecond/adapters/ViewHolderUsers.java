package com.example.applicationsecond.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderUsers extends RecyclerView.ViewHolder {

    @BindView(R.id.user_list_fragment_item_image)
    ImageView imageView;
    @BindView(R.id.user_list_fragment_item_username)
    TextView textViewUsername;


    public ViewHolderUsers(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateUi(User user, RequestManager glide) {
        if (user.getUrlPhoto() != null) {
            glide.load(user.getUrlPhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }

        textViewUsername.setText(user.getUsername());
    }
}
