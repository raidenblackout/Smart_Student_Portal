package ssp.smartstudentportal;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;
import ssp.smartstudentportal.Structures.Post;
import ssp.smartstudentportal.Structures.SORT;

import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ForumWindowController extends BaseController {
    /************** Attributes **********************/
    public AnchorPane sidePanel;
    public FontIcon toggle;
    public VBox postContainer;
    public ComboBox postSorting;
    private ArrayList<Post> currentPosts;
    private ArrayList<Post> allposts;
    public TextField searchPost;
    public FontIcon checkmark;
    public HBox followed,allDiscussion;
    private boolean followedPosts = false;
    /************** Constructors **********************/
    ForumWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }


    /************** Initialization **********************/
    public void initialize() {
        getPosts();
        postSorting.getItems().add("Newest");
        postSorting.getItems().add("Oldest");
        postSorting.getItems().add("Most Replies");
        postSorting.setOnAction(event -> {
            if(postSorting.getValue().equals("Newest")){
                sortPosts(SORT.NEWEST);
            }else if(postSorting.getValue().equals("Oldest")){
                sortPosts(SORT.OLDEST);
            }
        });
        searchPost.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty()){
                setPosts(currentPosts);
            }
            else{
                ArrayList<Post> filteredPosts = new ArrayList<>();
                for(Post post : currentPosts){
                    if(post.getTitle().toLowerCase().contains(newValue.toLowerCase())){
                        filteredPosts.add(post);
                    }
                }
                setPosts(filteredPosts);
            }
        });
    }

    /************** Methods **********************/
    public void exit() {
        viewFactory.closeWindow(this.getClass());
    }
    public void getPosts(){
        ClientController.getInstance().getPosts();
    }
    public void getReplies(String postId){
        ClientController.getInstance().getReplies(postId);
    }
    public void loadPosts(ArrayList<Post> posts){
        allposts = posts;
        currentPosts = posts;
        if(followedPosts){
            showFollowedForums();
        }else {
            setPosts(posts);
        }
    }
    public void setPosts(ArrayList<Post> posts){
        postContainer.getChildren().clear();
            if (posts.size() == 0) {
                postContainer.getChildren().add(viewFactory.createEmptyView("No posts found"));
            }
            for (Post post : posts) {
                postContainer.getChildren().add(createPostView(post));
            }
    }
    public void sortPosts(SORT type){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        if(type==SORT.NEWEST){
            //sort arraylist by date
            currentPosts.sort((o1, o2) -> {
                Date date1 = null;
                try {
                    date1 = formatter.parse(o1.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date2 = null;
                try {
                    date2 = formatter.parse(o2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date2.compareTo(date1);
            }
            );
        }else if(type==SORT.OLDEST){
            //sort arraylist by date
            currentPosts.sort((o1, o2) -> {
                System.out.println(o1.getDate() + " " + o2.getDate());
                Date date1 = null;
                try {
                    date1 = formatter.parse(o1.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date2 = null;
                try {
                    date2 = formatter.parse(o2.getDate());
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                return date1.compareTo(date2);
            });
        }else if (type==SORT.MOST_REPLIES){
            //sort arraylist by replies
            currentPosts.sort((o1, o2) -> {
                return o2.getComments().size() - o1.getComments().size();
            });
        }
        setPosts(currentPosts);
    }
    public HBox createPostView(Post post){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ForumPost.fxml"));
            HBox postView = loader.load();
            ((ForumPostController)loader.getController()).setPost(post);
            postView.setOnMouseClicked(event -> {
                if(event.getClickCount()==2) {
                    //showPostWithId(post.getId());
                    showSubForumWindow(post.getPostId(), post.getTitle());
                }
            });
            return postView;

        }catch (IOException e){
            e.printStackTrace();
        }
        return new HBox();
    }

    public void showSubForumWindow(int forumId,String name){
        viewFactory.showSubForumWindow(forumId,name);
    }
    public void showSubForumWindow(){
        viewFactory.showSubForumWindow(1,"");
    }
    public void showCreatePostWindow(){
        viewFactory.showCreatePostWindow();
    }
    public void refreshWindow(){
        getPosts();
    }
    public void showFollowedForums(){
        followedPosts = true;
        ArrayList<Post> filteredPosts = new ArrayList<>();
        for(Post post:currentPosts){
            if(post.getFollowedStatus()){
                filteredPosts.add(post);
            }
        }
        currentPosts=filteredPosts;
        setPosts(filteredPosts);
        HBox hBox=(HBox) checkmark.getParent();
        hBox.getChildren().remove(checkmark);
        followed.getChildren().add(checkmark);
    }
    public void showAllPosts(){
        followedPosts = false;
        currentPosts=allposts;
        setPosts(currentPosts);
        HBox hBox=(HBox) checkmark.getParent();
        hBox.getChildren().remove(checkmark);
        allDiscussion.getChildren().add(checkmark);
    }
}
