package com.carvalho.leonardo.ribbitnew;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    protected Uri mMediaUri;

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {

            switch (which)
            {
                case 0:
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                    //verifica se o armazenamento está disponível pra retornar um erro se não estiver
                    if(mMediaUri == null)
                    {
                        Toast.makeText(MainActivity.this, R.string.external_storage_unavailable, Toast.LENGTH_LONG).show();
                    }
                    else
                    {

                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        {

                            // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                                // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                                // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                            } else {
                                // Solicita a permissão
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},0);
                            }

                        }
                        else
                        {
                            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);

                        }

                    }
                        break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }

        }
    };

    //método pra salvar o arquivo de mídia baseado na URI gerada
    private Uri getOutputMediaFileUri(int mediaType)
    {

        //verifica primeiro se o armazenamento externo (cartão SD, etc...) está disponível
        if(isExternalStorageAvailable())
        {

            //1. Get the external directory
            String appName = MainActivity.this.getString(R.string.app_name);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

            Log.d("MainActivity", "Media storage dir is: " + mediaStorageDir.getPath());

            //2. Create our subdirectory
            if(!mediaStorageDir.exists())
            {

                // Se não possui permissão
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                        // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                    } else {
                        // Solicita a permissão
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                    }
                }
                else
                {
                    // Tudo OK, podemos prosseguir.

                    if (!mediaStorageDir.mkdir()) {
                        Log.e(TAG, "Failed to create directory ----");

                        Log.d("MainActivity", ">> Let's debug why this directory isn't being created: ");
                        Log.d("MainActivity", "Is it working?: " + mediaStorageDir.mkdirs());
                        Log.d("MainActivity", "Does it exist?: " + mediaStorageDir.exists());
                        Log.d("MainActivity", "What is the full URI?: " + mediaStorageDir.toURI());
                        Log.d("MainActivity", "--");
                        Log.d("MainActivity", "Can we write to this file?: " + mediaStorageDir.canWrite());
                        if (!mediaStorageDir.canWrite()) {
                            Log.d("MainActivity", ">> We can't write! Do we have WRITE_EXTERNAL_STORAGE permission?");
                            if (getBaseContext().checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {
                                Log.d("MainActivity", ">> We don't have permission to write - please add it.");
                            } else {
                                Log.d("MainActivity", "We do have permission - the problem lies elsewhere.");
                            }
                        }
                        Log.d("MainActivity", "Are we even allowed to read this file?: " + mediaStorageDir.canRead());
                        Log.d("MainActivity", "--");
                        Log.d("MainActivity", ">> End of debugging.");

                        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                            Log.d("MainActivity", "External SD card not mounted");
                        } else {
                            Log.d("MainActivity", "External SD card was mounted sucess");

                        }

                        return null;

                    }

                }
            }

            //3. Create a file name
            //4. Create the file

            File mediaFile;
            Date now = new Date();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("pt", "BR")).format(now);

            String path = mediaStorageDir.getPath() + File.separator;

            if(mediaType == MEDIA_TYPE_IMAGE)
            {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }
            else if(mediaType == MEDIA_TYPE_VIDEO)
            {
                mediaFile = new File(path + "VID_" + timestamp + ".mp4");

            }
            else
            {

                return null;
            }

            Log.d(TAG, "File:" + Uri.fromFile(mediaFile));

            //5. Return the file's URI
            return Uri.fromFile(mediaFile);


        }
        else
        {
            return null;

        }

    }


    private boolean isExternalStorageAvailable()
    {

        String state = Environment.getExternalStorageState();

        if(state.equals(Environment.MEDIA_MOUNTED))
        {
            return true;
        }
        else
        {
            return false;
        }

    }


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();


        if(currentUser == null)
        {
            navigateToLogin();
        }
        else
        {
            Log.i(TAG, currentUser.getUsername());
        }





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/


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
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }







}
