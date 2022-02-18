package ssp.smartstudentportal.Structures;

import java.io.Serializable;

public class DailyTask implements Serializable {
    private String taskTitle;
    private String taskDescription;
    private String taskDate;
    private int taskId;
    public DailyTask(String taskTitle, String taskDescription, String taskDate) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
    }
    public DailyTask(int taskId,String taskTitle, String taskDescription, String taskDate) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
    }
    public String getTaskTitle() {
        return taskTitle;
    }
    public String getTaskDescription() {
        return taskDescription;
    }
    public String getTaskDate() {
        return taskDate;
    }
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
