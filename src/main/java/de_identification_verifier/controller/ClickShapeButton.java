package de_identification_verifier.controller;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import de_identification_verifier.view.PictureView;

public class ClickShapeButton implements SelectionListener {
    private PictureView pictureView;
    private Button rectBtn;
    private Button ovalBtn;
    private int flag;

    public ClickShapeButton(Button rectBtn, Button ovalBtn, int flag, PictureView pictureView) {
        this.pictureView = pictureView;
        this.rectBtn = rectBtn;
        this.ovalBtn = ovalBtn;
        this.flag = flag;

        rectBtn.addSelectionListener(this);
        ovalBtn.addSelectionListener(this);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        // 버튼이 교차할 때
        if ((rectBtn.getSelection()) && (ovalBtn.getSelection())) {
            // 사각형 -> 타원
            if (flag == 1) {
                flag = 0;
                pictureView.setShape(0);
                ovalBtn.setSelection(true);
                rectBtn.setSelection(false);
            }
            // 타원 -> 사각형
            else {
                flag = 1;
                pictureView.setShape(1);
                rectBtn.setSelection(true);
                ovalBtn.setSelection(false);
            }
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
}
