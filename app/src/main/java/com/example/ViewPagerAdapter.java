package com.example;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.nutritionpackage.NewsFragment;
import com.example.trainingpackage.VideoFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
                return new VideoFragment();

            case 1:
                return new NewsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
