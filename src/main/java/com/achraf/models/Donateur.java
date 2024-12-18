package com.achraf.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Donateur {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nom;
    private final SimpleStringProperty email;
    private final SimpleDoubleProperty montantDonne;

    public Donateur(int id, String nom, String email, double montantDonne) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.email = new SimpleStringProperty(email);
        this.montantDonne = new SimpleDoubleProperty(montantDonne);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public double getMontantDonne() {
        return montantDonne.get();
    }

    public SimpleDoubleProperty montantDonneProperty() {
        return montantDonne;
    }

    public String getName() {
         return nom.get();
    }
}
