package com.example.mobileproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.HousingDetailsActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.model.Housing;
import java.util.List;

public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.ViewHolder> {
    private final Context context;

    private final List<Housing> accommodations;

    public AccommodationAdapter(Context context, List<Housing> accommodations) {
        this.accommodations = accommodations;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accommodation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Housing housing = accommodations.get(position);
        holder.titleTextView.setText(housing.getTitle());
        holder.descriptionTextView.setText(housing.getDescription());
    }
    @Override
    public int getItemCount() {
        return accommodations.size();
    }

    public void updateData(List<Housing> newAccommodations) {
        accommodations.clear();
        accommodations.addAll(newAccommodations);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.textViewTitle);
            descriptionTextView = view.findViewById(R.id.textViewDescription);
            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Housing selectedHousing = accommodations.get(position);
                    Intent intent = new Intent(context, HousingDetailsActivity.class);

                    intent.putExtra("HOUSING_ID", selectedHousing.getId());

                    context.startActivity(intent);
                }
            });
        }
    }
}
