package de_identification_verifier.controller;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

import de_identification_verifier.model.LabellerModel;
import de_identification_verifier.model.Segment;
import de_identification_verifier.view.PictureView;

public class Preload extends Thread {
    PictureView pictureView;
    boolean flag = false;
    ArrayList<Segment> list = new ArrayList<>();
    LabellerModel model;
    int currentNum = 0;
    int loadIndex = 0;
    String root;

    public Preload(PictureView pictureView) {
        this.pictureView = pictureView;
    }

    public void run() {
        int limit = 0;

        while (limit == 0) {
            limit = model.getLabller().size();
            System.out.println("check");
        }

        root = Path.of("").toAbsolutePath().toString() + "\\targetfolder";

        while (true) {
            loadIndex = currentNum + 1;

            while (flag && list.size() < 3) {
                if (loadIndex >= limit) {
                    flag = false;
                    break;
                }
                var name = root + "\\" +model.getLabller().get(loadIndex).getName();
                list.add(new Segment(name, loadIndex, new Image(pictureView.getDisplay(), name)));
                loadIndex++;
                System.out.println("in progress");
            }
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void prepare(int choose) {
        flag = false;
        var targetQuantity = model.getLabller().size();

        if (targetQuantity > 0 && list.size() > 0) {
            if (currentNum < choose) {
                if (choose - currentNum < 4) {
                    pictureView.setPreloadedImage(list.get(choose - currentNum - 1).getImage());
                    list.clear();
                } else {
                    list.clear();
                }
            }
        }

        this.currentNum = choose;
        flag = true;
    }

    public void setController(Controller controller) {
        controller.setLoader(this);
        this.model = controller.getModel();
    }
}
