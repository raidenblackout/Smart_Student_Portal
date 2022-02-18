package ssp.smartstudentportal;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class SignUpWindowController extends BaseController {
    public TextField fname, lname, username, pword, email, phone;
    public Text error;

    SignUpWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    public void signUp() {
        if (validate()) {
            if (ClientController.getInstance().register(username.getText(), pword.getText(), email.getText(), fname.getText(), lname.getText(), "Not yet set")) {
                onExit();
                error.setText("");
            } else {
                error.setText("Registration failed");
            }
        }
    }

    private boolean validate() {
        return !fname.getText().isEmpty() && !lname.getText().isEmpty() && !username.getText().isEmpty() && !pword.getText().isEmpty() && !email.getText().isEmpty();
    }

    public void onExit() {
        viewFactory.showLoginWindow();
        viewFactory.closeWindow(this.getClass());
    }

    public void showLoginWindow() {
        onExit();
    }
}
