package ssp.smartstudentportal;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;
import ssp.smartstudentportal.Structures.Message;
import ssp.smartstudentportal.Structures.UserInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ChatWindowController extends BaseController {
    /******************** Attributes *************************/
    public TextArea chatArea;
    public HBox chatBox;
    public VBox leftBar;
    public TextField searchBar;
    public Label titleBar;
    public VBox messageArea;
    public HBox iconPanel;
    public FontIcon sendMessage;
    public FontIcon newMessage;
    public ScrollPane messageContainer;
    public VBox userArea;
    public ArrayList<String[]> currentMessage;
    public UserInfo otherUserInfo;
    public Text UserFName;
    public UserSearchBoxController userSearchBoxController;
    public List<UserInfo> userList;
    public HBox imageContainer;
    private ArrayList<Node> nodes;
    private boolean autoScroll = true;
    private PopOver popOver;
    private byte[] data_image;
    private boolean lock=false;
    public Text userActiveStatus;
    public FontIcon currentStatus;
    private ImageView userLogo;
    private HashMap<String, UserInfo> users=new HashMap<>();
    private HashMap<String, WritableImage> userImages=new HashMap<>();
    /******************** Attributes *************************/

    /******************** Constructor *************************/
    ChatWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
        currentMessage = new ArrayList<>();
    }

    /******************** Constructor *************************/

    private static void addAllDescendants(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Text) nodes.add(node);
            if (node instanceof Parent)
                addAllDescendants((Parent) node, nodes);
        }
    }

    /******************** Initialization *************************/

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendants(root, nodes);
        return nodes;
    }

    /******************** Initialization *************************/

    @FXML
    public void initialize() throws ParseException {
        nodes = getAllNodes(messageArea);
        leftBar.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() <= 100) {
                    titleBar.setTranslateX(-1000);
                    searchBar.setTranslateX(-1000);
                } else {
                    titleBar.setTranslateX(0);
                    searchBar.setTranslateX(0);
                }
            }
        });
        chatArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //count the number of lines in the text area
                int lineCount = chatArea.getParagraphs().size();
                if (lineCount < 10) {
                    chatBox.setPrefHeight((lineCount - 1) * 30 + 65);
                }
            }
        });

        messageContainer.setOnMouseClicked(event -> {
            autoScroll = messageContainer.getVvalue() == 1;
        });
        messageContainer.setOnMouseDragged(event -> {
            autoScroll = false;
        });
        chatArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    chatArea.appendText("\n");
                } else {
                    sendMessage();
                }
            }
        });
        getUserList();
        messageArea.getChildren().clear();
        searchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String search = searchBar.getText();
                    ArrayList<UserInfo> searchResult = new ArrayList<>();
                    userList.forEach(user -> {
                        if (user.getFullName().toLowerCase().contains(search.toLowerCase())) {
                            searchResult.add(user);
                        }
                    });
                    setSearchUserList(searchResult);
            }
        });
