package solver.view;


import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Overlay extends Application {

    @Override
    public void start(Stage primaryStage) {

        String sTitle = "Окно";
        User32 sUser32 = User32.INSTANCE;

        primaryStage.setAlwaysOnTop(true);
        final StackPane layout = new StackPane();
        final Text mainText = new Text();
        layout.getChildren().add(mainText);
        mainText.setText("ХУЙ ЕБАНЫЙ ЧТОБ ТЫ СДОХ");

        final Scene mainScene = new Scene(layout);
        mainScene.setFill(null);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(mainScene);
        primaryStage.setTitle(sTitle);
        primaryStage.show();


        sUser32.EnumWindows(
                (hWnd, data) -> {

                    final char[] windowText = new char[512];
                    sUser32.GetWindowText(hWnd, windowText, 512);
                    final String wText = Native.toString(windowText);

                    if (!wText.isEmpty() && wText.equals(sTitle)) {

                        final int initialStyle = com.sun.jna.platform.win32.User32.INSTANCE.GetWindowLong(hWnd, WinUser.GWL_EXSTYLE);
                        com.sun.jna.platform.win32.User32.INSTANCE.SetWindowLong(hWnd, WinUser.GWL_EXSTYLE, initialStyle | WinUser.WS_EX_TRANSPARENT );
                        return false;
                    }
                    return true;
                }, null);
    }
    public static void main(String[] args) {
        launch(args);
    }
}