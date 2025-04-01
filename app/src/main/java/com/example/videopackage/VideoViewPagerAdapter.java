package com.example.videopackage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class VideoViewPagerAdapter extends FragmentStatePagerAdapter {
    public VideoViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default: return new VideoAllFragment();
            case 1: return new VideoAppleFragment();
            case 2: return new VideoSamsungFragment();
            case 3: return new VideoHuaweiFragment();
            case 4: return new VideoXiaomiFragment();
            case 5: return new VideoMicrosoftFragment();
            case 6: return new VideoAsusFragment();
        }
    }

    @Override
    public int getCount() {
        return 7;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            default: return "All";
//            case 1: return "Apple";
//            case 2: return "Samsung";
//            case 3: return "Huawei";
//            case 4: return "Xiaomi";
//            case 5: return "Microsoft";
//            case 6: return "Asus";
//        }
//    }
}
