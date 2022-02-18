package ssp.smartstudentportal;


import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ssp.smartstudentportal.ClientController;
import ssp.smartstudentportal.Structures.Course;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseWindowController extends BaseController{
    public TableView courses;
    public TableColumn c_name,c_code,c_credit,c_status,c_faculty,index;
    private TableView.TableViewSelectionModel selectionModel;
    private List<Course> courseList;
    /*************************Constructor
     * @param viewFactory
     * @param fxmlName*****************************/
    public CourseWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    public void initialize(){
        c_name.setCellValueFactory(new PropertyValueFactory<Course,String>("courseName"));
        c_code.setCellValueFactory(new PropertyValueFactory<Course,String>("courseCode"));
        c_credit.setCellValueFactory(new PropertyValueFactory<Course,Double>("courseCredit"));
        c_status.setCellValueFactory(new PropertyValueFactory<Course,Boolean>("status"));
        c_faculty.setCellValueFactory(new PropertyValueFactory<Course,String>("facultyName"));
        //calculate the index of the course
        index.setCellFactory(column ->{
                    TableCell cell = new TableCell<>();
                    cell.textProperty().bind(Bindings.createStringBinding(() -> {
                        if (cell.isEmpty()) {
                            return null ;
                        } else {
                            return Integer.toString(cell.getIndex() + 1);
                        }
                    }, cell.emptyProperty(), cell.indexProperty()));
                    return cell;
                });
        getCourses();
        selectionModel = courses.getSelectionModel();
        courses.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                System.out.println("Double Clicked");
                int index = selectionModel.getSelectedIndex();
                boolean status = courseList.get(index).getStatus();
                if(status){
                    ClientController.getInstance().setCourseStatus(courseList.get(index).getCourseCode(),false);
                    courseList.get(index).setStatus(false);
                }else{
                    ClientController.getInstance().setCourseStatus(courseList.get(index).getCourseCode(),true);
                    courseList.get(index).setStatus(true);
                }
                courses.refresh();
            }
        });
    }
    public void getCourses(){
        courseList= ClientController.getInstance().getCourses();
        ObservableList<Course> list = FXCollections.observableArrayList(courseList);
        courses.setItems(list);
    }
    public void goBack() throws IOException {
        viewFactory.showDashboardWindow();
        viewFactory.closeWindow(this.getClass());
    }

}
