package com.example.mobileproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.adapter.ActiveHousingAdapter;
import com.example.mobileproject.model.Housing;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActiveHousingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ActiveHousingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_housing);

        recyclerView = findViewById(R.id.recyclerViewActiveHousing);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchActiveHousings(UserSessionManager.getInstance().getCurrentUser().getId());
    }

    private void fetchActiveHousings(int userId) {
        String url = "http://10.0.2.2:5000/active_housings/" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Housing> activeHousingList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject housingObject = response.getJSONObject(i);

                            Housing housing = new Housing(
                                    housingObject.getInt("id"),
                                    housingObject.getString("title"),
                                    housingObject.getString("description"),
                                    housingObject.getDouble("price"),
                                    housingObject.getString("location"),
                                    housingObject.getString("amenities"),
                                    housingObject.getString("lease_duration"),
                                    housingObject.getString("available_from"),
                                    housingObject.getString("utilities_included")
                            );

                            activeHousingList.add(housing);
                        }
                        adapter = new ActiveHousingAdapter(activeHousingList, this::onHousingCancel);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ActiveHousingActivity.this, "Error parsing active housing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ActiveHousingActivity.this, "Error fetching active housing data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }

    private void cancelLease(int housingId, int userId) {
        String url = "http://10.0.2.2:5000/cancel_lease";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject leaseData = new JSONObject();
        try {
            leaseData.put("housing_id", housingId);
            leaseData.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ActiveHousingActivity.this, "Error creating lease cancellation data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, leaseData,
                response -> {
                    Toast.makeText(ActiveHousingActivity.this, "Lease cancelled successfully", Toast.LENGTH_SHORT).show();
                    fetchActiveHousings(userId);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ActiveHousingActivity.this, "Failed to cancel lease", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }



    private void onHousingCancel(int housingId) {
        cancelLease(housingId, UserSessionManager.getInstance().getCurrentUser().getId());
    }
}
