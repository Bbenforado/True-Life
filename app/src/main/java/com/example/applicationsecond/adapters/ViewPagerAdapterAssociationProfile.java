package com.example.applicationsecond.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.fragments.PostListFragment;

public class ViewPagerAdapterAssociationProfile extends FragmentPagerAdapter {


    public ViewPagerAdapterAssociationProfile(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ActualityListFragment("associationProfileActivity");
            case 1:
                return new PostListFragment(true);
                default:
                    return null;
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
            case 0:
                return "Projects";
            case 1:
                return "Posts";
            default:
                return null;
        }
    }
}
