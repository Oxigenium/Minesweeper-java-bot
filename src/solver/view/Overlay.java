package solver.view;


import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
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

        final GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label[][] digits = new Label[dimensions[4]][dimensions[5]];
        ColumnConstraints[] columns = new ColumnConstraints[dimensions[4]];
        RowConstraints[] rows = new RowConstraints[dimensions[5]];
        int cellSide = dimensions[2]/dimensions[4];



        for (int x = 0; x < dimensions[4]; x++) {
            for (int y = 0; y < dimensions[5]; y++) {
                digits[x][y] = new Label("0.21");
                digits[x][y].setStyle("\n    -fx-font-size: " + cellSide/2.5 + "px;");
                grid.add(digits[x][y], x, y);
                GridPane.setHalignment(digits[x][y], HPos.CENTER);
                if (x == 0)
                {

                    rows[y] = new RowConstraints(cellSide);
                    grid.getRowConstraints().add(rows[y]);
                }
            }
            columns[x] = new ColumnConstraints(cellSide);
            grid.getColumnConstraints().add(columns[x]);
        }


        primaryStage.setAlwaysOnTop(true);
//        grid.setGridLinesVisible(true);


        final Scene scene = new Scene(grid, dimensions[2]+50, dimensions[3]+50, null);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setTitle(sTitle);

        scene.getStylesheets().add
                (Overlay.class.getResource("assets/styles/overlay.css").toExternalForm());
        primaryStage.show();

        primaryStage.setX(dimensions[0]-25);
        primaryStage.setY(dimensions[1]-25);


//********************************************************************
        /*
        A way of doing this action in Windows is through user32.dll and Java Native Access (JNA).
        We used GetWindowLong to get the current configuration of the window and SetWindowLong
        to update the bit field that is controlling the ability of the window be transparent to the mouse.

        sTitle = title of the window
         */

        boolean clickThrough = true;

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