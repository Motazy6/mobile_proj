package com.example.mobileproject;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileproject.adapter.ReviewAdapter;
import com.example.mobileproject.db.DatabaseHelper;
import com.example.mobileproject.model.Housing;
import com.example.mobileproject.model.HousingHistory;
import com.example.mobileproject.model.LeaseAgreement;
import com.example.mobileproject.model.Review;
import com.example.mobileproject.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HousingDetailsActivity extends AppCompatActivity {

    private TextView textViewHousingTitle, textViewHousingDescription, textViewHousingPrice,
            textViewHousingLocation, textViewHousingAmenities, textViewLeaseDuration,
            textViewAvailableFrom, textViewUtilitiesIncluded;
    private Button buttonLeaseHousing;
    private DatabaseHelper databaseHelper;
    private Housing selectedHousing;
    private RecyclerView recyclerViewReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housing_details);

        initializeViews();

        databaseHelper = new DatabaseHelper(this);
        int housingId = getIntent().getIntExtra("HOUSING_ID", -1);
        if (housingId != -1) {
            fetchHousingDetails(housingId);
            buttonLeaseHousing.setOnClickListener(v -> leaseHousing());
            setupActionBar();
            loadReviews(housingId);
            Button addReviewButton = findViewById(R.id.buttonSubmitReview);
            addReviewButton.setOnClickListener(v -> showAddReviewDialog());
        }
    }

    private void fetchHousingDetails(int housingId) {
        String url = "http://10.0.2.2:5000/housing/" + housingId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Housing housing = new Housing(response.getInt("id"), response.getString("title"), response.getString("description"), response.getDouble("price"), response.getString("location"),
                                response.getString("amenities"), response.getString("lease_duration"), response.get("available_from").toString(), response.getString("utilities_included"));
                        selectedHousing = housing;
                        checkLeaseAgreementActive(UserSessionManager.getInstance().getCurrentUser().getId(), selectedHousing.getId());
                        displayHousingDetails(housing);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HousingDetailsActivity.this, "Error parsing housing details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(HousingDetailsActivity.this, "Error fetching housing details", Toast.LENGTH_SHORT).show();
                });
        queue.add(jsonObjectRequest);
    }

    private void checkLeaseAgreementActive(int userId, int housingId) {
        String url = "http://10.0.2.2:5000/lease_active/" + userId + "/" + housingId; // Adjust the URL to your API endpoint

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean isActive = response.getBoolean("is_active");

                        buttonLeaseHousing.setEnabled(!isActive);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HousingDetailsActivity.this, "Error parsing lease active status", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(HousingDetailsActivity.this, "Error checking lease active status", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

    private void initializeViews() {
        textViewHousingTitle = findViewById(R.id.textViewHousingTitle);
        textViewHousingDescription = findViewById(R.id.textViewHousingDescription);
        textViewAvailableFrom = findViewById(R.id.textViewAvailableFrom);
        textViewHousingAmenities = findViewById(R.id.textViewHousingAmenities);
        textViewHousingLocation = findViewById(R.id.textViewHousingLocation);
        textViewHousingPrice = findViewById(R.id.textViewHousingPrice);
        textViewLeaseDuration = findViewById(R.id.textViewLeaseDuration);
        textViewUtilitiesIncluded = findViewById(R.id.textViewUtilitiesIncluded);
        buttonLeaseHousing = findViewById(R.id.buttonLeaseHousing);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
    }

    private void displayHousingDetails(Housing housing) {
        if (housing != null) {
            textViewHousingTitle.setText(housing.getTitle());
            textViewHousingDescription.setText(textViewHousingDescription.getText() + housing.getDescription());
            textViewLeaseDuration.setText(textViewLeaseDuration.getText() + housing.getLeaseDuration());
            textViewHousingPrice.setText(textViewHousingPrice.getText() + String.valueOf(housing.getPrice()));
            textViewHousingLocation.setText(textViewHousingLocation.getText() + housing.getLocation());
            textViewHousingAmenities.setText(textViewHousingAmenities.getText() + housing.getAmenities());
            textViewUtilitiesIncluded.setText(textViewUtilitiesIncluded.getText() + housing.getUtilitiesIncluded());
            textViewAvailableFrom.setText(textViewAvailableFrom.getText() + housing.getAvailableFrom());
        }
    }

    private void leaseHousing() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_lease_housing, null);
        builder.setView(view);

        EditText etLeaseStartDate = view.findViewById(R.id.etLeaseStartDate);
        EditText etLeaseEndDate = view.findViewById(R.id.etLeaseEndDate);
        EditText etMonthlyRent = view.findViewById(R.id.etMonthlyRent);
        Button buttonSubmitLease = view.findViewById(R.id.buttonSubmitLease);

        AlertDialog dialog = builder.create();

        buttonSubmitLease.setOnClickListener(v -> {
            String leaseStartDate = etLeaseStartDate.getText().toString().trim();
            String leaseEndDate = etLeaseEndDate.getText().toString().trim();
            double monthlyRent = Double.parseDouble(etMonthlyRent.getText().toString().trim());

            User currentUser = UserSessionManager.getInstance().getCurrentUser();

            JSONObject leaseAgreement = new JSONObject();
            try {
                leaseAgreement.put("user_id", currentUser.getId());
                leaseAgreement.put("housing_id", selectedHousing.getId());
                leaseAgreement.put("lease_start_date", leaseStartDate);
                leaseAgreement.put("lease_end_date", leaseEndDate);
                leaseAgreement.put("monthly_rent", monthlyRent);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(HousingDetailsActivity.this, "Error creating lease agreement data", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://10.0.2.2:5000/add_lease_agreement";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, leaseAgreement,
                    response -> {
                        Toast.makeText(HousingDetailsActivity.this, "Lease agreement added successfully", Toast.LENGTH_SHORT).show();
                        buttonLeaseHousing.setEnabled(false);
                        dialog.dismiss();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(HousingDetailsActivity.this, "Failed to add lease agreement", Toast.LENGTH_SHORT).show();
                    });
            queue.add(jsonObjectRequest);
        });

        dialog.show();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(selectedHousing != null ? selectedHousing.getTitle() : "Housing Details");
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

    private void showAddReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
        builder.setView(view);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText editTextReview = view.findViewById(R.id.editTextReview);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        AlertDialog dialog = builder.create();

        buttonSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String reviewText = editTextReview.getText().toString().trim();
            addReview(rating, reviewText);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addReview(float rating, String reviewText) {
        String url = "http://10.0.2.2:5000/add_review";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject reviewData = new JSONObject();
        try {
            reviewData.put("housing_id", selectedHousing.getId());
            reviewData.put("user_id", UserSessionManager.getInstance().getCurrentUser().getId());
            reviewData.put("name", UserSessionManager.getInstance().getCurrentUser().getName());
            reviewData.put("rating", rating);
            reviewData.put("comment", reviewText);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(HousingDetailsActivity.this, "Error creating review data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, reviewData,
                response -> {
                    Toast.makeText(HousingDetailsActivity.this, "Review added successfully", Toast.LENGTH_SHORT).show();
                    loadReviews(selectedHousing.getId());
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(HousingDetailsActivity.this, "Failed to add review: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

    private void loadReviews(int housingId) {
        String url = "http://10.0.2.2:5000/get_reviews/" + housingId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Review> reviews = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject reviewObject = response.getJSONObject(i);
                            int id = reviewObject.getInt("id");
                            String reviewerName = reviewObject.getString("name");
                            String comment = reviewObject.getString("comment");
                            int rating = reviewObject.getInt("rating");
                            Review review = new Review(id, selectedHousing.getId(), UserSessionManager.getInstance().getCurrentUser().getId(), rating, comment);
                            review.setUserName(reviewerName);
                            reviews.add(review);
                        }

                        ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
                        recyclerViewReviews.setAdapter(reviewAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HousingDetailsActivity.this, "Error parsing reviews", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(HousingDetailsActivity.this, "Error fetching reviews", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }

}