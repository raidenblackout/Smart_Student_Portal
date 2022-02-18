package ssp.smartstudentportal;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;


public class LoginWindowController extends BaseController {
    /***********************Attributes***********************/
    public AnchorPane root;
    public TextField usernameField;
    public PasswordField passwordField;
    public Text err;
    /***********************Attributes***********************/


    /***********************Constructors***********************/
    public LoginWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }
    /***********************Constructors***********************/


    /***********************Methods***********************/
    public void login() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (ClientController.getInstance().login(username, password)) {
            viewFactory.showDashboardWindow();
            viewFactory.closeWindow(this.getClass());
            runTask();
        } else {
            err.setText("Invalid username or password");
        }
    }

    public void showSignUpWindow() {
        viewFactory.showSignUpWindow();
        viewFactory.closeWindow(this.getClass());
    }
    /***********************Methods***********************/
}
