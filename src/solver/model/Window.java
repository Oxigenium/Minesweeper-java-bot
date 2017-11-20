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
    int cellWidth = 0;
    int cellHeight = 0;
    int width = 0;
    int height = 0;
    int columns = 0;
    int rows = 0;

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

        System.out.println(start);

        Point[] fieldSize = findFieldSize(wholeScreen, start, cellWidth);

        if (start != null && fieldSize != null)
        {

            offsetx = start.x;
            offsety = start.y;

            width = fieldSize[0].x;
            height = fieldSize[0].y;

            columns = fieldSize[1].x;
            rows = fieldSize[1].y;

            Point initialMousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);

            System.out.println("Window found! width: " + fieldSize[0].x + ", height: " + fieldSize[0].y + ", columns: " + fieldSize[1].x + ", rows: " + fieldSize[1].y);
            robot.mouseMove(start.x, start.y);
            Thread.sleep(2000);
            robot.mouseMove(start.x + width, start.y + height);
            Thread.sleep(2000);
//            robot.mouseMove(initialMousePosition.x, initialMousePosition.y);
        }





//        this.boardRect = new Rectangle(offsetx, offsety, width, height);
//        this.capture = robot.createScreenCapture(boardRect);
    }

    private Point[] findFieldSize(BufferedImage wholeScreen, Point start, int cellSide) throws InterruptedException
    {

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        Robot robot = null;

        try {

            robot = new Robot();

        } catch (AWTException e) {
            e.printStackTrace();
        }

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        int width = 0;
        int height = 0;

        Point[] result = new Point[2];

        if(cellSide != 0)
        {

            int columns = 1;
            int rows = 1;

            for (int x = start.x + cellSide/2; x < wholeScreen.getWidth(); x += cellSide) {

                Point[] cellDimensions = isCell(wholeScreen, new Point(x, start.y + cellSide/2));

                if (cellDimensions != null)
                {
                    width =  Math.abs(start.x - cellDimensions[1].x);
                    columns++;

                    robot.mouseMove(x, start.y + cellSide/2);
                    Thread.sleep(200);

                } else
                {
                    break;
                }
            }
            for (int y = start.y + cellSide/2; y < wholeScreen.getHeight(); y += cellSide) {

                Point[] cellDimensions = isCell(wholeScreen, new Point(start.x + cellSide/2, y));

                if (cellDimensions != null)
                {
                    height = Math.abs(start.y - cellDimensions[1].y);
                    rows++;

                    robot.mouseMove(start.x + cellSide/2, y);
                    Thread.sleep(200);
                } else
                {
                    break;
                }
            }

            if (height != 0 &&
                    width != 0 &&
                    columns > MIN_COLUMNS &&
                    columns < MAX_COLUMNS &&
                    rows > MIN_ROWS &&
                    rows < MAX_ROWS)
            {

                result[0] = new Point(width,height);
                result[1] = new Point(columns,rows);


                return result;
            }
        }

        return null;
    }
    private Point findOffset(BufferedImage wholeScreen) {

        for (int x = 0; x < wholeScreen.getWidth(); x++) {
            heightLoop:
            for (int y = 0; y < wholeScreen.getHeight(); y++) {
                // wholeScreen.getRGB(x,y) - single pixel on the screen

                Point[] cellDimensions = isCell(wholeScreen, new Point(x,y));


                if (cellDimensions != null)
                {
                    System.out.println("WOW!");
                    cellWidth = cellDimensions[2].x;
                    cellHeight = cellDimensions[2].y;
                    offsetx = cellDimensions[0].x;
                    offsety = cellDimensions[0].y;

                    return cellDimensions[0];
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

    private Point[] isCell(BufferedImage wholeScreen, Point xy)
    {

        Color pixel = getColor(wholeScreen.getRGB(xy.x,xy.y));
        float[] pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

        if (checkIfPixelInRangeOfHue(pixelHSB, CELL_HUE_RANGE) && checkIfPixelLighter(pixelHSB,CELL_BRIGHTNESS))
        {
            Point[] result = new Point[3];

            int cellHeight = 0;
            int cellWidth = 0;

            int leftTopX = 0;
            int leftTopY = 0;

            int rightBottomX = 0;
            int rightBottomY = 0;

            searchGrid1:
            for (int i = 0; i < GRID_SEARCH_AREA && (xy.y - i) > 0; i++) {

                pixel = getColor(wholeScreen.getRGB(xy.x,xy.y - i));
                pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);


                if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                {
                    cellHeight += i;

                    leftTopY = xy.y - i;

                    break searchGrid1;
                }
                if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                {
                    return null;
                }
            }
            searchGrid2:
            for (int i = 0; i < GRID_SEARCH_AREA && (xy.y + i) < wholeScreen.getHeight(); i++) {

                pixel = getColor(wholeScreen.getRGB(xy.x,xy.y + i));
                pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                {
                    cellHeight += i;

                    rightBottomY = xy.y + i;

                    break searchGrid2;
                }
                if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                {
                    return null;
                }
            }
            searchGrid3:
            for (int i = 0; i < GRID_SEARCH_AREA && (xy.x - i) > 0; i++) {

                pixel = getColor(wholeScreen.getRGB(xy.x - i,xy.y));
                pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                {
                    cellWidth += i;

                    leftTopX = xy.x - i;

                    break searchGrid3;
                }
                if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                {
                    return null;
                }
            }
            searchGrid4:
            for (int i = 0; i < GRID_SEARCH_AREA && (xy.x + i) < wholeScreen.getWidth(); i++) {

                pixel = getColor(wholeScreen.getRGB(xy.x + i,xy.y));
                pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

                if (checkIfPixelDarker(pixelHSB,GRID_BRIGHTNESS))
                {
                    cellWidth += i;

                    rightBottomY = xy.x + i;

                    break searchGrid4;
                }
                if (!checkIfPixelInRangeOfHue(pixelHSB,CELL_HUE_RANGE))
                {
                    return null;
                }
            }

            if (cellWidth < CELL_MIN_SIZE || cellHeight < CELL_MIN_SIZE || Math.abs(cellWidth - cellHeight) > Math.max(cellWidth,cellHeight)/5)
            {
                return null;
            }
            cellWidth = cellHeight;


            result[0] = new Point(leftTopX, leftTopY);
            result[1] = new Point(rightBottomX, rightBottomY);
            result[2] = new Point(cellWidth, cellHeight);


            return result;


        }
        return null;
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

