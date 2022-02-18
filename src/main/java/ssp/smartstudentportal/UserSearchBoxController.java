package ssp.smartstudentportal;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ssp.smartstudentportal.Structures.UserInfo;

import java.util.ArrayList;

public class UserSearchBoxController {
    public ChatWindowController parent;
    public TextField textField;
    public VBox root;
    private ArrayList<String> userNames;

    public void initialize() {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String userName = textField.getText();
                if (userName.length() > 0) {
                    ClientController.getInstance().searchUser(userName);
                } else {
                    root.getChildren().clear();
                    root.getChildren().add(textField);
                }
            }
        });
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                String userName = textField.getText();
                if (userName.length() > 0) {
                    ClientController.otherUser = userName;

                    boolean isUser = false;
                    for (UserInfo s : parent.userList) {
                        if (s.getUserName().equals(userName)) {
                            isUser = true;
                            break;
                        }
                    }
                    if (!isUser) {
                        UserInfo userInfo = ClientController.getInstance().getUserInfoI(userName);
                        parent.userArea.getChildren().add(parent.createUserSnippet(userInfo, ""));
                        parent.userList.add(userInfo);
                    }
                    close();
                }
            } else if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                close();
            }
        });

    }

    public void setParent(BaseController parent) {
        this.parent = (ChatWindowController) parent;
    }

    public void close() {
        parent.closePopUp();
    }

    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

    public void updateUserNames() {
        root.getChildren().clear();
        root.getChildren().add(textField);
        if (userNames != null) {
            for (String name : userNames) {
                if (name.equals(ClientController.userName)) continue;
                Text text = new Text(name);
                text.setOnMouseClicked(event -> {
                    textField.setText(name);
                });
                root.getChildren().add(text);
            }
        }
    }
}
