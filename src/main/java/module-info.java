module com.example.taskmanager2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.taskmanager2 to javafx.fxml;
    exports com.example.taskmanager2;
}