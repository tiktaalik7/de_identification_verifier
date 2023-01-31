package de_identification_verifier.view;

import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de_identification_verifier.model.Veil;

public class PictureView extends Composite {
    private Label picture;
    private String path;
    private GC gc;
    private ArrayList<Veil> mVeils = new ArrayList<>();
    private int focusedVeil;
    private Point mouseDown = new Point(0, 0);
    private boolean isDrag = false;
    private boolean isMove = true;
    private boolean isResize = false;
    private int shape = 1;
    private Image preloadedImage;

    public PictureView(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        picture = new Label(this, SWT.RESIZE);
        gc = new GC(picture);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        picture.setBackground(new Color(255, 255, 255));
        picture.setLayoutData(gridData);

        this.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (path != null) {
                    picture.setImage(makeFit(path, picture.getShell()));
                    picture.requestLayout();
                    drawVeils();
                }
            }
        });

        picture.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if (picture.getImage() != null) {
                    if (!isOnImage(e, picture.getImage().getBounds(), picture.getBounds())) {
                        return;
                    }

                    // 우클릭
                    if (e.button == 3) {
                        Menu menu = new Menu(picture);

                        MenuItem drawOval = new MenuItem(menu, SWT.CASCADE);
                        drawOval.setText("타원 그리기");
                        Menu ovalMenu = new Menu(menu);
                        drawOval.setMenu(ovalMenu);
                        MenuItem smallOval = new MenuItem(ovalMenu, SWT.NONE);
                        smallOval.setText("작은 타원");
                        MenuItem bigOval = new MenuItem(ovalMenu, SWT.NONE);
                        bigOval.setText("큰 타원");

                        MenuItem drawRect = new MenuItem(menu, SWT.CASCADE);
                        drawRect.setText("사각형 그리기");
                        Menu rectMenu = new Menu(menu);
                        drawRect.setMenu(rectMenu);
                        MenuItem smallRect = new MenuItem(rectMenu, SWT.NONE);
                        smallRect.setText("작은 사각형");
                        MenuItem bigRect = new MenuItem(rectMenu, SWT.NONE);
                        bigRect.setText("큰 사각형");

                        MenuItem delete_Item = new MenuItem(menu, SWT.NONE);
                        delete_Item.setText("지우기");

                        delete_Item.addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent evnet) {
                                var index = whichVeil(e, picture.getImage().getBounds(),
                                        picture.getBounds());

                                if (index == -1) {
                                    return;
                                }

                                mVeils.remove(index);

                                drawVeils();
                            }
                        });

                        Color color = new Color(picture.getDisplay(), 255, 0, 0);
                        // gc.setBackground(color);
                        gc.setForeground(color);

                        smallOval.addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                gc.drawOval(e.x, e.y, 60, 70);
                                mVeils.add(value2Ratio(new Veil(0, e.x, e.y, 60, 70, 0),
                                        picture.getImage().getBounds(),
                                        picture.getBounds()));
                            }
                        });

                        bigOval.addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                gc.drawOval(e.x, e.y, 110, 120);
                                mVeils.add(value2Ratio(new Veil(0, e.x, e.y, 110, 120, 0),
                                        picture.getImage().getBounds(), picture.getBounds()));
                            }
                        });

                        smallRect.addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                gc.drawRectangle(e.x, e.y, 100, 40);
                                mVeils.add(value2Ratio(new Veil(1, e.x, e.y, 100, 40, 0),
                                        picture.getImage().getBounds(), picture.getBounds()));
                            }
                        });

                        bigRect.addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event) {
                                gc.drawRectangle(e.x, e.y, 200, 60);
                                mVeils.add(value2Ratio(new Veil(1, e.x, e.y, 200, 60, 0),
                                        picture.getImage().getBounds(), picture.getBounds()));
                            }
                        });

                        picture.setMenu(menu);
                    }

                    // 좌클릭
                    if (e.button == 1) {
                        var index = whichVeil(e, picture.getImage().getBounds(),
                                picture.getBounds());

                        isDrag = true;
                        focusedVeil = index;

                        mouseDown.x = e.x;
                        mouseDown.y = e.y;

                    }
                }
            }

            @Override
            public void mouseUp(MouseEvent e) {
                if ((picture.getImage() != null) && isDrag) {
                    // 그리기 모드
                    if (focusedVeil == -1) {

                        var diffX = Math.abs(mouseDown.x - e.x);
                        var diffY = Math.abs(mouseDown.y - e.y);

                        Veil veil = new Veil();

                        if (mouseDown.x < e.x) {
                            if (mouseDown.y < e.y) {
                                veil = new Veil(shape, mouseDown.x, mouseDown.y, diffX, diffY, 0);
                            } else {
                                veil = new Veil(shape, mouseDown.x, mouseDown.y - diffY, diffX, diffY, 0);
                            }
                        } else {
                            if (mouseDown.y < e.y) {
                                veil = new Veil(shape, mouseDown.x - diffX, mouseDown.y, diffX, diffY, 0);
                            } else {
                                veil = new Veil(shape, mouseDown.x - diffX, mouseDown.y - diffY, diffX,
                                        diffY, 0);
                            }
                        }

                        mVeils.add(value2Ratio(veil, picture.getImage().getBounds(),
                                picture.getBounds()));
                        isDrag = false;
                        drawVeils();

                    } else {
                        // 크기 조절 모드
                        if (isResize) {

                            var diffX = Math.abs(mouseDown.x - e.x);
                            var diffY = Math.abs(mouseDown.y - e.y);

                            var veils = ratio2Value(mVeils, picture.getImage().getBounds(),
                                    picture.getBounds());
                            Veil veil = veils.get(focusedVeil);

                            if (mouseDown.x < e.x) {
                                // (증가, 증가)
                                if (mouseDown.y < e.y) {
                                    veil.width += diffX;
                                    veil.height += diffY;
                                }
                                // (증가, 감소)
                                else {
                                    veil.width += diffX;
                                    veil.height -= diffY;
                                }
                            } else {
                                // (감소, 증가)
                                if (mouseDown.y < e.y) {
                                    veil.width -= diffX;
                                    veil.height += diffY;
                                }
                                // (감소, 감소)
                                else {
                                    veil.width -= diffX;
                                    veil.height -= diffY;
                                }
                            }

                            if ((veil.width >= 0) && (veil.height >= 0)) {
                                mVeils.remove(focusedVeil);
                                mVeils.add(value2Ratio(veil, picture.getImage().getBounds(),
                                        picture.getBounds()));

                                drawVeils();
                            }

                            focusedVeil = -1;
                            isDrag = false;
                        }
                        // 이동 모드
                        else if (isMove) {

                            var diffX = Math.abs(mouseDown.x - e.x);
                            var diffY = Math.abs(mouseDown.y - e.y);

                            var veils = ratio2Value(mVeils, picture.getImage().getBounds(),
                                    picture.getBounds());
                            Veil veil = veils.get(focusedVeil);

                            if (mouseDown.x < e.x) {
                                // (증가, 증가)
                                if (mouseDown.y < e.y) {
                                    veil.x += diffX;
                                    veil.y += diffY;
                                }
                                // (증가, 감소)
                                else {
                                    veil.x += diffX;
                                    veil.y -= diffY;
                                }
                            } else {
                                // (감소, 증가)
                                if (mouseDown.y < e.y) {
                                    veil.x -= diffX;
                                    veil.y += diffY;
                                }
                                // (감소, 감소)
                                else {
                                    veil.x -= diffX;
                                    veil.y -= diffY;
                                }
                            }

                            mVeils.remove(focusedVeil);
                            mVeils.add(value2Ratio(veil, picture.getImage().getBounds(),
                                    picture.getBounds()));

                            drawVeils();

                            focusedVeil = -1;
                            isDrag = false;
                        }
                    }
                }

            }
        });
    }

    public void setBasicPicture(String path, String rawVeils) {
        String root = Path.of("").toAbsolutePath().toString() + "\\targetfolder";
        this.path = root + path;

        picture.setImage(makeFit(this.path, picture.getShell()));

        picture.setAlignment(SWT.CENTER);
        mVeils.clear();

        if (rawVeils.equals("n")) {
        } else {

            ArrayList<Veil> resultVeils = new ArrayList<>();
            int shape;
            double x;
            double y;
            double width;
            double height;
            int angle;

            var veils = rawVeils.split("\\|");
            for (var piece : veils) {
                var veil = piece.split("@");

                shape = Integer.parseInt(veil[0]);
                x = Double.parseDouble(veil[1]);
                y = Double.parseDouble(veil[2]);
                width = Double.parseDouble(veil[3]);
                height = Double.parseDouble(veil[4]);
                angle = Integer.parseInt(veil[5]);

                resultVeils.add(new Veil(shape, x, y, width, height, angle));
            }

            mVeils = resultVeils;
        }

        drawVeils();
    }

    public void setPreloadedImage(Image image) {
        this.preloadedImage = image;
    }

    private Image makeFit(String imagePath, Shell shell) {
        Image originalImage;
        Rectangle imgSize;
        if (preloadedImage != null) {
            originalImage = preloadedImage;
            imgSize = originalImage.getBounds();
            preloadedImage = null;
        } else {
            originalImage = new Image(shell.getDisplay(), imagePath);
            imgSize = originalImage.getBounds();
        }

        var backSize = picture.getBounds();

        var width = 0;
        var height = 0;

        var originalRatio = (double) imgSize.height / (double) imgSize.width;
        var backRatio = (double) backSize.height / (double) backSize.width;

        if (originalRatio > backRatio) {
            var ratio = (double) backSize.height / (double) imgSize.height;
            width = (int) (ratio * imgSize.width);
            height = (int) (ratio * imgSize.height);
        }

        else {
            var ratio = (double) backSize.width / (double) imgSize.width;
            width = (int) (ratio * imgSize.width);
            height = (int) (ratio * imgSize.height);
        }

        var resizedImage = new Image(shell.getDisplay(), originalImage.getImageData().scaledTo(width, height));

        return resizedImage;
    }

    private boolean isOnImage(MouseEvent e, Rectangle imgBounds, Rectangle backBounds) {
        imgBounds.x = (backBounds.width - imgBounds.width) / 2;
        imgBounds.y = (backBounds.height - imgBounds.height) / 2;

        if ((imgBounds.x < e.x) && (e.x < imgBounds.x + imgBounds.width)) {
            if ((imgBounds.y < e.y) && (e.y < imgBounds.y + imgBounds.height)) {
                return true;
            }
        }

        return false;
    }

    private int whichVeil(MouseEvent e, Rectangle imgBounds, Rectangle backBounds) {
        var veils = ratio2Value(mVeils, imgBounds, backBounds);
        Veil veil;

        for (int i = 0; i < mVeils.size(); i++) {
            veil = veils.get(i);

            if ((veil.x < e.x) && (e.x < (veil.x + veil.width))) {
                if ((veil.y < e.y) && (e.y < (veil.y + veil.height))) {

                    return i;
                }
            }
        }

        return -1;
    }

    public ArrayList<Veil> ratio2Value(ArrayList<Veil> veils, Rectangle imgBounds, Rectangle backBounds) {
        ArrayList<Veil> resultVeils = new ArrayList<>();
        Veil veil;
        double posX;
        double posY;
        double valueX;
        double valueY;
        double valueWidth;
        double valueHeight;

        for (var piece : veils) {
            posX = piece.x * imgBounds.width;
            posY = piece.y * imgBounds.height;

            valueX = posX + ((backBounds.width - imgBounds.width) / 2);
            valueY = posY + ((backBounds.height - imgBounds.height) / 2);

            valueWidth = piece.width * imgBounds.width;
            valueHeight = piece.height * imgBounds.height;

            veil = new Veil(piece.shape, valueX, valueY, valueWidth, valueHeight, piece.angle);
            resultVeils.add(veil);
        }

        return resultVeils;
    }

    private Veil value2Ratio(Veil veil, Rectangle imgBounds, Rectangle backBounds) {
        // 파라미터 veil은 value값을 갖는 veil;
        double posX;
        double posY;
        double ratioX;
        double ratioY;
        double ratioWidth;
        double ratioHeight;

        posX = veil.x - ((backBounds.width - imgBounds.width) / 2);
        posY = veil.y - ((backBounds.height - imgBounds.height) / 2);

        ratioX = posX / imgBounds.width;
        ratioY = posY / imgBounds.height;

        ratioWidth = veil.width / imgBounds.width;
        ratioHeight = veil.height / imgBounds.height;

        Veil resultVeil = new Veil(veil.shape, ratioX, ratioY, ratioWidth, ratioHeight, veil.angle);

        return resultVeil;
    }

    public String getVeils() {
        String result = "";

        for (var piece : mVeils) {
            var veil = Integer.toString(piece.shape) + "@" + Double.toString(piece.x) + "@" + Double.toString(piece.y)
                    + "@" + Double.toString(piece.width) + "@" + Double.toString(piece.height) + "@"
                    + Integer.toString(piece.angle) + "|";
            result += veil;
        }

        if (result.equals("")) {
            result = "n";
        }

        return result;
    }

    public Label getPicture() {
        return picture;
    }

    private void drawVeils() {
        if (mVeils.size() > 0) {
            picture.print(gc);
            Color color = new Color(picture.getDisplay(), 255, 0, 0);
            // gc.setBackground(color);
            gc.setForeground(color);
            ArrayList<Veil> drawVeils = ratio2Value(mVeils, picture.getImage().getBounds(), picture.getBounds());

            for (var piece : drawVeils) {
                if (piece.shape == 0) {
                    gc.drawOval((int) piece.x, (int) piece.y, (int) piece.width, (int) piece.height);
                } else {
                    gc.drawRectangle((int) piece.x, (int) piece.y, (int) piece.width, (int) piece.height);
                }
            }
        } else {
            picture.print(gc);
        }
    }

    public void setDragMode(boolean resize, boolean move) {
        if (resize ^ move) {
            isResize = resize;
            isMove = move;
        }
    }

    public void setShape(int shape) {
        // 1 -> rectangle
        // 0 -> oval

        this.shape = shape;
    }

    public ArrayList<Veil> getmVeils() {
        return mVeils;
    }
}