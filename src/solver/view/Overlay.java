package solver.view;


import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Overlay extends Application {

    static int[] dimensions;

    public static void setDimensions(int[] inputDimensions) {
        dimensions = inputDimensions;
    }

    @Override
    public void start(Stage primaryStage) {

        if (dimensions == null)
        {
            return;
        }

        String sTitle = "Minesweeper bot";

        final StackPane root = new StackPane();

        root.setStyle("-fx-background-color: #FFFFFF;");

//        grid.setAlignment(Pos.CENTER);
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(25, 25, 25, 25));

        primaryStage.setAlwaysOnTop(true);

        final Scene scene = new Scene(root, dimensions[2], dimensions[3], null);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setTitle(sTitle);
        primaryStage.show();


        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(dimensions[0]);
        primaryStage.setY(dimensions[1]);


//********************************************************************
        /*
        A way of doing this action in Windows is through user32.dll and Java Native Access (JNA).
        We used GetWindowLong to get the current configuration of the window and SetWindowLong
        to update the bit field that is controlling the ability of the window be transparent to the mouse.

        sTitle = title of the window
         */

        boolean clickThrough = false;

        if (clickThrough) {

            User32 sUser32 = User32.INSTANCE;

            sUser32.EnumWindows(
                    (hWnd, data) -> {

                        final char[] windowTitleChars = new char[512];
                        sUser32.GetWindowText(hWnd, windowTitleChars, 512);
                        final String windowTitle = Native.toString(windowTitleChars);

                        if (!windowTitle.isEmpty() && windowTitle.equals(sTitle)) {

                            final int initialStyle = sUser32.GetWindowLong(hWnd, WinUser.GWL_EXSTYLE);
                            sUser32.SetWindowLong(hWnd, WinUser.GWL_EXSTYLE, initialStyle | WinUser.WS_EX_TRANSPARENT);
                            return false;
                        }
                        return true;
                    }, null);

        }

//********************************************************************

    }
    public static void main(String[] args) {
        launch(args);
    }
}