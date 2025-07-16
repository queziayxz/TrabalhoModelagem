module org.trab.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens org.trab.demo to javafx.fxml;
    opens org.trab.demo.controller to javafx.fxml;
    exports org.trab.demo;
    exports org.trab.demo.controller;
}