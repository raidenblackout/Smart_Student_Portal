package ssp.smartstudentportal;

import ssp.smartstudentportal.Structures.Post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubForumCreatePost extends CreatePostWindow{

    /*************************Constructor
     * @param viewFactory
     * @param fxmlName****************************
    */
    private int forumId;
    public void setForumId(int forumId) {
        this.forumId = forumId;
    }
    public SubForumCreatePost(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }
    public void initialize(){
        files.getChildren().clear();
    }
    @Override
    public void publishBtnClicked() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        if(postTitle.getText().isEmpty()||postContent.getText().isEmpty()){
            return;
        }
        Post post = new Post(postTitle.getText(), postContent.getText(),dtf.format(now), ClientController.userName);
        post.setForumId(forumId);
        post.setFile(fileLists);
        ClientController.getInstance().createForumPost(post);
        viewFactory.closeWindow(this.getClass());
        viewFactory.showSubForumWindow(forumId);
    }
    public void cancelBtnClicked() {
        viewFactory.closeWindow(this.getClass());
        viewFactory.showSubForumWindow(forumId);
    }
}
