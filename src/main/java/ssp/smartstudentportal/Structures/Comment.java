package ssp.smartstudentportal.Structures;

import java.io.Serializable;

public class Comment implements Serializable {
    String commentTime;
    UserInfo commentUser;
    String commentText;
    int commentId;
    int postId;
    public Comment(UserInfo commentUser, String commentText, int postId) {
        this.commentUser = commentUser;
        this.commentText = commentText;
        this.postId = postId;
    }
    public Comment(){}
    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public UserInfo getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(UserInfo commentUser) {
        this.commentUser = commentUser;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
