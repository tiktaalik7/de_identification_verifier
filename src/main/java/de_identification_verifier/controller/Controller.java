package de_identification_verifier.controller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import de_identification_verifier.model.Labeller;
import de_identification_verifier.model.LabellerModel;
import de_identification_verifier.view.MenuView;
import de_identification_verifier.view.PictureView;
import de_identification_verifier.view.SideView;

public class Controller {
    private MenuView menuView;
    private LabellerModel model;
    private SideView sideView;
    private PictureView pictureView;
    private int currentNum = 0;
    private ClickSaveButton csb;
    private ClickSaveButton cnb;
    private Preload preload;
    private int enterMode = 0;

    public void setModel(LabellerModel model) {
        this.model = model;
    }

    public void setView(MenuView menuView, PictureView pictureView, SideView sideView) {
        this.menuView = menuView;
        this.sideView = sideView;
        this.pictureView = pictureView;
        keypress();
        hiddenKey();
    }

    public void setLoader(Preload preload) {
        this.preload = preload;
    }

    public LabellerModel getModel() {
        return this.model;
    }

    public void setOpenDirecotry(Button btn, Text text) {
        new ClickOpenDirectory(btn, text, model, menuView, sideView, preload);
    }

    public void clickSaveButton(Button btn) {
        csb = new ClickSaveButton(btn, sideView, pictureView, model, currentNum);
    }

    public void clickNextButton(Button btn) {
        cnb = new ClickSaveButton(btn, sideView, pictureView, model, currentNum);
    }

    public void setControlButton(Button moveBtn, Button resizeBtn, int flag) {
        new ClickControlButton(moveBtn, resizeBtn, flag, pictureView);
    }

    public void setShapeButton(Button rectBtn, Button ovalBtn, int flag) {
        new ClickShapeButton(rectBtn, ovalBtn, flag, pictureView);
    }

    public void selectPicture(Widget widget) {
        widget.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (widget.getClass() == Button.class) {
                    if (currentNum >= (model.getLabller().size() - 1))
                        return;
                    currentNum++;
                } else if (widget.getClass() == Table.class) {
                    Table w = (Table) widget;
                    currentNum = w.getSelectionIndex();
                }

                choose(currentNum);
                csb.setInfo(currentNum, model);
                cnb.setInfo(currentNum, model);
            }
        });
    }

    private void hiddenKey() {
        sideView.getParent().getDisplay().addFilter(SWT.KeyDown, event -> {
            switch (event.keyCode) {
                case 'm':
                case 'M':
                case 'ㅡ':
                    if (enterMode == 4) {
                        if (cnb.getManagerMode()) {
                            MessageBox msgBox = new MessageBox(sideView.getShell(),
                                    SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL);
                            msgBox.setText("안내");
                            msgBox.setMessage("0, 0, 0 모드를 해제합니다.");
                            int input = msgBox.open();
                            switch (input) {
                                case SWT.OK:
                                    cnb.setManagerModeFalse();
                                    enterMode = 0;
                                    break;

                                case SWT.CANCEL:
                                    enterMode = 0;
                                    break;

                                default:
                                    break;
                            }
                        } else {
                            MessageBox msgBox = new MessageBox(sideView.getShell(),
                                    SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL);
                            msgBox.setText("안내");
                            msgBox.setMessage("0, 0, 0 모드를 실행합니다.");
                            int input = msgBox.open();
                            switch (input) {
                                case SWT.OK:
                                    cnb.setManagerModeTrue();
                                    enterMode = 0;
                                    break;

                                case SWT.CANCEL:
                                    enterMode = 0;
                                    break;

                                default:
                                    break;
                            }
                        }
                    } else {
                        enterMode++;
                    }
            }
        });
    }

    private void keypress() {
        sideView.getParent().getDisplay().addFilter(SWT.KeyDown, event -> {
            if (sideView.getParent().equals(sideView.getParent().getDisplay().getActiveShell())) {
                if (model.getFilename() == null | model.getFilename() == "")
                    return;

                switch (event.keyCode) {
                    // 다음
                    case 'E':
                    case 'e':
                    case 'ㄷ':
                        if (currentNum >= (model.getLabller().size() - 1))
                            break;

                        currentNum++;
                        choose(currentNum);
                        csb.setInfo(currentNum, model);
                        cnb.setInfo(currentNum, model);
                        sideView.text1.setFocus();
                        break;

                    // 이전
                    case 'Q':
                    case 'q':
                    case 'ㅂ':
                        if (currentNum <= 0)
                            break;

                        currentNum--;
                        choose(currentNum);
                        csb.setInfo(currentNum, model);
                        cnb.setInfo(currentNum, model);
                        sideView.text1.setFocus();
                        break;

                    // 저장
                    case 'W':
                    case 'w':
                    case 'ㅈ':
                        model.getLabller().set(currentNum, new Labeller(model.getLabller().get(currentNum).getName(),
                                sideView.getInfo1(), sideView.getInfo2(), sideView.getInfo3(), pictureView.getVeils()));
                        sideView.setColorView(currentNum);

                        SaveController sc = new SaveController(sideView);
                        sc.save(model, pictureView, currentNum);
                        break;

                    // 초기화
                    case 'R':
                    case 'r':
                    case 'ㄱ':
                        sideView.setReset();

                    default:
                        break;
                }
                ;
            }
        });
    }

    private void choose(int num) {
        Labeller labeller = model.getLabller().get(num);
        preload.prepare(num);
        pictureView.setBasicPicture(labeller.getName(), labeller.getVeils());
        sideView.setInfo(labeller.getInfo1(), labeller.getInfo2(), labeller.getInfo3());
        sideView.setFocus(currentNum);
    }
}