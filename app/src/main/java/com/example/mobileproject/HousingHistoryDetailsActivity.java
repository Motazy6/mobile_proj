package com.example.mobileproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.db.DatabaseHelper;
import com.example.mobileproject.model.HousingHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HousingHistoryDetailsActivity extends AppCompatActivity {
    private TextView tvAddress, tvStartDate, tvEndDate;
    private EditText etNotes;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private HousingHistory selectedHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_history_details);

        initializeViews();

        databaseHelper = new DatabaseHelper(this);
        int historyId = getIntent().getIntExtra("HOUSING_HISTORY_ID", -1);
        if (historyId != -1) {
            fetchHousingHistoryById(historyId);
        }


        btnSave.setOnClickListener(v -> {
            selectedHistory.setNotes(etNotes.getText().toString());
            updateHousingHistory(selectedHistory);
        });
        setupActionBar();
    }

    private void updateHousingHistory(HousingHistory selectedHistory) {
        String url = "http://10.0.2.2:5000/housing_history/" + selectedHistory.getId();

        RequestQueue queue = Volley.newRequestQueue(this);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        JSONObject historyData = new JSONObject();
        try {
            historyData.put("user_id", selectedHistory.getUserId());
            historyData.put("name", selectedHistory.getHousingName());
            historyData.put("address", selectedHistory.getAddress());
            historyData.put("start_date", outputFormat.format(new Date(selectedHistory.getStartDate())));
            historyData.put("end_date", outputFormat.format(new Date(selectedHistory.getEndDate())));
            historyData.put("notes", selectedHistory.getNotes());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(HousingHistoryDetailsActivity.this, "Error creating housing history data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, historyData,
                response -> {
                    Toast.makeText(HousingHistoryDetailsActivity.this, "Housing history updated successfully", Toast.LENGTH_SHORT).show();
                    fetchHousingHistoryById(this.selectedHistory.getId());
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(HousingHistoryDetailsActivity.this, "Failed to update housing history", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(selectedHistory != null ? selectedHistory.getHousingName() : "Leasing Details");
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

    private void initializeViews() {
        tvAddress = findViewById(R.id.tvAddress);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);
    }

    private void displayHousingHistoryDetails(HousingHistory history) {
        if (history != null) {
            tvAddress.setText(history.getAddress());
            tvStartDate.setText(history.getStartDate());
            tvEndDate.setText(history.getEndDate());
            etNotes.setText(history.getNotes());
        }
    }

    private void fetchHousingHistoryById(int historyId) {
        String url = "http://10.0.2.2:5000/housing_history/" + historyId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        HousingHistory selectedHistory = new HousingHistory(
                                response.getInt("id"),
                                response.getInt("user_id"),
                                response.getString("name"),
                                response.getString("address"),
                                response.getString("start_date"),
                                response.getString("end_date"),
                                response.optString("notes", "")
                        );
                        this.selectedHistory = selectedHistory;
                        displayHousingHistoryDetails(selectedHistory);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HousingHistoryDetailsActivity.this, "Error parsing housing history data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(HousingHistoryDetailsActivity.this, "Error fetching housing history data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

}
