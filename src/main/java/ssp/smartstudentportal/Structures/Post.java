package ssp.smartstudentportal.Structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {
    private String title;
    private String description;
    private String date;
    private String author;
    private String authorFirstName;
    private String authorLastName;
    private UserInfo authorData;
    private boolean followedStatus;
    private int forumId;
    private ArrayList<Comment> replies;
    private List<FileData> files;
    private int postId;
    private int NOUsers;
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description=description;
    }
    public Post(String title, String description, String date, String author) {
        files = new ArrayList<>();
        this.title = title;
        this.description = description;
        this.date = date;
        this.author = author;
        replies = new ArrayList<>();
    }
    public Post(){
        files= new ArrayList<>();
        replies= new ArrayList<>();
    };
    public Post(int postId,String title, String description, String date, String author) {
        files=new ArrayList<>();
        this.title = title;
        this.description = description;
        this.date = date;
        this.author = author;
        this.postId= postId;
        replies = new ArrayList<>();
    }
    public String getDate() {
        return date;
    }
    public String getAuthor() {
        return author;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public ArrayList<Comment> getComments() {
        return replies;
    }
    public void setFile(String extension,byte[] file) {
        files.add(new FileData(extension,file));
    }
    public void setFile(List<FileData> files) {
        this.files = files;
    }
    public void setPostId(int postId) {
        this.postId = postId;
    }
    public int getPostId() {
        return postId;
    }
    public void setForumId(int forumId) {
        this.forumId = forumId;
    }
    public int getForumId() {
        return forumId;
    }
    public List<FileData> getFiles() {
        return files;
    }
    public int getId() {
        return postId;
    }
    public int getNOUsers(){
        return NOUsers;
    }
    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }
    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }
    public String getAuthorFirstName() {
        return authorFirstName;
    }
    public String getAuthorLastName() {
        return authorLastName;
    }
    public void setAuthorData(UserInfo authorData) {
        this.authorData = authorData;
    }
    public UserInfo getAuthorData() {
        return authorData;
    }
    public void setFile(String ext, byte[] file, String file_name) {
        files.add(new FileData(ext,file,file_name));
    }
    public void setFollowedStatus(boolean followedStatus) {
        this.followedStatus = followedStatus;
    }
    public boolean getFollowedStatus() {
        return followedStatus;
    }
    public void setNOUsers(int NOUsers){
        this.NOUsers=NOUsers;
    }
}
