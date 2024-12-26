package com.achraf.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class HistoriqueDon {
    private final IntegerProperty id;
    private final IntegerProperty donId;
    private final IntegerProperty beneficiaireId;
    private final ObjectProperty<LocalDate> dateAttribution;
    private final DoubleProperty montant;

    public HistoriqueDon(int id, int donId, int beneficiaireId, LocalDate dateAttribution, double montant) {
        this.id = new SimpleIntegerProperty(id);
        this.donId = new SimpleIntegerProperty(donId);
        this.beneficiaireId = new SimpleIntegerProperty(beneficiaireId);
        this.dateAttribution = new SimpleObjectProperty<>(dateAttribution);
        this.montant = new SimpleDoubleProperty(montant);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getDonId() {
        return donId.get();
    }

    public IntegerProperty donIdProperty() {
        return donId;
    }

    public int getBeneficiaireId() {
        return beneficiaireId.get();
    }

    public IntegerProperty beneficiaireIdProperty() {
        return beneficiaireId;
    }

    public LocalDate getDateAttribution() {
        return dateAttribution.get();
    }

    public ObjectProperty<LocalDate> dateAttributionProperty() {
        return dateAttribution;
    }

    public double getMontant() {
        return montant.get();
    }

    public DoubleProperty montantProperty() {
        return montant;
    }
}
