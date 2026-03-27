package com.n2.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.n2.gameshop.R;
import com.n2.gameshop.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList != null ? reviewList : new ArrayList<Review>();
    }

    public void updateData(List<Review> newList) {
        this.reviewList = newList != null ? newList : new ArrayList<Review>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        String username = review.getUsername();
        holder.tvReviewUsername.setText(username != null && !username.isEmpty() ? username : "Ẩn danh");
        holder.ratingBarReview.setRating(review.getRating());
        holder.tvReviewComment.setText(review.getComment());
        holder.tvReviewDate.setText(review.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewUsername;
        RatingBar ratingBarReview;
        TextView tvReviewComment;
        TextView tvReviewDate;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewUsername = itemView.findViewById(R.id.tvReviewUsername);
            ratingBarReview = itemView.findViewById(R.id.ratingBarReview);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
        }
    }
}

