package com.example.pmitev.chatterbox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.pmitev.chatterbox.fragments.ChatsFragment;
import com.example.pmitev.chatterbox.fragments.FriendsFragment;
import com.example.pmitev.chatterbox.fragments.RequestsFragment;

/**
 * Created by pmitev on 09.04.18.
 */

class SectionsPageAdapter extends FragmentPagerAdapter {


    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;


            case 1:

                FriendsFragment friendsFragment = new FriendsFragment();
                return  friendsFragment;



            case 2:

                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

                default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public  CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "REQUESTS";

            case 1:
                return  "FRIENDS";

            case 2:
                 return "CHATS";
            default:
                return null;
        }


    }
}
