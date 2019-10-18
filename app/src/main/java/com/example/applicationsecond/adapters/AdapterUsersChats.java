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
import com.example.applicationsecond.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class AdapterUsersChats extends FirestoreRecyclerAdapter<Chat, ViewHolderUsersChats> {


    public interface Listener {
        void onDataChanged();
    }

    private RequestManager glide;
    private AdapterUsersChats.Listener callback;

    public AdapterUsersChats(@NonNull FirestoreRecyclerOptions<Chat> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolderUsersChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderUsersChats(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.users_chats_activity_item, parent, false));
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderUsersChats viewHolderUsersChats, int i, @NonNull Chat chat) {
        viewHolderUsersChats.updateUi(chat, glide);
    }
}
