package com.example.applicationsecond.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.applicationsecond.R;
import com.example.applicationsecond.models.Chat;
import com.example.applicationsecond.models.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class AdapterUsersChats extends RecyclerView.Adapter<ViewHolderUsersChats> {


   /* public interface Listener {
        void onDataChanged();
    }*/
   private List<Message> messages;

    private RequestManager glide;
   // private AdapterUsersChats.Listener callback;

    /*public AdapterUsersChats(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }*/
    public AdapterUsersChats(List<Message> messages, RequestManager glide) {
        this.messages = messages;
        this.glide = glide;
    }

    /*@Override
    protected void onBindViewHolder(@NonNull ViewHolderUsersChats viewHolderUsersChats, int i, @NonNull Message message) {
        viewHolderUsersChats.updateUi(message, glide);
    }*/

    @NonNull
    @Override
    public ViewHolderUsersChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderUsersChats(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.users_chats_activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUsersChats holder, int position) {
        holder.updateUi(messages.get(position), glide);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public Message getItem(int position) {
        return messages.get(position);
    }

    /*@Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }*/
}