//        Thread thread = new Thread(backgroundTask);
//        thread.setDaemon(true);
//        thread.start();
    }

    /******************** Methods *************************/
    public void onExit() {
        viewFactory.closeWindow(this.getClass());
    }

    public void sendMessage() {
        if (chatArea.getText().length() > 0 || data_image != null) {
            System.out.println("Sending message");
            String message = chatArea.getText();
            Message msg = new Message(ClientController.userName, ClientController.otherUser, message, data_image, "");
            chatArea.clear();
            ClientController.getInstance().sendMessage(ClientController.userName, ClientController.otherUser, msg);
            messageArea.getChildren().add(createMessageBubble(message, data_image));
            messageContainer.setVvalue(1);
            imageContainer.getChildren().clear();
            data_image = null;
        }
    }

    public void setOtherUserInfo() {
        this.otherUserInfo = ClientController.otherUserInfo;
        if(otherUserInfo != null && otherUserInfo.getProfilePicture() != null) {
            userLogo= new ImageView(otherUserInfo.getProfilePicture());
            WritableImage img=ImageUtils.cropImage(userLogo,5000);
            userLogo.setImage(img);
            userLogo.setFitHeight(50);
            userLogo.setFitWidth(50);
            ImageUtils.clipImage(userLogo,ImageUtils.types.Circle);
        }
        UserFName.setText(otherUserInfo.getFullName());
        userActiveStatus.setText(otherUserInfo.getAuthStatus()?"Active now":"Offline");
        currentStatus.setIconColor(otherUserInfo.getAuthStatus()? Color.GREEN:Color.RED);

    }

    public HBox createMessageBubble(String message, byte[] image) {
        HBox res = createMessageBubble(message);
        if (image != null) {
            System.out.println("Image is not null");
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(image)));
            imageView.setFitHeight(400);
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);
            //ImageUtils.clipImage(imageView, ImageUtils.types.Rectangle);
            ImageUtils.clipRectangleChat(imageView);
            ((VBox) res.getChildren().get(0)).getChildren().add(imageView);
        }
        return res;
    }

    public HBox createMessageBubble(String message) {
        HBox messageBubble = new HBox();
        messageBubble.getStyleClass().add("message-bubble");
        VBox dataContent = new VBox();
        message = message.trim();
        if (message.length() > 0) {
            HBox messageContent = new HBox();
            messageContent.getStyleClass().add("message-content");

            Text messageText = new Text(message);
            messageText.getStyleClass().add("message-text");
            messageText.setFont(Font.font("System", FontWeight.NORMAL, 14.5));
            if (message.length() > 400) {
                messageText.setWrappingWidth(400);
            }
            messageContent.getChildren().add(messageText);
            messageContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            messageContent.setPadding(new Insets(0, 20, 0, 20));
            //messageContent.prefHeight(10);
            messageContent.setMaxWidth(Region.USE_COMPUTED_SIZE);
            messageContent.setMaxHeight(Region.USE_COMPUTED_SIZE);
            dataContent.getChildren().add(messageContent);
        }
        dataContent.setSpacing(10);
        messageBubble.setFillHeight(true);
        messageBubble.getChildren().add(dataContent);
        messageBubble.setAlignment(Pos.TOP_RIGHT);
        messageBubble.setPadding(new Insets(0, 40, 0, 0));
        messageBubble.getStyleClass().add("my-text");
        return messageBubble;
    }

    public HBox createMessageBubbleForOther(String message, byte[] image) {
        HBox res = createMessageBubbleForOther(message);
        if (image != null) {
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(image)));
            imageView.setFitHeight(400);
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);
            //ImageUtils.clipImage(imageView, ImageUtils.types.Rectangle);
            ImageUtils.clipRectangleChat(imageView);
            ((VBox) res.getChildren().get(1)).getChildren().add(imageView);
        }
        return res;
    }

    public HBox createMessageBubbleForOther(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatBox.fxml"));
        HBox messageBubble = null;
        try {
            messageBubble = fxmlLoader.load();
            if(userImages.containsKey(ClientController.otherUser)){
                ImageView imageView = new ImageView(userImages.get(ClientController.otherUser));
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                ImageUtils.clipImage(imageView,ImageUtils.types.Circle);
                ((VBox) messageBubble.getChildren().get(0)).getChildren().clear();
                ((VBox) messageBubble.getChildren().get(0)).getChildren().add(imageView);
            }else{
                if(ClientController.otherUserInfo.getProfilePicture()!=null) {
                    WritableImage image = ImageUtils.cropImage(new ImageView(ClientController.otherUserInfo.getProfilePicture()), 5000);
                    userImages.put(ClientController.otherUser, image);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    ImageUtils.clipImage(imageView, ImageUtils.types.Circle);
                    ((VBox) messageBubble.getChildren().get(0)).getChildren().clear();
                    ((VBox) messageBubble.getChildren().get(0)).getChildren().add(imageView);
                }
            }
            if (message.length() == 0) {
                ((VBox) messageBubble.getChildren().get(1)).getChildren().clear();
                return messageBubble;
            }
            Text messageText = new Text(message);
            messageText.setFont(Font.font("System", FontWeight.NORMAL, 14.5));
            if (message.length() > 400) {
                messageText.setWrappingWidth(400);
            }
            ((HBox) ((VBox) messageBubble.getChildren().get(1)).getChildren().get(0)).getChildren().add(messageText);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return messageBubble;
    }


    public HBox createUserSnippet(UserInfo user, String lastMessage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserChatSnippet.fxml"));

        HBox userSnippet = null;
        try {
            userSnippet = fxmlLoader.load();
            UserChatSnippetController controller = fxmlLoader.getController();
            controller.setProfilePic(new ImageView(user.getProfilePicture()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((Text) ((VBox) userSnippet.getChildren().get(1)).getChildren().get(0)).setText(user.getFullName());
        ((Text) ((VBox) userSnippet.getChildren().get(1)).getChildren().get(1)).setText(lastMessage);
        ((VBox) userSnippet.getChildren().get(1)).getChildren().get(0).setId(user.getUserName());
        System.out.println(user.getUserName());
        HBox finalUserSnippet = userSnippet;
        HBox finalUserSnippet1 = userSnippet;
        userSnippet.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                changeAnotherUser(((VBox) finalUserSnippet.getChildren().get(1)).getChildren().get(0).getId());
                getUserInfo(ClientController.otherUser);
                getMessages(ClientController.userName, ClientController.otherUser);
                if (!finalUserSnippet1.getStyleClass().contains("selected-user")) {
                    finalUserSnippet1.getStyleClass().add("selected-user");
                }
                userArea.getChildren().forEach(node -> {
                    if (node != finalUserSnippet1) {
                        node.getStyleClass().remove("selected-user");
                    }
                });
                chatArea.clear();
            }
        });
        return userSnippet;
    }

    public void changeAnotherUser(String userName) {
        System.out.println("this is the new user: "+userName);
        ClientController.otherUser = userName;
    }

    public void getUserInfo(String userName) {
        if(!users.containsKey(userName)) {
            ClientController.otherUserInfo = ClientController.getInstance().getUserInfoI(userName);
            users.put(userName, ClientController.otherUserInfo);
        }else{
            ClientController.otherUserInfo = users.get(userName);
        }
        ClientController.otherUser = ClientController.otherUserInfo.getUserName();
    }

    public void setOtherUserInfo(UserInfo userInfo) {
        otherUserInfo = userInfo;
    }

    public void getUserList() {
        System.out.println("getUserList");
        ClientController.getInstance().getUserList(ClientController.userName);
    }

    public void getMessages(String userName, String otherUserName) {
        ClientController.getInstance().getMessages(userName, otherUserName, "10");
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
        if(lock==false) {
            userArea.getChildren().clear();
            userList.forEach(user -> {
                userArea.getChildren().add(createUserSnippet(user, ""));
            });
        }
    }
    public void setSearchUserList(List<UserInfo> userList) {
        userArea.getChildren().clear();
        userList.forEach(user -> {
            userArea.getChildren().add(createUserSnippet(user, ""));
        });
    }

    public void setMessages(List<Message> messages) {
        messageArea.getChildren().clear();
        messages.forEach(message -> {
            System.out.println(message.getSender());
            if (message.getSender().equals(ClientController.userName)) {
                messageArea.getChildren().add(0, createMessageBubble(message.getMessage(), message.getImage()));
            } else {
                messageArea.getChildren().add(0, createMessageBubbleForOther(message.getMessage(), message.getImage()));
            }
        });
        if (autoScroll) {
            messageContainer.setVvalue(1);
        }
    }

    public void setPopUp(Node popUpWindow, UserSearchBoxController childController) {
        this.popOver = new PopOver();
        this.popOver.setContentNode(popUpWindow);
        childController.setParent(this);
        userSearchBoxController = childController;
        this.popOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);
    }

    public void showPopUp() {
        popOver.show(newMessage);
    }

    public void closePopUp() {
        this.popOver.hide(Duration.seconds(0));
    }

    public void UploadImageActionPerformed() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG
                = new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
        FileChooser.ExtensionFilter extFilterjpg
                = new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG
                = new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
        FileChooser.ExtensionFilter extFilterpng
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters()
                .addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng);
        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                ImageView img = new ImageView();
                img.setImage(image);
                img.setFitWidth(200);
                img.setFitHeight(200);
                img.setPreserveRatio(true);
                img.scaleXProperty();
                img.scaleYProperty();
                img.setSmooth(true);
                img.setCache(true);
                imageContainer.getChildren().add(createImageView(img));
                FileInputStream fin = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];

                for (int readNum; (readNum = fin.read(buf)) != -1; ) {
                    bos.write(buf, 0, readNum);
                }
                data_image = bos.toByteArray();
                fin.close();
                bos.close();
            } catch (IOException ex) {
                Logger.getLogger("ss");
            }
        }
    }
    public HBox createImageView(ImageView imageView) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatImageContainer.fxml"));
            HBox imageContainer = loader.load();
            ChatImageContainer chatImageContainerController = loader.getController();
            chatImageContainerController.setImage(imageView);
            return imageContainer;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /******************** Methods *************************/


}
