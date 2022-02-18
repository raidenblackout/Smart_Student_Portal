module ssp.smartstudentportal {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.kordamp.ikonli.core;
    requires java.sql;
    requires mysql.connector.java;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.commons.text;
    requires javafx.media;

    opens ssp.smartstudentportal to javafx.fxml;
    opens ssp.smartstudentportal.Structures;
    exports ssp.smartstudentportal;
}