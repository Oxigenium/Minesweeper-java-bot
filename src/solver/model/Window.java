package solver.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Window {

    private static final int GRID_SEARCH_AREA = 150; //px
    private static final int CELL_MIN_SIZE = 16;

    private static final int MIN_ROWS = 9;
    private static final int MAX_ROWS = 24;
    private static final int MIN_COLUMNS = 9;
    private static final int MAX_COLUMNS = 30;


    private static final float GRID_BRIGHTNESS = 0.3f;
    private static final float CELL_BRIGHTNESS = 0.6f;
    private static final float[] CELL_HUE_RANGE = { 0.50f , 0.7f };
    private static final float[] CELL_CENTER_HUE_RANGE = { 0.50f , 0.7f };

    private static final float CELL_CORNER_BRIGHTNESS = 0.6f;
    private static final float[] CELL_CORNER_HUE_RANGE = { 0.62f , 0.7f };
    private static final float[] CELL_CORNER_CENTER_HUE_RANGE = { 0.64f , 0.65f };


    private static final boolean DEBUG_ROBOT = false;

    int offsetx;
    int offsety;
    int cellSide;
    int width;
    int height;
    int columns;
    int rows;

    BufferedImage board;
    Rectangle boardRect;

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


        Point initialMousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);

        Point start = findOffset(wholeScreen);
        Point[] fieldSize = null;

        if (start != null)
        {

            fieldSize = findFieldSize(wholeScreen, start, cellSide);
        }

        if (start != null && fieldSize != null)
        {

            width = fieldSize[0].x;
            height = fieldSize[0].y;

            offsetx = start.x - width;
            offsety = start.y - height;

            columns = fieldSize[1].x;
            rows = fieldSize[1].y;

            cellSide = width / columns;

            System.out.println("Window found! width: " + fieldSize[0].x + ", height: " + fieldSize[0].y + ", columns: " + fieldSize[1].x + ", rows: " + fieldSize[1].y);

            if (DEBUG_ROBOT) {
                robot.mouseMove(start.x, start.y);
                Thread.sleep(1500);
                robot.mouseMove(start.x - width, start.y - height);
                Thread.sleep(1500);
                robot.mouseMove(initialMousePosition.x, initialMousePosition.y);
            }

            boardRect = new Rectangle(offsetx, offsety, width, height);
            board = robot.createScreenCapture(boardRect);


        } else if (fieldSize == null && start != null)
        {
            boardRect = null;
            board = null;

            if (DEBUG_ROBOT) {
                System.out.println("Start point is incorrect but found");
                System.out.println(start);

                robot.mouseMove(start.x, start.y);
            }
        } else
        {
            boardRect = null;
            board = null;

        }


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

            int columns = 0;
            int rows = 0;



            for (int x = start.x - cellSide/2; x >= 0; x -= cellSide) {

                Point[] cellDimensions = isCell(wholeScreen, new Point(x, start.y - cellSide/2), false);

                if (cellDimensions != null)
                {
                    x = cellDimensions[0].x + cellDimensions[2].x/2;

                    width =  Math.abs(start.x - cellDimensions[0].x);
                    columns++;

                    if (DEBUG_ROBOT) {
                        robot.mouseMove(x, start.y - cellSide / 2);
                        Thread.sleep(150);
                    }
                } else
                {
                    break;
                }
            }
            for (int y = start.y - cellSide/2; y >= 0; y -= cellSide) {

                Point[] cellDimensions = isCell(wholeScreen, new Point(start.x - cellSide/2, y), false);

                if (cellDimensions != null)
                {
                    y = cellDimensions[0].y + cellDimensions[2].y/2;

                    height = Math.abs(start.y - cellDimensions[0].y);
                    rows++;
                    if (DEBUG_ROBOT) {
                        robot.mouseMove(start.x - cellSide / 2, y);
                        Thread.sleep(150);
                    }
                } else
                {
                    break;
                }
            }

            if (height > 0 &&
                    width > 0 &&
                    columns >= MIN_COLUMNS &&
                    columns <= MAX_COLUMNS &&
                    rows >= MIN_ROWS &&
                    rows <= MAX_ROWS)
            {

                result[0] = new Point(width,height);
                result[1] = new Point(columns,rows);


                return result;
            }
        }

        return null;
    }
    private Point findOffset(BufferedImage wholeScreen) {

        for (int x = wholeScreen.getWidth() - 1; x >= 0; x--) {
            heightLoop:
            for (int y = wholeScreen.getHeight() - 1; y >= 0; y--) {
                // wholeScreen.getRGB(x,y) - single pixel on the screen

                Color pixel = getColor(wholeScreen.getRGB(x,y));
                float[] pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);
                Point[] cellDimensions = null;

                if (checkIfPixelInRangeOfHue(pixelHSB, CELL_CORNER_CENTER_HUE_RANGE) && checkIfPixelLighter(pixelHSB,CELL_CORNER_BRIGHTNESS))
                {
                    cellDimensions = isCell(wholeScreen, new Point(x,y), true);
                }


                if (cellDimensions != null)
                {
                    cellSide = cellDimensions[2].x;
                    offsetx = cellDimensions[1].x;
                    offsety = cellDimensions[1].y;


                    Point[] tempCellDimensions = isCell(wholeScreen, new Point(cellDimensions[0].x - cellSide / 2,cellDimensions[1].y - cellSide/2), false);

                    if (tempCellDimensions != null)
                    {
                        cellSide = cellDimensions[0].x - tempCellDimensions[0].x;
                    }

                    return cellDimensions[1];
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

    private Point[] isCell(BufferedImage wholeScreen, Point xy, boolean corner)
    {

        final float[] range;
        final float[] centerRange;
        final float brightness;

        if (corner)
        {
            centerRange = CELL_CORNER_CENTER_HUE_RANGE;
            range = CELL_CORNER_HUE_RANGE;
            brightness = CELL_CORNER_BRIGHTNESS;
        } else
        {
            centerRange = CELL_CENTER_HUE_RANGE;
            range = CELL_HUE_RANGE;
            brightness = CELL_BRIGHTNESS;
        }

        Color pixel = getColor(wholeScreen.getRGB(xy.x,xy.y));
        float[] pixelHSB = Color.RGBtoHSB(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), null);

        if (checkIfPixelInRangeOfHue(pixelHSB, centerRange) && checkIfPixelLighter(pixelHSB,brightness))
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
                if (!checkIfPixelInRangeOfHue(pixelHSB,range))
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
                if (!checkIfPixelInRangeOfHue(pixelHSB,range))
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
                if (!checkIfPixelInRangeOfHue(pixelHSB,range))
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

                    rightBottomX = xy.x + i;

                    break searchGrid4;
                }
                if (!checkIfPixelInRangeOfHue(pixelHSB,range))
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

    public int getOffsetx() {
        return offsetx;
    }

    public int getOffsety() {
        return offsety;
    }

    public int getCellSide() {
        return cellSide;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public BufferedImage getBoard() {
        return board;
    }

    public Rectangle getBoardRect() {
        return boardRect;
    }
}

