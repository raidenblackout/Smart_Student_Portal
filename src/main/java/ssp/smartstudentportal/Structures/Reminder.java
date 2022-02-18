package ssp.smartstudentportal.Structures;

import java.io.Serializable;

public class Reminder implements Serializable {
    private String content;
    private String date;
    private String username;
    private int id;
    private boolean isDone;
    public Reminder(String content, String date, String username) {
        this.content = content;
        this.date = date;
        this.username = username;

    }
    public Reminder(){};
    public String getContent() {
        return content;
    }
    public String getDate() {
        return date;
    }
    public String getUsername() {
        return username;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
