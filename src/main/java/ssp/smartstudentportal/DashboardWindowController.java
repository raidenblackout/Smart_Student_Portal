package ssp.smartstudentportal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.events.TileEvent;
import eu.hansolo.tilesfx.events.TileEventListener;
import eu.hansolo.tilesfx.skins.BarChartItem;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;
import ssp.smartstudentportal.Structures.Course;
import ssp.smartstudentportal.Structures.Post;
import ssp.smartstudentportal.Structures.Reminder;
import ssp.smartstudentportal.TaskWindowController;
import ssp.smartstudentportal.Structures.DailyTask;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ssp.smartstudentportal.TimeUtils.convertirAFecha;

public class DashboardWindowController extends BaseController {
    /************* Public **************/
    public PopOver popOver;
    public ImageView profilePic;
    public PopOver logOutPopOver;
    public VBox calendarContainer;
    public VBox cgpaGraph;
    public VBox skillsPieChart;
    public VBox dashboardForums;
    public ImageView rightPPImage;
    public Label userFullname;
    public FontIcon showPopup;
    public PopOver addReminder;
    public VBox reminderList;
    public VBox todoListContainer;
    public FontIcon addTodoList;
    public Tile tile;
    public List<ChartData> calendarData = new ArrayList<>();
    public ArrayList<Reminder> reminders;
    public Text username;
    /************* Private ****************/
    @FXML
    private FontIcon fx;
    @FXML
    private VBox taskBox;
    @FXML
    private HBox hbox;

