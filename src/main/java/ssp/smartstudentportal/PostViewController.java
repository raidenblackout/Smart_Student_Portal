package ssp.smartstudentportal;

import javafx.scene.control.Label;
import javafx.scene.text.Text;
import ssp.smartstudentportal.Structures.Post;

public class PostViewController {
    public Text postDescription;
    public Label postTitle;
    public int postId;
    public void setPost(Post post) {
        postTitle.setText(post.getTitle());
        postDescription.setText(post.getDescription());
        postId = post.getId();
    }
}
