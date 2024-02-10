package com.example.mobileproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileproject.HousingHistoryDetailsActivity;
import com.example.mobileproject.R;
import com.example.mobileproject.model.HousingHistory;
import java.util.List;

public class HousingHistoryAdapter extends RecyclerView.Adapter<HousingHistoryAdapter.ViewHolder> {
    private final List<HousingHistory> housingHistoryList;
    private Context context;

    public HousingHistoryAdapter(Context context, List<HousingHistory> housingHistoryList) {
        this.context = context;
        this.housingHistoryList = housingHistoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_housing_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HousingHistory housingHistory = housingHistoryList.get(position);
        holder.tvAddress.setText(housingHistory.getAddress());
        holder.tvStartDate.setText(housingHistory.getStartDate());
        holder.tvEndDate.setText(housingHistory.getEndDate());
        holder.tvName.setText(housingHistory.getHousingName());
    }

    @Override
    public int getItemCount() {
        return housingHistoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress, tvStartDate, tvEndDate, tvName;

        ViewHolder(View view) {
            super(view);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvStartDate = view.findViewById(R.id.tvStartDate);
            tvEndDate = view.findViewById(R.id.tvEndDate);
            tvName = view.findViewById(R.id.tvName);
            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    HousingHistory selectedHistory = housingHistoryList.get(position);
                    Intent intent = new Intent(context, HousingHistoryDetailsActivity.class);
                    intent.putExtra("HOUSING_HISTORY_ID", selectedHistory.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
