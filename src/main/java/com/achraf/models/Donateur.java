package com.achraf.models;

import javafx.beans.property.*;

public class Donateur {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final DoubleProperty montantDonne;

    public Donateur(int id, String name, String email, double montantDonne) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.montantDonne = new SimpleDoubleProperty(montantDonne);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public double getMontantDonne() {
        return montantDonne.get();
    }

    public DoubleProperty montantDonneProperty() {
        return montantDonne;
    }

    public void setMontantDonne(double montantDonne) {
        this.montantDonne.set(montantDonne);
    }
}
