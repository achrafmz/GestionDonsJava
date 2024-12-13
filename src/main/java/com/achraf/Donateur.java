package com.achraf;

public class Donateur {
    private String nom;
    private String email;
    private double montantDonne;

    // Constructeur
    public Donateur(String nom, String email, double montantDonne) {
        this.nom = nom;
        this.email = email;
        this.montantDonne = montantDonne;
    }

    // Getters et setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getMontantDonne() {
        return montantDonne;
    }

    public void setMontantDonne(double montantDonne) {
        this.montantDonne = montantDonne;
    }
}
