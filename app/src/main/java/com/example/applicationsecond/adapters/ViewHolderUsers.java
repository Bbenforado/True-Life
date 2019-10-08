package com.example.applicationsecond.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationsecond.models.User;

import java.util.List;

public class ViewHolderUsers extends RecyclerView.ViewHolder {


    public ViewHolderUsers(@NonNull View itemView) {
        super(itemView);
    }

    public void updateUi(User user) {
        System.out.println("UPDATING ...");
    }
}
