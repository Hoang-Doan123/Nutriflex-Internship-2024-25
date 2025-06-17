package com.example.ui.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.model.OnboardingItem;

import java.util.Arrays;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<OnboardingItem> items;

    public OnboardingAdapter(Context context) {
        items = Arrays.asList(
                new OnboardingItem(
                        context.getString(R.string.onboarding_body_info),
                        context.getString(R.string.onboarding_gender),
                        R.drawable.ic_gender
                ),
                new OnboardingItem(
                        context.getString(R.string.onboarding_goal),
                        context.getString(R.string.onboarding_motivation),
                        R.drawable.ic_goal
                ),
                new OnboardingItem(
                        context.getString(R.string.healthcare_title),
                        context.getString(R.string.healthcare_issues),
                        R.drawable.ic_healthcare
                ),
                new OnboardingItem(
                        context.getString(R.string.fitness_title),
                        context.getString(R.string.fitness_experience),
                        R.drawable.ic_fitness
                )
        );
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final ImageView ivImage;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        public void bind(OnboardingItem item) {
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
            ivImage.setImageResource(item.getImageResId());
        }
    }
}
