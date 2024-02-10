package com.example.mobileproject;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.adapter.HousingHistoryAdapter;
import com.example.mobileproject.db.DatabaseHelper;
import com.example.mobileproject.model.HousingHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HousingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HousingHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_history);

        recyclerView = findViewById(R.id.rvHousingHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchHousingHistory(UserSessionManager.getInstance().getCurrentUser().getId());
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Historical Housing Data");
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

    private void fetchHousingHistory(int userId) {
        String url = "http://10.0.2.2:5000/housing_histories/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<HousingHistory> housingHistoryList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject historyObject = response.getJSONObject(i);

                            HousingHistory history = new HousingHistory(
                                    historyObject.getInt("id"),
                                    userId,
                                    historyObject.getString("name"),
                                    historyObject.getString("address"),
                                    historyObject.getString("start_date"),
                                    historyObject.getString("end_date"),
                                    historyObject.optString("notes", "")
                            );

                            housingHistoryList.add(history);
                        }
                        adapter = new HousingHistoryAdapter(this, housingHistoryList);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HousingHistoryActivity.this, "Error parsing housing history data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(HousingHistoryActivity.this, "Error fetching housing history data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }



}
