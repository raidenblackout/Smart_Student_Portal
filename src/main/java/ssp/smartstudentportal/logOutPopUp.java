package ssp.smartstudentportal;

import javafx.scene.layout.HBox;

import java.io.IOException;

public class logOutPopUp {
    public HBox logOutBtn;
    DashboardWindowController p;

    public void logOut() throws IOException {
        ViewFactory.windows.forEach((k, v) -> {
            v.close();
        });
        ViewFactory.windows.clear();
        ClientController.socket.close();
        ClientController.socket = null;
        ClientController.outputStream.close();
        ClientController.inputStream.close();
        mainApp.viewFactory.showLoginWindow();
    }

    public void setParent(DashboardWindowController p) {
        this.p = p;
    }
}
