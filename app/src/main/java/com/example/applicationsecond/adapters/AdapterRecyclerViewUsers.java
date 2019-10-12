package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.User;

import java.util.List;

public class AdapterRecyclerViewUsers extends RecyclerView.Adapter<ViewHolderUsers> {

    private List<User> users;
    private RequestManager glide;

    public AdapterRecyclerViewUsers(List<User> users, RequestManager glide) {
        this.users = users;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ViewHolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_list_fragment_item, parent, false);
        return new ViewHolderUsers(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUsers holder, int position) {
        holder.updateUi(users.get(position), glide);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public User getUser(int position){
        return this.users.get(position);
    }
}
