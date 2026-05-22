package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/main-layout.fxml"));

            Scene scene = new Scene(root, 1200, 760);

            String[] globalCssFiles = {
                    "/css/global/theme.css",
                    "/css/global/layout.css",
                    "/css/global/pages.css",
                    "/css/global/cards.css",
                    "/css/global/forms.css",
                    "/css/global/buttons.css",
                    "/css/global/tables.css"
            };

            for (String css : globalCssFiles) {
                scene.getStylesheets().add(
                        getClass().getResource(css).toExternalForm());
            }

            stage.setTitle("FleetCare - Quản lý hồ sơ & bảo dưỡng phương tiện");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
