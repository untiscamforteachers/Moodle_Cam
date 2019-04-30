package com.example.moodle_cam;

public class Student {
    private String name;
    private String last_name;
    private int iconID;

    public Student(String name, String last_name, int iconID) {
        this.name = name;
        this.last_name = last_name;
        this.iconID = iconID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
