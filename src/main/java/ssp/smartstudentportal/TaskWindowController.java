package ssp.smartstudentportal;

import javafx.scene.control.*;
import javafx.scene.text.Text;
import ssp.smartstudentportal.Structures.DailyTask;

public class TaskWindowController {
    public Label taskTitle;
    public TextArea taskDescription;
    public Text taskDate;
    public Button btnEdit;
    private DailyTask task;
    public void initialize(){

    }
    public void setTaskTitle(String title){
        taskTitle.setText(title);
    }
    public void setTaskDescription(String description){
        taskDescription.setText(description);
    }
    public void setTaskDate(String date){
        taskDate.setText(date);
    }
    public void toggleEdit(){
        btnEdit.setText("Save");
        taskDescription.setEditable(true);

    }
    public void toggleSave(){
        btnEdit.setText("Edit");
        taskDescription.setEditable(false);
        task.setTaskDescription(taskDescription.getText());

    }
    public void toggleDone(){

    }
    public void toggleRemove(){

    }
    public void setTask(DailyTask task){
        this.task=task;
        setTaskTitle(task.getTaskTitle());
        setTaskDescription(task.getTaskDescription());
        setTaskDate(task.getTaskDate());
    }
}
