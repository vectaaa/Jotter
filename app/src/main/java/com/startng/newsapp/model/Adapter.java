package com.startng.newsapp.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.startng.newsapp.Jotter.JotterDetails;
import com.startng.newsapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<String> titles;
    List<String> contents;

    //Here we create the constructor for the adapter so as to be able to pass data to the main activity.
    public Adapter(List<String> titles, List<String> contents){
        this.titles = titles;
        this.contents = contents;
    }
    @NonNull
    @Override
    //This is used to create the view for our recycler view where we would display the data
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //We inflate th view that would be displayed in the view holder of the recycler view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent, false );
        return new ViewHolder(view);
    }

    //The onBindViewHolder is used to bind our data to the view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Here we are binding the actual data that we recieve from the main-activity,
        // when we created the adapter object and we bind it to the view we have here.
        //To execute that i made use of the holder object.(we use the "holder" to reference the noteTitles and note Content.
        holder.noteTitles.setText(titles.get(position));
        holder.noteContent.setText(contents.get(position));
        //NB: This will extract both title and content from the list and assign it to the view layout
        //Here we create a integer variable for our colour so we can also display the same colour on the card view in our background.
        final int code = getRandomColor();
        //we use the holder to get the card view and set the background color
        holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(code,null));
        //we create the method get random color so as to pick a random color line 58.

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(v.getContext(), JotterDetails.class);
               i.putExtra("title", titles.get(position));
               i.putExtra("content", contents.get(position)); //this is what happens here,
                // when someone clicks on the first item of the recycler-view, it will get the position as zero,
                // plus we are extracting the title and content from the zero position and we pass it
                // to the JotterDetails.xml so we get the coorect title and description in the JotterDetails.xml
                //now we move back to the JotterDetails.Java file to get our data with getDataIntent.
                i.putExtra("code", code);//Here we pass in the color to the next activity.
                // after creating the variable in line 48. and using it in line 50.
                // we change the color of the background manually to one of our selected colors in the content_jotter_details.xml.
                // and proceed to line 35 in JotterDetails.java
               v.getContext().startActivity(i);
            }
        });
    }
     //The created method for the random color hex code remember vectaa!
     // its an integer number as specified in onBindViewholder.
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

    //Here the number of data we want to display in the recycler view is actually displayed.
    @Override
    public int getItemCount() {
        //Here we display the size of the array of list we are recieving from the activity
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //We need to create variables for the data going into the title and content area of the cardview.
        TextView noteTitles, noteContent;
        //this view i created here basically will handle click on any item of the recycler.
        //so when an item is clicked they are being sent to the details of that particular note.
        View view;
        //create card view variable
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Down here i assigned the xml resources needed for the txt views.
            //we also need to link it to the parent view that will be inflating the layout
            noteTitles = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            //this is the implementation for the item click
            mCardView = itemView.findViewById(R.id.noteCard);//we specify the xml for the card view.
            view = itemView;
        }
    }
}
