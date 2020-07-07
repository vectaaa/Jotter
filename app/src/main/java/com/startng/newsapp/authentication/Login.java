package com.startng.newsapp.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.startng.newsapp.MainActivity;
import com.startng.newsapp.R;
import com.startng.newsapp.SplashScreen;

public class Login extends AppCompatActivity {
        EditText lEmail, lPassword;
        Button loginNow;
        TextView forgotPword, createAccnt;
        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login To Jotter");

        lEmail = findViewById(R.id.emailLogin);
        lPassword = findViewById(R.id.lPassword);
        loginNow = findViewById(R.id.loginBtn);

        forgotPword = findViewById(R.id.forgotPasword);
        createAccnt = findViewById(R.id.createAccount);

        //we instanciate this for the firebase login in line 48
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        showWarning();//Used to display the popup message on the sync note area when trying to sync anonymously


        createAccnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
            }
        });

        //Now when one clicks on login we extract the needed data
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgEmail = lEmail.getText().toString();
                String msgPassword = lPassword.getText().toString();
                //If all fields are not filled.
                if (msgEmail.isEmpty() || msgPassword.isEmpty()) {
                    Toast.makeText(Login.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                //We delete all the notes from an anonymous user acct when he or she logs out
                if (fAuth.getCurrentUser().isAnonymous()){
                    FirebaseUser user = fAuth.getCurrentUser();

                    fStore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "All Temporary Notes Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //We would also delete temporary user.
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "Temporary User Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }






                fAuth.signInWithEmailAndPassword(msgEmail,msgPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Login  Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Login Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
//This Pop-up shows when one clicks on sync not from an annonymous user stand point.
    private void showWarning() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Linking existing Accounts Will Delete All Temporary Notes, Create New Account To Save Them.")
                .setPositiveButton("Save Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Its okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Execute Nothing
                    }
                });
        warning.show();
    }
    }

