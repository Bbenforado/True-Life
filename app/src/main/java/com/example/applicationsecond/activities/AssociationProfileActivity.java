package com.example.applicationsecond.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AssociationProfileActivity extends AppCompatActivity {

    @BindView(R.id.follow_button)
    ImageButton followButton;
    @BindView(R.id.textview_follow) TextView textViewFollow;
    //--------------------------------
    private boolean isButtonClicked;
    private String authorId;
    private String currentUserId;
    private ColorStateList defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_profile);

        ButterKnife.bind(this);
        defaultColor = textViewFollow.getTextColors();
        currentUserId = Utils.getCurrentUser().getUid();
        Bundle bundle = getIntent().getExtras();
        authorId = null;
        if (bundle != null) {
            authorId = bundle.getString("authorId");
        }
        hideFollowButtonIfItsCurrentUserProfile();

    }

    @OnClick(R.id.follow_button)
    public void followThisAssociation() {
        isButtonClicked = !isButtonClicked;
        if (isButtonClicked) {
            //change the color of the button
            followButton.setBackground(getResources().getDrawable(R.drawable.ic_heart_red));
            textViewFollow.setTextColor(getResources().getColor(R.color.red));

            //create a subscription in user s tuple
            UserHelper.updateAssociationSubscriptions(currentUserId, authorId);

            //display toast message to tell the user he s now following this association
            Toast.makeText(this, "You are now following " + authorId, Toast.LENGTH_SHORT).show();


        } else {
            //unclick button
            UserHelper.removeAssociationSubscription(currentUserId, authorId);
            //change button color
            followButton.setBackground(getResources().getDrawable(R.drawable.ic_heart));
            textViewFollow.setTextColor(defaultColor);
            Toast.makeText(this, "You are not part of this project anymore", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideFollowButtonIfItsCurrentUserProfile() {
        //check if it s current user s profile
        if (currentUserId.equals(authorId)) {
            followButton.setEnabled(false);
            followButton.setBackground(getResources().getDrawable(R.drawable.ic_heart_disabled));
            textViewFollow.setTextColor(getResources().getColor(R.color.disabledColor));
        } else {
            followButton.setEnabled(true);
        }
    }
}
