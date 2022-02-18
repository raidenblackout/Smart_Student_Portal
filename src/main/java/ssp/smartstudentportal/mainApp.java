package ssp.smartstudentportal;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Scanner;

public class mainApp extends Application {
    public static ViewFactory viewFactory;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Scanner scanner = new Scanner(System.in);
////        String username = scanner.nextLine();
////        String anotherUser = scanner.nextLine();
////        ClientController.userName= username;
////        ClientController.anotherUser= anotherUser;
        mainApp.viewFactory = new ViewFactory();
        mainApp.viewFactory.showMainWindow();
    }
}
