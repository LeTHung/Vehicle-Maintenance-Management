package util;

import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public final class AlertUtil {

    private static final String APP_ICON = "/images/fleetcare-app-icon.png";

    private AlertUtil() {
    }

    public static void applyFleetCareIcon(Dialog<?> dialog) {
        if (dialog == null) {
            return;
        }

        dialog.setOnShown(event -> {
            if (dialog.getDialogPane().getScene() == null) {
                return;
            }
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            applyFleetCareIcon(stage);
        });
    }

    public static void applyFleetCareIcon(Stage stage) {
        if (stage == null || !stage.getIcons().isEmpty()) {
            return;
        }

        InputStream iconStream = AlertUtil.class.getResourceAsStream(APP_ICON);
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        }
    }
}
