package com.example.moodle_cam;

public class Student {
    private String name;
    private String iconID;
    private Boolean exists;

    public Student(String name, String iconID, Boolean exists) {
        this.name = name;
        this.iconID = iconID;
        this.exists = exists;
    }
    public Student(){
        //empty
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIconID() {
        return iconID;
    }

    public void setIconID(String iconID) {
        this.iconID = iconID;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", iconID=" + iconID +
                '}';
    }
}
