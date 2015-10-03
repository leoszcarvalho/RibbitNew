package com.carvalho.leonardo.ribbitnew;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
    }


