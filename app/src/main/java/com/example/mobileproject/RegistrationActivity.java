package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.db.DatabaseHelper;
import com.example.mobileproject.model.User;
import com.example.mobileproject.util.PasswordUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone, editTextStudentId;
    private Button buttonSubmitRegistration, buttonBackToLogin;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextStudentId = findViewById(R.id.editTextStudentId);
        buttonSubmitRegistration = findViewById(R.id.buttonSubmitRegistration);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);
        databaseHelper = new DatabaseHelper(this);
        buttonSubmitRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerUser();

                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, "Error during register", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        buttonBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String studentId = editTextStudentId.getText().toString().trim();

        if (validateInput(name, email, password, phone, studentId)) {
            String encryptedPassword = null;
            try {
                encryptedPassword = PasswordUtil.encrypt(password);
            } catch (Exception e) {
                Toast.makeText(RegistrationActivity.this, "Error encrypting the password", Toast.LENGTH_SHORT).show();
            }
            User newUser = new User(0, name, email, encryptedPassword, phone, studentId);
            addUser(newUser);
        }
    }

    public void addUser(User user) {
        String url = "http://10.0.2.2:5000/user";
        RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
        Map<String, String> params = new HashMap<>();
        params.put("name", user.getName());
        params.put("email", user.getEmail());
        params.put("password", user.getPassword());
        params.put("phone", user.getPhone());
        params.put("student_id", user.getStudentId());

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,
                response -> {
                    try {
                        if(response.getLong("user_id") > 0) {
                            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                },
                error -> {
                    Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                });
        queue.add(request);
    }

    private boolean validateInput(String name, String email, String password, String phone, String studentId) {
        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || studentId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
