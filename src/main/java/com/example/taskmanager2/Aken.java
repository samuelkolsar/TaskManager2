package com.example.taskmanager2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Aken extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Loo nupud
        Button button1 = new Button("Lisa ülesanne");
        Button button2 = new Button("Väljasta olemasolevad ülesanded");
        Button button3 = new Button("Vali suvaline ülesanne");
        Button button4 = new Button("Nupp 4");

        // Lisa tegevused nuppudele
        button1.setOnAction(e -> createNewWindow("Aken 1"));
        button2.setOnAction(e -> createNewWindow("Aken 2"));
        button3.setOnAction(e -> createNewWindow("Aken 3"));
        button4.setOnAction(e -> createNewWindow("Aken 4"));

        // Loo paigutus ja lisa nupud paigutusse
        HBox hbox = new HBox(10); // 10 pikslit vahet nuppude vahel
        hbox.getChildren().addAll(button1, button2, button3, button4);

        // Loo stseen ja lisa see pealavale
        Scene scene = new Scene(hbox, 800, 600); // Aken suurusega 400x100 pikslit
        primaryStage.setScene(scene);
        primaryStage.setTitle("Neli Nuput");
        primaryStage.show();
    }

    // Meetod uue akna loomiseks
    private void createNewWindow(String title) {
        Stage newWindow = new Stage();
        newWindow.setTitle(title);

        StackPane pane = new StackPane();
        Scene scene = new Scene(pane, 200, 150);
        newWindow.setScene(scene);

        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

