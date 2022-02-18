package ssp.smartstudentportal.Structures;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class UserInfo implements Serializable {
    private String uname;
    private String email;
    private String password;
    private String phone;
    private String fname;
    private String lname;
    private String fullName;
    private byte[] profilePicture;
    private boolean authStatus;
    //private String lastMessage;

    public UserInfo() {

    }

    public UserInfo(String uname, String email, String fname, String lname, String phone) {
        this.uname = uname;
        this.email = email;
        this.phone = phone;
        this.fname = fname;
        this.lname = lname;
        this.fullName = fname + " " + lname;
    }

    public String getUserName() {
        return uname;
    }

    public void setUserName(String uname) {
        this.uname = uname;
    }

    public String getFullName() {
        return fname + " " + lname;
    }

    public void setFullName(String fname) {
        this.fullName = fname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFullName(String fname, String lname) {
        this.fname = fname;
        this.lname = lname;
        this.fullName = fname + " " + lname;
    }

    public String getFirstName() {
        return fname;
    }

    public void setFirstName(String text) {
        this.fname = text;
    }

    public String getLastName() {
        return lname;
    }

    public void setLastName(String text) {
        this.lname = text;
    }

    public Image getProfilePicture() {
        if (profilePicture != null) {
            System.out.println("Getting profile picture");
            Image image = new Image(new ByteArrayInputStream(profilePicture));
            return image;
        }
        return null;
    }

    public void setProfilePicture(Image image) {
        System.out.println("Setting profile picture");
        BufferedImage img = SwingFXUtils.fromFXImage(image, null);
        byte[] imgBytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            baos.flush();
            imgBytes = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.profilePicture = imgBytes;
    }

    public byte[] getRawProfilePicture() {
        return profilePicture;
    }

    public void setRawProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
    public void setAuthStatus(boolean authStatus) {
        this.authStatus = authStatus;
    }

    public boolean getAuthStatus() {
        return authStatus;
    }
//    public void setLastMessage(String lastMessage) {
//        if(lastMessage.length() > 20) {
//            this.lastMessage = lastMessage.substring(0, 20) + "...";
//        }else {
//            this.lastMessage = lastMessage;
//        }
//    }
//    public String getLastMessage() {
//        return lastMessage;
//    }
}
