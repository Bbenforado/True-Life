package com.example.applicationsecond.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.fragments.SearchFragment;
import com.example.applicationsecond.fragments.UserListFragment;

public class ViewPagerAdapterFollowedProjectsAndAssociations extends FragmentPagerAdapter {

    public ViewPagerAdapterFollowedProjectsAndAssociations(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ActualityListFragment(true);
            case 1:
                return new UserListFragment();
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
                return "Followed projects";
            case 1:
                return "Followed associations";
            default:
                return null;
        }
    }
}
