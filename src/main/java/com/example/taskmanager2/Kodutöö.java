package com.example.taskmanager2;

import java.time.LocalDate;

public class Kodutöö extends Ülesanne {
    public Kodutöö(String aine, LocalDate tähtaeg) {
        super(aine, tähtaeg);
    }

    @Override
    public String toString() {
        return "Kodutöö aines " + getAine() + ", tähtaeg: " + getTähtaeg();
    }
}

