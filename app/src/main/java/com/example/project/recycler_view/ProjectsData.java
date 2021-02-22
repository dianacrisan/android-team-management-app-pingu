package com.example.project.recycler_view;

public class ProjectsData {
    String projectName;
    String deadline;
    String activities;

    public ProjectsData(String name, String date, String activities)
    {
        this.projectName = name;
        this.deadline = date;
        this.activities = activities;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }
}
