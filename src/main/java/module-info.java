module com.jackson {
    requires javafx.controls;
    requires javafx.base;
    requires java.desktop;
    requires kotlin.stdlib;


    opens com.jackson.main to javafx.fxml;
    exports com.jackson.main;
}