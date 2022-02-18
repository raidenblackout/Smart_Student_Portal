package ssp.smartstudentportal;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class addReminderController {
    public DashboardWindowController parentController;
    public TextField content;
    public DatePicker date;
    public void setParentController(DashboardWindowController parentController) {
        this.parentController = parentController;
    }
    public void AddReminder(){
        System.out.println("Add Reminder");

        //parentController.addReminder(content.getText(),date.getValue().toString());
        String content = this.content.getText();
        String date = this.date.getValue().toString();
        ZonedDateTime now =ZonedDateTime.now();
        ZonedDateTime zonedDateTime = TimeUtils.convertirAFecha(date);
        //difference in days
        long diff = zonedDateTime.toLocalDate().toEpochDay() - now.toLocalDate().toEpochDay();
        ChartData chartData= new ChartData(content, now.plusDays(diff).toInstant());
        parentController.calendarData.add(chartData);
        parentController.tile= TileBuilder.create().skinType(Tile.SkinType.CALENDAR)
                .prefSize(400, 320)
                .title("Calendar")
                .description("Click to view calendar")
                .textVisible(true)
                .descriptionAlignment(Pos.CENTER)
                .backgroundColor(new Color(1, 1, 1, 1))
                .foregroundColor(Color.BLACK)
                .chartData(parentController.calendarData)
                .activeColor(Color.BLACK)
                .textColor(Color.BLACK)
                .titleColor(Color.BLACK)
                .descriptionColor(Color.BLACK)
                .build();
        parentController.calendarContainer.getChildren().clear();
        parentController.calendarContainer.getChildren().add(parentController.tile);
        ClientController.getInstance().addReminder(content,date);
        parentController.getReminders();
    }
}
