package com.example.applicationsecond.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.MainActivity;
import com.example.applicationsecond.adapters.AdapterRecyclerViewUsers;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    @BindView(R.id.input_username_search_fragment)
    EditText inputUsername;
    //-----------------------------
    private List<User> users;
    private SharedPreferences preferences;
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String RESULTS = "results";




    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, result);

        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return result;
    }

    @OnClick(R.id.button_search_fragment)
    public void searchData() {
        if (!TextUtils.isEmpty(inputUsername.getText().toString())) {
            UserHelper.getUserForSearchOnName(inputUsername.getText().toString()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            users = new ArrayList<>();
                            users.add(user);
                        }

                        //send the result found to the fragment that will display results
                        Gson gson = new Gson();
                        preferences.edit().putString(RESULTS, gson.toJson(users)).apply();
                        UserListFragment fragment = new UserListFragment();
                        showFragment(fragment);


                    }
                }
            });
        }
    }

    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout_content_main_activity, fragment);
        transaction.commit();
    }

}
