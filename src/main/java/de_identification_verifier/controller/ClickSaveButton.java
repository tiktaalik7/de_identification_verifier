package de_identification_verifier.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Button;

import de_identification_verifier.model.Labeller;
import de_identification_verifier.model.LabellerModel;
import de_identification_verifier.view.PictureView;
import de_identification_verifier.view.SideView;

public class ClickSaveButton implements SelectionListener {
    private LabellerModel model;
    private SideView sideView;
    private PictureView pictureView;
    private int currentNum;
    private boolean managerMode = false;

    public ClickSaveButton(Button btn, SideView sideView, PictureView pictureView, LabellerModel model, int num) {
        this.model = model;
        this.sideView = sideView;
        this.pictureView = pictureView;
        this.currentNum = num;
        btn.addSelectionListener(this);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (managerMode) {
            model.getLabller().set(currentNum, new Labeller(model.getLabller().get(currentNum).getName(),
                    0, 0, 0, pictureView.getVeils()));

            sideView.text1.setText("0");
            sideView.text2.setText("0");
            sideView.text3.setText("0");
        } else {
            model.getLabller().set(currentNum, new Labeller(model.getLabller().get(currentNum).getName(),
                    sideView.getInfo1(), sideView.getInfo2(), sideView.getInfo3(), pictureView.getVeils()));
        }
        sideView.setColorView(currentNum);

        try {
            File file = new File(model.getFilename());
            FileInputStream fileStream = new FileInputStream(file);

            HSSFWorkbook workbook = new HSSFWorkbook(fileStream);

            HSSFSheet sheet = workbook.getSheetAt(0);

            Labeller labeller = model.getLabller().get(currentNum);
            sheet.getRow(currentNum + 1).createCell(1).setCellValue(labeller.getInfo1());
            sheet.getRow(currentNum + 1).createCell(2).setCellValue(labeller.getInfo2());
            sheet.getRow(currentNum + 1).createCell(3).setCellValue(labeller.getInfo3());
            sheet.getRow(currentNum + 1).createCell(4).setCellValue(labeller.getVeils());

            FileOutputStream outStream = new FileOutputStream(model.getFilename());
            workbook.write(outStream);

            workbook.close();
            fileStream.close();
            outStream.close();

            // 블러처리 및 이미지 저장
            // var path = makeDir(sheet.getRow(currentNum)
            // for (int i = 0; i < veilList.size(); i++) {
            // Veil veil = veilList.get(i);
            // Rect rect = new Rect((int) veil.x, (int) veil.y, (int) veil.width, (int)
            // veil.height);
            // Imgproc.GaussianBlur(new Mat(src, rect), new Mat(src, rect), new Size(23,
            // 23), 30);
            // }
            // Imgcodecs.imwrite(path, src);

            // 블러 없이 이미지 저장
            if (!(sideView.text1.getText().equals("0") &&
                    sideView.text2.getText().equals("0") &&
                    sideView.text3.getText().equals("0"))) {
                var path = model.getFoldername();
                var paths = path.split("\\\\");

                path = "";

                for (int i = 0; i < paths.length - 1; i++) {
                    path += paths[i];
                    if (i != paths.length - 2) {
                        path += "\\";
                    }
                }

                path = path + sheet.getRow(currentNum + 1).getCell(0).getStringCellValue();
                var picture = pictureView.getPicture();
                ImageLoader loader = new ImageLoader();
                GC gc = new GC(picture);
                int adjustX = (picture.getBounds().width -
                        picture.getImage().getBounds().width) / 2;
                int adjustY = (picture.getBounds().height -
                        picture.getImage().getBounds().height) / 2;
                gc.copyArea(picture.getImage(), adjustX, adjustY);
                loader.data = new ImageData[] { picture.getImage().getImageData() };
                loader.save(makeDir(path), SWT.IMAGE_PNG);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {

    }

    public void setInfo(int num, LabellerModel model) {
        currentNum = num;
        this.model = model;
    }

    public void setManagerModeTrue() {
        this.managerMode = true;
    }

    public void setManagerModeFalse() {
        this.managerMode = false;
    }

    public boolean getManagerMode() {
        return managerMode;
    }

    private String makeDir(String path) {
        var paths = path.split("\\\\");
        String result = "";

        for (int i = 0; i < paths.length - 1; i++) {
            result += paths[i];
            result += "\\";
        }

        result += "IIC_Anonymized_Result\\";

        result += paths[paths.length - 1];

        return result;
    }
}