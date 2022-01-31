package com.sbr.attendme;

public class Student {
    private String name;
    private String id;
    private int roll;

    public Student(String name, String id, int roll) {
        this.name = name;
        this.id = id;
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }
}
