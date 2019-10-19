package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Post;

import java.util.List;

public class AdapterPostListFragment extends RecyclerView.Adapter<ViewHolderPosts> {

    private List<Post> postList;

    public AdapterPostListFragment(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_list_item, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPosts holder, int position) {
        holder.updateUi(this.postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
