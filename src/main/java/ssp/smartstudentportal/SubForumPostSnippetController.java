package ssp.smartstudentportal;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ssp.smartstudentportal.Structures.Comment;
import ssp.smartstudentportal.Structures.FileData;
import ssp.smartstudentportal.Structures.Post;
import ssp.smartstudentportal.Structures.UserInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class SubForumPostSnippetController {
    public Label postTitle;
    public Label postUser;
    public Label postTime;
    public Label postCategory;
    public Text postContent;
    public HBox postFiles;
    public HBox avatarContainer;
    public HBox currentUserAvatar;
    public TextArea postComment;
    public int postId;
    public Post postInfo;
    public SubForumPostSnippetController setPostTitle(String title) {
        postTitle.setText(title);
        return this;
    }

    public SubForumPostSnippetController setPostUser(UserInfo user) {
        if(user.getProfilePicture() != null) {
            this.avatarContainer.getChildren().clear();
            ImageView avatar = new ImageView(user.getProfilePicture());
            avatar=new ImageView(ImageUtils.cropImage(avatar, 1000));
            avatar.setFitWidth(50);
            avatar.setFitHeight(50);
            ImageUtils.clipImage(avatar, ImageUtils.types.Circle);
            this.avatarContainer.getChildren().add(avatar);
        }
        this.postUser.setText(user.getFullName());
        return this;
    }

    public SubForumPostSnippetController setPostTime(String time) {
        this.postTime.setText(time);
        return this;
    }

    public SubForumPostSnippetController setPostCategory(String category) {
        this.postCategory.setText(category);
        return this;
    }

    public SubForumPostSnippetController setPostContent(String content) {
        String contentString = content;
        if(content.length() > 400) {
            contentString = content.substring(0, 400) + "...";
        }
        this.postContent.setText(contentString);
        this.postContent.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                showPost();
            }
        });
        return this;
    }
    public SubForumPostSnippetController setFiles(List<FileData> files){
        System.out.println("Files: " + files.size());
        if(files.size() > 0) {
            for (FileData file : files) {
                try {
                    HBox fileBox = FXMLLoader.load(getClass().getResource("iconsFile.fxml"));
                    Label fileLabel = new Label(file.getName());
                    fileLabel.setStyle("-fx-text-fill: #ffffff;");
                    HBox fileContainer=((HBox)fileBox.getChildren().get(0));
                    fileContainer.getChildren().add(fileLabel);
                    fileContainer.setPrefHeight(100);
                    fileBox.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            //fileLabel.getScene().getWindow().hide();
                            //show file save dialog
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Save File");
                            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL", "*.*"));
                            fileChooser.setInitialFileName(file.getName());
                            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                            File fileToSave = fileChooser.showSaveDialog(fileLabel.getScene().getWindow());
                            if (fileToSave != null) {
                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(fileToSave);
                                    fos.write(file.getFile());
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    this.postFiles.getChildren().add(fileBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return this;
    }

    public void setPostId(int id) {
        this.postId=id;
    }
    public void setPostInfo(Post postInfo) {
        this.postInfo = postInfo;
    }
    public void initialize(){
        if(ClientController.userInfo.getProfilePicture() != null) {
            this.currentUserAvatar.getChildren().clear();
            ImageView avatar = new ImageView(ClientController.userInfo.getProfilePicture());
            avatar=new ImageView(ImageUtils.cropImage(avatar, 1000));
            avatar.setFitWidth(30);
            avatar.setFitHeight(30);
            ImageUtils.clipImage(avatar, ImageUtils.types.Circle);
            this.currentUserAvatar.getChildren().add(avatar);
        }
        this.postComment.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                sendComment();
            }
        });
    }
    public void sendComment(){
        if(this.postComment.getText().length() > 0) {
            ClientController.getInstance().sendComment(new Comment(ClientController.userInfo,this.postComment.getText(),this.postId));
            this.postComment.clear();
        }
    }
    public void showPost(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("IndividualPostWindow.fxml"));
            Scene scene = new Scene(loader.load());
            IndividualPostViewController controller = loader.getController();
            controller.setPostId(this.postId);
            controller.setPostInfo(this.postInfo);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Post");
            stage.show();
            controller.initPost();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
