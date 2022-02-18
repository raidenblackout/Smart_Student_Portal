package ssp.smartstudentportal;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ssp.smartstudentportal.Structures.Post;
import ssp.smartstudentportal.Structures.FileData;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreatePostWindow extends BaseController{
    public TextField postTitle;
    public TextArea postContent;
    public Button cancelBtn;
    public Button publishBtn;
    public BaseController parentController;
    public HBox files;
    public List<FileData> fileLists=new ArrayList<>();
    public ImageView forumLogo;
    private FileData forumlogodata;
    /*************************Constructor
     * @param viewFactory
     * @param fxmlName*****************************/
    public CreatePostWindow(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }
    public void initialize(){
        //files.getChildren().clear();
        WritableImage img=ImageUtils.cropImage(forumLogo,1000);
        forumLogo.setImage(img);
        ImageUtils.clipImage(forumLogo, ImageUtils.types.Circle);
    }
    public void setParentController(BaseController parentController) {
        this.parentController = parentController;
    }
    public void cancelBtnClicked() {
        viewFactory.closeWindow(this.getClass());
        viewFactory.showForumWindow();
    }
    public void publishBtnClicked() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        if(postTitle.getText().isEmpty()||postContent.getText().isEmpty()){
            return;
        }
        Post post = new Post(postTitle.getText(), postContent.getText(),dtf.format(now), ClientController.userName);
        post.setFile(fileLists);
        ClientController.getInstance().createPost(post);
        viewFactory.closeWindow(this.getClass());
        viewFactory.showForumWindow();
    }


    public void UploadImageActionPerformed() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG
                = new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
        FileChooser.ExtensionFilter extFilterjpg
                = new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter extFilterPNG
                = new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
        FileChooser.ExtensionFilter extFilterpng
                = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        FileChooser.ExtensionFilter extensionFilterAll = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters()
                .addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng,extensionFilterAll);
        //Show open file dialog
        List<File> fileList = fileChooser.showOpenMultipleDialog(null);
        if (fileList != null) {
            byte[] data_image=null;
            for (File file : fileList) {
                try {
                    FileInputStream fin = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    for (int readNum; (readNum = fin.read(buf)) != -1; ) {
                        bos.write(buf, 0, readNum);
                    }
                    data_image = bos.toByteArray();
                    String fileName = file.getName();
                    String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                    System.out.println(extension);
                    fileLists.add(new FileData(extension, data_image, fileName));
                    fin.close();
                    bos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            loadFileIcons();
        }
    }
    public void loadFileIcons(){
        files.getChildren().clear();
        fileLists.forEach(fileData -> {
            if(fileData.getFileExtension().equals("jpg")||fileData.getFileExtension().equals("JPG")) {
                ImageView imageView = new ImageView();
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
                imageView.setImage(new Image(new ByteArrayInputStream(fileData.getFile())));
                HBox hBox = null;
                try {
                    FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("fileIcon.fxml"));
                    hBox = fxmlLoader.load();
                    FileIconController fileIconController = fxmlLoader.getController();
                    fileIconController.setFileDataList(fileLists);
                    ((HBox)hBox.getChildren().get(0)).getChildren().add(imageView);
                    files.getChildren().add(hBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                HBox hBox = null;
                try {
                    hBox = FXMLLoader.load(getClass().getResource("fileIcon.fxml"));
                    files.getChildren().add(hBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void addLogo(){
            FileChooser fileChooser = new FileChooser();
            //Set extension filter
            FileChooser.ExtensionFilter extFilterJPG
                    = new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
            FileChooser.ExtensionFilter extFilterjpg
                    = new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
            FileChooser.ExtensionFilter extFilterPNG
                    = new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
            FileChooser.ExtensionFilter extFilterpng
                    = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            FileChooser.ExtensionFilter extensionFilterAll = new FileChooser.ExtensionFilter("All Files", "*.*");
            fileChooser.getExtensionFilters()
                    .addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng,extensionFilterAll);
            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);
            byte[] data_image=null;
            try{
                FileInputStream fin = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                for (int readNum; (readNum = fin.read(buf)) != -1; ) {
                    bos.write(buf, 0, readNum);
                }
                data_image = bos.toByteArray();
                String fileName = file.getName();
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                forumlogodata = new FileData(extension, data_image, fileName);
                fileLists.add(forumlogodata);
                forumLogo.setImage(ImageUtils.getImage(data_image));
                WritableImage img=ImageUtils.cropImage(forumLogo,1000);
                forumLogo.setImage(img);
                ImageUtils.clipImage(forumLogo, ImageUtils.types.Circle);
                fin.close();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
