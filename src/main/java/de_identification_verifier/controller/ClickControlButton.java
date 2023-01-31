package de_identification_verifier.controller;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import de_identification_verifier.view.PictureView;

public class ClickControlButton implements SelectionListener {
    private PictureView pictureView;
    private Button moveBtn;
    private Button resizeBtn;
    private int flag;

    public ClickControlButton(Button moveBtn, Button resizeBtn, int flag, PictureView pictureView) {
        this.pictureView = pictureView;
        this.moveBtn = moveBtn;
        this.resizeBtn = resizeBtn;
        this.flag = flag;

        moveBtn.addSelectionListener(this);
        resizeBtn.addSelectionListener(this);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        // 버튼이 교차할 때
        if ((moveBtn.getSelection()) && (resizeBtn.getSelection())) {
            // 이동 -> 조절
            if (flag == 1) {
                flag = 0;
                pictureView.setDragMode(true, false);
                resizeBtn.setSelection(true);
                moveBtn.setSelection(false);
            }
            // 조절 -> 이동
            else {
                flag = 1;
                pictureView.setDragMode(false, true);
                moveBtn.setSelection(true);
                resizeBtn.setSelection(false);
            }
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }
}
