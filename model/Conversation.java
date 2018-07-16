package com.example.pmitev.chatterbox.model;

public class Conversation {

    public  boolean seen;
    public  long timestamp;
    public  String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public  Conversation(){

    }

    public Conversation(boolean seen, long timestamp,String date) {
        this.seen = seen;
        this.timestamp = timestamp;
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
