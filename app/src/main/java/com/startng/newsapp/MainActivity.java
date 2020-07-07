package com.startng.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.startng.newsapp.Jotter.AddJotter;
import com.startng.newsapp.Jotter.EditJotter;
import com.startng.newsapp.Jotter.JotterDetails;
import com.startng.newsapp.authentication.Login;
import com.startng.newsapp.authentication.Register;
import com.startng.newsapp.model.Adapter;
import com.startng.newsapp.model.Notes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Creation of needed variables.
        DrawerLayout drawerLayout;
        ActionBarDrawerToggle toggle;
        NavigationView nav_view;
        RecyclerView noteList;
        //Call the instance of the adapter for the MODEL!!!!
        Adapter adapter;
        //Call the instance of firestore
        FirebaseFirestore fStore;
        //We need to create an instance for the Firestore Recycler view
        FirestoreRecyclerAdapter<Notes,NotesViewHolder> noteAdapter;
        FirebaseUser user;
        FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user  = fAuth.getCurrentUser();


        // Now we query the database
        Query query = fStore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Notes> allNotes = new FirestoreRecyclerOptions.Builder<Notes>()
                .setQuery(query, Notes.class)
                .build();
        //We go to model and create a clas called notes and it possess the fields that will retrieve the data from firestore
        noteAdapter = new FirestoreRecyclerAdapter<Notes, NotesViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, final int i, @NonNull final Notes notes) {
                //Here we are binding the actual data that we recieve from the main-activity,
                // when we created the adapter object and we bind it to the view we have here.
                //To execute that i made use of the holder object.(we use the "holder" to reference the noteTitles and note Content.
                //Here we don't have the title of the note and content of the note but we need to run get title.
                notesViewHolder.noteTitles.setText(notes.getTitle());
                notesViewHolder.noteContent.setText(notes.getContent());
                //NB: This will extract both title and content from the list and assign it to the view layout
                //Here we create a integer variable for our colour so we can also display the same colour on the card view in our background.
                final int code = getRandomColor();
                //we use the holder to get the card view and set the background color
                notesViewHolder.mCardView.setCardBackgroundColor(notesViewHolder.view.getResources().getColor(code, null));
                //we create the method get random color so as to pick a random color line 58.
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();//This code is the id need to for the updating of data relative to the Adapter and it is used in line 95.
                notesViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), JotterDetails.class);
                        i.putExtra("title", notes.getTitle());
                        i.putExtra("content", notes.getContent()); //this is what happens here,
                        // when someone clicks on the first item of the recycler-view, it will get the position as zero,
                        // plus we are extracting the title and content from the zero position and we pass it
                        // to the JotterDetails.xml so we get the coorect title and description in the JotterDetails.xml
                        //now we move back to the JotterDetails.Java file to get our data with getDataIntent.
                        i.putExtra("code", code);//Here we pass in the color to the next activity.
                        // after creating the variable in line 48. and using it in line 50.
                        // we change the color of the background manually to one of our selected colors in the content_jotter_details.xml.
                        // and proceed to line 35 in JotterDetails.java
                        i.putExtra("noteId", docId);//We use the i.d here from line 80.
                        v.getContext().startActivity(i);
                    }
                });
                //Edit a note
                ImageView editIcon = notesViewHolder.view.findViewById(R.id.ediIcon);
                editIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();//We use the id here to get the noteId
                        Intent i = new Intent(v.getContext(), EditJotter.class);
                        i.putExtra("title", notes.getTitle());//instead of passing data. we use notes.
                        // because we are just opening the same edit activity so we just need to take the title and content
                        i.putExtra("content", notes.getContent());//content being passed
                        i.putExtra("noteId", docId);//The use of the noteId is basically for being able to edit the text.
                        //Plus we are passing the details from the jotter details activity to the editjotter activity.
                        startActivity(i);
                    }
                });
                //Delete a note
                ImageView deleteIcon = notesViewHolder.view.findViewById(R.id.deleteIcon);
                deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference docRef =  fStore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Note Deleted
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Opps Error Deleting Note", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });



            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NotesViewHolder(view);
            }
        };
        noteList = findViewById(R.id.noteList);
        // Assigning the resources of the variables.
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        //here we trying to select where the listener will listen to.
        nav_view.setNavigationItemSelectedListener(this);


        //Here i created the object for actionbar drawer toggle
        //passing two main parameters, 1.drawer layout and
        // 2. where we intend displaying the ham-burger menu sign.
        // Also i specified two string resource which are to OPEN and CLOSE the drawer
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        //We add a listener to our toggle
        drawerLayout.addDrawerListener(toggle);
        //We enable the menu (ham-burger sign in the tool bar)
        //ps. the true means the menu has been enabled in the tool bar.
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState(); //This one is used to inform that the nav bar is either open or closed.

        View headerView =nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView useremail = headerView.findViewById(R.id.userDisplayEmail);

        if (user.isAnonymous()){
            useremail.setVisibility(View.GONE);
            username.setText("Tempprary User");
        }else {
            useremail.setText(user.getEmail());//We use this to get the userName
            username.setText(user.getDisplayName());//We use this to get the userName
        }


       /* List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();*/

        /*titles.add("Stage 5");
        content.add("Stage 6");

        titles.add("Stage 5");
        content.add("Stage 6");

        titles.add("Stage 5");
        content.add("Stage 6 Stage 6 Stage 6 Stage 6 Stage 6 Stage 6 Stage 6 Stage 6 Stage 6 Stage 6");*/

       /* adapter = new Adapter(titles, content);*/
        //here we set the layout manager
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //The staggered Grid layout expands when text in put in it.
        noteList.setAdapter(noteAdapter);

        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddJotter.class));
                //This will help to display the animation created in anim

            }
        });

    }


