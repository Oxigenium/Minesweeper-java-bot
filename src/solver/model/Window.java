package solver.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window {

    private static final int GRID_LINE_BRIGHTNESS = 20;
    private static final int GRID_FRAME_BRIGHTNESS = 200;
    private static final int GRID_LINE_GAP = 10; //px

    private static final int GRID_SEARCH_AREA = 150; //px
    private static final int CELL_MIN_SIZE = 16;
    private static final float GRID_BRIGHTNESS = 0.3f;
    private static final float CELL_BRIGHTNESS = 0.8f;

    private static final int MIN_ROWS = 9;
    private static final int MAX_ROWS = 24;
    private static final int MIN_COLUMNS = 9;
    private static final int MAX_COLUMNS = 30;

    private static final float[] CELL_HUE_RANGE = { 0.50f , 0.64f };
    int offsetx;
    int offsety;
    int cellwidth = 0;
    int cellheight = 0;
    int width = 0;
    int height = 0;

    BufferedImage capture;
    private Rectangle boardRect;

    public Window() throws InterruptedException {

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

        if (start != null && findFieldSize(wholeScreen, start))
        {
            System.out.println("Window found!");
        }





        this.boardRect = new Rectangle(offsetx, offsety, width, height);
        this.capture = robot.createScreenCapture(boardRect);
    }

    private boolean findFieldSize(BufferedImage wholeScreen, Point start)
    {
        width = 0;
        height = 0;
        if(cellheight != 0  && cellwidth != 0)
        {

            for (int x = start.x + cellwidth/2; x < wholeScreen.getWidth(); x += cellwidth) {
                Color pixel = getColor(wholeScreen.getRGB(x,start.y + cellheight/2);
                float[] pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);
                if (checkIfPixelInRangeOfHue(pixelHSB, CELL_HUE_RANGE) && checkIfPixelLighter(pixelHSB,CELL_BRIGHTNESS))
                {
                    width += cellwidth;
                } else
                {
                    break;
                }
            }
            for (int y = start.y + cellheight/2; y < wholeScreen.getHeight(); y += cellheight) {
                Color pixel = getColor(wholeScreen.getRGB(start.x + cellwidth/2,y));
                float[] pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);
                if (checkIfPixelInRangeOfHue(pixelHSB, CELL_HUE_RANGE) && checkIfPixelLighter(pixelHSB,CELL_BRIGHTNESS))
                {
                    height += cellheight;
                } else
                {
                    break;
                }
            }

            if (height != 0 && width != 0)
            {
                return true;
            }
        }

        return false;
    }
    private Point findOffset(BufferedImage wholeScreen) throws InterruptedException {

        for (int x = 0; x < wholeScreen.getWidth(); x++) {
            heightLoop:
            for (int y = 0; y < wholeScreen.getHeight(); y++) {
                // wholeScreen.getRGB(x,y) - single pixel on the screen

                Color pixel = getColor(wholeScreen.getRGB(x,y));
                float[] pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                if (checkIfPixelInRangeOfHue(pixelHSB, CELL_HUE_RANGE) && checkIfPixelLighter(pixelHSB,CELL_BRIGHTNESS))
                {

                    cellheight = 0;
                    cellwidth = 0;
                    searchGrid1:
                    for (int i = 0; i < GRID_SEARCH_AREA && (y - i) > 0; i++) {

                        pixel = getColor(wholeScreen.getRGB(x,y - i));
                        pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                        if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                        {
                            cellheight += i;

                            offsety = y - i;

                            break searchGrid1;
                        }
                        if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                        {
                            continue heightLoop;
                        }
                    }
                    searchGrid2:
                    for (int i = 0; i < GRID_SEARCH_AREA && (y + i) < wholeScreen.getHeight(); i++) {

                        pixel = getColor(wholeScreen.getRGB(x,y + i));
                        pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                        if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                        {
                            cellheight += i;
                            break searchGrid2;
                        }
                        if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                        {
                            continue heightLoop;
                        }
                    }
                    searchGrid3:
                    for (int i = 0; i < GRID_SEARCH_AREA && (x - i) > 0; i++) {

                        pixel = getColor(wholeScreen.getRGB(x - i,y));
                        pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                        if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                        {
                            cellwidth += i;

                            offsetx = x - i;

                            break searchGrid3;
                        }
                        if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                        {
                            continue heightLoop;
                        }
                    }
                    searchGrid4:
                    for (int i = 0; i < GRID_SEARCH_AREA && (x + i) < wholeScreen.getWidth(); i++) {

                        pixel = getColor(wholeScreen.getRGB(x + i,y));
                        pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                        if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                        {
                            cellwidth += i;
                            break searchGrid4;
                        }
                        if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                        {
                            continue heightLoop;
                        }
                    }

                    if (cellwidth < CELL_MIN_SIZE || cellheight < CELL_MIN_SIZE || Math.abs(cellwidth - cellheight) > Math.max(cellwidth,cellheight)/10)
                    {
                        continue heightLoop;
                    }

                    System.out.println("Dot Found!, coordinates: offsetx: " + offsetx + ", offsety: " + offsety + ", width: " + cellwidth + ", height: " + cellheight);

                    // Block on the top finds left top offset point of game and now we must determine amount of fields;

                    return new Point(offsetx, offsety);


                }

            }
        }
        return null;
    }

    private Color getColor (int rgb)
    {
        Color color = new Color(rgb);
        return color;
    }

    private boolean checkIfPixelInRangeOfHue(float[] hsb, float[] hue) {
        if (hsb[0] > hue[0] && hsb[0] < hue[1]) {
            return true;
        }

        return false;
    }
    private boolean checkIfPixelDarker(float[] hsb, float lightness) {
        if (hsb[2] < lightness) {
            return true;
        }

        return false;
    }

    private boolean checkIfPixelLighter(float[] hsb, float lightness) {
        if (hsb[2] > lightness) {
            return true;
        }

        return false;
    }


}

