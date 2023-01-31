package de_identification_verifier.model;

import java.util.ArrayList;
import java.util.List;

public class LabellerModel {
    private String filename;
    private String foldername;

    private List<Labeller> labellers = new ArrayList<Labeller>();

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setLabeller(Labeller labeller) {
        labellers.add(labeller);
    }

    public List<Labeller> getLabller() {
        return labellers;
    }
}