package com.startng.newsapp.Jotter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.startng.newsapp.R;

import java.util.HashMap;
import java.util.Map;

public class AddJotter extends AppCompatActivity {
    // we create the variable for firebase fire store
    FirebaseFirestore fStore;
    EditText noteTitle, noteContent;
    ProgressBar progressBarSave;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jotter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //We intialize using firebase fire-store
            fStore = FirebaseFirestore.getInstance();
            noteContent = findViewById(R.id.addJotterContent);
            noteTitle  = findViewById(R.id.addJotterTitle);

            progressBarSave = findViewById(R.id.progressBar);
            user = FirebaseAuth.getInstance().getCurrentUser();


        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nTitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();

                if (nTitle.isEmpty() | (nContent.isEmpty())) {
                    Toast.makeText(AddJotter.this, "Cannot save note with empty field. ", Toast.LENGTH_SHORT).show();
                    return;
                    /* Snackbar.make(view, "Replace with action", Snackbar.LENGTH_SHORT).setAction("Action", null).show();*/
                }
                //We display the progressbar when a user clicks the save button.
                progressBarSave.setVisibility(View.VISIBLE);
                        //Save note area
                //First we create a collection container called notes to save all our jottings with details.
                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document();
                //Now we create a map object that will hold the title and the notes
                //Map always take the key value pairs.
                Map<String,Object> note = new HashMap<>();
                note.put("title", nTitle);
                note.put("content", nContent);

                //Now we bring back the document reference
                //We also add a listener to know if the data has been added succesfully to the database or not.(onSuccessListener)
                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //here we handle the condition where the note is handled succesfully
                        Toast.makeText(AddJotter.this, "Note Added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddJotter.this, "Error Try again", Toast.LENGTH_SHORT).show();

                        progressBarSave.setVisibility(View.VISIBLE);
                    }
                });


            }

        });
        //We remove the initial back button with comments.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    // Here we Create the action for the close menu using onClick and onOptionsSelected

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //now for the click activity on the x icon

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.closeMenu)
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
