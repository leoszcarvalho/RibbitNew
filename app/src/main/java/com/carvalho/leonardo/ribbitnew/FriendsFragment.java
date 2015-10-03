package com.carvalho.leonardo.ribbitnew;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Leonardo on 02/10/2015.
 */
public class FriendsFragment extends ListFragment
{

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    private ProgressBar mProgressBar;

    //private ListView mLista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e)
            {
                mProgressBar.setVisibility(View.INVISIBLE);

                if(e == null)
                {

                    mFriends = list;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;

                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    //guarda os resultados dentro de um array adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_1,
                            usernames);

                    //captura a lista pra fazer a população através do adapter e setar o onclick dos itens
                    //mLista = (ListView) findViewById(R.id.lista);

                    setListAdapter(adapter);


                }
                else
                {
                    Log.e(TAG, e.getMessage());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(e.getMessage())
                            .setTitle("Expection Error")
                            .setPositiveButton(R.string.ok, null);

                    AlertDialog dialog = builder.create();

                    dialog.show();
                }

            }
        });


    }

}
