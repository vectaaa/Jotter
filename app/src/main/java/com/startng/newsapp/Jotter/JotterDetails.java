package com.startng.newsapp.Jotter;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.startng.newsapp.R;

public class JotterDetails extends AppCompatActivity {
    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jotter_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        TextView content = findViewById(R.id.jotterDetailsContent);
        TextView title = findViewById(R.id.JotterDetailsTitle);
        content.setMovementMethod(new ScrollingMovementMethod()); //Now move to adapter to pass the data using key value pairs in Line 55 and 55.

        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));
        content.setBackgroundColor(getResources().getColor(data.getIntExtra("code", 0),null));// here we use intExtra here because we are trying to get color which is an integer variable

        //The floating action button to crete a new note. although it displays a snack back for now.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //The idea here is that when a user clicks on this button he or she is sent to the edit activity with  the intial data displayed so they can edit.
                Intent i = new Intent(view.getContext(), EditJotter.class);
                i.putExtra("title", data.getStringExtra("title"));
                i.putExtra("content", data.getStringExtra("content"));
                i.putExtra("noteId", data.getStringExtra("noteId"));//The use of the noteId is basically for being able to edit the text.
                //Plus we are passing the details from the jotter details activity to the editjotter activity.
                startActivity(i);
                /*Snackbar.make(view, "Replace with action", Snackbar.LENGTH_SHORT).setAction("Action", null).show(); */
            }


        });

    }

        //for the back arrow process which will take you back to home activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
