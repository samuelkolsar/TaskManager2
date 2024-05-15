package com.example.taskmanager2;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static Ülesanne lisaUusÜlesanne() {
        String sisestus = JOptionPane.showInputDialog(null, "Sisesta ülesande ülesanne formaadis (tüüp,tähtaeg,aine)\nSisesta tähtaeg formaadis yyyy-mm-dd\nVõimalikud ülesande tüübid on: kontrolltöö, rühmatöö, praktikum, kodutöö", "Sisestamine", JOptionPane.QUESTION_MESSAGE);
        String[] info = sisestus.split(",");
        if (info.length != 3) {
            return null;
        }
        String tüüp = info[0];
        LocalDate tähtaeg = LocalDate.parse(info[1]);
        String aine = info[2];

        if (tüüp.toLowerCase().equals("kontrolltöö")) {
            Kontrolltöö kt = new Kontrolltöö(aine, tähtaeg);
            return kt;
        } else if (tüüp.toLowerCase().equals("praktikum")) {
            Praktikum praktikum = new Praktikum(aine, tähtaeg);
            return praktikum;
        } else if (tüüp.toLowerCase().equals("rühmatöö")) {
            String kaaslased = JOptionPane.showInputDialog(null, "Sisesta rühmakaaslased", "Sisestamine", JOptionPane.QUESTION_MESSAGE);
            Rühmatöö rühmatöö = new Rühmatöö(aine, tähtaeg, kaaslased);
            return rühmatöö;
        } else if (tüüp.toLowerCase().equals("kodutöö")) {
            Kodutöö kodutöö = new Kodutöö(aine, tähtaeg);
            return kodutöö;
        }
        return null;
    }

    public static List<Ülesanne> loeÜlesanded(String failinimi) throws Exception {
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

            if (tüüp.equals("kontrolltöö")) {
                ülesanded.add(new Kontrolltöö(aine, tähtaeg));
            } else if (tüüp.equals("rühmatöö")) {
                ülesanded.add(new Rühmatöö(aine, tähtaeg, osad[3]));
            } else if (tüüp.equals("praktikum")) {
                ülesanded.add(new Praktikum(aine, tähtaeg));
            } else if (tüüp.equals("kodutöö")) {
                ülesanded.add(new Kodutöö(aine, tähtaeg));
            }
        }
        return ülesanded;
    }
    public static void loeFaili(List<Ülesanne> ülesanded) throws IOException {
        String fail = "toDo.txt";

        try (FileWriter kirjutaja = new FileWriter(fail))  {
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

    public static void main(String[] args) throws Exception {
        List<Ülesanne> ülesanded = loeÜlesanded("toDo.txt");
        while (true) {
            String tegevus = JOptionPane.showInputDialog(null, "1-lisa ülesanne \n2-Väljasta ülesanded ekraanile\n3-Vali suvaline ülesanne\n4-Sulge programm", "Sisesta tegevus", JOptionPane.QUESTION_MESSAGE);
            if (tegevus == null){
                System.out.println("\nSulgen programmi...");
                loeFaili(ülesanded);
                break;
            }
            if (tegevus.equals("4")) {
                System.out.println("\nSulgen programmi...");
                loeFaili(ülesanded);
                break;
            }
            if (tegevus.equals("1")) {
                Ülesanne lisatav = lisaUusÜlesanne();
                if (lisatav == null) {
                    System.out.println("Vale formaat, ülesannet ei lisatud!");
                } else {
                    ülesanded.add(lisatav);
                    System.out.println("\nülesanne lisatud!");
                }
            }
            if (tegevus.equals("2")) {
                Collections.sort(ülesanded);
                System.out.println("\nÜlesannete nimekiri:\n");
                for (Ülesanne ülesanne : ülesanded) {
                    System.out.println(ülesanne);
                }
            }
            if (tegevus.equals("3")) {
                System.out.println("\nValitud ülesanne:");
                System.out.println(ülesanded.get((int) (Math.random() * ülesanded.size())));
            }
        }
    }
}