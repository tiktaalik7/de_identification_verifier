package de_identification_verifier.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de_identification_verifier.controller.Controller;

public class MenuView extends Composite {
    private Button folderBtn;
    private Button moveBtn;
    private Button resizeBtn;
    private Button rectBtn;
    private Button ovalBtn;
    private Text folderText;
    private boolean isVerfiy = false;
    private int shapeFlag = 1;
    private int controlFlag = 1;

    public MenuView(Composite parent, int style) {
        super(parent, style);
        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = true;
        rowLayout.justify = false;
        setLayout(rowLayout);
        setBackground(new Color(255, 255, 255));

        folderText = new Text(this, SWT.BORDER);
        folderText.setLayoutData(new RowData(500, 30));
        folderText.addListener(SWT.Verify, new Listener() {
            @Override
            public void handleEvent(Event e) {
                String string = e.text;
                char[] chars = new char[string.length()];
                string.getChars(0, chars.length, chars, 0);

                if (!isVerfiy) {
                    for (int i = 0; i < chars.length; i++) {
                        e.doit = false;
                    }
                }
            }
        });
        Font font_text = new Font(getDisplay(), new FontData("SansSerif", 20, SWT.NORMAL));
        folderText.setFont(font_text);

        Font font = new Font(getDisplay(), new FontData("SansSerif", 15, SWT.BOLD));

        folderBtn = new Button(this, SWT.NONE);
        folderBtn.setLayoutData(new RowData(150, 36));
        folderBtn.setText("파일 선택");
        folderBtn.setFont(font);

        Composite controlComposite = new Composite(this, SWT.BORDER);
        controlComposite.setLayout(rowLayout);

        Composite shapeComposite = new Composite(this, SWT.BORDER);
        shapeComposite.setLayout(rowLayout);

        moveBtn = new Button(controlComposite, SWT.TOGGLE);
        moveBtn.setLayoutData(new RowData(50, 25));
        moveBtn.setSelection(true);
        moveBtn.setText("이동");
        moveBtn.setFont(font);

        resizeBtn = new Button(controlComposite, SWT.TOGGLE);
        resizeBtn.setLayoutData(new RowData(50, 25));
        resizeBtn.setSelection(false);
        resizeBtn.setText("조절");
        resizeBtn.setFont(font);

        Font shapeFont = new Font(getDisplay(), new FontData("SansSerif", 20, SWT.BOLD));

        rectBtn = new Button(shapeComposite, SWT.TOGGLE);
        rectBtn.setLayoutData(new RowData(50, 25));
        rectBtn.setSelection(true);
        rectBtn.setText("ㅁ");
        rectBtn.setFont(shapeFont);

        ovalBtn = new Button(shapeComposite, SWT.TOGGLE);
        ovalBtn.setLayoutData(new RowData(50, 25));
        ovalBtn.setSelection(false);
        ovalBtn.setText("ㅇ");
        ovalBtn.setFont(shapeFont);
    }

    public void setController(Controller controller) {
        controller.setOpenDirecotry(folderBtn, folderText);
        controller.setControlButton(moveBtn, resizeBtn, controlFlag);
        controller.setShapeButton(rectBtn, ovalBtn, shapeFlag);
    }

    public void setVerifyText(boolean isVerify) {
        this.isVerfiy = isVerify;
    }
}
