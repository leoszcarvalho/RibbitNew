package com.carvalho.leonardo.ribbitnew.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carvalho.leonardo.ribbitnew.adapters.UserAdapter;
import com.carvalho.leonardo.ribbitnew.utils.ParseConstants;
import com.carvalho.leonardo.ribbitnew.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    private ProgressBar mProgressBar;

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    private ListView mLista;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mProgressBar.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);


        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);

        mGridView.setEmptyView(emptyTextView);

    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

            if(mGridView.isItemChecked(position))
            {
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
            }
            else
            {
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }

            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null)
                    {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

        }
    };


    @Override
    protected void onResume()
    {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();

        Log.d("user", String.valueOf(mCurrentUser));

        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        mProgressBar.setVisibility(View.VISIBLE);


        //Executa a query pra enconrtar os usuários no Parse
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> list, ParseException e) {

                mProgressBar.setVisibility(View.INVISIBLE);

                if (e == null) {
                    //Success

                    mUsers = list;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;

                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    if(mGridView.getAdapter() == null)
                    {

                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);

                    }
                    else
                    {
                        ((UserAdapter) mGridView.getAdapter()).refill(mUsers);
                    }


                    addFriendsChecks();

                   /* mLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            //Verifica se o usuário está checado na lista e se estiver adiciona senão exclui
                            if (mLista.isItemChecked(position)) {

                                mFriendsRelation.add(mUsers.get(position));

                                mCurrentUser.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            //Usuário salvo com sucesso
                                            Toast.makeText(EditFriendsActivity.this, "Usuário adicionado aos amigos com sucesso", Toast.LENGTH_LONG).show();

                                        } else {
                                            Log.e(TAG, e.getMessage());
                                        }

                                    }

                                });

                            } else {

                                mFriendsRelation.remove(mUsers.get(position));

                                mCurrentUser.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            //Usuário salvo com sucesso
                                            Toast.makeText(EditFriendsActivity.this, "Usuário removido dos amigos com sucesso", Toast.LENGTH_LONG).show();

                                        } else {
                                            Log.e(TAG, e.getMessage());
                                        }

                                    }

                                });

                            }

                        }

                    });*/


                } else {

                    Log.e(TAG, e.getMessage());

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("Expection Error")
                            .setPositiveButton(R.string.ok, null);

                    AlertDialog dialog = builder.create();

                    dialog.show();

                }

            }
        });


    }

    public void addFriendsChecks()
    {

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if(e == null)
                {

                    for(int i = 0; i < mUsers.size(); i++)
                    {
                        ParseUser user = mUsers.get(i);

                        for(ParseUser friend : list)
                        {
                            if(friend.getObjectId().equals(user.getObjectId()))
                            {
                                mGridView.setItemChecked(i, true);
                            }
                        }

                    }

                }
                else
                {

                }

            }
        });

    }




    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {

            ParseUser.logOut();
            navigateToLogin();

        }
        else if(id == R.id.action_friends_edit_menu)
        {
            Intent intent = new Intent(this, EditFriendsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }




}
