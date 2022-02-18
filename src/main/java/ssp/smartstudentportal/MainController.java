package ssp.smartstudentportal;

import javafx.application.Platform;
import javafx.concurrent.Task;
import ssp.smartstudentportal.Structures.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MainController {
    public static Map<Class<?>, MainController> controllers = new HashMap<>();

    public MainController() {
        controllers.put(this.getClass(), this);
    }

    public static Object taskSet() {
        System.out.println("Background task started");
        ClientResponse response;
        try {
            while ((response = (ClientResponse) ClientController.inputStream.readObject()) != null) {
                System.out.println(response.getResponseType());
                if (controllers.containsKey(ChatWindowController.class)) {
                    System.out.println("ChatWindowController");
                    ChatWindowController controller = (ChatWindowController) controllers.get(ChatWindowController.class);
                    if (response.getResponseType() == Response.UPDATE_USER_LIST) {
                        ClientResponse finalResponse1 = response;
                        Platform.runLater(() -> {
                            controller.setUserList((List<UserInfo>) finalResponse1.getData());
                        });
                    } else if (response.getResponseType() == Response.UPDATE_MESSAGES) {
                        ClientResponse finalResponse = response;
                        System.out.println("Message received");
                        Platform.runLater(() -> {
                            controller.setMessages((List<Message>) finalResponse.getData());
                        });
                    } else if (response.getResponseType() == Response.UPDATE_SEARCH_USERNAMES) {
                        ClientResponse finalResponse = response;
                        Platform.runLater(() -> {
                            controller.userSearchBoxController.setUserNames((ArrayList<String>) finalResponse.getData());
                            controller.userSearchBoxController.updateUserNames();
                        });
                    } else if (response.getResponseType() == Response.UPDATE_OTHER_USER_INFO) {
                        ClientResponse finalResponse = response;
                        Platform.runLater(() -> {
                            ClientController.otherUserInfo = (UserInfo) finalResponse.getData();
                            controller.setOtherUserInfo();
                        });
                    } else if (response.getResponseType() == Response.UPDATE_USER_INFO) {
                        ClientResponse finalResponse = response;
                        Platform.runLater(() -> {
                            ClientController.userInfo = (UserInfo) finalResponse.getData();
                            ClientController.userName = ClientController.userInfo.getUserName();
                        });
                    }
                }
                if(controllers.containsKey(ForumWindowController.class)) {
                    System.out.println("ForumWindowController");
                    ForumWindowController controller = (ForumWindowController) controllers.get(ForumWindowController.class);
                    if (response.getResponseType()== Response.UPDATE_POSTS){
                        ClientResponse finalResponse = response;
                        Platform.runLater(() -> {
                            controller.loadPosts((ArrayList<Post>) finalResponse.getData());
                        });
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }catch (Exception e){

        }
        System.out.println("Background task finished");
        return null;
    }

    public static void runTask() {
        System.out.println("Starting background task");
        Task temp = new Task() {
            @Override
            protected Object call() throws Exception {
                return MainController.taskSet();
            }
        };
        Thread thread = new Thread(temp);
        thread.setDaemon(true);
        thread.start();
    }
}
