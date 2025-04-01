package com.example.savepackage;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SaveAdapter extends FragmentStatePagerAdapter {
    private Integer userId;

    public SaveAdapter(
            @NonNull FragmentManager fm,
            int behavior,
            Integer userId
    ) {
        super(fm, behavior);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (userId != null) {
            switch (position) {
                default: return SaveVideoFragment.newInstance(userId);
                case 1: return SaveNewsFragment.newInstance(userId);
            }
        }
        else {
            switch (position) {
                default: return new SaveVideoFragment();
                case 1: return new SaveNewsFragment();
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            default: return "Video";
            case 1: return "News";
        }
    }
}

