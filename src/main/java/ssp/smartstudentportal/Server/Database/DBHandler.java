package ssp.smartstudentportal.Server.Database;

import javafx.util.Pair;
import ssp.smartstudentportal.Structures.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends Configs {
    public static String filePath = "D:\\Projects\\AOOPGIT\\src\\main\\java\\ssp\\smartstudentportal\\Server\\Database\\images\\";
    public static String PostfilePath = "D:\\Projects\\AOOPGIT\\src\\main\\java\\ssp\\smartstudentportal\\Server\\Database\\files\\";
    public Connection dbConnection;
    public String userName;
    public Connection getConnection() {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?autoReconnect=true&useSSL=false";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dbConnection;
    }

    public UserInfo login(String username, String password) {
        UserInfo userInfo = new UserInfo();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.userName = username;
                String uname = rs.getString("username");
                String fname = rs.getString("firstName");
                String lname = rs.getString("lastName");
                String email = rs.getString("email");
                String phone = rs.getString("phoneNumber");
                userInfo = new UserInfo(uname, email, fname, lname, phone);
                setAuthStatus(username, true);
                System.out.println("Logged in");
            } else {
                userInfo = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public String register(String username, String password, String email, String firstName, String lastName, String phoneNumber) {
        String result = "";
        String query = "INSERT INTO users (username, password, email, firstName, lastName, phoneNumber) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            stmt.setString(6, phoneNumber);
            stmt.executeUpdate();
            result = "success";
        } catch (SQLException e) {
            e.printStackTrace();
            result = "fail";
        }
        return result;
    }

    private Boolean setAuthStatus(String username, Boolean authStatus) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?autoReconnect=true&useSSL=false", dbUser, dbPassword);
            System.out.println("Setting auth status");
            String query = "UPDATE users SET authStatus = ? WHERE username = ?";
            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                System.out.println(authStatus + " " + username);
                stmt.setInt(1, authStatus ? 1 : 0);
                stmt.setString(2, username);
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean getAuthStatus(String username) {
        String query = "SELECT authStatus FROM users WHERE username = '" + username + "'";
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getBoolean("authStatus");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logout(String username) {
        return setAuthStatus(username, false);
    }

    public ArrayList<String> getUserList(String username) {
        ArrayList<String> messages = new ArrayList<>();
        String query = "SELECT sender, receiver FROM messages GROUP BY sender,receiver HAVING sender=? OR receiver=? ORDER BY MAX(time) DESC;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String receiver = rs.getString("receiver");
                if (sender.equals(username)) {
                    if (!messages.contains(receiver)) {
                        messages.add(receiver);
                    }
                } else {
                    if (!messages.contains(sender)) {
                        messages.add(sender);
                    }
                }
            }
            System.out.println(messages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public String getLastMessageFrom(String username) {
        String query = "SELECT receiver FROM messages WHERE sender='" + username + "' ORDER BY time DESC LIMIT 1;";
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("receiver");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public ArrayList<Message> getMessagesFromUser(String username, String sender, String limit) {
        ArrayList<Message> result = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE sender = ? AND receiver = ? OR sender = ? AND receiver = ? ORDER BY time DESC LIMIT ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, sender);
            stmt.setString(3, sender);
            stmt.setString(4, username);
            stmt.setInt(5, Integer.parseInt(limit));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String senderName = rs.getString("sender");
                String receiverName = rs.getString("receiver");
                String message = rs.getString("message");
                String time = rs.getString("time");
                String query2 = "SELECT * FROM images WHERE p_id='" + rs.getString("id") + "'";
                Statement stmt2 = dbConnection.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);
                byte[] image = null;
                if (rs2.next()) {
                    String imagePath = filePath + "image" + rs2.getString("id") + ".png";
                    System.out.println(imagePath);
                    File file = new File(imagePath);
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                        bos.write(buf, 0, readNum);
                    }
                    image = bos.toByteArray();
                    fis.close();
                    bos.close();
                }
                result.add(new Message(senderName, receiverName, message, image, time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return result;
    }

    public boolean sendMessage(String sender, String receiver, Message message) {
        String query = "INSERT INTO messages (sender, receiver, message,time) VALUES (?,?,?,NOW());";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setString(3, message.getMessage());
            stmt.executeUpdate();
            if (message.getImage() != null) {
                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
                int messageId = 0;
                if (rs.next()) {
                    messageId = rs.getInt(1);
                }
                query = "INSERT INTO images (p_id) VALUES (" + messageId + ");";
                stmt.executeUpdate(query);
                query = "SELECT LAST_INSERT_ID()";
                ResultSet re = stmt.executeQuery(query);
                if (re.next()) {
                    messageId = re.getInt(1);
                }

                byte[] image = message.getImage();
                String fileName = "image" + messageId + ".png";
                String filepath = filePath + fileName;
                FileOutputStream fos = new FileOutputStream(filepath);
                fos.write(image);
                fos.close();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> searchUsers(String username) {
        String query = "SELECT username FROM users WHERE username LIKE ?";
        System.out.println(query);
        ArrayList<String> users = new ArrayList<>();
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, "%" + username + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public UserInfo getUserInfo(String username) {
        String query = "SELECT * FROM users WHERE username = ?;";
        UserInfo userInfo = null;
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String uname = rs.getString("username");
                String email = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String phone = rs.getString("phoneNumber");
                boolean authStatus = rs.getBoolean("authStatus");
                userInfo = new UserInfo(uname, email, firstName, lastName, phone);
                userInfo.setAuthStatus(authStatus);
                userInfo.setRawProfilePicture(loadImage(username + "profile.png"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }


    public void updateUserInfo(UserInfo userInfo, String username) {
        System.out.println(userInfo.getEmail());
        System.out.println(userInfo.getFirstName());
        System.out.println(userInfo.getLastName());
        String query = "UPDATE users SET email = ?, firstName = ?, lastName = ?, phoneNumber = ? WHERE username = ?";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, userInfo.getEmail());
            stmt.setString(2, userInfo.getFirstName());
            stmt.setString(3, userInfo.getLastName());
            stmt.setString(4, userInfo.getPhone());
            stmt.setString(5, username);
            stmt.executeUpdate();
            if (userInfo.getProfilePicture() != null) {
                uploadImage(username + "profile.png", userInfo.getRawProfilePicture());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(String fileName, byte[] image) {
        System.out.println("Uploading image");
        String filepath = filePath + fileName;
        try {
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(image);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] loadImage(String fileName) {
        String filepath = filePath + fileName;
        byte[] image = null;
        try {
            System.out.println(filepath);
            FileInputStream fin = new FileInputStream(filepath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fin.read(buf)) != -1; ) {
                bos.write(buf, 0, readNum);
            }
            image = bos.toByteArray();
            bos.close();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Image loaded");
            return image;
        }
    }

    public String register(UserInfo userInfo, String password) {
        return register(userInfo.getUserName(), password, userInfo.getEmail(), userInfo.getFirstName(), userInfo.getLastName(), userInfo.getPhone());
    }
    public ArrayList<Post> getPosts() {
        ArrayList<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM post ORDER BY postDate DESC;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int postId = rs.getInt("postId");
                String postTitle = rs.getString("postTitle");
                String postContent = rs.getString("postDescription");
                String postDate = rs.getString("postDate");
                String postAuthor = rs.getString("postAuthor");
                Post post = new Post(postId, postTitle, postContent, postDate, postAuthor);
                query="SELECT COUNT(*) FROM followedforums where f_id = ?;";
                PreparedStatement stmt2 = dbConnection.prepareStatement(query);
                stmt2.setInt(1, postId);
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    post.setNOUsers(rs2.getInt(1));
                }
                query = "SELECT * FROM followedforums WHERE f_id = ? AND uname = ?;";
                stmt = dbConnection.prepareStatement(query);
                stmt.setInt(1, postId);
                stmt.setString(2, this.userName);
                rs2 = stmt.executeQuery();
                if (rs2.next()) {
                    post.setFollowedStatus(true);
                }else{
                    post.setFollowedStatus(false);
                }
                byte [] image = loadImage(postId+"forum.png");
                if(image!=null) {
                    post.setFile("png", image);
                }
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post getPost(int postId) {
        Post post = null;
        String query = "SELECT * FROM post WHERE postId = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int postId1 = rs.getInt("postId");
                String postTitle = rs.getString("postTitle");
                String postContent = rs.getString("postDescription");
                String postDate = rs.getString("postDate");
                String postAuthor = rs.getString("postAuthor");
                query = "SELECT * FROM post_files WHERE p_id = ?;";
                PreparedStatement stmt2 = dbConnection.prepareStatement(query);
                stmt2.setInt(1, postId);
                ResultSet rs2 = stmt2.executeQuery();
                post = new Post(postId, postTitle, postContent, postDate, postAuthor);
                while (rs2.next()) {
                    String fileName = rs2.getString("file_id");
                    String extension = rs2.getString("ext");
                    byte[] file = loadFile(fileName + "." + extension);
                    post.setFile(extension, file);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    public DailyTask getTaskById(int taskId) {
        DailyTask task = null;
        String query = "SELECT * FROM tasks WHERE id = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int taskId1 = rs.getInt("id");
                String taskTitle = rs.getString("taskTitle");
                String taskDescription = rs.getString("taskDescription");
                String taskDate = rs.getString("taskDate");
                task = new DailyTask(taskId1, taskTitle, taskDescription, taskDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    public int addTask(DailyTask task) {
        String query = "INSERT INTO tasks (taskTitle, taskDescription, taskDate) VALUES (?, ?, ?);";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, task.getTaskTitle());
            stmt.setString(2, task.getTaskDescription());
            stmt.setString(3, task.getTaskDate());
            stmt.executeUpdate();
            query = "SELECT LAST_INSERT_ID()";
            stmt = dbConnection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void createPost(Post post) {
        String query = "INSERT INTO post (postTitle, postDescription, postDate, postAuthor) VALUES (?, ?, ?, ?);";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getDescription());
            stmt.setString(3, post.getDate());
            stmt.setString(4, post.getAuthor());
            stmt.executeUpdate();
            List<FileData> files = post.getFiles();
            if (files.size() > 0) {
                query = "SELECT LAST_INSERT_ID()";
                stmt = dbConnection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int postId = rs.getInt(1);
                    for (FileData file : files) {
                        System.out.println("file: " + file.getFileExtension());
                        uploadImage(postId + "Forum.png", file.getFile());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void uploadFile(String fileName, byte[] file) {
        String filepath = PostfilePath + fileName;
        try {
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(file);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] loadFile(String fileName) {
        String filepath = PostfilePath + fileName;
        byte[] file = null;
        try {
            System.out.println(filepath);
            FileInputStream fin = new FileInputStream(filepath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int readNum; (readNum = fin.read(buf)) != -1; ) {
                bos.write(buf, 0, readNum);
            }
            file = bos.toByteArray();
            bos.close();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("file loaded");
            return file;
        }
    }
    public Pair<Boolean,ArrayList<Post>> getPostsForum(int forumId, String username) {
        String query="SELECT * from followedforums where uname=? and f_id=?";
        boolean isFollowed=false;
        try{
            PreparedStatement stmt=dbConnection.prepareStatement(query);
            stmt.setString(1,username);
            stmt.setInt(2,forumId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                isFollowed=true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        List<Post> posts = getPostsForum(forumId);
        return new Pair<>(isFollowed,(ArrayList<Post>)posts);
    }
    public List<Post> getPostsForum(int forumId) {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM forumposts INNER JOIN users ON forumposts.postAuthor = users.username WHERE forumId = ? ORDER BY postDate DESC;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, forumId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("postID"));
                post.setTitle(rs.getString("postTitle"));
                post.setDescription(rs.getString("postDescription"));
                post.setDate(rs.getString("postDate"));
                post.setAuthor(rs.getString("postAuthor"));
                post.setAuthorFirstName(rs.getString("firstName"));
                post.setAuthorLastName(rs.getString("lastName"));
                UserInfo user = getUserInfo(post.getAuthor());
                post.setAuthorData(user);
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public void createForumPost(Post post) {
        String query = "INSERT INTO forumposts (postTitle,postDescription,postAuthor,forumId) VALUES (?,?,?,?);";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getDescription());
            stmt.setString(3, post.getAuthor());
            stmt.setInt(4, post.getForumId());
            stmt.executeUpdate();
            List<FileData> files = post.getFiles();
            if (files.size() > 0) {
                query = "SELECT LAST_INSERT_ID()";
                stmt = dbConnection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int postId = rs.getInt(1);
                    for (FileData file : files) {
                        query = "INSERT INTO post_files (p_id,ext,file_name) VALUES (?,?,?);";
                        stmt = dbConnection.prepareStatement(query);
                        stmt.setInt(1, postId);
                        stmt.setString(2, file.getFileExtension());
                        stmt.setString(3, file.getName());
                        stmt.executeUpdate();
                        query = "SELECT LAST_INSERT_ID()";
                        stmt = dbConnection.prepareStatement(query);
                        rs = stmt.executeQuery();
                        if (rs.next()) {
                            int fileId = rs.getInt(1);
                            uploadFile(fileId + "." + file.getFileExtension(), file.getFile());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendComment(Comment comment) {
        String query = "INSERT INTO forumcomments (comment,commentAuthor,p_id) VALUES (?,?,?);";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, comment.getCommentText());
            stmt.setString(2, comment.getCommentUser().getUserName());
            stmt.setInt(3, comment.getPostId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Comment> getComments(int postId) {
        String query = "SELECT * FROM forumcomments WHERE p_id = ? ORDER BY date DESC ;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            List<Comment> comments = new ArrayList<>();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setCommentId(rs.getInt("c_id"));
                comment.setCommentText(rs.getString("comment"));
                comment.setCommentUser(getUserInfo(rs.getString("commentAuthor")));
                comment.setPostId(rs.getInt("p_id"));
                comments.add(comment);
            }
            return comments;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Post> getForums(int limit,String userName) {
        String query = "SELECT * FROM followedforums INNER JOIN post ON followedforums.f_id=post.postId WHERE followedforums.uname=? ORDER BY postDate DESC LIMIT ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(2, limit);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            List<Post> posts = new ArrayList<>();
            while (rs.next()) {
                Post post = new Post();
                post.setTitle(rs.getString("postTitle"));
                post.setDescription(rs.getString("postDescription"));
                post.setAuthor(rs.getString("postAuthor"));
                post.setForumId(rs.getInt("postId"));
                post.setPostId(post.getForumId());
                post.setDate(rs.getString("postDate"));
                post.setAuthorData(getUserInfo(post.getAuthor()));
                byte [] image = loadImage(post.getPostId()+"forum.png");
                if(image!=null) {
                    post.setFile("png", image);
                }
                posts.add(post);
            }
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void followSubForum(String username, int forumId) {
        String query = "SELECT * FROM followedforums WHERE uname = ? AND f_id = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setInt(2, forumId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                query = "INSERT INTO followedforums (uname,f_id) VALUES (?,?);";
                stmt = dbConnection.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setInt(2, forumId);
                stmt.executeUpdate();
            } else {
                query = "DELETE FROM followedforums WHERE uname = ? AND f_id = ?;";
                stmt = dbConnection.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setInt(2, forumId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addReminder(Reminder reminder) {
        String query="INSERT INTO reminders (uname,content, date) VALUES (?,?,?);";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, reminder.getUsername());
            stmt.setString(2, reminder.getContent());
            stmt.setString(3, reminder.getDate());
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addTodoList(Reminder reminder) {
        String query="INSERT INTO todotasks (uname,content) VALUES (?,?);";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, reminder.getUsername());
            stmt.setString(2, reminder.getContent());
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Reminder> getTodoList(String username) {
        String query = "SELECT * FROM todotasks WHERE uname = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            List<Reminder> reminders = new ArrayList<>();
            while (rs.next()) {
                Reminder reminder = new Reminder();
                reminder.setUsername(rs.getString("uname"));
                reminder.setContent(rs.getString("content"));
                reminder.setId(rs.getInt("t_id"));
                reminder.setDone(rs.getBoolean("is_done"));
                reminders.add(reminder);
            }
            return reminders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<FileData> getForumPostFiles(int postId){
        String query = "SELECT * FROM post_files WHERE p_id = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            List<FileData> files = new ArrayList<>();
            while (rs.next()) {
                byte[] fileBytes = loadFile(rs.getInt("file_id")+"."+rs.getString("ext"));
                FileData file = new FileData(rs.getString("ext"), fileBytes, rs.getString("file_name"));
                files.add(file);
            }
            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reminder> getReminders(String username) {
        String query = "SELECT * FROM reminders WHERE uname = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            List<Reminder> reminders = new ArrayList<>();
            while (rs.next()) {
                Reminder reminder = new Reminder();
                reminder.setUsername(rs.getString("uname"));
                reminder.setContent(rs.getString("content"));
                reminder.setDate(rs.getString("date"));
                reminder.setId(rs.getInt("r_id"));
                reminder.setDone(rs.getBoolean("is_done"));
                reminders.add(reminder);
            }
            return reminders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeTodo(int todoId, String username) {
        String query = "DELETE FROM todotasks WHERE t_id = ? AND uname = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, todoId);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeReminder(int reminderId, String username) {
        String query = "DELETE FROM reminders WHERE r_id = ? AND uname = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setInt(1, reminderId);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTodoList(int todoId, boolean isDone, String username) {
        String query = "UPDATE todotasks SET is_done = ? WHERE t_id = ? AND uname = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setBoolean(1, isDone);
            stmt.setInt(2, todoId);
            stmt.setString(3, username);
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateReminder(int reminderId, boolean isDone, String username) {
        String query = "UPDATE reminders SET is_done = ? WHERE r_id = ? AND uname = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setBoolean(1, isDone);
            stmt.setInt(2, reminderId);
            stmt.setString(3, username);
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Course> getCourses(String username) {
        String query = "SELECT * FROM courses";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            List<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course course = new Course();
                course.setCourseName(rs.getString("c_name"));
                course.setCourseCode(rs.getString("c_id"));
                course.setCourseCredit(rs.getDouble("c_credit"));
                course.setFacultyName(rs.getString("c_faculty"));
                course.setCourseCategory(rs.getString("category"));
                query ="SELECT * FROM completedcourses WHERE u_name = ? AND c_id = ?;";
                stmt = dbConnection.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, course.getCourseCode());
                ResultSet rs2 = stmt.executeQuery();
                if (rs2.next()) {
                    course.setStatus(true);
                }
                courses.add(course);
            }
            return courses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCourseStatus(String courseCode, boolean status, String username) {
        if(status) {
            String query = "INSERT INTO completedcourses (c_id, u_name) VALUES (?, ?);";
            try {
                PreparedStatement stmt = dbConnection.prepareStatement(query);
                stmt.setString(1, courseCode);
                stmt.setString(2, username);
                stmt.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            String query = "DELETE FROM completedcourses WHERE c_id = ? AND u_name = ?;";
            try {
                PreparedStatement stmt = dbConnection.prepareStatement(query);
                stmt.setString(1, courseCode);
                stmt.setString(2, username);
                stmt.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Course> getCompletedCourses(String username) {
        String query = "SELECT * FROM completedcourses INNER JOIN courses ON completedcourses.c_id = courses.c_id WHERE u_name = ?;";
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Course> courses = new ArrayList<>();
            while (rs.next()) {
                Course course = new Course();
                course.setCourseName(rs.getString("c_name"));
                course.setCourseCode(rs.getString("c_id"));
                course.setCourseCredit(rs.getDouble("c_credit"));
                course.setFacultyName(rs.getString("c_faculty"));
                course.setCourseCategory(rs.getString("category"));
                course.setStatus(true);
                courses.add(course);
            }
            return courses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}