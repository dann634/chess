module com.jackson {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.jackson.main to javafx.fxml;
    exports com.jackson.main;
}