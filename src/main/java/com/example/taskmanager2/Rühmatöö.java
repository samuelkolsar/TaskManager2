package com.example.taskmanager2;

import java.time.LocalDate;

public class Rühmatöö extends Ülesanne {
    String kaaslased;
    public Rühmatöö(String aine, LocalDate tähtaeg, String kaaslased) {
        super(aine, tähtaeg);
        this.kaaslased = kaaslased;
    }

    @Override
    public String toString() {
        return "Rühmatöö aines " + getAine() + " koos tudengi(te)ga " + kaaslased + ". tähtaeg: " + getTähtaeg();
    }

    public String getKaaslased() {
        return kaaslased;
    }
}

