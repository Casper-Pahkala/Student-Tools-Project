package com.example.kalenteri;

public class Model {
    String text,date,id;

    public Model(String text, String date,String id) {
        this.text = text;
        this.date = date;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public Model() {
    }
}
