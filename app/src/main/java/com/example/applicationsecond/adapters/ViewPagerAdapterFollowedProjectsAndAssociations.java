package com.example.applicationsecond.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.applicationsecond.fragments.ActualityListFragment;
import com.example.applicationsecond.fragments.SearchFragment;
import com.example.applicationsecond.fragments.UserListFragment;

public class ViewPagerAdapterFollowedProjectsAndAssociations extends FragmentPagerAdapter {

    private boolean isCurrentUsersProfile;
    @Nullable
    private String profileId;

    public ViewPagerAdapterFollowedProjectsAndAssociations(FragmentManager fm, boolean isCurrentUsersProfile) {
        super(fm);
        this.isCurrentUsersProfile = isCurrentUsersProfile;
    }

    public ViewPagerAdapterFollowedProjectsAndAssociations(FragmentManager fm, boolean isCurrentUsersProfile, String profileId) {
        super(fm);
        this.isCurrentUsersProfile = isCurrentUsersProfile;
        this.profileId = profileId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (isCurrentUsersProfile) {
                    return new ActualityListFragment("profileActivity");
                } else {
                    return new ActualityListFragment("notCurrentUsersProfile", profileId);
                }

            case 1:
                if (isCurrentUsersProfile) {
                    return new UserListFragment(true);
                } else {
                    return new UserListFragment(false, profileId);
                }
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
