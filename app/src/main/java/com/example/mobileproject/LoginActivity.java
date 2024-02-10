package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.db.DatabaseHelper;
import com.example.mobileproject.model.User;
import com.example.mobileproject.util.PasswordUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin, buttonRegister;
    private CheckBox checkBoxRememberMe;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserSessionManager.getInstance().setCurrentUser(null);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("MobileProjectSharedPref", MODE_PRIVATE);
        boolean isRememberMe = sharedPreferences.getBoolean("rememberMe", false);

        if(isRememberMe) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            editTextUsername.setText(savedUsername);
            editTextPassword.setText(savedPassword);
            checkBoxRememberMe.setChecked(true);
        }

        buttonLogin.setOnClickListener(v -> loginUser());

        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String usernameOrEmail = editTextUsername.getText().toString().trim();
        String enteredPassword = editTextPassword.getText().toString();

        String url = "http://10.0.2.2:5000/login";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject loginCredentials = new JSONObject();
        try {
            loginCredentials.put("username_or_email", usernameOrEmail);
            loginCredentials.put("password", PasswordUtil.encrypt(enteredPassword));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error creating login data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, loginCredentials,
                response -> {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    try {
                        User user = new User(response.getInt("id"), response.getString("name"), response.getString("email"), response.getString("phone"), response.getString("student_id"));
                        UserSessionManager.getInstance().setCurrentUser(user);

                        if (checkBoxRememberMe.isChecked()) {
                            saveLoginDetails(usernameOrEmail, enteredPassword);
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Login failed: Invalid credentials or network error", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }


    private void saveLoginDetails(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("MobileProjectSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

}