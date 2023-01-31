package de_identification_verifier.view;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.BorderData;
import org.eclipse.swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de_identification_verifier.controller.Controller;
import de_identification_verifier.controller.Preload;
import de_identification_verifier.model.LabellerModel;

public class ViewMain {

    public ViewMain() {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("비식별화 검증 도구");
        shell.setSize(1350, 800);
        shell.setLayout(new BorderLayout());
        shell.setMinimumSize(700, 700);

        MenuView menuView = new MenuView(shell, SWT.BORDER);
        PictureView pictureView = new PictureView(shell, SWT.RESIZE);
        SideView sideView = new SideView(shell, SWT.NONE);

        BorderData bd = new BorderData(SWT.RIGHT);
        bd.wHint = 270;

        menuView.setLayoutData(new BorderData(SWT.TOP));
        pictureView.setLayoutData(new BorderData(SWT.CENTER));
        sideView.setLayoutData(bd);

        LabellerModel model = new LabellerModel();

        Controller controller = new Controller();
        controller.setView(menuView, pictureView, sideView);
        controller.setModel(model);

        Preload preload = new Preload(pictureView);
        controller.setLoader(preload);

        menuView.setController(controller);
        sideView.setController(controller);
        preload.setController(controller);
        preload.start();

        shell.open();
        shell.layout();

        while (!shell.isDisposed()) {
            if (display.readAndDispatch())
                display.sleep();
        }

        preload.interrupt();
        display.dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        File file = new File("lib\\external\\opencv_java460.dll");
        if (file.exists()) {
            System.load(file.getAbsolutePath());
            new ViewMain();
        } else System.out.println("opencv.dll 파일이 없습니다.");
    }
}