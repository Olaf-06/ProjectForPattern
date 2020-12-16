package com.example.projectforpattern;

public class Products {
    public String name;
    public String description;
    public String photoID;
    public String userID;

    public Products() {}

    public Products(String name, String description, String photoID, String userID){
        this.name = name;
        this.description = description;
        this.photoID = photoID;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoID() {
        return photoID;
    }

    public String getUserID() {return userID;}
}

