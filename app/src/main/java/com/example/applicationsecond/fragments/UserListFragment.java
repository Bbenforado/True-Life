package com.example.applicationsecond.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.applicationsecond.R;
import com.example.applicationsecond.adapters.AdapterRecyclerViewUsers;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

    @BindView(R.id.recycler_view_user_list_fragment)
    RecyclerView recyclerView;
    private AdapterRecyclerViewUsers adapter;
    private List<User> users;
    private SharedPreferences preferences;
    private static final String APP_PREFERENCES = "appPreferences";
    private static final String RESULTS = "results";


    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, result);

        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        retrieveResults();
        doBasicConfiguration();
        return result;
    }

    private void doBasicConfiguration() {
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        adapter = new AdapterRecyclerViewUsers(users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void retrieveResults() {
        Gson gson = new Gson();
        users = new ArrayList<>();
        String json = preferences.getString(RESULTS, null);
        Type type = new TypeToken<List<User>>() {
        }.getType();

        users = gson.fromJson(json, type);
    }

}
