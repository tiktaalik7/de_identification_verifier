package de_identification_verifier.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de_identification_verifier.controller.Controller;
import de_identification_verifier.model.LabellerModel;

public class SideView extends Composite {
    private Table pictureTable;
    public Text text1;
    public Text text2;
    public Text text3;
    private Button saveBtn;
    public Button nextBtn;
    private Button resetBtn;
    private Group questionGroup;

    public SideView(Composite parent, int style) {
        super(parent, style);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        this.setLayout(gridLayout);
        this.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.horizontalSpan = 4;

        // Picture File
        pictureTable = new Table(this, SWT.BORDER);
        pictureTable.setLayout(gridLayout);
        pictureTable.setLayoutData(gridData);

        // Question List
        questionGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
        questionGroup.setText("Question List");
        questionGroup.setLayout(new RowLayout());
        questionGroup.setLayoutData(gridData);

        new Label(questionGroup, SWT.NONE).setText("1. 비식별화되지 않은 얼굴 수 ");
        text1 = new Text(questionGroup, SWT.BORDER);
        text1.setText("");
        text1.setLayoutData(new RowData(30, 12));
        setOnlyNumber(text1);

        new Label(questionGroup, SWT.NONE).setText("2. 차량 번호가 보이는 차량 수 ");
        text2 = new Text(questionGroup, SWT.BORDER);
        text2.setText("");
        text2.setLayoutData(new RowData(30, 12));
        setOnlyNumber(text2);

        new Label(questionGroup, SWT.NONE).setText("3. 전화번호가 보이는 개수(광고 제외) ");
        text3 = new Text(questionGroup, SWT.BORDER);
        text3.setText("");
        text3.setLayoutData(new RowData(30, 12));
        setOnlyNumber(text3);

        Font font = new Font(getDisplay(), new FontData("SansSerif", 12, SWT.BOLD));

        saveBtn = new Button(this, SWT.PUSH);
        saveBtn.setText("Save");
        saveBtn.setFont(font);
        nextBtn = new Button(this, SWT.PUSH);
        nextBtn.setText("Next");
        nextBtn.setFont(font);
        resetBtn = new Button(this, SWT.PUSH);
        resetBtn.setText("Reset");
        resetBtn.setFont(font);
        resetBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReset();
            }
        });
    }

    private void setOnlyNumber(Text text) {
        text.addListener(SWT.Verify, new Listener() {
            @Override
            public void handleEvent(Event e) {
                String string = e.text;
                char[] chars = new char[string.length()];

                string.getChars(0, chars.length, chars, 0);

                for (int i = 0; i < chars.length; i++) {
                    if (!('0' <= chars[i] && chars[i] <= '9')) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });
    }

    public void setController(Controller controller) {
        controller.clickSaveButton(saveBtn);
        controller.selectPicture(pictureTable);

        controller.clickNextButton(nextBtn);
        controller.selectPicture(nextBtn);

        controller.selectPicture(resetBtn);
    }

    public void setColorView(int offset) {
        TableItem item = pictureTable.getItem(offset);

        String infoA = text1.getText();
        String infoB = text2.getText();
        String infoC = text3.getText();

        if (infoA == "" | infoB == "" | infoC == "")
            item.setForeground(new Color(0, 0, 0)); // 검정
        else if (infoA.equals("0") && infoB.equals("0") && infoC.equals("0"))
            item.setForeground(new Color(0, 0, 255)); // 초록
        else
            item.setForeground(new Color(255, 0, 0)); // 빨강
    }

    public void setLabeller(LabellerModel model) {
        pictureTable.removeAll();
        for (var piece : model.getLabller()) {
            String name = piece.getName();
            TableItem item = new TableItem(pictureTable, SWT.NONE);
            item.setText(name.substring(name.lastIndexOf("\\") + 1));

            if (piece.getInfo1() == -1 | piece.getInfo2() == -1 | piece.getInfo3() == -1)
                item.setForeground(new Color(0, 0, 0)); // 검정
            else if (piece.getInfo1() == 0 && piece.getInfo2() == 0 && piece.getInfo3() == 0)
                item.setForeground(new Color(0, 0, 255)); // 초록
            else
                item.setForeground(new Color(255, 0, 0)); // 빨강
        }
    }

    public void setInfo(int num1, int num2, int num3) {
        text1.setText(num1 != -1 ? Integer.toString(num1) : "");
        text2.setText(num2 != -1 ? Integer.toString(num2) : "");
        text3.setText(num3 != -1 ? Integer.toString(num3) : "");
    }

    public void setFocus(int num) {
        pictureTable.setSelection(num);
        pictureTable.setFocus();
    }

    public String getFileName() {
        return pictureTable.getSelection()[pictureTable.getSelectionIndex()].getText();
    }

    public int getInfo1() {
        return text1.getText() != "" ? Integer.parseInt(text1.getText()) : -1;
    }

    public int getInfo2() {
        return text2.getText() != "" ? Integer.parseInt(text2.getText()) : -1;
    }

    public int getInfo3() {
        return text3.getText() != "" ? Integer.parseInt(text3.getText()) : -1;
    }

    public void setReset() {
        text1.setText("0");
        text2.setText("0");
        text3.setText("0");
    }
}