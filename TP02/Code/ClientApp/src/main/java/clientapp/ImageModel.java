package clientapp;

import java.util.Arrays;

public class ImageModel {
    private String id;
    private String imageName;
    private String image;
    private String[] marks;

    public ImageModel() {
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id; }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String[] getMarks() {
        return marks;
    }

    public void setMarks(String[] marks) {
        this.marks = marks;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "id='" + id + '\'' +
                "imageName='" + imageName + '\'' +
                ", marks='" + Arrays.toString(marks) + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

}
