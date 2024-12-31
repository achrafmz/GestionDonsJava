package com.achraf.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Statistics {
    private final SimpleStringProperty date;
    private final SimpleDoubleProperty amount;

    public Statistics(String date, Double amount) {
        this.date = new SimpleStringProperty(date);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getDate() {
        return date.get();
    }

    public Double getAmount() {
        return amount.get();
    }
}
