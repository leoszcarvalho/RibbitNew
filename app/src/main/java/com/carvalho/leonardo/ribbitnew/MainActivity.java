package com.carvalho.leonardo.ribbitnew;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
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

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; //10 MB

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
                case 0: //Take Photo
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

                        if (dontHavePermission(Manifest.permission.CAMERA))
                        {

                            askForPermission(Manifest.permission.CAMERA);

                        }
                        else
                        {
                            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);

                        }

                    }
                        break;
                case 1: //Take Video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

                    if(mMediaUri == null)
                    {
                        Toast.makeText(MainActivity.this, R.string.external_storage_unavailable, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);


                        if (dontHavePermission(Manifest.permission.CAMERA))
                        {

                            askForPermission(Manifest.permission.CAMERA);

                        }
                        else
                        {

                            startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);

                        }

                    }

                    break;
                case 2:

                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;
                case 3:

                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, R.string.video_too_large, Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;
            }

        }
    };

    private boolean dontHavePermission(String permission)
    {


        if(ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }



    private void askForPermission(String permission)
    {
        // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
            // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
            // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
            alertUser("Permissão necessária", "É necessária permissão de câmera para que este aplicativo possa funcionar corretamente");

        } else {
            // Solicita a permissão
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission},0);
        }

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
                if (dontHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {

            if(requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST)
            {
                if(data == null)
                {
                    Toast.makeText(MainActivity.this, R.string.captura_cancelada, Toast.LENGTH_LONG).show();
                }
                else
                {
                    mMediaUri = data.getData();
                }

                Log.i(TAG, "Media URI: " + mMediaUri);

                if(requestCode == PICK_VIDEO_REQUEST)
                {
                    // make sure the file is less than 10 MB
                    int fileSize = 0;
                    InputStream inputStream = null;

                    try
                    {

                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(this, R.string.error_selected_file, Toast.LENGTH_LONG);
                        e.printStackTrace();
                        return;
                    }
                    catch (IOException e)
                    {
                        Toast.makeText(this, R.string.error_selected_file, Toast.LENGTH_LONG);
                        e.printStackTrace();
                        return;
                    }
                    finally
                    {
                        try
                        {
                            inputStream.close();
                        }
                        catch (IOException e)
                        {
                            /* Intentionally blank */

                        }
                    }


                    if(fileSize >= FILE_SIZE_LIMIT)
                    {
                        Toast.makeText(this, R.string.file_too_large, Toast.LENGTH_LONG).show();
                        //Retorna pra atividade
                        return;
                    }

                }

            }
            else
            {

                //add to the gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);

            String fileType;

            if(requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST)
            {
                fileType = ParseConstants.TYPE_IMAGE;
            }
            else
            {
                fileType = ParseConstants.TYPE_VIDEO;
            }

            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);

            startActivity(recipientsIntent);
        }
        else if(resultCode == RESULT_CANCELED)
        {
            Toast.makeText(this, R.string.error_msg, Toast.LENGTH_LONG).show();
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
