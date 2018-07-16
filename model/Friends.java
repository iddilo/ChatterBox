package com.example.pmitev.chatterbox.model;

public class Friends {

    private String date,name,thumb_image;



    public Friends(){


    }

    public Friends(String date, String name,String thumb_image) {
        this.date = date;
        this.name = name;
        this.thumb_image = thumb_image;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbImage() {
        return thumb_image;
    }

    public void setThumbImage(String thumb_image) {
        this.thumb_image = thumb_image;
    }

}
