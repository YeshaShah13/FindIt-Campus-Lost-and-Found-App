public class Post {
    String type, title, description, location, dateTime;
    int imageResId;

    public Post(String type, String title, String description, String location, String dateTime, int imageResId) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.imageResId = imageResId;
    }

    // Add getters if needed
}
