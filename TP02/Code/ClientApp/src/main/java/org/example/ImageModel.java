package org.example;

public class ImageModel {
    private String imageName;
    private String image;

    private String[] marks;


    public ImageModel() {

    }

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
                "imageName='" + imageName + '\'' +
                ", marks='" + marks + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

}
