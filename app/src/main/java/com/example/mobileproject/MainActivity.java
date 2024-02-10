package com.example.mobileproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileproject.adapter.MenuAdapter;
import com.example.mobileproject.model.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<MenuItem> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMenuItems();
        setUpRecyclerView();
    }

    private void initializeMenuItems() {
        menuItems = new ArrayList<>();

        menuItems.add(new MenuItem("Profile", R.drawable.ic_profile, ProfileActivity.class));
        menuItems.add(new MenuItem("Active Housings", R.drawable.baseline_home_24, ActiveHousingActivity.class));
        menuItems.add(new MenuItem("Search Accommodation", R.drawable.ic_search, SearchAccommodationActivity.class));
        menuItems.add(new MenuItem("Booking History", R.drawable.ic_history, HousingHistoryActivity.class));
        menuItems.add(new MenuItem("Notifications and ToDo", R.drawable.ic_notifications, NotificationsActivity.class));
        menuItems.add(new MenuItem("Support/Help", R.drawable.ic_help, SupportActivity.class));
        menuItems.add(new MenuItem("Logout", R.drawable.ic_logout, LoginActivity.class));
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter adapter = new MenuAdapter(menuItems, this);
        recyclerView.setAdapter(adapter);
    }
}
