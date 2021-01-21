package com.example.labeler;

public class CustomData {
    String project;
    int active;

    public CustomData(String project, int active) {
        this.project = project;
        this.active = active;
    }

    public String getProject() {
        return project;
    }

    public int getActive() {
        return active;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
