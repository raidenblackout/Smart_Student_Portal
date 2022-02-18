package ssp.smartstudentportal;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ssp.smartstudentportal.Structures.Comment;
import ssp.smartstudentportal.Structures.Post;

import java.text.ParseException;
import java.util.ArrayList;

import static ssp.smartstudentportal.TimeUtils.getTimeDifference;

public class IndividualPostViewController extends SubForumPostSnippetController{
    public VBox commentContainer;
    public void setComments(ArrayList<Comment> comments){
        commentContainer.getChildren().clear();
        if(comments!=null) {
            try {
                for (Comment c : comments) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("CommentSnippet.fxml"));
                    HBox vBox = loader.load();
                    CommentSnippetController controller = loader.getController();
                    controller.setComment(c);
                    commentContainer.getChildren().add(vBox);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void initPost() throws ParseException {
        this.setPostTitle(postInfo.getTitle()).setPostTitle(postInfo.getTitle()).setPostContent(postInfo.getDescription()).setPostUser(postInfo.getAuthorData());
        this.setPostTime(getTimeDifference(postInfo.getDate()));
        this.setFiles(ClientController.getInstance().getPostFiles(postInfo.getId()));
        System.out.println(this.postId);
        getComments();
    }
    public void getComments(){
        ArrayList<Comment> comments = ClientController.getInstance().getComments(this.postId);
        setComments(comments);
    }
    public SubForumPostSnippetController setPostContent(String content) {
        this.postContent.setText(content);
        return this;
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
                onSubmitComment();
            }
        });
    }
    public void onSubmitComment(){
        sendComment();
        getComments();
    }
}