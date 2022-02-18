package ssp.smartstudentportal;

import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import ssp.smartstudentportal.Structures.FileData;

import java.util.List;


public class FileIconController {
    private List<FileData> fileDataList;
    public Parent root;
    public void setFileDataList(List<FileData> fileDataList) {
        this.fileDataList = fileDataList;
    }
    public void onCross(){
        fileDataList.remove(((HBox)root.getParent()).getChildren().indexOf(root));
        ((HBox)root.getParent()).getChildren().remove(root);
    }
}
