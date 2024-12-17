package com.nesnidal.farm.disasters;

import java.util.Random;

public class Disaster {
    private final Random rand;
    private final double probability;

    private DisasterType disasterType;
    private double multiplier;

    public Disaster(Random random, double probability) {
        this.rand = random;
        this.probability = probability;
    }

    public void generateRandom() {
        setNone();
        if (rand.nextInt(100) < probability * 100) {
            disasterType = DisasterType.values()[1 + rand.nextInt(DisasterType.values().length - 1)];
            multiplier = (10.0 * rand.nextInt(10)) / 100.0;
        }
    }
    public void setNone() {
        disasterType = DisasterType.NONE;
        multiplier = 1.0;
    }

    public DisasterType getDisasterType() {
        return disasterType;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getDescription() {
        switch (disasterType) {
            case MOLD -> {
                return String.format("Tvá pole napadla plíseň! Zničeno %d %% plodin na každém poli", (int)Math.round(((1.0 - multiplier) * 100.0)));
            }
            case FREEZE -> {
                return String.format("Přes noc byl příliš velký mráz! Zničeno %d %% plodin na každém poli", (int)Math.round(((1.0 - multiplier) * 100.0)));
            }
            case DROUGHT -> {
                return String.format("Poslední den byl velmi suchý! Zničeno %d %% plodin na každém poli", (int)Math.round(((1.0 - multiplier) * 100.0)));
            }
            case PEST -> {
                return String.format("Přes tvá pole se přehnali škůdci! Zničeno %d %% plodin na každém poli", (int)Math.round(((1.0 - multiplier) * 100.0)));
            }
        }
        return "";
    }
}
