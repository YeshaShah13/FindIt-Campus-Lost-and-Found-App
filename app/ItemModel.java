package com.example.findit;

public class ItemModel {
    private int id;
    private String type; // Lost or Found
    private String name;
    private String description;
    private String location;
    private String date;
    private String time;
    private String imagePath;

    public ItemModel(int id, String type, String name, String description,
                     String location, String date, String time, String imagePath) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.time = time;
        this.imagePath = imagePath;
    }

    // Getters
    public int getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getImagePath() { return imagePath; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