    /************* Constructors **************/
    DashboardWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }


    /************* Setter **************/
    public void setPopUp(Node popUpWindow, TaskController childController) {
        this.popOver = new PopOver();
        this.popOver.setContentNode(popUpWindow);
        childController.setParent(this);
        this.popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
    }

    /************* Initialization **************/
    public void initialize() {
        ImageUtils.clipImage(profilePic, ImageUtils.types.Circle);
        find(hbox);
        if (ClientController.userInfo.getProfilePicture() != null) {
            profilePic.setImage(ClientController.userInfo.getProfilePicture());
            ImageView proPic = profilePic;
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
        }
        username.setText("@"+ClientController.userInfo.getUserName());



        XYChart.Series<String, Number> lineChart = new XYChart.Series ();
        lineChart.setName("Trimester GPA");
        lineChart.getData().add(new XYChart.Data<>("1", 4));
        lineChart.getData().add(new XYChart.Data<>("2", 3.5));
        lineChart.getData().add(new XYChart.Data<>("3", 3));
        lineChart.getData().add(new XYChart.Data<>("4", 4));
        lineChart.getData().add(new XYChart.Data<>("5", 3.5));
        lineChart.getData().add(new XYChart.Data<>("6", 4));
        XYChart.Series<String, Number> series2 = new XYChart.Series();
        series2.setName("Inside");
        series2.getData().add(new XYChart.Data("MO", 8));
        series2.getData().add(new XYChart.Data("TU", 5));
        series2.getData().add(new XYChart.Data("WE", 0));
        series2.getData().add(new XYChart.Data("TH", 2));
        series2.getData().add(new XYChart.Data("FR", 4));
        series2.getData().add(new XYChart.Data("SA", 3));
        series2.getData().add(new XYChart.Data("SU", 5));
        Tile barChartTile = TileBuilder.create()
                .skinType(Tile.SkinType.BAR_CHART)
                .prefSize(400, 400)
                .title("Skills")
                .backgroundColor(new Color(1, 1, 1, 1))
                .textColor(Color.BLACK)
                .valueColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .barChartItems(new BarChartItem("Problem Solving",50),new BarChartItem("C++",100),new BarChartItem("Java",100) ,new BarChartItem("Maths",70))
                .build();
        //cgpaGraph.getChildren().add(barChartTile);
        rightPPImage.setImage(ClientController.userInfo.getProfilePicture());
        if(rightPPImage.getImage()!=null) {
            WritableImage img = ImageUtils.cropImage(rightPPImage, 5000);
            rightPPImage.setImage(img);
            ImageUtils.clipImage(rightPPImage, ImageUtils.types.Circle);
        }
        getForums();
        userFullname.setText(ClientController.userInfo.getFullName());
        getTodoList();
        getReminders();
        getCompletedCourses();
        reminders.forEach(reminder -> {
                    String date = reminder.getDate();
                    ZonedDateTime now =ZonedDateTime.now();
                    ZonedDateTime zonedDateTime = convertirAFecha(date);
                    long diff = zonedDateTime.toLocalDate().toEpochDay() - now.toLocalDate().toEpochDay();
                    calendarData.add(new ChartData(reminder.getContent(),now.plusDays(diff).toInstant()));
                });
        tile= TileBuilder.create().skinType(Tile.SkinType.CALENDAR)
                .prefSize(400, 320)
                .title("Calendar")
                .description("Click to view calendar")
                .textVisible(true)
                .descriptionAlignment(Pos.CENTER)
                .backgroundColor(new Color(1, 1, 1, 1))
                .foregroundColor(Color.BLACK)
                .chartData(calendarData)
                .activeColor(Color.BLACK)
                .textColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .descriptionColor(Color.BLACK)
                .build();
        calendarContainer.getChildren().add(tile);
    }



    /******************** Methods ***********************/
    public void find(Node node) {
        if (node instanceof VBox) {
            ((VBox) node).getChildren().forEach(this::find);
        }
        if (node instanceof HBox) {
            ((HBox) node).getChildren().forEach(this::find);
        }
        if (node instanceof AnchorPane) {
            ((AnchorPane) node).getChildren().forEach(this::find);
        }
        if (node instanceof ImageView) {
            reImage((ImageView) node);
        }
    }


    public void findCircle(Node node) {
        if (node instanceof VBox) {
            ((VBox) node).getChildren().forEach(this::findCircle);
        }
        if (node instanceof HBox) {
            ((HBox) node).getChildren().forEach(this::findCircle);
        }
        if (node instanceof AnchorPane) {
            ((AnchorPane) node).getChildren().forEach(this::findCircle);
        }
        if (node instanceof ImageView) {
            reImageCircle((ImageView) node);
        }
    }


    public void reImageCircle(ImageView image) {
        ImageUtils.clipImage(image, ImageUtils.types.Circle);
    }


    public void reImage(ImageView image) {
        ImageUtils.clipImage(image, ImageUtils.types.Rectangle);
    }


    public void addTask(TextField taskName, TextArea taskDescription, int taskId) {
        HBox hbox = new HBox();
        String text = taskName.getText();
        if (text.equals("")) {
            text = "New Task";
        }
        TextField label = new TextField(text);
        label.setMinHeight(30);
        label.setPrefWidth(105);
        label.setAlignment(Pos.CENTER_LEFT);
        //set bold font
        label.setStyle("-fx-font-weight: bold");
        label.setStyle("-fx-font-size: 20px");
        label.setPrefWidth(label.getText().length() * 10 + 30);
        label.setEditable(false);
        label.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                showTask(taskId);
            }
        });
