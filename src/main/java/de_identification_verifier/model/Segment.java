package de_identification_verifier.model;

import org.eclipse.swt.graphics.Image;

public class Segment {
    String name;
    int num;
    Image img;

    public Segment(String name, int num, Image img) {
        this.name = name;
        this.num = num;
        this.img = img;
    }

    public Image getImage() {
        return this.img;
    }
}