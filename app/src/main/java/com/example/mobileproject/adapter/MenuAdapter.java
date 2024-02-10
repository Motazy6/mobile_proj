package com.example.mobileproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileproject.R;
import com.example.mobileproject.model.MenuItem;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final List<MenuItem> menuItems;
    private final Context context;

    public MenuAdapter(List<MenuItem> menuItems, Context context) {
        this.menuItems = menuItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.icon.setImageResource(item.getIcon());
        holder.title.setText(item.getTitle());
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, item.getActivity());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        CardView cardView;

        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.item_icon);
            title = view.findViewById(R.id.item_title);
            cardView = view.findViewById(R.id.card_view);
        }
    }
}
