package com.example.mobileproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.R;
import com.example.mobileproject.model.Housing;

import java.util.List;

public class ActiveHousingAdapter extends RecyclerView.Adapter<ActiveHousingAdapter.ViewHolder> {
    private final List<Housing> housingList;
    private final OnCancelLeaseListener onCancelLeaseListener;

    public interface OnCancelLeaseListener {
        void onCancelLease(int housingId);
    }

    public ActiveHousingAdapter(List<Housing> housingList, OnCancelLeaseListener listener) {
        this.housingList = housingList;
        this.onCancelLeaseListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_housing, parent, false);
        return new ViewHolder(view, onCancelLeaseListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Housing housing = housingList.get(position);
        holder.bind(housing);
    }

    @Override
    public int getItemCount() {
        return housingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvLocation, tvPrice;
        Button btnCancelLease;
        int housingId;

        ViewHolder(View itemView, OnCancelLeaseListener listener) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHousingTitle);
            tvDescription = itemView.findViewById(R.id.tvHousingDescription);
            tvLocation = itemView.findViewById(R.id.tvHousingLocation);
            tvPrice = itemView.findViewById(R.id.tvHousingPrice);
            btnCancelLease = itemView.findViewById(R.id.btnCancelLease);

            btnCancelLease.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelLease(housingId);
                }
            });
        }

        void bind(Housing housing) {
            housingId = housing.getId();
            tvTitle.setText(housing.getTitle());
            tvDescription.setText(housing.getDescription());
            tvLocation.setText(housing.getLocation());
            tvPrice.setText(String.format("$%.2f", housing.getPrice()));
        }
    }
}

