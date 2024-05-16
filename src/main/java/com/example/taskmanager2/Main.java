package com.example.taskmanager2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
    private List<Ülesanne> ülesanded = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // Taustapildi lisamine
        Image backgroundImage = new Image(getClass().getResourceAsStream("/Ye.jpg"));
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        root.setBackground(new Background(background));

        Scene scene = new Scene(root, 500, 350);

        Button addButton = new Button("Lisa ülesanne");
        Button listButton = new Button("Vaata ülesandeid");
        Button randomButton = new Button("Vali suvaline ülesanne");
        Button closeButton = new Button("Sulge programm");

        addButton.setOnAction(e -> {
            Ülesanne uusÜlesanne = lisaUusÜlesanne();
            if (uusÜlesanne != null) {
                ülesanded.add(uusÜlesanne);
                näitaTeavitust(Alert.AlertType.INFORMATION, "Ülesanne lisatud!");
            }
        });


        listButton.setOnAction(e -> {
            Collections.sort(ülesanded);
            VBox container = new VBox(10);
            container.setAlignment(Pos.CENTER);

            Label tasksLabel = new Label();
            tasksLabel.setAlignment(Pos.CENTER);
            tasksLabel.setWrapText(true);

            StringBuilder tasksText = new StringBuilder();
            for (Ülesanne ülesanne : ülesanded) {
                tasksText.append(ülesanne).append("\n");
            }

            tasksLabel.setText(tasksText.toString());
            container.getChildren().add(tasksLabel);

            ScrollPane scrollPane = new ScrollPane(container);
            scrollPane.setFitToWidth(true);

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(scrollPane));
            dialogStage.setTitle("Ülesannete nimekiri");

            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
                scrollPane.setPrefWidth(primaryStage.getWidth());
            });
            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
                scrollPane.setPrefHeight(primaryStage.getHeight());
            });
            dialogStage.show();
        });


        //Vali suvaline ülesanne
        randomButton.setOnAction(e -> {
            if (!ülesanded.isEmpty()) {
                Ülesanne suvaline = ülesanded.get((int) (Math.random() * ülesanded.size()));

                Label taskLabel = new Label(suvaline.toString());
                taskLabel.setAlignment(Pos.CENTER);

                Button closeButton2 = new Button("Sulge");
                closeButton2.setOnAction(event -> {
                    ((Stage) closeButton2.getScene().getWindow()).close();
                });

                VBox root2 = new VBox(10, taskLabel, closeButton2);
                root2.setAlignment(Pos.CENTER);

                Scene scene2 = new Scene(root2);
                Stage randomTaskStage = new Stage();
                randomTaskStage.setScene(scene2);

                scene2.widthProperty().addListener((obs, oldVal, newVal) -> {
                    double textWidth = taskLabel.getBoundsInLocal().getWidth();
                    randomTaskStage.setWidth(textWidth + 100);
                });
                scene2.heightProperty().addListener((obs, oldVal, newVal) -> {
                    double textHeight = taskLabel.getBoundsInLocal().getHeight();
                    randomTaskStage.setHeight(textHeight + 100);
                });

                randomTaskStage.setTitle("Suvaline ülesanne");
                randomTaskStage.show();
            } else {
                näitaTeavitust(Alert.AlertType.INFORMATION, "Ülesannete nimekiri on tühi.");
            }
        });

        //Akna sulgemisel faili salvestamine
        primaryStage.setOnCloseRequest(e -> {
            try {
                loeFaili(ülesanded);
            } catch (IOException ex) {
                näitaTeavitust(Alert.AlertType.ERROR, "Tekkis viga faili salvestamisel!");
            }
            primaryStage.close();
        });
        closeButton.setOnAction(e -> {
            try {
                loeFaili(ülesanded);
            } catch (IOException ex) {
                näitaTeavitust(Alert.AlertType.ERROR, "Faili salvestamisel tekkis viga!");
            }
            primaryStage.close();
        });


        VBox.setVgrow(addButton, Priority.ALWAYS);
        VBox.setVgrow(listButton, Priority.ALWAYS);
        VBox.setVgrow(randomButton, Priority.ALWAYS);
        VBox.setVgrow(closeButton, Priority.ALWAYS);

        root.getChildren().addAll(addButton, listButton, randomButton, closeButton);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Ülesannete Haldamine");
        primaryStage.show();

        File file = new File("toDo.txt");
        if (file.exists()) {
            ülesanded = loeÜlesanded("toDo.txt");
        } else {
            file.createNewFile(); // Loome uue faili, kui see ei eksisteeri
        }
    }


    private static void näitaTeavitust(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.showAndWait();
    }

    public static Ülesanne lisaUusÜlesanne() {
        Dialog<Ülesanne> dialog = new Dialog<>();
        dialog.setTitle("Lisa uus ülesanne");
        dialog.setHeaderText("Sisesta ülesande info");

        Label typeLabel = new Label("Tüüp:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Kontrolltöö", "Rühmatöö", "Praktikum", "Kodutöö");
        typeComboBox.setValue("Kontrolltöö");

        Label dateLabel = new Label("Tähtaeg (yyyy-mm-dd):");
        TextField dateField = new TextField();
        Label subjectLabel = new Label("Aine:");
        TextField subjectField = new TextField();

        VBox vbox = new VBox(10, typeLabel, typeComboBox, dateLabel, dateField, subjectLabel, subjectField);
        vbox.setAlignment(Pos.CENTER_LEFT);

        dialog.getDialogPane().setContent(vbox);

        ButtonType confirmButtonType = new ButtonType("Lisa", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                String type = typeComboBox.getValue();
                String date = dateField.getText();
                String subject = subjectField.getText();
                try {
                    LocalDate deadline = LocalDate.parse(date);
                    return createÜlesanne(type, deadline, subject);
                } catch (DateTimeParseException e) {
                    näitaTeavitust(Alert.AlertType.ERROR, "Vale kuupäeva formaat! Palun sisestage kuupäev kujul yyyy-mm-dd.");
                    return null;
                }
            }
            return null;
        });

        Optional<Ülesanne> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private static Ülesanne createÜlesanne(String type, LocalDate deadline, String subject) {
        switch (type.toLowerCase()) {
            case "kontrolltöö":
                return new Kontrolltöö(subject, deadline);
            case "praktikum":
                return new Praktikum(subject, deadline);
            case "rühmatöö":
                TextInputDialog groupDialog = new TextInputDialog();
                groupDialog.setTitle("Rühmatöö");
                groupDialog.setHeaderText("Sisesta rühmakaaslased:");
                return groupDialog.showAndWait()
                        .map(groupMembers -> new Rühmatöö(subject, deadline, groupMembers))
                        .orElse(null);
            case "kodutöö":
                return new Kodutöö(subject, deadline);
            default:
                return null;
        }
    }

    public static List<Ülesanne> loeÜlesanded(String failinimi) throws IOException {
        List<Ülesanne> ülesanded = new ArrayList<>();
        List<String> info = Files.readAllLines(Paths.get(failinimi), StandardCharsets.UTF_8);
        for (String rida : info) {
            String[] osad = rida.split(";");
            String tüüp = osad[0];
            String aine = osad[2];
            LocalDate tähtaeg = LocalDate.parse(osad[1]);
            if (tähtaeg.isBefore(LocalDate.now())) {
                continue;
            }

            switch (tüüp) {
                case "kontrolltöö":
                    ülesanded.add(new Kontrolltöö(aine, tähtaeg));
                    break;
                case "rühmatöö":
                    ülesanded.add(new Rühmatöö(aine, tähtaeg, osad[3]));
                    break;
                case "praktikum":
                    ülesanded.add(new Praktikum(aine, tähtaeg));
                    break;
                case "kodutöö":
                    ülesanded.add(new Kodutöö(aine, tähtaeg));
                    break;
            }
        }
        return ülesanded;
    }


    public static void loeFaili(List<Ülesanne> ülesanded) throws IOException {
        String fail = "toDo.txt";

        try (FileWriter kirjutaja = new FileWriter(fail)) {
            for (Ülesanne ülesanne : ülesanded) {
                String[] rida = ülesanne.toString().split(" ");
                String tüüp = rida[0];
                String aine = ülesanne.getAine();
                LocalDate tähtaeg = ülesanne.getTähtaeg();
                if (ülesanne instanceof Rühmatöö) {
                    String kaaslased = ((Rühmatöö) ülesanne).getKaaslased();
                    kirjutaja.write(tüüp.toLowerCase() + ";" + tähtaeg + ";" + aine + ";" + kaaslased + "\n");
                } else {
                    kirjutaja.write(tüüp.toLowerCase() + ";" + tähtaeg + ";" + aine + "\n");
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
