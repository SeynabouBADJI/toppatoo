package com.projet.toppatoo.model;

public enum MesureType {
    GLYCEMIE("g/L", "Glycémie"),
    TENSION_ARTERIELLE("mmHg", "Tension artérielle"),
    TEMPERATURE("°C", "Température"),
    POIDS("kg", "Poids"),
    FREQUENCE_CARDIAQUE("bpm", "Fréquence cardiaque");

    private final String unite;
    private final String label;

    MesureType(String unite, String label) {
        this.unite = unite;
        this.label = label;
    }

    public String getUnite() { return unite; }
    public String getLabel() { return label; }
}