package ssp.smartstudentportal;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class TaskController {

    /*************** Attributes ********************/
    private Object parent;
    @FXML
    private TextField taskName;
    @FXML
    private TextArea taskDescription;
    @FXML
    private DatePicker taskDate;
    /******************* Constructors *********************/
    public void setHidden() {
        ((DashboardWindowController) parent).setHidden();
    }

    public void addTask() {
        LocalDate date = taskDate.getValue();
        int taskId=ClientController.getInstance().addTask(taskName.getText(), taskDescription.getText(),date.toString());
        System.out.println(taskId);
        ((DashboardWindowController) parent).addTask(taskName, taskDescription,taskId);
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }
}
