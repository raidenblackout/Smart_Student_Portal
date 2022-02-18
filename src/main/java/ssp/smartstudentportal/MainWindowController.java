package ssp.smartstudentportal;

import java.io.IOException;

public class MainWindowController extends BaseController {

    /***************** Constructors *****************/
    MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }


    /***************** Methods *****************/
    public void onLogin() throws IOException {
        viewFactory.showLoginWindow();
        onExit();
    }

    public void onSignUp() {
        viewFactory.showSignUpWindow();
        onExit();
    }

    public void onExit() {
        viewFactory.closeWindow(this.getClass());
    }
    /***************** Methods *****************/
}
