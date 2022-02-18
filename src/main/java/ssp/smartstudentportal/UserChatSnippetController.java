package ssp.smartstudentportal;

import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


public class UserChatSnippetController {
    public VBox profilePlaceholder;
    public void setProfilePic(ImageView imageView) {
        if(imageView.getImage() != null) {
            profilePlaceholder.getChildren().clear();
            imageView=new ImageView(ImageUtils.cropImage(imageView, 500));
//            imageView.setPreserveRatio(true);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            ImageUtils.clipCircle(imageView, 25);
            profilePlaceholder.getChildren().add(imageView);
        }
    }
}
