package com.carvalho.leonardo.ribbitnew.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.carvalho.leonardo.ribbitnew.R;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        setSupportActionBar(toolbar);


        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Uri imageUri = getIntent().getData();

        Picasso.with(this).load(imageUri.toString()).into(imageView);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run()
            {
                finish();
            }
        }, 10 * 1000);

    }

}
