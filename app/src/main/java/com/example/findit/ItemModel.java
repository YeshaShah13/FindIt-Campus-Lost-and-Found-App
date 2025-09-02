package com.example.findit;

public class ItemModel {
    // Firestore does NOT support int for IDs unless explicitly stored
    private String id; // Make this String if you want to map Firestore doc ID
    private String type;
    private String name;
    private String description;
    private String location;
    private String date;
    private String time;
    private String imagePath;
    private String userId;
    private String contactInfo;

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }



    public ItemModel() {
        // Firestore needs this
    }

    public ItemModel(String id,String type, String name, String description, String location,
                     String date, String time, String imagePath) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.time = time;
        this.imagePath = imagePath;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
