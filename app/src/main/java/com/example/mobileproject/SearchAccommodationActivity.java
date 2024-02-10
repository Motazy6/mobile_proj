package com.example.mobileproject;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.adapter.AccommodationAdapter;
import com.example.mobileproject.db.DatabaseHelper;
import com.example.mobileproject.model.Housing;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchAccommodationActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView accommodationsRecyclerView;
    private AccommodationAdapter adapter;
    private DatabaseHelper databaseHelper;
    private List<Housing> allAccommodations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_accommodation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Search Accommodation");
        }
        searchEditText = findViewById(R.id.searchEditText);
        accommodationsRecyclerView = findViewById(R.id.accommodationsRecyclerView);
        fetchAllHousings();
        setupSearchFunctionality();
    }

    private void fetchAllHousings() {
        String url = "http://10.0.2.2:5000/get_all_housings";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Housing> housings = new ArrayList<>();
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

                            housings.add(housing);
                        }

                        allAccommodations = housings;
                        accommodationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        adapter = new AccommodationAdapter(this, allAccommodations);
                        accommodationsRecyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SearchAccommodationActivity.this, "Error parsing housing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(SearchAccommodationActivity.this, "Error fetching housing data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }

    private void fetchFilteredHousings(String query) {
        String url = "http://10.0.2.2:5000/housings/filtered?query=" + Uri.encode(query);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Housing> filteredList = new ArrayList<>();
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

                            filteredList.add(housing);
                        }
                        adapter.updateData(filteredList);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SearchAccommodationActivity.this, "Error parsing filtered housing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(SearchAccommodationActivity.this, "Error fetching filtered housing data", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAccommodations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterAccommodations(String query) {
        fetchFilteredHousings(query);
    }
}
