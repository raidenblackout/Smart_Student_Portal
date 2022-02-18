package ssp.smartstudentportal;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ssp.smartstudentportal.Structures.Post;
import ssp.smartstudentportal.Structures.UserInfo;

public class ForumPostController {
    public Label postTitle;
    public Text postUser;
    public Text postContent;
    public Text postUsers;
    public Text postComments;
    public int postId;
    public VBox userProfile;
    private UserInfo userInfo;
    public void initialize(){
        postContent.wrappingWidthProperty().bind(((VBox)postContent.getParent()).widthProperty().subtract(20));
    }
    public void setPost(Post post) {
        postTitle.setText(post.getTitle());
        postContent.setText(post.getDescription());
        postId = post.getId();
        postUser.setText(post.getAuthor());
        //postComments.setText(post.getComments().size() + " Comments");
        postUsers.setText(post.getNOUsers()+ " Users");
        System.out.println("HEY");
        System.out.println(post.getAuthor());
        userInfo = ClientController.getInstance().getUserInfoI(post.getAuthor());
        if(userInfo != null) {
            if(post.getFiles().size() > 0) {
                ImageView imageView = new ImageView(ImageUtils.getImage(post.getFiles().get(0).getFile()));
                setProfilePic(imageView);
            }
        }
    }
    public void setProfilePic(ImageView imageView) {
        if(imageView.getImage() != null) {
            userProfile.getChildren().clear();
            imageView=new ImageView(ImageUtils.cropImage(imageView, 500));
//            imageView.setPreserveRatio(true);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            ImageUtils.clipCircle(imageView, 25);
            userProfile.getChildren().add(imageView);
        }
    }


}
