package com.carvalho.leonardo.ribbitnew.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.carvalho.leonardo.ribbitnew.R;
import com.carvalho.leonardo.ribbitnew.RibbidApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;
    private Button mSignUpButton;
    private ProgressBar mProgressBar;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().hide();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // String activityBack = SignUpActivity.this.getCallingActivity().getShortClassName();

                SignUpActivity.this.finish();

            }
        });




        mUsername = (EditText) findViewById(R.id.userNameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mSignUpButton = (Button) findViewById(R.id.signupButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mProgressBar.setVisibility(View.INVISIBLE);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                String username = String.valueOf(mUsername.getText()).trim();
                String password = String.valueOf(mPassword.getText()).trim();
                String email = String.valueOf(mEmail.getText()).trim();

                if(username.isEmpty() || password.isEmpty() || email.isEmpty())
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.message)
                            .setTitle("Campos incompletos")
                            .setPositiveButton(R.string.ok, null);

                    AlertDialog dialog = builder.create();

                    dialog.show();

                }
                else
                {

                    mProgressBar.setVisibility(View.VISIBLE);

                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {

                        @Override
                        public void done(ParseException e) {

                            if(e == null)
                            {
                                mProgressBar.setVisibility(View.INVISIBLE);

                                RibbidApplication.updateParseInstallation(ParseUser.getCurrentUser());

                                //Usuário criado com sucesso
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                            else
                            {

                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
