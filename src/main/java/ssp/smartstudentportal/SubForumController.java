package ssp.smartstudentportal;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;
import ssp.smartstudentportal.Structures.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static ssp.smartstudentportal.TimeUtils.getTimeDifference;


public class SubForumController extends BaseController{

    private int subForumId;
    public TextField searchPost;
    public VBox postContainer;
    private ArrayList<Post> posts;
    public FontIcon followButton;
    public String subForumName;
    /*************************Constructor
     * @param viewFactory
     * @param fxmlName*****************************/
    public SubForumController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }
    public void initialize(){
        getPosts();
        searchPost.textProperty().addListener((observable, oldValue, newValue) -> {
            ArrayList<Post> filteredPosts = new ArrayList<>();
            for(Post post : posts){
                if(post.getTitle().toLowerCase().contains(newValue.toLowerCase())){
                    filteredPosts.add(post);
                }
            }
            setPosts(filteredPosts);
        });
        followButton.onMouseClickedProperty().set(event -> {
            ClientController.getInstance().followSubForum(subForumId);
            if(followButton.getIconLiteral().equals("fas-star")){
                followButton.setIconLiteral("far-star");
                followButton.setIconColor(new Color(0,0,0,1));
            }else{
                followButton.setIconLiteral("fas-star");
                followButton.setIconColor(new Color(0.9,0.9,0.9,1));
            }
        });
    }

    public void showCreatePostWindow(){
        viewFactory.closeWindow(this.getClass());
        viewFactory.showCreatePostWindow(subForumId);
    }

    public void setSubForumId(int id) {
        this.subForumId = id;
    }
    public void goBack(){
        viewFactory.closeWindow(this.getClass());
        viewFactory.showForumWindow();
    }
    public void getPosts(){
        Pair<Boolean,ArrayList<Post>> forumDetails = ClientController.getInstance().getPosts(subForumId);
        posts=forumDetails.getValue();
        if(forumDetails.getKey()){
            followButton.setIconLiteral("fas-star");
            followButton.setIconColor(new Color(0.9,0.9,0.9,1));
        }
        setPosts(posts);
    }
    public void setPosts(ArrayList<Post> posts) {
        postContainer.getChildren().clear();
        for(Post post : posts){
            postContainer.getChildren().add(createPostSnippet(post));
        }
    }
    public VBox createPostSnippet(Post post){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubForumPostSnippet.fxml"));
            VBox postSnippet = loader.load();
            SubForumPostSnippetController controller = loader.getController();
            controller.setPostTitle(post.getTitle()).setPostTitle(post.getTitle()).setPostContent(post.getDescription()).setPostUser(post.getAuthorData());
            controller.setPostTime(getTimeDifference(post.getDate()));
            controller.setFiles(post.getFiles());
            controller.setPostId(post.getId());
            controller.setPostInfo(post);
            return postSnippet;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
