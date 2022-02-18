package ssp.smartstudentportal;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ssp.smartstudentportal.Structures.Comment;

public class CommentSnippetController {
    public VBox userAvatar;
    public Label commentUser;
    public Text commentText;
    public void setComment(Comment c) {
        setUserAvatar(c.getCommentUser().getProfilePicture());
        commentUser.setText(c.getCommentUser().getFullName());
        setCommentText(c.getCommentText());
    }
    public void setUserAvatar(Image image){
        if(image != null) {
            userAvatar.getChildren().clear();
            ImageView img = new ImageView(image);
            img = new ImageView(ImageUtils.cropImage(img, 10000));
            img.setFitWidth(30);
            img.setFitHeight(30);
            ImageUtils.clipImage(img, ImageUtils.types.Circle);
            this.userAvatar.getChildren().add(img);
        }
    }
    public void setCommentText(String text){
        if(text.length()> 200){
            commentText.setWrappingWidth(480);
        }
        commentText.setText(text);
    }
}
