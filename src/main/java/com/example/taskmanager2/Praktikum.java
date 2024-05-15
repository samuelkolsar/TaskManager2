package com.example.taskmanager2;

import java.time.LocalDate;

public class Praktikum extends Ülesanne {
    public Praktikum(String aine, LocalDate tähtaeg) {
        super(aine, tähtaeg);
    }

    @Override
    public String toString() {
        return "Praktikum aines " + getAine() + ", tähtaeg: " + getTähtaeg();
    }
}
