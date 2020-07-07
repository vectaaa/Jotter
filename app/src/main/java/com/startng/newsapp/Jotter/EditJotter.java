package com.startng.newsapp.Jotter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.startng.newsapp.MainActivity;
import com.startng.newsapp.R;

import java.util.HashMap;
import java.util.Map;


public class EditJotter extends AppCompatActivity {
        Intent data;
        EditText editJotterTitle, editJotterContent;
        FirebaseFirestore fStore;
        ProgressBar progBar;
        FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_jotter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        data = getIntent();
        fStore = fStore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();//This is the initialization that will help out in getting the current user

        //We use this to find the xml portions where we would be displaying the passed data for edit.
        editJotterTitle = findViewById(R.id.addEditJotterTitle);
        editJotterContent = findViewById(R.id.editJotterText);
        progBar = findViewById(R.id.progressBar2);

        //This code was used to recieve the data in the new activity and display it for editing.
        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        //Now we set noteTitle and noteContent to our editJotterTitle and  editJotterContent  so as the user can run his or her edits.
        editJotterTitle.setText(noteTitle);
        editJotterContent.setText(noteContent);

        FloatingActionButton fab = findViewById(R.id.editJotterFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nTitle = editJotterTitle.getText().toString();
                String nContent = editJotterContent.getText().toString();

                if (nTitle.isEmpty() | (nContent.isEmpty())) {
                    Toast.makeText(EditJotter.this, "Cannot save note with empty field. ", Toast.LENGTH_SHORT).show();
                    return;
                    /* Snackbar.make(view, "Replace with action", Snackbar.LENGTH_SHORT).setAction("Action", null).show();*/
                }
                //We display the progressbar when a user clicks the save button.
                progBar.setVisibility(View.VISIBLE);
                //Save note area
                //First we create a collection container called notes to save all our jottings with details.
                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                //we use data.getString() because it was declared in line 38.
                //Now we create a map object that will hold the title and the notes
                //Map always take the key value pairs.
                Map<String,Object> note = new HashMap<>();
                note.put("title", nTitle);
                note.put("content", nContent);


                //Now we bring back the document reference
                //We also add a listener to know if the data has been added succesfully to the database or not.(onSuccessListener)
                //Here we use the update keyword since we are updating data to the database. unlike in AddJotter line 74 where we where just creating them.
                docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //here we handle the condition where the note is handled succesfully
                        Toast.makeText(EditJotter.this, "Note Saved", Toast.LENGTH_SHORT).show();
                        //When the note is added it will send us back to mainactivity.java
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditJotter.this, "Error Try again", Toast.LENGTH_SHORT).show();

                        progBar.setVisibility(View.VISIBLE);
                    }
                });


            }

        });


}}
