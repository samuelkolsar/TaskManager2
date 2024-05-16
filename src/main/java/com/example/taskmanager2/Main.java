package com.example.taskmanager2;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
        VBox root = new VBox(10); // 10px padding between buttons
        root.setAlignment(Pos.CENTER); // Center align all children of VBox

        Scene scene = new Scene(root, 400, 300);

        Button addButton = new Button("Lisa ülesanne");
        Button listButton = new Button("Väljasta ülesanded ekraanile");
        Button randomButton = new Button("Vali suvaline ülesanne");
        Button closeButton = new Button("Sulge programm");

        addButton.setOnAction(e -> {
            Ülesanne uusÜlesanne = lisaUusÜlesanne();
            if (uusÜlesanne != null) {
                ülesanded.add(uusÜlesanne);
                showAlert(Alert.AlertType.INFORMATION, "Ülesanne lisatud!");
            }
        });

        listButton.setOnAction(e -> {
            Collections.sort(ülesanded);
            StringBuilder sb = new StringBuilder();
            for (Ülesanne ülesanne : ülesanded) {
                sb.append(ülesanne).append("\n");
            }
            showAlert(Alert.AlertType.INFORMATION, sb.toString());
        });

        randomButton.setOnAction(e -> {
            if (!ülesanded.isEmpty()) {
                Ülesanne suvaline = ülesanded.get((int) (Math.random() * ülesanded.size()));
                showAlert(Alert.AlertType.INFORMATION, suvaline.toString());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Ülesannete nimekiri on tühi.");
            }
        });

        closeButton.setOnAction(e -> {
            try {
                loeFaili(ülesanded);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Tekkis viga failisse salvestamisel!");
            }
            primaryStage.close();
        });

        // Set VBox properties to center buttons
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

    private static void showAlert(Alert.AlertType alertType, String message) {
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
        typeComboBox.setValue("Kontrolltöö"); // Default value

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
                    showAlert(Alert.AlertType.INFORMATION, "Vale kuupäeva formaat! Palun sisestage kuupäev kujul yyyy-mm-dd.");
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
