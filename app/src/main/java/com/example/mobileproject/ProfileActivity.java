package com.example.mobileproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.db.DatabaseHelper;
import com.example.mobileproject.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone, editTextStudentId;
    private Button buttonEdit, buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhoneNumber);
        editTextStudentId = findViewById(R.id.editTextStudentId);

        buttonEdit = findViewById(R.id.buttonEdit);
        buttonSave = findViewById(R.id.buttonSave);
        enableEditing(false);
        buttonEdit.setOnClickListener(v -> enableEditing(true));
        buttonSave.setOnClickListener(v -> {
            saveUserProfile();
            enableEditing(false);
        });
        setupActionBar();
        loadUserProfile();
    }

    private void enableEditing(boolean enable) {
        editTextName.setEnabled(enable);
        editTextEmail.setEnabled(enable);
        editTextPhone.setEnabled(enable);
        editTextStudentId.setEnabled(enable);

        buttonEdit.setVisibility(enable ? View.GONE : View.VISIBLE);
        buttonSave.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("User Profile");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadUserProfile() {
        User user = UserSessionManager.getInstance().getCurrentUser();
        editTextName.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        editTextStudentId.setText(user.getStudentId());
    }

    private void updateUser(User currentUser) {
        String url = "http://10.0.2.2:5000/user/" + currentUser.getId();

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject userData = new JSONObject();
        try {
            userData.put("name", currentUser.getName());
            userData.put("email", currentUser.getEmail());
            userData.put("phone", currentUser.getPhone());
            userData.put("student_id", currentUser.getStudentId());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ProfileActivity.this, "Error creating user update data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, userData,
                response -> {
                    Toast.makeText(ProfileActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    UserSessionManager.getInstance().setCurrentUser(currentUser);

                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public int getMethod() {
                return Request.Method.PUT;
            }
        };

        queue.add(jsonObjectRequest);
    }


    private void saveUserProfile() {
        String updatedName = editTextName.getText().toString().trim();
        String updatedEmail = editTextEmail.getText().toString().trim();
        String updatedPhone = editTextPhone.getText().toString().trim();
        String updatedStudentId = editTextStudentId.getText().toString().trim();

        User currentUser = UserSessionManager.getInstance().getCurrentUser();
        currentUser.setName(updatedName);
        currentUser.setEmail(updatedEmail);
        currentUser.setPhone(updatedPhone);
        currentUser.setStudentId(updatedStudentId);

        updateUser(currentUser);
    }

}
