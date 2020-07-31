package com.example.hometrainng.events;

import org.json.JSONObject;

public class MessageEvent {
    private String message;
    private String type;
    private JSONObject data;
    private String content;

    public MessageEvent(String message, String type, String content) {
        this.message = message;
        this.type = type;
        this.content = content;
    }

    public MessageEvent(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public MessageEvent(String message, String type, JSONObject data) {
        this.message = message;
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
