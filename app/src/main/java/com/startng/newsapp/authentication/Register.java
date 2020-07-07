package com.startng.newsapp.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.startng.newsapp.MainActivity;
import com.startng.newsapp.R;

public class Register extends AppCompatActivity {
        EditText rUserName, rUserEmail, rUserPassword, rUserConfPass;
        TextView login;
        Button syncAcct;
        ProgressBar progressBar;
        FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Create Jotter Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rUserName = findViewById(R.id.userName);
        rUserEmail = findViewById(R.id.userEmail);
        rUserPassword = findViewById(R.id.usPassword);
        rUserConfPass = findViewById(R.id.passwordConfirm);

        login = findViewById(R.id.login);
        syncAcct = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progressBar4);

        fAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        syncAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usUserName = rUserName.getText().toString();
                String usUserEmail = rUserEmail.getText().toString();
                String usUserPassword = rUserPassword.getText().toString();
                String usUserConfPass = rUserConfPass.getText().toString();

                if (usUserName.isEmpty() || usUserEmail.isEmpty() || usUserPassword.isEmpty() || usUserConfPass.isEmpty()){
                    Toast.makeText(Register.this, "All Fields are Needed", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!usUserPassword.equals(usUserConfPass)){
                    rUserConfPass.setError("Password do not match");
                }

                //This block of code from line 68 to line 81 is here to link the new user with their emal and password.
                //To follow up with the Auth credential to link the anonymous user with the real account we are creating.
                AuthCredential credential = EmailAuthProvider.getCredential(usUserEmail, usUserPassword);
                //This going to mege the annon acct to the real acct.
                fAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Notes are Synced", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        //This is the user who has been connected to the real time db.
                        FirebaseUser usr = fAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(usUserName)
                                .build();
                        usr.updateProfile(request); //This means that anytime a user saves thier acct to the real acct we can save the username object from the real to the username object locally
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            //This will help to display the animation created in anim
                            finish();



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Failed To Connect, Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
               /* Toast.makeText(Register.this, "All is in order", Toast.LENGTH_SHORT).show();*/
                }


        });
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item ){
            startActivity( new Intent(this, MainActivity.class));
            finish();
        return super.onOptionsItemSelected(item);

    }
}
