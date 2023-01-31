package de_identification_verifier.model;

public class Veil {
    public int shape;
    public double x;
    public double y;
    public double width;
    public double height;
    public int angle;

    public Veil() {}

    public Veil(int shape, double x, double y, double width, double height, int angle) {
        this.shape = shape;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
    }
}
