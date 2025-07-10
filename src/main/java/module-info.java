module org.trab.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.trab.demo to javafx.fxml;
    exports org.trab.demo;
}