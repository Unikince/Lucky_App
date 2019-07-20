package com.bt_121shoppe.lucky_app.models;

public class User {

    private String id;
    private String username;
    private String imageURL;

    public User(String id,String username,String imageURL){
        this.id=id;
        this.username=username;
        this.imageURL=imageURL;
    }

    public User(){

    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
