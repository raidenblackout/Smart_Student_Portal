package ssp.smartstudentportal;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.ByteArrayInputStream;


public class ImageUtils {

    public static void clipImage(ImageView image, ImageUtils.types type) {
        double ratio = image.getImage().getWidth() / image.getImage().getHeight();
        if (type == types.Circle) {
            Circle clip = new Circle(ratio * image.getFitHeight() / 2.0, image.getFitHeight() / 2.0, Math.min(image.getFitHeight() * ratio, image.getFitHeight()) / 2.0);
            image.setClip(clip);
        } else if (type == types.Square) {
            double width = Math.min(image.getFitHeight() * ratio, image.getFitHeight());
            Rectangle clip = new Rectangle(ratio * image.getFitHeight() / 2 - width / 2, image.getFitHeight() / 2 - width / 2, width, width);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            image.setClip(clip);
        } else if (type == types.Rectangle) {
            Rectangle clip = new Rectangle(ratio * image.getFitHeight(), image.getFitHeight());
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            image.setClip(clip);

        }
    }

    public static void clipRectangleChat(ImageView image) {
        double ratio = image.getImage().getWidth() / image.getImage().getHeight();
        if (image.getImage().getHeight() > image.getImage().getWidth()) {
            Rectangle clip = new Rectangle(ratio * image.getFitHeight(), image.getFitHeight());
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            image.setClip(clip);
        } else {
            Rectangle clip = new Rectangle(image.getFitWidth(), image.getFitWidth() / ratio);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            image.setClip(clip);
        }
    }

    public static void clipCircle(ImageView image, double radius) {
        double ratio = image.getImage().getWidth() / image.getImage().getHeight();
        Circle clip = new Circle(ratio * image.getFitHeight() / 2.0, image.getFitHeight() / 2.0, radius);
        image.setClip(clip);
    }

    public static WritableImage cropImage(ImageView img, int m_value) {
        int width = (int) img.getImage().getWidth();
        int height = (int) img.getImage().getHeight();
        PixelReader reader = img.getImage().getPixelReader();
        WritableImage croppedImage = null;
        if (width > height) {
            int len = Math.min(m_value, height);
            croppedImage = new WritableImage(reader, (width - len) / 2, (height - len) / 2, len, len);
        } else {
            int len = Math.min(width, m_value);
            croppedImage = new WritableImage(reader, (width - len) / 2, (height - len) / 2, len, len);
        }
        return croppedImage;
    }

    public enum types {
        Circle,
        Square,
        Rectangle
    }
    public static Image getImage(byte[] profilePicture) {
        if (profilePicture != null) {
            System.out.println("Getting profile picture");
            Image image = new Image(new ByteArrayInputStream(profilePicture));
            return image;
        }
        return null;
    }
}
