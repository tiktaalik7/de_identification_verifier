package de_identification_verifier.model;

public class Labeller {
    private String name;
    private int info1, info2, info3;
    private String veils;

    public Labeller() {

    }

    public Labeller(String name) {
        this.name = name;
        this.info1 = -1;
        this.info2 = -1;
        this.info3 = -1;
        this.veils = "n";
    }

    public Labeller(String name, int info1, int info2, int info3, String veils) {
        this.name = name;
        this.info1 = info1;
        this.info2 = info2;
        this.info3 = info3;
        this.veils = veils;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInfo(int a1, int a2, int a3, String veils) {
        this.info1 = a1;
        this.info2 = a2;
        this.info3 = a3;
        this.veils = veils;
    }

    public int getInfo1() {
        return info1;
    }

    public int getInfo2() {
        return info2;
    }

    public int getInfo3() {
        return info3;
    }

    public void setVeils(String veils) {
        this.veils = veils;
    }

    public String getVeils() {
        return veils;
    }
}