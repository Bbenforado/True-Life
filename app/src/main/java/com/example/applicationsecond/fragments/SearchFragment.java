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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.activities.MainActivity;
import com.example.applicationsecond.adapters.AdapterRecyclerViewUsers;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    @BindView(R.id.input_city) EditText inputCity;
    @BindView(R.id.search_activity_text_view_title)
    TextView textViewTitle;
    @BindView(R.id.button_search_projects_fragment)
    MaterialButton buttonSearchProjects;
    @BindView(R.id.button_search_users_fragment) MaterialButton buttonSearchUsers;
    @BindView(R.id.id_layout) LinearLayout layout;
    @BindView(R.id.search_fragment_scroll_view)
    ScrollView scrollView;
    //-----------------------------
    private List<User> users;
    private SharedPreferences preferences;
    public static final String APP_PREFERENCES = "appPreferences";
    public static final String RESULTS = "results";
    public static final String KEYWORD = "keyword";
    private static final String CITY = "city";


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

    @OnClick(R.id.button_search_projects_fragment)
    public void searchProjectsInAGivenCity() {
        if (!TextUtils.isEmpty(inputCity.getText().toString())) {
                ActualityListFragment fragment = new ActualityListFragment("searchResults");
                preferences.edit().putString(CITY, inputCity.getText().toString()).apply();
                showFragment(fragment);

                layout.animate().translationY(-textViewTitle.getHeight());
                buttonSearchProjects.animate().translationY(-textViewTitle.getHeight());
                buttonSearchUsers.animate().translationY(-textViewTitle.getHeight());
                scrollView.animate().translationY(-textViewTitle.getHeight());
            } else {
                Toast.makeText(getContext(), "You have to fill the city field", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.button_search_users_fragment)
    public void searchUsersInAGivenCity() {
        if (!TextUtils.isEmpty(inputCity.getText().toString())) {
            users = new ArrayList<>();

            UserHelper.getUserForSearchOnCity(inputCity.getText().toString().toLowerCase()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            users.add(user);

                        }
                        System.out.println("users = " + users);
                        Gson gson = new Gson();
                        preferences.edit().putString(RESULTS, gson.toJson(users)).apply();
                        UserListFragment fragment = new UserListFragment(false, true);
                        showFragment(fragment);

                        layout.animate().translationY(-textViewTitle.getHeight());
                        buttonSearchProjects.animate().translationY(-textViewTitle.getHeight());
                        buttonSearchUsers.animate().translationY(-textViewTitle.getHeight());
                        scrollView.animate().translationY(-textViewTitle.getHeight());
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "You have to fill the city field", Toast.LENGTH_SHORT).show();
    }
    }

    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.search_content, fragment);
        transaction.commit();
    }

}
