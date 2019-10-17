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

public class AdapterModalFollower extends RecyclerView.Adapter<FollowerViewHolder> {

    private List<User> followers;
    private RequestManager glide;

    public AdapterModalFollower(List<User> followers, RequestManager glide) {
        this.followers = followers;
        this.glide = glide;
    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View result = inflater.inflate(R.layout.modal_follower_item, parent,
                false);
        return new FollowerViewHolder(result);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {
        holder.updateUi(followers.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    public User getFollower(int position) {
        return followers.get(position);
    }

    public void update(List<User> followers) {
        this.followers = followers;
        this.notifyDataSetChanged();
    }
}
