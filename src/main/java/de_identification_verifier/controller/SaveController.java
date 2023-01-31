package de_identification_verifier.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import de_identification_verifier.model.Labeller;
import de_identification_verifier.model.LabellerModel;
import de_identification_verifier.view.PictureView;
import de_identification_verifier.view.SideView;

public class SaveController {
    SideView sideView;

    public SaveController(SideView sideView) {
        this.sideView = sideView;
    }

    public void save(LabellerModel model, PictureView pictureView, int currentNum) {
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
        }
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