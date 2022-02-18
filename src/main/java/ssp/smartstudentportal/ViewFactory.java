package ssp.smartstudentportal;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewFactory {
    /******************** Attributes *************************/
    public static Map<Class<?>, Stage> windows = new HashMap<>();
    /******************** Attributes *************************/

    /******************************* Window Controller Methods *********************************/
    public void showLoginWindow() {
        if (!checkIfWindowWasPrevOpened(LoginWindowController.class)) {
            LoginWindowController controller = new LoginWindowController(this, "LoginWindow.fxml");
            init_window(controller, "Login");
        } else {
            windows.get(LoginWindowController.class).show();
        }
    }

    public void showDashboardWindow() throws IOException {
        if (!checkIfWindowWasPrevOpened(DashboardWindowController.class)) {
            DashboardWindowController controller = new DashboardWindowController(this, "Dashboard.fxml");
            init_window(controller, "Dashboard");
            FXMLLoader addTaskLoader = new FXMLLoader(getClass().getResource("AddTask.fxml"));
            Node addTaskNode = addTaskLoader.load();
            controller.setPopUp(addTaskNode, addTaskLoader.getController());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("logOutPopUp.fxml"));
            Node logoutNode = null;
            try {
                logoutNode = fxmlLoader.load();
                System.out.println("Logout box loaded");
                controller.setLogOutPopUp(logoutNode, fxmlLoader.getController());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            windows.get(DashboardWindowController.class).show();
        }
    }

    public void showMainWindow() {
        if (!checkIfWindowWasPrevOpened(MainWindowController.class)) {
            MainWindowController controller = new MainWindowController(this, "MainWindow.fxml");
            init_window(controller, "Main Window");
        } else {
            windows.get(MainWindowController.class).show();
        }
    }

    public void showForumWindow() {
        if (!checkIfWindowWasPrevOpened(ForumWindowController.class)) {
            ForumWindowController controller = new ForumWindowController(this, "ForumWindow.fxml");
            init_window(controller, "Forum");
        } else {
            windows.get(ForumWindowController.class).show();
        }
    }


    public void showChatWindow() {
        if (!checkIfWindowWasPrevOpened(ChatWindowController.class)) {
            ChatWindowController controller = new ChatWindowController(this, "ChatWindow.fxml");
            init_window(controller, "Chat");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserSearchBox.fxml"));
            Node userSearchBox = null;
            try {
                userSearchBox = fxmlLoader.load();
                System.out.println("User search box loaded");
                controller.setPopUp(userSearchBox, fxmlLoader.getController());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            windows.get(ChatWindowController.class).show();
            System.out.println("Chat window was opened");
            ChatWindowController chatWindowController = (ChatWindowController) MainController.controllers.get(ChatWindowController.class);
            chatWindowController.getUserList();

        }
    }

    public void showSignUpWindow() {
        if (!checkIfWindowWasPrevOpened(SignUpWindowController.class)) {
            SignUpWindowController controller = new SignUpWindowController(this, "SignUpWindow.fxml");
            init_window(controller, "Sign Up");
        } else {
            windows.get(SignUpWindowController.class).show();
        }
    }

    public void showProfileWindow() {
        if (!checkIfWindowWasPrevOpened(ProfileWindowController.class)) {
            ProfileWindowController controller = new ProfileWindowController(this, "ProfileWindow.fxml");
            init_window(controller, "Profile");
        } else {
            windows.get(ProfileWindowController.class).show();
        }
    }

    public void showCreatePostWindow() {
        if (!checkIfWindowWasPrevOpened(CreatePostWindow.class)) {
            if(checkIfWindowWasPrevOpened(ForumWindowController.class)) {
                windows.get(ForumWindowController.class).hide();
            }
            CreatePostWindow controller = new CreatePostWindow(this, "CreateForumWindow.fxml");
            init_window(controller, "Create Post");
        } else {
            windows.get(ForumWindowController.class).hide();
            windows.get(CreatePostWindow.class).show();
        }
    }
    public void showCreatePostWindow(int id) {
        if (!checkIfWindowWasPrevOpened(CreatePostWindow.class)) {
            if(checkIfWindowWasPrevOpened(SubForumController.class)) {
                windows.get(SubForumController.class).hide();
            }
            SubForumCreatePost controller = new SubForumCreatePost(this, "CreatePostWindow.fxml");
            controller.setForumId(id);
            init_window(controller, "Create Post");
        } else {
            windows.get(SubForumController.class).hide();
            windows.get(SubForumCreatePost.class).show();
        }
    }
    public void showSubForumWindow(int id){
        if(!checkIfWindowWasPrevOpened(SubForumController.class)){
            if(checkIfWindowWasPrevOpened(ForumWindowController.class)){
                windows.get(ForumWindowController.class).hide();
            }
            SubForumController controller = new SubForumController(this, "SubForumWindow.fxml");
            controller.setSubForumId(id);
            init_window(controller, "SubForum");
        }else{
            windows.get(SubForumController.class).show();
        }
    }
    public void showSubForumWindow(int id,String name){
        if(!checkIfWindowWasPrevOpened(SubForumController.class)){
            if(checkIfWindowWasPrevOpened(ForumWindowController.class)){
                windows.get(ForumWindowController.class).hide();
            }
            SubForumController controller = new SubForumController(this, "SubForumWindow.fxml");
            controller.setSubForumId(id);
            init_window(controller, name);
        }else{
            windows.get(SubForumController.class).show();
        }
    }
    /******************************* Window Controller Methods *********************************/


    /******************************************** private methods ************************************************/

    private boolean checkIfWindowWasPrevOpened(Class<?> controllerClass) {
        if (windows.containsKey(controllerClass)) {
            System.out.println("I'm in");
            windows.get(controllerClass).show();
            return true;
        }

        return false;
    }

    private void init_window(BaseController controller, String title) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(controller.getFxmlName()));
        fxmlLoader.setController(controller);

        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle(title);
        //stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(event -> {
            windows.remove(controller.getClass());
        });
        windows.put(controller.getClass(), stage);
        controller.init();
    }
    public void showCourseWindow(){
        if(!checkIfWindowWasPrevOpened(CourseWindowController.class)){
            CourseWindowController controller = new CourseWindowController(this, "CoursesWindow.fxml");
            init_window(controller, "Courses");
        }else{
            windows.get(CourseWindowController.class).show();
        }
    }
    /******************************************** private methods ************************************************/

    public void closeWindow(Class<?> controllerClass) {
        System.out.println("Closing window" + controllerClass.getName());
        windows.get(controllerClass).close();
        windows.remove(controllerClass);
        if (windows.isEmpty()) {
            try {
                ClientController.socket.close();
                ClientController.outputStream.close();
                ClientController.inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){

            }

        }
    }
    public void hideWindow(Class<?> controllerClass) {
        if(checkIfWindowWasPrevOpened(controllerClass)) {
            windows.get(controllerClass).hide();
        }
    }


    public Node createEmptyView(String no_posts_found) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setMaxWidth(Double.MAX_VALUE);
        Label label = new Label(no_posts_found);
        label.setStyle("-fx-font-size: 20px; -fx-text-fill: #000000;");
        label.setAlignment(Pos.CENTER);
        vBox.getChildren().add(label);
        return vBox;
    }
}
