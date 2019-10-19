package com.example.applicationsecond.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.applicationsecond.utils.Utils.getCurrentUser;

public class AuthenticationActivity extends AppCompatActivity {

    @BindView(R.id.button_register_authentication_activity) Button buttonRegister;
    @BindView(R.id.switch_button_authentication_activity)
    Switch switchButton;
    @BindView(R.id.password_input_authentication_activity) EditText passwordEditText;
    @BindView(R.id.email_input_authentication_activity) EditText emailEditText;
    @BindView(R.id.button_login_authentication_activity) Button buttonLogin;
    @BindView(R.id.user_name_input_authentication_activity) EditText usernameEditText;
    @BindView(R.id.city_input_authentication_activity) EditText cityEditText;
    @BindView(R.id.country_input_authentication_activity) EditText countryEditText;
    //-------------------------------------
    //-------------------------------------
    private FirebaseAuth mAuth;
    private boolean isAssociation;
    private String email;
    private String password;
    private String username;
    private String city;
    private String country;
    //----------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        configureSwitchButton();
        mAuth = FirebaseAuth.getInstance();
    }

    public void registerUser() {
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        username = usernameEditText.getText().toString();
        if (!TextUtils.isEmpty(cityEditText.getText())) {
            city = cityEditText.getText().toString().toLowerCase();
        } else {
            city = null;
        }
        if (!TextUtils.isEmpty(countryEditText.getText())) {
            country = countryEditText.getText().toString();
        } else {
            country = null;
        }
      if (checkAllFieldsAreCompleted()) {
          mAuth.createUserWithEmailAndPassword(email, password)
                  .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          try {
                              if (task.isSuccessful()) {
                                  if (isAssociation) {
                                      UserHelper.createUser(getCurrentUser().getUid(), username, true, city, country);
                                  } else {
                                      UserHelper.createUser(getCurrentUser().getUid(), username, false, city, country);
                                  }
                                  Toast.makeText(getApplicationContext(), "registration successful",
                                          Toast.LENGTH_SHORT).show();
                                  finish();
                                  Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                                  startActivity(mainActivityIntent);
                              } else {
                                  Toast.makeText(getApplicationContext(), "Couldn't register, try again",
                                          Toast.LENGTH_SHORT).show();
                              }
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                      }
                  });
      } else {
          Toast.makeText(getApplicationContext(), "Fields are not all completed!", Toast.LENGTH_SHORT).show();
      }
    }

    @OnClick(R.id.button_register_authentication_activity)
    public void register() {
        registerUser();
    }

    @OnClick(R.id.button_login_authentication_activity)
    public void login() {
        Intent loginItent = new Intent(this, LoginActivity.class);
        startActivity(loginItent);

    }

    private void configureSwitchButton() {
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isAssociation = true;

                } else {
                    isAssociation = false;
                }
            }
        });
    }

    private boolean checkIfStringIsNotEmpty(String value) {
        return !TextUtils.isEmpty(value);
    }

    private boolean checkAllFieldsAreCompleted() {
        return checkIfStringIsNotEmpty(email) && checkIfStringIsNotEmpty(password) && checkIfStringIsNotEmpty(username);
    }
}
