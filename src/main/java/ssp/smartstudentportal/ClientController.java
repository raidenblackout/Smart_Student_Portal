package ssp.smartstudentportal;

import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.SpreadsheetViewSelectionModel;
import ssp.smartstudentportal.Structures.*;

import java.io.BufferedWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientController<T>{
    /********************* Variables *********************/

    public static Socket socket;
    public static BufferedWriter writer;
    public static ObjectInputStream inputStream;
    public static ObjectOutputStream outputStream;
    public static String userName;
    public static String otherUser = "parvez";
    public static String fname;
    public static String lname;
    public static String url = "localhost";
    public static int port = 8080;
    public static UserInfo userInfo;
    public static UserInfo otherUserInfo;
    /********************* Variables *********************/
    /************************* Constructor *************************/
    public ClientController(String url, int port){
        if (ClientController.socket == null) {
            try {
                System.out.println("Socket is here");
                ClientController.socket = new Socket(url, port);
                ClientController.outputStream = new ObjectOutputStream(ClientController.socket.getOutputStream());
                ClientController.outputStream.writeObject(true);
                ClientController.outputStream.flush();
                ClientController.inputStream = new ObjectInputStream(ClientController.socket.getInputStream());
                System.out.println("Socket is created");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /****************************** Constructor *************************/


    /****************************** Methods *****************************/
    public static ClientController getInstance() {
        return new ClientController(ClientController.url, ClientController.port);
    }

    public void getMessages(String username, String otherUser, String limit) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            params.add(username);
            params.add(otherUser);
            params.add(limit);
            System.out.println("Get Messages");
            outputStream.writeObject(new ServerRequest(Request.GET_MESSAGES, params));
            outputStream.flush();
            outputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void connect(String url, int port) {
        try {
            ClientController.socket.connect(new InetSocketAddress(url, port), 5000);
            outputStream = new ObjectOutputStream(ClientController.socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(ClientController.socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean login(String userName, String password) {
        System.out.println("Login");
        try {
            ArrayList<Object> login = new ArrayList<>();
            login.add(userName);
            login.add(password);
            outputStream.writeObject(new ServerRequest(Request.LOGIN, login));
            outputStream.flush();
            outputStream.reset();
            System.out.println("Message sent");
            UserInfo loginData = (UserInfo) ((ClientResponse) inputStream.readObject()).getData();
            System.out.println("Message received");
            if (loginData == null) {
                System.out.println("Login failed");
                return false;
            } else {
                ClientController.userInfo = loginData;
                ClientController.userName = userName;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public void getUserInfo(String userName) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            params.add(userName);
            outputStream.writeObject(new ServerRequest(Request.GET_USER_INFO, params));
            outputStream.flush();
            outputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserInfo(UserInfo userInfo) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            params.add(ClientController.userInfo);
            params.add(userInfo.getUserName());
            outputStream.writeObject(new ServerRequest(Request.UPDATE_USER_INFO, params));
            outputStream.flush();
            outputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserList(String userName) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            params.add(userName);
            outputStream.writeObject(new ServerRequest(Request.GET_USER_LIST, params));
            outputStream.flush();
            outputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String userName, String otherUser, Message message) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            params.add(userName);
            params.add(otherUser);
            params.add(message);
            System.out.println("Send Message "+userName+" "+otherUser+" "+message);
            outputStream.writeObject(new ServerRequest(Request.SEND_MESSAGE, params));
            outputStream.flush();
            outputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean register(String userName, String password, String email, String firstName, String lastName, String phone) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            UserInfo userInfo = new UserInfo(userName, email, firstName, lastName, phone);
            params.add(userInfo);
            params.add(password);
            outputStream.writeObject(new ServerRequest(Request.REGISTER, params));
            outputStream.flush();
            outputStream.reset();
            ClientResponse response = (ClientResponse) inputStream.readObject();
            return response.getResponseType() == Response.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void searchUser(String userName) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            params.add(userName);
            outputStream.writeObject(new ServerRequest(Request.SEARCH_USER, params));
            outputStream.flush();
            outputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPosts() {
        try{
            ArrayList<Object> params = new ArrayList<>();
            outputStream.writeObject(new ServerRequest(Request.GET_POSTS, params));
            outputStream.flush();
            outputStream.reset();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getPosts(String forumName) {
        try{
            ArrayList<Object> params = new ArrayList<>();
            outputStream.writeObject(new ServerRequest(Request.GET_POSTS_FORUM, params));
            outputStream.flush();
            outputStream.reset();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getPost(String postId) {
        try{
            ArrayList<Object> params = new ArrayList<>();
            params.add(postId);
            outputStream.writeObject(new ServerRequest(Request.GET_POST, params));
            outputStream.flush();
            outputStream.reset();
        }catch (Exception e){

        }
    }
    public void createPost(Post post) {
        try{
            ArrayList<Object> params = new ArrayList<>();
            params.add(post);
            outputStream.writeObject(new ServerRequest(Request.CREATE_POST, params));
            outputStream.flush();
            outputStream.reset();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getReplies(String postId) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            params.add(postId);
            outputStream.writeObject(new ServerRequest(Request.GET_REPLIES, params));
            outputStream.flush();
            outputStream.reset();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public DailyTask getTaskById(int taskId) {
        try{
            Socket tempSocket = new Socket(ClientController.url, ClientController.port);
            ObjectOutputStream tempOutputStream = new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            ObjectInputStream tempInputStream = new ObjectInputStream(tempSocket.getInputStream());
            ArrayList<Object> params = new ArrayList<>();
            params.add(taskId);
            tempOutputStream.writeObject(new ServerRequest(Request.GET_TASK_BY_ID, params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            ClientResponse response = (ClientResponse) tempInputStream.readObject();
            //tempInputStream.close();
            //tempOutputStream.close();
            tempSocket.close();
            return (DailyTask) response.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int addTask(String taskTitle, String taskDescription, String taskDate) {
        try{
            Socket tempSocket = new Socket(ClientController.url, ClientController.port);
            ObjectOutputStream tempOutputStream = new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            ObjectInputStream tempInputStream = new ObjectInputStream(tempSocket.getInputStream());
            ArrayList<Object> params = new ArrayList<>();
            DailyTask task = new DailyTask(taskTitle, taskDescription, taskDate);
            params.add(task);
            tempOutputStream.writeObject(new ServerRequest(Request.ADD_TASK, params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            ClientResponse response = (ClientResponse) tempInputStream.readObject();
            tempSocket.close();
            return (Integer) response.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    public UserInfo getUserInfoI(String userName){
        try{
            Socket tempSocket=new Socket(ClientController.url,ClientController.port);
            ObjectOutputStream tempOutputStream=new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            ObjectInputStream tempInputStream=new ObjectInputStream(tempSocket.getInputStream());
            ArrayList<Object> params=new ArrayList<>();
            params.add(userName);
            tempOutputStream.writeObject(new ServerRequest(Request.GET_USER_INFO,params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            ClientResponse response=(ClientResponse)tempInputStream.readObject();
            tempSocket.close();
            return (UserInfo) response.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Pair<Boolean,ArrayList<Post>> getPosts(int forumId){
        try{
            Socket tempSocket=new Socket(ClientController.url,ClientController.port);
            ObjectOutputStream tempOutputStream=new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            ObjectInputStream tempInputStream=new ObjectInputStream(tempSocket.getInputStream());
            ArrayList<Object> params=new ArrayList<>();
            params.add(forumId);
            params.add(ClientController.userName);
            tempOutputStream.writeObject(new ServerRequest(Request.GET_POSTS_FORUM,params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            ClientResponse response=(ClientResponse)tempInputStream.readObject();
            tempSocket.close();
            return (Pair<Boolean,ArrayList<Post>>) response.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void createForumPost(Post post) {
        try{
            ArrayList<Object> params = new ArrayList<>();
            params.add(post);
            outputStream.writeObject(new ServerRequest(Request.CREATE_FORUM_POST, params));
            outputStream.flush();
            outputStream.reset();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendComment(Comment comment){
        try{
            ArrayList<Object> params=new ArrayList<>();
            params.add(comment);
            outputStream.writeObject(new ServerRequest(Request.SEND_COMMENT,params));
            outputStream.flush();
            outputStream.reset();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<Comment> getComments(int postId){
        try{
            Socket tempSocket=new Socket(ClientController.url,ClientController.port);
            ObjectOutputStream tempOutputStream=new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            ObjectInputStream tempInputStream=new ObjectInputStream(tempSocket.getInputStream());
            ArrayList<Object> params=new ArrayList<>();
            params.add(postId);
            tempOutputStream.writeObject(new ServerRequest(Request.GET_COMMENTS,params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            ClientResponse response=(ClientResponse)tempInputStream.readObject();
            tempSocket.close();
            return (ArrayList<Comment>) response.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Post> getForums(int i) {
        try{
            Socket tempSocket=new Socket(ClientController.url,ClientController.port);
            ObjectOutputStream tempOutputStream=new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            ObjectInputStream tempInputStream=new ObjectInputStream(tempSocket.getInputStream());
            ArrayList<Object> params=new ArrayList<>();
            params.add(i);
            params.add(ClientController.userName);
            tempOutputStream.writeObject(new ServerRequest(Request.GET_FORUMS,params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            ClientResponse response=(ClientResponse)tempInputStream.readObject();
            tempSocket.close();
            return (ArrayList<Post>) response.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void followSubForum(int subForumId) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(subForumId);
        params.add(ClientController.userName);
        individualRequestWithoutResponse(Request.FOLLOW_SUB_FORUM,params);
    }
    public ClientResponse individualRequest(Request request, ArrayList<Object> params){
        try{
            Socket tempSocket=new Socket(ClientController.url,ClientController.port);
            ObjectOutputStream tempOutputStream=new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            ObjectInputStream tempInputStream=new ObjectInputStream(tempSocket.getInputStream());
            tempOutputStream.writeObject(new ServerRequest(request,params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            ClientResponse response=(ClientResponse)tempInputStream.readObject();
            tempSocket.close();
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void individualRequestWithoutResponse(Request request, ArrayList<Object> params){
        try{
            Socket tempSocket=new Socket(ClientController.url,ClientController.port);
            ObjectOutputStream tempOutputStream=new ObjectOutputStream(tempSocket.getOutputStream());
            tempOutputStream.writeObject(false);
            tempOutputStream.flush();
            tempOutputStream.writeObject(new ServerRequest(request,params));
            tempOutputStream.flush();
            tempOutputStream.reset();
            //tempSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addReminder(String content, String date) {
        ArrayList<Object> params = new ArrayList<>();
        Reminder reminder = new Reminder(content, date, ClientController.userName);
        params.add(reminder);
        individualRequestWithoutResponse(Request.ADD_REMINDER,params);
    }

    public void addTodoList(String content) {
        ArrayList<Object> params = new ArrayList<>();
        Reminder todoList = new Reminder(content, "",ClientController.userName);
        params.add(todoList);
        individualRequestWithoutResponse(Request.ADD_TODO_LIST,params);
    }

    public ArrayList<Reminder> getTodoList() {
        ArrayList<Object> params = new ArrayList<>();
        params.add(ClientController.userName);
        ClientResponse response = individualRequest(Request.GET_TODO_LIST, params);
        return (ArrayList<Reminder>) response.getData();
    }

    public ArrayList<Reminder> getReminders() {
        ArrayList<Object> params = new ArrayList<>();
        params.add(ClientController.userName);
        ClientResponse response = individualRequest(Request.GET_REMINDERS, params);
        return (ArrayList<Reminder>) response.getData();
    }

    public void removeToDo(int id) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(id);
        params.add(ClientController.userName);
        individualRequestWithoutResponse(Request.REMOVE_TODO, params);
    }
    public void removeReminder(int id) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(id);
        params.add(ClientController.userName);
        individualRequestWithoutResponse(Request.REMOVE_REMINDER, params);
    }

    public void updateToDoList(int id, boolean selected) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(id);
        params.add(selected);
        params.add(ClientController.userName);
        individualRequestWithoutResponse(Request.UPDATE_TODO_LIST, params);
    }

    public void updateReminder(int id, boolean selected) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(id);
        params.add(selected);
        params.add(ClientController.userName);
        individualRequestWithoutResponse(Request.UPDATE_REMINDER, params);
    }

    public List<Course> getCourses() {
        ArrayList<Object> params = new ArrayList<>();
        params.add(ClientController.userName);
        ClientResponse response = individualRequest(Request.GET_COURSES, params);
        return (List<Course>) response.getData();
    }

    public void setCourseStatus(String courseCode, boolean b) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(courseCode);
        params.add(b);
        params.add(ClientController.userName);
        individualRequestWithoutResponse(Request.SET_COURSE_STATUS, params);
    }

    public ArrayList<Course> getCompletedCourses() {
        ArrayList<Object> params = new ArrayList<>();
        params.add(ClientController.userName);
        ClientResponse response = individualRequest(Request.GET_COMPLETED_COURSES, params);
        return (ArrayList<Course>) response.getData();
    }

    public List<FileData> getPostFiles(int id) {
        ArrayList<Object> params = new ArrayList<>();
        params.add(id);
        ClientResponse response = individualRequest(Request.GET_POST_FILES, params);
        return (List<FileData>) response.getData();
    }


    /**********************************************************************************************************************/
}
