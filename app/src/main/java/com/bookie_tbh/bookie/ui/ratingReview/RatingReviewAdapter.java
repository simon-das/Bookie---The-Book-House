package com.bookie_tbh.bookie.ui.ratingReview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookie_tbh.bookie.R;

import java.util.List;

public class RatingReviewAdapter extends RecyclerView.Adapter<RatingReviewAdapter.ReviewViewHolder> {

    private List<RatingReview> reviewList;
    private Context context;

    public RatingReviewAdapter(List<RatingReview> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rating_review, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        RatingReview ratingReview = reviewList.get(position);
        holder.userName.setText(ratingReview.getUserName());
        holder.providerName.setText("<" + ratingReview.getProviderName() + ">");
        holder.review.setText(ratingReview.getReview());
        holder.ratingBar.setRating(ratingReview.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, providerName, review;
        private RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            providerName = (TextView) itemView.findViewById(R.id.provider_name);
            review = (TextView) itemView.findViewById(R.id.review);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        }
    }

}
