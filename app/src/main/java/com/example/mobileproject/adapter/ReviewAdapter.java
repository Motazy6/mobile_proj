package com.example.mobileproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileproject.R;
import com.example.mobileproject.model.Review;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.textViewReviewerName.setText(review.getUserName());
        holder.textViewReviewComment.setText(review.getComment());
        holder.ratingBarReview.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewReviewerName, textViewReviewComment;
        RatingBar ratingBarReview;

        ViewHolder(View view) {
            super(view);
            textViewReviewerName = view.findViewById(R.id.textViewReviewerName);
            textViewReviewComment = view.findViewById(R.id.textViewReviewComment);
            ratingBarReview = view.findViewById(R.id.ratingBarReview);
        }
    }
}
