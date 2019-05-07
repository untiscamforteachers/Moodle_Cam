package com.example.moodle_cam;

public class Student {
    private String name;
    private String iconID;

    public Student(String name, String iconID) {
        this.name = name;
        this.iconID = iconID;
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

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", iconID=" + iconID +
                '}';
    }
}
