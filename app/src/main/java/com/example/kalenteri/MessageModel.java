package com.example.kalenteri;

public class MessageModel {

    String senderToken,name;
    String message;
    long timestamp;
    String currenttime, imageRef, lastText,lastTextTime,color;

    public MessageModel(String message, String senderToken, Long timestamp, String imageRef,String name,String color) {
        this.senderToken = senderToken;
        this.message = message;
        this.timestamp = timestamp;
        this.imageRef = imageRef;
        this.name=name;
        this.color=color;

    }

    public String getColor() {
        return color;
    }

    public String getImageRef() {
        return imageRef;
    }

    public String getName() {
        return name;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public MessageModel() {
    }
}
