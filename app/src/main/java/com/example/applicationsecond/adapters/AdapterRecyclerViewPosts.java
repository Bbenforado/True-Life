package com.example.applicationsecond.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class AdapterRecyclerViewPosts extends FirestoreRecyclerAdapter<Post, ViewHolderPosts>{
    public interface Listener {
        void onDataChanged();
    }
    private AdapterRecyclerViewPosts.Listener callback;
    private RequestManager glide;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AdapterRecyclerViewPosts(@NonNull FirestoreRecyclerOptions<Post> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }


    @NonNull
    @Override
    public ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderPosts(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_item, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderPosts viewHolderPosts, int i, @NonNull Post post) {
        viewHolderPosts.updateUi(post);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}
