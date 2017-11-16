package solver.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window {

    private static final int GRID_LINE_BRIGHTNESS = 20;
    private static final int GRID_FRAME_BRIGHTNESS = 200;
    private static final int GRID_LINE_GAP = 10; //px
    int offsetx;
    int offsety;
    int width;
    int height;

    BufferedImage capture;
    private Rectangle boardRect;

    public Window() {

        Robot robot = null;

        try {

            robot = new Robot();

        } catch (AWTException e) {
            e.printStackTrace();
        }

        Rectangle screenRect = new Rectangle(0, 0, 0, 0);

        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
        }

        BufferedImage wholeScreen = robot.createScreenCapture(screenRect);
        System.out.println("screen dimensions: " + wholeScreen.getWidth() + " : " + wholeScreen.getHeight());

        Point start = findOffset(wholeScreen);


        this.boardRect = new Rectangle(offsetx, offsety, width, height);
        this.capture = robot.createScreenCapture(boardRect);
    }

    private Point findOffset(BufferedImage wholeScreen) {

        for (int x = 0; x < wholeScreen.getWidth(); x++) {
            heightLoop:
            for (int y = 0; y < wholeScreen.getHeight(); y++) {
                if (wholeScreen.getRGB(x, y) == 0xFFFF << 16)
                {
                    System.out.println("red dot!" + new Point(x,y));
                }
                if (checkIfPixelDarker(wholeScreen.getRGB(x, y), GRID_LINE_BRIGHTNESS)) {
                    ArrayList<Point> darkSpots = new ArrayList<>();
                    boolean flagBrightExist = true;
                    int tempx;
                    gridLoop:
                    for (int i = x; i < wholeScreen.getWidth() && (i - x + y) < wholeScreen.getHeight(); i++) {
                        Point tempPoint = new Point(i, (i - x + y));
                        if (flagBrightExist &&
                            checkIfPixelDarker(wholeScreen.getRGB(tempPoint.x, tempPoint.y), GRID_LINE_BRIGHTNESS) &&
                            (darkSpots.size() == 0 ||
                            darkSpots.get(darkSpots.size()-1).x > tempPoint.x + GRID_LINE_GAP))
                        {
                            darkSpots.add(new Point( i, (i - x + y)));
                            flagBrightExist = false;
                        }
                        if (checkIfPixelLighter(wholeScreen.getRGB(i, (i - x + y)), GRID_FRAME_BRIGHTNESS)) {
                            flagBrightExist = true;
                        }
                    }
                    if (darkSpots.size() > 0)
                        System.out.println("end of sequense, size: " + darkSpots.size());
                    for (Point spot : darkSpots) {
                        System.out.println(spot);
                    }


                }
            }
        }
        return null;
    }

    private boolean checkIfPixelDarker(int rgb, int lightness) {
        int[] l1;
        int lightness1;

        l1 = new int[3];

        l1[0] = (rgb >> 16) & 0xFF;
        l1[1] = (rgb >> 8) & 0xFF;
        l1[2] = rgb & 0xFF;

        lightness1 = ((l1[0] + l1[1] + l1[2]) / 2);

        if (lightness1 < lightness) {
            return true;
        }

        return false;
    }

    private boolean checkIfPixelLighter(int rgb, int lightness) {
        int[] l1;
        int lightness1;

        l1 = new int[3];

        l1[0] = (rgb >> 16) & 0xFF;
        l1[1] = (rgb >> 8) & 0xFF;
        l1[2] = rgb & 0xFF;

        lightness1 = ((l1[0] + l1[1] + l1[2]) / 2);

        if (lightness1 > lightness) {
            return true;
        }

        return false;
    }


}

