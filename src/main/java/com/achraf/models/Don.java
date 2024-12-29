package com.achraf.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Don {
    private final IntegerProperty id;
    private final IntegerProperty donateurId;
    private final DoubleProperty montant;
    private final ObjectProperty<LocalDate> dateDon;

    public Don(int id, int donateurId, double montant, LocalDate dateDon) {
        this.id = new SimpleIntegerProperty(id);
        this.donateurId = new SimpleIntegerProperty(donateurId);
        this.montant = new SimpleDoubleProperty(montant);
        this.dateDon = new SimpleObjectProperty<>(dateDon);
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

    public int getDonateurId() {
        return donateurId.get();
    }

    public IntegerProperty donateurIdProperty() {
        return donateurId;
    }

    public void setDonateurId(int donateurId) {
        this.donateurId.set(donateurId);
    }

    public double getMontant() {
        return montant.get();
    }

    public DoubleProperty montantProperty() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant.set(montant);
    }

    public LocalDate getDateDon() {
        return dateDon.get();
    }

    public ObjectProperty<LocalDate> dateDonProperty() {
        return dateDon;
    }

    public void setDateDon(LocalDate dateDon) {
        this.dateDon.set(dateDon);
    }

}
