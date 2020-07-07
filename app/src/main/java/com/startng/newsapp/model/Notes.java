package com.startng.newsapp.model;

public class Notes {
    //We create the fields and they much match what is in the database
    private String title;
    private String content;

    //We create our public constructors for handling our data
    public Notes() {}
    public Notes(String title, String content){
        this.title = title;
        this.content = content;
    }

   // Now we generate the setter and getter methods

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

