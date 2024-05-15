package com.example.taskmanager2;

import java.time.LocalDate;

public abstract class Ülesanne implements Comparable<Ülesanne> {
    private String aine;
    private LocalDate tähtaeg;

    public Ülesanne(String aine, LocalDate tähtaeg) {
        this.aine = aine;
        this.tähtaeg = tähtaeg;
    }

    public String getAine() {
        return aine;
    }

    public LocalDate getTähtaeg() {
        return tähtaeg;
    }

    @Override
    public int compareTo(Ülesanne ülesanne) {
        return this.tähtaeg.compareTo(ülesanne.tähtaeg);
    }
}
