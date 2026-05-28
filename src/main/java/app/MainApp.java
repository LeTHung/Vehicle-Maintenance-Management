package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.StylesheetLoader;

import java.net.URL;

public class MainApp extends Application {

    private static final double LOGIN_WIDTH = 420;
    private static final double LOGIN_HEIGHT = 360;

    @Override
    public void start(Stage stage) {
        try {
            URL fxmlUrl = getClass().getResource("/view/auth/login-view.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("Không tìm thấy /view/auth/login-view.fxml");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root, LOGIN_WIDTH, LOGIN_HEIGHT);
            StylesheetLoader.addBaseStyles(scene);

            stage.setTitle("FleetCare - Đăng nhập");
            stage.setResizable(false);
            stage.setMinWidth(LOGIN_WIDTH);
            stage.setMinHeight(LOGIN_HEIGHT);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi khởi động ứng dụng");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
