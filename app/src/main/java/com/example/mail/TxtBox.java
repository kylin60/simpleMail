package com.example.mail;

public class TxtBox {
    private String sender;
    private String content;
    private String icon;

    public TxtBox(String sender, String content, String icon) {
        this.sender =sender;
        this.content = content;
        this.icon=icon;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getIcon() {
       return icon;
    }
}
