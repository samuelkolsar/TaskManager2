package com.example.taskmanager2;

import java.time.LocalDate;

public class Kontrolltöö extends Ülesanne {
    public Kontrolltöö(String aine, LocalDate tähtaeg) {
        super(aine, tähtaeg);
    }

    @Override
    public String toString() {
        return "Kontrolltöö aines " + getAine() + ", tähtaeg: " + getTähtaeg();
    }
}
