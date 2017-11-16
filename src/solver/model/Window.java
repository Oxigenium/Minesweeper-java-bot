package solver.model;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Window {

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

        } catch (AWTException e)
        {
            e.printStackTrace();
        }

        Rectangle screenRect = new Rectangle(0, 0, 0, 0);

        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
        }

        this.capture = robot.createScreenCapture(screenRect);
    }


}

