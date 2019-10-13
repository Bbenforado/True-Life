package com.example.applicationsecond.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    private final RequestManager glide;
    private final String idCurrentUser;
    private Listener callback;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide, Listener callback, String idCurrentUser) {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i, @NonNull Message message) {
        chatViewHolder.updateWithMessage(message, this.idCurrentUser, this.glide);

    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_chat_item, parent, false));
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}
