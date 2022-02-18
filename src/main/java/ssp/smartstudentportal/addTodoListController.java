package ssp.smartstudentportal;

import javafx.scene.control.TextField;

public class addTodoListController {
    public DashboardWindowController parent;
    public TextField content;

    public void setParent(DashboardWindowController parent) {
        this.parent = parent;
    }
    public void addTodoList() {
        //parent.addToDoList(content.getText());
        String content = this.content.getText();
        ClientController.getInstance().addTodoList(content);
        parent.getTodoList();
    }
}
