package ssp.smartstudentportal;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class ChatImageContainer {
    public HBox imageContainer;
    public BaseController parentController;
    public void setImage(ImageView image) {
        this.imageContainer.getChildren().add(image);
    }
    public void onClickCancel(){
        ((HBox)imageContainer.getParent().getParent()).getChildren().clear();
    }
}