//        label.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                label.setPrefWidth(label.getText().length() * 10 + 30);
//            }
//        });
        hbox.getChildren().add(label);
        FontIcon iconCheck = new FontIcon("fas-check");
        FontIcon iconCross = new FontIcon("fas-times");
        iconCheck.setTextAlignment(TextAlignment.CENTER);
        iconCheck.setIconSize(20);
        iconCheck.setWrappingWidth(40);
        iconCheck.setTabSize(10);
        iconCheck.setIconColor(Color.web("#00bfff"));
        iconCheck.setOnMouseClicked(event -> {
            //destroy the hbox
            VBox p = (VBox) hbox.getParent();
            p.getChildren().remove(hbox);
        });
        iconCheck.setCursor(Cursor.HAND);
        iconCheck.getStyleClass().add("task-check-icon");
        iconCross.setIconSize(20);
        iconCross.setWrappingWidth(40);
        iconCross.setTabSize(10);
        iconCross.setIconColor(Color.web("#ff0000"));
        iconCross.setOnMouseClicked(event -> {
            //destroy the hbox
            VBox p = (VBox) hbox.getParent();
            p.getChildren().remove(hbox);
        });
        iconCross.setCursor(Cursor.HAND);
        iconCross.getStyleClass().add("task-cross-icon");
        //set the icon to the right of the hbox
        HBox.setHgrow(label, Priority.ALWAYS);
        hbox.setStyle("-fx-border-color: #00bfff;");
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(iconCheck);
        hbox.getChildren().add(iconCross);
        taskBox.getChildren().add(0, hbox);
        taskName.setText("");
        taskDescription.setText("");
        this.setHidden();
    }
    public void showTask(int taskId) {
        DailyTask task=ClientController.getInstance().getTaskById(taskId);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskWindow.fxml"));
        try {
            Parent root = loader.load();
            TaskWindowController controller = loader.getController();
            controller.setTask(task);
            Stage stage = new Stage();
            stage.setTitle("Task");
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void showAddTaskPanel(Event e) {
        popOver.show(fx);
    }

    public void setHidden() {
        popOver.hide();
    }

    public void onExit() {
        viewFactory.closeWindow(this.getClass());
    }

    public void showForum() {
        viewFactory.showForumWindow();
    }

    public void showChat() {
        viewFactory.showChatWindow();
    }

    public void showProfile() {
        viewFactory.showProfileWindow();
        viewFactory.closeWindow(this.getClass());
    }

    public void setLogOutPopUp(Node content, logOutPopUp controller) {
        logOutPopOver = new PopOver();
        logOutPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        logOutPopOver.setDetachable(false);
        logOutPopOver.setContentNode(content);
        controller.setParent(this);
    }

    public void showLogOut(Event e) {
        logOutPopOver.show((Node) e.getSource());
    }
    public void addForum(Post p){
        try {
            HBox pp = FXMLLoader.load(getClass().getResource("DashboardForumSnippet.fxml"));
            pp.setOnMouseClicked(event -> {
                int id=p.getId();
                viewFactory.showSubForumWindow(id,p.getTitle());
                //viewFactory.closeWindow(this.getClass());
            });
            pp.getChildren().get(0).setStyle("-fx-background-color: transparent;");
            pp.getChildren().get(1).setStyle("-fx-background-color: transparent;");

            WritableImage img = ImageUtils.cropImage((ImageView) ((VBox) pp.getChildren().get(0)).getChildren().get(0), 50000);
            if(p.getFiles().size()>0){
                System.out.println("has files");
                ImageView imageView = new ImageView(ImageUtils.getImage(p.getFiles().get(0).getFile()));
                if(imageView.getImage()!=null) {
                    img = ImageUtils.cropImage(imageView, 50000);
                }
            }
            ImageView imag = ((ImageView) ((VBox) pp.getChildren().get(0)).getChildren().get(0));
            imag.setImage(img);
            ImageUtils.clipImage(imag, ImageUtils.types.Square);
            ((Label)((VBox)pp.getChildren().get(1)).getChildren().get(0)).setText(p.getTitle());
            dashboardForums.getChildren().add(pp);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void getForums(){
        dashboardForums.getChildren().clear();
        ArrayList<Post> forums = ClientController.getInstance().getForums(3);
        if(forums!=null) {
            for (Post p : forums) {
                addForum(p);
            }
        }
    }
    public void showAddReminder(){
        addReminder=new PopOver(showPopup);
        addReminder.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        addReminder.setDetachable(false);
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addReminder.fxml"));
            VBox v=loader.load();
            addReminderController controller=loader.getController();
            controller.setParentController(this);
            addReminder.setContentNode(v);
        }catch (IOException e){
            e.printStackTrace();
        }
        addReminder.show(showPopup);
    }
    public void addReminder(String content, String date, int id,boolean isDone){
        try{
            HBox v=FXMLLoader.load(getClass().getResource("reminderSnippet.fxml"));
            ((CheckBox)v.getChildren().get(0)).setText(content);
            CheckBox c=(CheckBox)v.getChildren().get(0);
            c.setSelected(isDone);
            c.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ClientController.getInstance().updateReminder(id,c.isSelected());
                }
            });
            FontIcon cross = ((FontIcon)v.getChildren().get(1));
            cross.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    reminderList.getChildren().remove(v);
                    ClientController.getInstance().removeReminder(id);
                }
            });
            reminderList.getChildren().add(v);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addToDoList(String text,int id,boolean done){
        try{
            HBox v=FXMLLoader.load(getClass().getResource("reminderSnippet.fxml"));
            ((CheckBox)v.getChildren().get(0)).setText(text);
            CheckBox checkBox = ((CheckBox)v.getChildren().get(0));
            checkBox.setSelected(done);
            checkBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ClientController.getInstance().updateToDoList(id,checkBox.isSelected());
                }
             });
            FontIcon cross = ((FontIcon)v.getChildren().get(1));
            cross.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    todoListContainer.getChildren().remove(v);
                    ClientController.getInstance().removeToDo(id);
                }
            });
            todoListContainer.getChildren().add(v);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void showAddTodoList(){
        addReminder=new PopOver(addTodoList);
        addReminder.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
        addReminder.setDetachable(false);
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addTodoList.fxml"));
            VBox v=loader.load();
            addTodoListController controller=loader.getController();
            controller.setParent(this);
            addReminder.setContentNode(v);
        }catch (IOException e){
            e.printStackTrace();
        }
        addReminder.show(addTodoList);
    }
    public void getTodoList(){
        ArrayList<Reminder> todoList = ClientController.getInstance().getTodoList();
        setTodoList(todoList);
    }
    public void setTodoList(ArrayList<Reminder> todoList){
        todoListContainer.getChildren().clear();
        for(Reminder r: todoList){
            addToDoList(r.getContent(),r.getId(),r.isDone());
        }
    }
    public void getReminders(){
        reminders = ClientController.getInstance().getReminders();
        setReminders(reminders);
    }

    private void setReminders(ArrayList<Reminder> reminders) {
        reminderList.getChildren().clear();
        for(Reminder r: reminders){
            addReminder(r.getContent(), r.getDate(), r.getId(),r.isDone());
        }
    }
    public void showCourseWindow(){
        viewFactory.showCourseWindow();
        viewFactory.closeWindow(this.getClass());
    }
    public void getCompletedCourses(){
        ArrayList<Course> courses = ClientController.getInstance().getCompletedCourses();
        setCompletedCourses(courses);
    }

    private void setCompletedCourses(ArrayList<Course> courses) {
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(new Color(40/255.0,152/255.0,136/255.0,1));
        colors.add(new Color(255/255.0,128/255.0,0/255.0,1));
        colors.add(new Color(0/255.0,213/255.0,13/255.0,1));
        colors.add(new Color(206/255.0,0/255.0,213/255.0,1));
        colors.add(new Color(255/255.0,20/255.0,20/255.0,1));
        skillsPieChart.getChildren().clear();
        Tile donutChartTile = TileBuilder.create()
                .skinType(Tile.SkinType.DONUT_CHART)
                .descriptionAlignment(Pos.CENTER)
                .title("Courses Completed")
                .textVisible(false)
                .textColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .barColor(Color.BLACK)
                .backgroundColor(new Color(1, 1, 1, 1))
                .valueColor(Color.BLACK)
                .build();
        //get number of distinct course categories
        ArrayList<String> categories = new ArrayList<>();
        for(Course c: courses){
            if(!categories.contains(c.getCourseCategory())){
                categories.add(c.getCourseCategory());
            }
        }
        //get number of courses in each category
        ArrayList<Integer> categoryCount = new ArrayList<>();
        for(String s: categories){
            int count = 0;
            for(Course c: courses){
                if(c.getCourseCategory().equals(s)){
                    count++;
                }
            }
            categoryCount.add(count);
        }
        for(int i=0;i<categories.size();i++){
            //generate color for each category they should be different
            ChartData c=new ChartData(categories.get(i),categoryCount.get(i),colors.get(i));
            c.setTextColor(Color.BLACK);
            donutChartTile.addChartData(c);
        }
        VBox.setVgrow(donutChartTile, Priority.ALWAYS);
        donutChartTile.getStyleClass().add("donut-chart-tile");
        skillsPieChart.getChildren().add(donutChartTile);
    }
    /*************** Methods *********************/
}
