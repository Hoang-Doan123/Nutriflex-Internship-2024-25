package com.example.ui.daily;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DailyPagerAdapter extends FragmentStateAdapter {

    public DailyPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DailyWorkoutFragment();
            case 1:
                return new DailyNutritionFragment();
            default:
                throw new IllegalArgumentException("Invalid position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
