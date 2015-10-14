package com.carvalho.leonardo.ribbitnew.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.carvalho.leonardo.ribbitnew.adapters.UserAdapter;
import com.carvalho.leonardo.ribbitnew.utils.FileHelper;
import com.carvalho.leonardo.ribbitnew.utils.ParseConstants;
import com.carvalho.leonardo.ribbitnew.R;
import com.carvalho.leonardo.ribbitnew.utils.ToastGen;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends AppCompatActivity
{

    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;
    protected GridView mGridView;

    private ProgressBar mProgressBar;
    private TextView mEmptyText;
    private ListView mlistaView;
    private ListView itensChecked;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);

        mGridView.setEmptyView(emptyTextView);

    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

            if(mGridView.getCheckedItemCount() > 0)
            {
                mSendMenuItem.setVisible(true);
            }
            else
            {
                mSendMenuItem.setVisible(false);
            }

            if(mGridView.isItemChecked(position))
            {
                checkImageView.setVisibility(View.VISIBLE);
            }
            else
            {
                checkImageView.setVisibility(View.INVISIBLE);
            }

        }
    };


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
            public void done(List<ParseUser> list, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);

                if (e == null) {

                    mFriends = list;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;

                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    //guarda os resultados dentro de um array adapter
                    if(mGridView.getAdapter() == null)
                    {

                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);

                    }
                    else
                    {
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }





                } else {
                    Log.e(TAG, e.getMessage());

                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("Expection Error")
                            .setPositiveButton(R.string.ok, null);

                    AlertDialog dialog = builder.create();

                    dialog.show();
                }

            }
        });




    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENTS_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if(fileBytes == null)
        {
            return null;
        }
        else
        {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE))
            {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);

            }

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;

        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();

        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }

        return recipientIds;

    }

    public static int getItensCount(ListView itensChecked)
    {
        if (Build.VERSION.SDK_INT >= 11)
        {
            return itensChecked.getCheckedItemCount();
        }
        else
        {
            int count = 0;
            for (int i = itensChecked.getCount() - 1; i >= 0; i--)
                if (itensChecked.isItemChecked(i)) count++;
            return count;
        }
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
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_logout:

                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_friends_edit_menu:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_send:
                ParseObject message = createMessage();

                if(message == null)
                {
                    alertUser("File error", "There was an error with the selected file. Please select a different file");
                }
                else
                {
                    send(message);
                    finish();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void alertUser(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    protected void send(ParseObject message)
    {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    new ToastGen(RecipientsActivity.this, "Message sent!");
                    sendPushNotifications();
                } else {
                    alertUser("Sending Error", "There was an error sending your message, please try again");
                }

            }
        });
    }

    protected void sendPushNotifications()
    {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds());

        //send push notifications
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.user_push_message,
                    ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();

    }

}
