package ssp.smartstudentportal.Server;

import javafx.util.Pair;
import ssp.smartstudentportal.Server.Database.DBHandler;
import ssp.smartstudentportal.Structures.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private DBHandler dbHandler;
    private ArrayList<Message> prevMessages;
    private UserInfo currentUser;
    private UserInfo otherUser;
    private ArrayList<String> prevUsers;
    private boolean connectionIsAlive = true;

    public ClientHandler(Socket socket) {
        try {
            this.dbHandler = new DBHandler();
            this.dbHandler.getConnection();
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectOutputStream.flush();
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            boolean helper=(boolean) objectInputStream.readObject();
            if(helper) {
                connectionIsAlive = true;
            }else{
                connectionIsAlive = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (connectionIsAlive) {
                        System.out.println("Waiting for message");
                        if (currentUser != null) {
                            System.out.println("ClientHandler: " + currentUser.getUserName() + " is waiting for a message");
                            ArrayList<Message> currentMessages = dbHandler.getMessagesFromUser(currentUser.getUserName(), otherUser.getUserName(), "10");
                            if (prevMessages != null && prevMessages.equals(currentMessages)) {
                                Thread.sleep(1000);
                            } else {
                                prevMessages = currentMessages;
                                System.out.println("ClientHandler: " + currentUser.getUserName() + " is sending messages");
                                synchronized (objectOutputStream) {
                                    objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_MESSAGES, currentMessages));
                                    objectOutputStream.flush();
                                }
                            }
                            ArrayList<String> users = dbHandler.getUserList(currentUser.getUserName());
                            if (prevUsers != null && prevUsers.equals(users)) {
                                Thread.sleep(1000);
                            } else {
                                prevUsers = users;
                                synchronized (objectOutputStream) {
                                    ArrayList<UserInfo> userInfos = new ArrayList<>();
                                    for (String user : users) {
                                        userInfos.add(dbHandler.getUserInfo(user));
                                    }
                                    objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_USER_LIST, userInfos));
                                    objectOutputStream.flush();
                                }
                            }
                        } else {
                            Thread.sleep(1000);
                        }
                    }
                } catch (InterruptedException e) {
                    connectionIsAlive = false;
                    dbHandler.logout(currentUser.getUserName());
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionIsAlive = false;
                    dbHandler.logout(currentUser.getUserName());
                } catch (Exception e) {
                    e.printStackTrace();
                    connectionIsAlive = false;
                    dbHandler.logout(currentUser.getUserName());
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        try {
            ServerRequest request;
            while ((request = (ServerRequest) objectInputStream.readObject()) != null) {
                if (request.getRequestType() == Request.LOGIN) {
                    System.out.println("Login request received");
                    String username = (String) request.getParams().get(0);
                    String password = (String) request.getParams().get(1);
                    UserInfo userInfo = dbHandler.login(username, password);
                    UserInfo otherUser = dbHandler.getUserInfo(username);
                    this.currentUser = userInfo;
                    this.otherUser = otherUser;
                    if (userInfo != null) {
                        synchronized (objectOutputStream) {
                            objectOutputStream.writeObject(new ClientResponse(Response.SUCCESS, otherUser));
                            objectOutputStream.flush();
                        }
                    } else {
                        synchronized (objectOutputStream) {
                            objectOutputStream.writeObject(new ClientResponse(Response.FAILURE, null));
                            objectOutputStream.flush();
                        }
                    }
                } else if (request.getRequestType() == Request.GET_MESSAGES) {
                    System.out.println("Get messages request received");
                    String username = (String) request.getParams().get(0);
                    String otherUser = (String) request.getParams().get(1);
                    String limit = (String) request.getParams().get(2);
                    this.otherUser = dbHandler.getUserInfo(otherUser);
                    ArrayList<Message> response = dbHandler.getMessagesFromUser(username, otherUser, limit);
                    synchronized (objectOutputStream) {
                        System.out.println("Sending messages");
                        objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_MESSAGES, response));
                        objectOutputStream.flush();
                        UserInfo anotherUser = dbHandler.getUserInfo(otherUser);
                        objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_OTHER_USER_INFO, anotherUser));
                        objectOutputStream.flush();
                    }
                } else if (request.getRequestType() == Request.SEND_MESSAGE) {
                    System.out.println("Send message request received");
                    String username = (String) request.getParams().get(0);
                    String otherUser = (String) request.getParams().get(1);
                    System.out.println("Sending message to " + otherUser + " from " + username);
                    Message message = (Message) request.getParams().get(2);
                    dbHandler.sendMessage(username, otherUser, message);
                } else if (request.getRequestType() == Request.SEARCH_USER) {
                    System.out.println("Search user request received");
                    String username = (String) request.getParams().get(0);
                    ArrayList<String> users = dbHandler.searchUsers(username);
                    synchronized (objectOutputStream) {
                        objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_SEARCH_USERNAMES, users));
                        objectOutputStream.flush();
                    }
                } else if (request.getRequestType() == Request.GET_USER_LIST) {
                    System.out.println("Get user list request received");
                    ArrayList<String> users = dbHandler.getUserList(currentUser.getUserName());
                    synchronized (objectOutputStream) {
                        System.out.println("Sending user list");
                        ArrayList<UserInfo> userInfos = new ArrayList<>();
                        for (String user : users) {
                            userInfos.add(dbHandler.getUserInfo(user));
                        }
                        objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_USER_LIST, userInfos));
                        objectOutputStream.flush();
                    }
                } else if (request.getRequestType() == Request.UPDATE_USER_INFO) {
                    System.out.println("Update user info request received");
                    UserInfo userInfo2 = (UserInfo) request.getParams().get(0);
                    System.out.println(userInfo2.getLastName());
                    String username = (String) request.getParams().get(1);
                    dbHandler.updateUserInfo(userInfo2, username);
                    currentUser = userInfo2;
                    synchronized (objectOutputStream) {
                        objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_USER_INFO, userInfo2));
                        objectOutputStream.flush();
                    }
                } else if (request.getRequestType() == Request.REGISTER) {
                    System.out.println("Register request received");
                    UserInfo userInfo = (UserInfo) request.getParams().get(0);
                    String password = (String) request.getParams().get(1);
                    String status = dbHandler.register(userInfo, password);
                    synchronized (objectOutputStream) {
                        if(status.equals("success")) {
                            objectOutputStream.writeObject(new ClientResponse(Response.SUCCESS, status));
                            objectOutputStream.flush();
                        }else{
                            objectOutputStream.writeObject(new ClientResponse(Response.FAILURE, status));
                            objectOutputStream.flush();
                        }
                    }
                }else if(request.getRequestType()==Request.GET_POSTS){
                    System.out.println("Get posts request received");
                    getPosts();

                }else if(request.getRequestType()==Request.GET_POST){
                    System.out.println("Get post request received");
                    int postId = (int) request.getParams().get(0);
                    getPost(postId);
                }else if(request.getRequestType()==Request.GET_TASK_BY_ID){
                    System.out.println("Get task by id request received");
                    int taskId = (int) request.getParams().get(0);
                    getTaskById(taskId);
                }else if(request.getRequestType()==Request.ADD_TASK){
                    System.out.println("Add task request received");
                    DailyTask task = (DailyTask) request.getParams().get(0);
                    addTask(task);
                }else if(request.getRequestType()==Request.CREATE_POST){
                    System.out.println("Create post request received");
                    Post post = (Post) request.getParams().get(0);
                    createPost(post);
                }else if(request.getRequestType()==Request.GET_USER_INFO){
                    System.out.println("Get user info request received");
                    String username = (String) request.getParams().get(0);
                    UserInfo userInfo = dbHandler.getUserInfo(username);
                    synchronized (objectOutputStream) {
                        objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_USER_INFO, userInfo));
                        objectOutputStream.flush();
                    }
                }else if(request.getRequestType()==Request.GET_POSTS_FORUM){
                    System.out.println("Get posts forum request received");
                    int forumId = (int) request.getParams().get(0);
                    String username = (String) request.getParams().get(1);
                    getPostsForum(forumId, username);
                }else if(request.getRequestType()==Request.CREATE_FORUM_POST){
                    System.out.println("Create forum post request received");
                    Post post = (Post) request.getParams().get(0);
                    dbHandler.createForumPost(post);
                }else if(request.getRequestType()==Request.SEND_COMMENT){
                    System.out.println("Send comment request received");
                    Comment comment = (Comment) request.getParams().get(0);
                    sendComment(comment);
                }else if(request.getRequestType()==Request.GET_COMMENTS){
                    System.out.println("Get comments request received");
                    int postId = (int) request.getParams().get(0);
                    getComments(postId);
                }else if(request.getRequestType()==Request.GET_FORUMS){
                    System.out.println("Get forums request received");
                    int number= (int) request.getParams().get(0);
                    String username = (String) request.getParams().get(1);
                    getForums(number, username);
                }else if(request.getRequestType()==Request.FOLLOW_SUB_FORUM){
                    System.out.println("Follow sub forum request received");
                    int forumId = (int) request.getParams().get(0);
                    String username = (String) request.getParams().get(1);
                    followSubForum(username,forumId);
                }else if(request.getRequestType()==Request.ADD_REMINDER){
                    System.out.println("Add reminder request received");
                    Reminder reminder = (Reminder) request.getParams().get(0);
                    addReminder(reminder);
                }else if(request.getRequestType()==Request.ADD_TODO_LIST){
                    System.out.println("Add todo list request received");
                    Reminder reminder = (Reminder) request.getParams().get(0);
                    addTodoList(reminder);
                }else if(request.getRequestType()==Request.GET_TODO_LIST){
                    System.out.println("Get todo list request received");
                    String username = (String) request.getParams().get(0);
                    getTodoList(username);
                }else if(request.getRequestType()==Request.GET_REMINDERS){
                    System.out.println("Get reminders request received");
                    String username = (String) request.getParams().get(0);
                    getReminders(username);
                }else if(request.getRequestType()==Request.REMOVE_TODO){
                    System.out.println("Remove todo request received");
                    int todoId = (int) request.getParams().get(0);
                    String username = (String) request.getParams().get(1);
                    removeTodo(todoId,username);
                }else if (request.getRequestType()==Request.REMOVE_REMINDER){
                    System.out.println("Remove reminder request received");
                    int reminderId = (int) request.getParams().get(0);
                    String username = (String) request.getParams().get(1);
                    removeReminder(reminderId,username);
                }else if(request.getRequestType()==Request.UPDATE_TODO_LIST){
                    System.out.println("Update todo list request received");
                    int todoId = (int) request.getParams().get(0);
                    boolean isDone = (boolean) request.getParams().get(1);
                    String username = (String) request.getParams().get(2);
                    updateTodoList(todoId,isDone,username);
                }else if(request.getRequestType()==Request.UPDATE_REMINDER){
                    System.out.println("Update reminder request received");
                    int reminderId = (int) request.getParams().get(0);
                    boolean isDone = (boolean) request.getParams().get(1);
                    String username = (String) request.getParams().get(2);
                    updateReminder(reminderId,isDone,username);
                }else if(request.getRequestType()==Request.GET_COURSES){
                    System.out.println("Get courses request received");
                    String username = (String) request.getParams().get(0);
                    getCourses(username);
                }else if(request.getRequestType()==Request.SET_COURSE_STATUS){
                    System.out.println("Set course status request received");
                    String courseCode = (String) request.getParams().get(0);
                    boolean status = (boolean) request.getParams().get(1);
                    String username = (String) request.getParams().get(2);
                    setCourseStatus(courseCode,status,username);
                }else if(request.getRequestType()==Request.GET_COMPLETED_COURSES){
                    System.out.println("Get completed courses request received");
                    String username = (String) request.getParams().get(0);
                    getCompletedCourses(username);
                }else if(request.getRequestType()==Request.GET_POST_FILES){
                    System.out.println("Get post files request received");
                    int postId = (int) request.getParams().get(0);
                    getPostFiles(postId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                this.dbHandler.dbConnection.close();
                this.objectInputStream.close();
                this.objectOutputStream.close();
                Server.clients.remove(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    private void setCourseStatus(String courseCode, boolean status, String username) {
        dbHandler.setCourseStatus(courseCode,status,username);
    }


    private void updateReminder(int reminderId, boolean isDone, String username) {
        dbHandler.updateReminder(reminderId,isDone,username);
    }

    private void updateTodoList(int todoId, boolean isDone, String username) {
        dbHandler.updateTodoList(todoId,isDone,username);
    }

    private void removeReminder(int reminderId, String username) {
        dbHandler.removeReminder(reminderId,username);
    }

    private void removeTodo(int todoId, String username) {
        dbHandler.removeTodo(todoId,username);
    }

    private void getReminders(String username) throws IOException {
        List<Reminder> reminders = dbHandler.getReminders(username);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.GET_REMINDERS, reminders));
            objectOutputStream.flush();
        }
    }

    private void getTodoList(String username) throws IOException {
        List<Reminder> reminders = dbHandler.getTodoList(username);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_TODO_LIST, reminders));
            objectOutputStream.flush();
        }
    }

    private void addTodoList(Reminder reminder) {
        dbHandler.addTodoList(reminder);
    }

    private void addReminder(Reminder reminder) {
        dbHandler.addReminder(reminder);
    }


    private void followSubForum(String username,int forumId) {
        dbHandler.followSubForum(username,forumId);
    }

    private void getForums(int limit,String userName) throws IOException {
        List<Post> forums = dbHandler.getForums(limit,userName);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_FORUMS, forums));
            objectOutputStream.flush();
        }
    }

    private void getComments(int postId) throws IOException {
        List<Comment> comments = dbHandler.getComments(postId);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_COMMENTS, comments));
            objectOutputStream.flush();
        }
    }

    private void sendComment(Comment comment) {
        dbHandler.sendComment(comment);
    }


    private void getPostsForum(int forumId,String username) throws IOException {
        Pair<Boolean,ArrayList<Post>> posts = dbHandler.getPostsForum(forumId,username);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.GET_POSTS_FORUM, posts));
            objectOutputStream.flush();
        }
    }

    private void createPost(Post post) throws IOException {
        dbHandler.createPost(post);
        getPosts();
    }

    private void getTaskById(int taskId) throws IOException {
        DailyTask task = dbHandler.getTaskById(taskId);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_TASK, task));
            objectOutputStream.flush();
        }
    }


    private void getPost(int postId) throws IOException {
        Post post = dbHandler.getPost(postId);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_POST, post));
            objectOutputStream.flush();
        }
    }
    private void getPosts() throws IOException{
        ArrayList<Post> posts = dbHandler.getPosts();
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_POSTS, posts));
            objectOutputStream.flush();
        }
    }
    private int addTask(DailyTask task) throws IOException {
        int taskId = dbHandler.addTask(task);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.TASK_ID, taskId));
            objectOutputStream.flush();
            System.out.println("Task id sent");
        }
        return taskId;
    }
    private void getCourses(String username) throws IOException {
        List<Course> courses = dbHandler.getCourses(username);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_COURSES, courses));
            objectOutputStream.flush();
        }
    }
    private void getCompletedCourses(String username) throws IOException {
        ArrayList<Course> courses = this.dbHandler.getCompletedCourses(username);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_COMPLETED_COURSES, courses));
            objectOutputStream.flush();
        }
    }
    private void getPostFiles(int postId) throws IOException {
        List<FileData> files = dbHandler.getForumPostFiles(postId);
        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(new ClientResponse(Response.UPDATE_POST_FILES, files));
            objectOutputStream.flush();
        }
    }
}
