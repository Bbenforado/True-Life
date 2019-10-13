package com.example.applicationsecond.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    //-----------------------------------
    //BIND VIEWS
    //-------------------------------------
    @BindView(R.id.activity_chat_item_root_view)
    RelativeLayout rootView;
    @BindView(R.id.activity_chat_item_profile_container)
    LinearLayout profileContainer;
    @BindView(R.id.activity_chat_item_profile_container_profile_image)
    ImageView imageViewProfile;
    @BindView(R.id.activity_chat_item_message_container) RelativeLayout messageContainer;
    @BindView(R.id.activity_chat_item_message_container_image_sent_cardview)
    CardView cardViewImageSent;
    @BindView(R.id.activity_chat_item_message_container_image_sent_cardview_image) ImageView imageViewSent;
    @BindView(R.id.activity_chat_item_message_container_text_message_container) LinearLayout textMessageContainer;
    @BindView(R.id.activity_chat_item_message_container_text_message_container_text_view)
    TextView textViewMessage;
    @BindView(R.id.activity_chat_item_message_container_text_view_date) TextView textViewDate;
    //-------------------------------------
    private final int colorCurrentUser;
    private final int colorRemoteUser;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.red);
    }

    public void updateWithMessage(Message message, String currentUserId, RequestManager glide) {

        //check if current user is the sender
        Boolean isCurrentUser = message.getUserSender().getId().equals(currentUserId);

        textViewMessage.setText(message.getMessage());
        textViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);
        if (message.getDateCreated() != null) {
            textViewDate.setText(convertDateToHour(message.getDateCreated()));
        }

        if (message.getUserSender().getUrlPhoto() != null) {
            glide.load(message.getUserSender().getUrlPhoto())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);
        }
        if (message.getUrlImage() != null) {
            glide.load(message.getUrlImage())
                    .into(imageViewSent);
            imageViewSent.setVisibility(View.VISIBLE);
        } else {
            imageViewSent.setVisibility(View.GONE);
        }

        ((GradientDrawable) textMessageContainer.getBackground()).setColor(isCurrentUser?
                colorCurrentUser : colorRemoteUser);
        updateDesignDependingOnUser(isCurrentUser);
    }

    private void updateDesignDependingOnUser(Boolean isSender) {
        //PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender?
                RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        profileContainer.setLayoutParams(paramsLayoutHeader);

        //MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender? RelativeLayout.LEFT_OF :
                RelativeLayout.RIGHT_OF, R.id.activity_chat_item_profile_container);
        messageContainer.setLayoutParams(paramsLayoutContent);

        //CARDVIEW IMAGE SENT
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender? RelativeLayout.ALIGN_LEFT :
                RelativeLayout.ALIGN_RIGHT, R.id.activity_chat_item_message_container_text_message_container);
        cardViewImageSent.setLayoutParams(paramsImageView);
        rootView.requestLayout();
    }

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }
}
