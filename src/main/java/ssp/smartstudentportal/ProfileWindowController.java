package ssp.smartstudentportal;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;
import ssp.smartstudentportal.Structures.UserInfo;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class ProfileWindowController extends BaseController {
    public TextField fname;
    public TextField lname;
    public TextField uemail;
    public TextField uphone;
    public Button btnEdit;
    public FontIcon profileIcon;
    public VBox profilePicHolder;
    public StackedFontIcon defaultProfile;
    private boolean iconStatus;
    private ImageView profilePic;
    private UserInfo newUser;

    /*************************Constructor
     * @param viewFactory
     * @param fxmlName*****************************/
    public ProfileWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    public void initialize() {
        setProfileInfo(ClientController.userInfo);
        loadPicture();
    }

    public void setProfileInfo(UserInfo userInfo) {
        fname.setText(userInfo.getFirstName());
        lname.setText(userInfo.getLastName());
        uemail.setText(userInfo.getEmail());
        uphone.setText(userInfo.getPhone());
    }

    public void onEdit() {
        fname.setEditable(true);
        lname.setEditable(true);
        uemail.setEditable(true);
        uphone.setEditable(true);
        btnEdit.setText("Save Profile");
        btnEdit.removeEventHandler(javafx.event.ActionEvent.ACTION, event -> onEdit());
        btnEdit.setOnAction(event -> onSave());
    }

    public void onSave() {
        fname.setEditable(false);
        lname.setEditable(false);
        uemail.setEditable(false);
        uphone.setEditable(false);
        ClientController.userInfo.setFirstName(fname.getText());
        ClientController.userInfo.setLastName(lname.getText());
        ClientController.userInfo.setEmail(uemail.getText());
        ClientController.userInfo.setPhone(uphone.getText());
        System.out.println(ClientController.userInfo.getFullName());
        ClientController.getInstance().updateUserInfo(ClientController.userInfo);
        DashboardWindowController dashboardWindowController = (DashboardWindowController) MainController.controllers.get(DashboardWindowController.class);
        dashboardWindowController.profilePic.setImage(ClientController.userInfo.getProfilePicture());
        ImageView proPic = dashboardWindowController.profilePic;
        try {
            Image image1 = proPic.getImage();
            double ratio = image1.getWidth() / image1.getHeight();
            proPic.setImage(image1);
            if (image1.getWidth() > image1.getHeight()) {
                proPic.setFitWidth(200);
                proPic.setFitHeight(200 / ratio);
            } else {
                proPic.setFitHeight(200);
                proPic.setFitWidth(200 * ratio);
            }
            proPic.setPreserveRatio(true);
            proPic.scaleXProperty();
            proPic.scaleYProperty();
            proPic.setSmooth(true);
            proPic.setCache(true);
            ImageUtils.clipCircle(proPic, 50);
        } catch (Exception ex) {
        }
        btnEdit.setText("Edit Profile");
        btnEdit.removeEventHandler(javafx.event.ActionEvent.ACTION, event -> onSave());
        btnEdit.setOnAction(event -> onEdit());
    }

    public void onHoverProfile(MouseEvent e) {
        this.profileIcon.setIconLiteral("fa-plus");
        this.profileIcon.translateYProperty().set(10);
    }

    public void onHoverOutProfile(MouseEvent e) {
        this.profileIcon.setIconLiteral("fa-user");
        this.profileIcon.translateYProperty().set(0);
    }

    public void onClickProfile(MouseEvent e) {
        setProfilePicture();
    }

    public void setProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        btnEdit.setCursor(Cursor.HAND);
        onEdit();
        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG
                = new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
        FileChooser.ExtensionFilter extFilterjpg
                = new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG
                = new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
        FileChooser.ExtensionFilter extFilterpng
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters()
                .addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng);
        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println(file.getAbsolutePath());
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Iterator<ImageWriter> writers=ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = (ImageWriter) writers.next();
                byte[] imageInByte;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.1f);  // Change the quality value you prefer
                writer.setOutput(ios);
                writer.write(null, new IIOImage(bufferedImage, null, null), param);
                imageInByte = baos.toByteArray();
                WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                ImageView img = new ImageView();
                Image image1 = image;
                double ratio = image1.getWidth() / image1.getHeight();
                img.setImage(image);
                if (image1.getWidth() > image1.getHeight()) {
                    img.setFitWidth(400);
                    img.setFitHeight(400 / ratio);
                } else {
                    img.setFitHeight(400);
                    img.setFitWidth(400 * ratio);
                }
                img.setPreserveRatio(true);
                img.scaleXProperty();
                img.scaleYProperty();
                img.setSmooth(true);
                img.setCache(true);
                ImageUtils.clipCircle(img, 100);
                ClientController.userInfo.setRawProfilePicture(imageInByte);
                loadPicture();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void loadPicture() {
        Image image1 = ClientController.userInfo.getProfilePicture();
        System.out.println("loaded");
        if (image1 != null) {
            System.out.println("image1 is not null");
            double ratio = image1.getWidth() / image1.getHeight();
            ImageView img = new ImageView();
            img.setImage(image1);
            if (image1.getWidth() > image1.getHeight()) {
                img.setFitWidth(400);
                img.setFitHeight(400 / ratio);
            } else {
                img.setFitHeight(400);
                img.setFitWidth(400 * ratio);
            }
            img.setPreserveRatio(true);
            img.scaleXProperty();
            img.scaleYProperty();
            img.setSmooth(true);
            img.setCache(true);
            //crop the image
            int width = (int) img.getImage().getWidth();
            int height = (int) img.getImage().getHeight();
            PixelReader reader = img.getImage().getPixelReader();
            WritableImage croppedImage = null;
            if (width > height) {
                int len = Math.min(400, height);
                croppedImage = new WritableImage(reader, (width - len) / 2, (height - len) / 2, len, len);
            } else {
                int len = Math.min(width, 400);
                croppedImage = new WritableImage(reader, (width - len) / 2, (height - len) / 2, len, len);
            }
            img.setImage(croppedImage);
            img.setFitWidth(200);
            img.setFitHeight(200);
            ImageUtils.clipCircle(img, 100);
            img.setOnMouseClicked(event -> setProfilePicture());
            profilePicHolder.getChildren().clear();
            profilePicHolder.getChildren().add(img);
            defaultProfile.setVisible(false);
        }
    }

    public void onHoverProfilePane() {
        System.out.println("hover");
        profilePic = (ImageView) profilePicHolder.getChildren().get(0);
        profilePicHolder.getChildren().clear();
        profilePicHolder.getChildren().add(defaultProfile);
        profileIcon.setIconLiteral("fa-plus");
    }

    public void onExitProfilePane() {
        System.out.println("exit");
        profilePicHolder.getChildren().clear();
        profilePicHolder.getChildren().add(profilePic);
        defaultProfile.setVisible(iconStatus);
        profileIcon.setIconLiteral("fa-user");
    }
    public void goBack() throws IOException {
        viewFactory.showDashboardWindow();
        viewFactory.closeWindow(this.getClass());

    }
}
