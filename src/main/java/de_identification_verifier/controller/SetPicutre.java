package de_identification_verifier.controller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;

import de_identification_verifier.model.Labeller;
import de_identification_verifier.model.LabellerModel;
import de_identification_verifier.view.PictureView;
import de_identification_verifier.view.SideView;

public class SetPicutre implements Listener {
    private LabellerModel model;
    private PictureView pictureView;
    private SideView sideView;
    private Widget widget;
    private int currentNum;

    public SetPicutre(Widget widget, PictureView pictureView, SideView sideView, LabellerModel model, int num) {
        this.model = model;
        this.widget = widget;
        this.pictureView = pictureView;
        this.sideView = sideView;
        currentNum = num;

        widget.addListener(SWT.Selection, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (widget.getClass() == Button.class) {
            currentNum++;
        } else if (widget.getClass() == Table.class) {
            Table w = (Table) widget;
            currentNum = w.getSelectionIndex();
        }
        setPicture(currentNum);
    }

    private void setPicture(int num) {
        Labeller labeller = model.getLabller().get(num);
        pictureView.setBasicPicture(labeller.getName(), labeller.getVeils());
        sideView.setInfo(labeller.getInfo1(), labeller.getInfo2(), labeller.getInfo3());
    }

    public int getNum() {
        return currentNum;
    }
}
