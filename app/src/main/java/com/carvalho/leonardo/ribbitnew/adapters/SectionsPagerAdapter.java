package com.carvalho.leonardo.ribbitnew.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.carvalho.leonardo.ribbitnew.R;
import com.carvalho.leonardo.ribbitnew.ui.FriendsFragment;
import com.carvalho.leonardo.ribbitnew.ui.InboxFragment;

/**
 * Created by Leonardo on 02/10/2015.
 */


    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

       // protected Context mContext;

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);



        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch(position)
            {
                case 0: return new InboxFragment();
                case 1: return new FriendsFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "INBOX";
                case 1:
                    return "FRIENDS";

            }
            return null;
        }


        public int getIcon(int position)
        {
            switch (position)
            {
                case 0:
                    return R.mipmap.ic_tab_inbox;
                case 1:
                    return R.mipmap.ic_tab_friends;

            }

            return R.mipmap.ic_tab_inbox;

        }

        private int[] imageResId = {
                R.mipmap.ic_tab_inbox,
                R.mipmap.ic_tab_friends
        };

    }