//here we check which item was clicked and after that
// we can specify to open the ativity needed.
// Ps: it generates from the "implements NavigationView.OnNavigationItemSelectedListener" above^

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);//This code closes the drwaer when you click a button on the nav.
        //we use a switch statement here because of the plenty items on our drawerlist
        switch (menuItem.getItemId()){
            case R.id.addNote:
            startActivity(new Intent(this, AddJotter.class));
                //This will help to display the animation created in anim

            //Here we activate the sync note feature
            case R.id.sync:
                //First we check if the user is anonymous
                if (user.isAnonymous()) {
                    startActivity(new Intent(this, Login.class));
                    //This will help to display the animation created in anim

                }else {
                    Toast.makeText(this,"You Are Connected.", Toast.LENGTH_SHORT).show();
                }

                break;
            //We activate the logout section.
            case R.id.logout:
                checkUser();
                break;

            default:
            Toast.makeText(this,"Note Added", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        //Here we check if the user is real or not
        if (user.isAnonymous()){
            displayAlert();
            //If he or she is not a real user the else statement will be executed
        }else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
            finish();
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with temporary account, Logout will delete all the notes.")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ToDo: we delete all notes created by the annonymous user

                        //ToDo: We delete the anon user
                        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();*/
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), SplashScreen.class));

                                finish();
                            }
                        });
                    }
                });
        warning.show();
    }

    //implementing the options menu we need to override two options
    //onCreateOptionsMenu to display the options menu in the activity and also inflate it with inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //inflating the options menu
        inflater.inflate(R.menu.opt_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //onOptionsItemSelected is for handling the click on the menu.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem){
        if (menuItem.getItemId() == R.id.settings) {
            Toast.makeText(this, "Options is clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

        public class NotesViewHolder extends RecyclerView.ViewHolder{
         TextView noteTitles, noteContent;
         View view;
         CardView mCardView;
        public NotesViewHolder(@NonNull View itemView){
            super(itemView);
            noteTitles = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.contents);
            //this is the implementation for the item click
            mCardView = itemView.findViewById(R.id.noteCard);//we specify the xml for the card view.
            view = itemView;

        }}

            private int getRandomColor() {
                //We now create a list of integers color code we intend using specifically
                List<Integer> colorCode = new ArrayList<>();
                //here we add the colors
                colorCode.add(R.color.colorBlue);
                colorCode.add(R.color.colorAsh);

                //This is the code for generating a random color for each
                // recyclerview within the specified size of the colors you stated in line 62 and 63
                Random randomColor = new Random();
                int number = randomColor.nextInt(colorCode.size());
                return colorCode.get(number);
     }
//This method is used to start listening to see if any change is being made to the database from the array
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }
//This method is used to stop listening since no change is being made to the database from the array when the application is closed.
    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null)
        noteAdapter.stopListening();
    }
}
/*//Find textviews, get the extras and assign both to each other
        TextView textView = findViewById(R.id.textView3);
        String headline = getIntent().getStringExtra("headline");
        textView.setText(headline);*/
