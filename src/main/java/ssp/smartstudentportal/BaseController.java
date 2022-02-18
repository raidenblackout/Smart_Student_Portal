package ssp.smartstudentportal;

import javafx.scene.Parent;
import javafx.stage.Stage;

public abstract class BaseController extends MainController {
    public ViewFactory viewFactory;
    public String fxmlName;
    public Stage stage;
    public Parent root;
    /********************* attributes ********************/
    private double x, y;
    /********************* attributes ********************/


    /*************************Constructor*****************************/
    public BaseController(ViewFactory viewFactory, String fxmlName) {
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }
    /*************************Constructor*****************************/


    /*************************Getters and Setters*****************************/
    public String getFxmlName() {

        return fxmlName;
    }

    public ViewFactory getViewFactory() {

        return viewFactory;
    }
    /*************************Getters and Setters*****************************/


    /************************* Initialize *****************************/
    public void init() {
        this.stage = ViewFactory.windows.get(this.getClass());
        this.root = stage.getScene().getRoot();
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });
    }
    /************************* Initialize *****************************/
}
