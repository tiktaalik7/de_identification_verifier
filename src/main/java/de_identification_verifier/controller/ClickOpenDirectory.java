package de_identification_verifier.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

import de_identification_verifier.model.Labeller;
import de_identification_verifier.model.LabellerModel;
import de_identification_verifier.view.MenuView;
import de_identification_verifier.view.SideView;

public class ClickOpenDirectory implements SelectionListener {
    private MenuView menuView;
    private Text text;
    private LabellerModel model;
    private SideView sideView;
    private Preload preload;
    private String path;
    private String leaf = "";

    public ClickOpenDirectory(Button button, Text text, LabellerModel model, MenuView menuView, SideView sideView,
            Preload preload) {
        this.menuView = menuView;
        this.text = text;
        this.model = model;
        this.sideView = sideView;
        this.preload = preload;

        button.addSelectionListener(this);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        DirectoryDialog directoryDialog = new DirectoryDialog(((Button) e.getSource()).getShell(), SWT.NONE);

        String standPath = System.getProperty("user.dir");

        directoryDialog.setFilterPath(standPath + "\\targetfolder");
        directoryDialog.setText("데이터셋 디렉토리 선택");
        path = directoryDialog.open();

        // 선택하지 않을 경우
        if (path == null || path.length() == 0) {
            return;
        }

        // 잘못 선택한 경우
        var fileName = path.split("\\\\");
        if (fileName[fileName.length - 1].equals("IIC_Anonymized_Result")) {
            path = "";
            for (int i = 0; i < fileName.length - 1; i++) {
                path += fileName[i];
                if (i != fileName.length - 2) {
                    path += "\\";
                }
            }
        }

        var targetfolder = "targetfolder";

        if (path.contains(targetfolder)) {
            leaf = path.split(targetfolder)[1];
        } else {
            return;
        }

        try {
            var paths = path.split("\\\\");
            File file = new File(path + "\\" + paths[paths.length - 1] + ".xls");
            File folder = new File(path + "\\IIC_Anonymized_Result");

            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 파일이 존재하는 경우
            if (file.exists()) {
                setLabellerFromFile(file);
            }
            // 파일이 존재하지 않는 경우
            else {
                File dir = new File(path);
                model.getLabller().clear();
                dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String fileName) {
                        if (fileName.endsWith("jpg") | fileName.endsWith("png")) {
                            Labeller labeller = new Labeller(leaf + "\\" + fileName);
                            model.setLabeller(labeller);
                        }
                        return true;
                    }
                });

                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet();

                Row idxRow = sheet.createRow(0);

                Cell cell_Name = idxRow.createCell(0);
                cell_Name.setCellValue("파일명");

                Cell cell_Q1 = idxRow.createCell(1);
                cell_Q1.setCellValue("비식별화 되지 않은 얼굴 수");

                Cell cell_Q2 = idxRow.createCell(2);
                cell_Q2.setCellValue("비식별화 되지 않은 차량번호 수");

                Cell cell_Q3 = idxRow.createCell(3);
                cell_Q3.setCellValue("비식별화 되지 않은 전화번호 수");

                for (int i = 0; i < model.getLabller().size(); i++) {
                    Row row = sheet.createRow(i + 1);

                    Cell cell = row.createCell(0);
                    var cmodel = model.getLabller().get(i);
                    cell.setCellValue(cmodel.getName());

                    for (int j = 1; j <= 3; j++) {
                        cell = row.createCell(j);
                        cell.setCellValue(-1);
                    }

                    cell = row.createCell(4);
                    cell.setCellValue("n");
                }

                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                workbook.close();
            }

        } catch (Exception exception) {

        } finally {
            menuView.setVerifyText(true);
            text.setText(path.split("\\\\")[path.split("\\\\").length - 1]);
            menuView.setVerifyText(false);
            var paths = path.split("\\\\");
            model.setFilename(path + "\\" + paths[paths.length - 1] + ".xls");
            model.setFoldername(path);
            sideView.setLabeller(model);
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
    }

    public String getPath() {
        return path;
    }

    private void setLabellerFromFile(File file) {
        try {
            FileInputStream fileStream = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fileStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            model.getLabller().clear();
            int rows = sheet.getPhysicalNumberOfRows();
            for (int rowN = 1; rowN < rows + 1; rowN++) {
                HSSFRow row = sheet.getRow(rowN);
                if (row != null) {
                    Labeller labeller = new Labeller(row.getCell(0).getStringCellValue());
                    int term1 = (int) row.getCell(1).getNumericCellValue();
                    int term2 = (int) row.getCell(2).getNumericCellValue();
                    int term3 = (int) row.getCell(3).getNumericCellValue();
                    String term4 = row.getCell(4).getStringCellValue();

                    labeller.setInfo(term1, term2, term3, term4);

                    model.getLabller().add(labeller);
                    preload.setFlag(true);
                }
            }
            workbook.close();
        } catch (Exception e) {
        }
    }
}