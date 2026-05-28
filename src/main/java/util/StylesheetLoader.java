package util;

import javafx.scene.Scene;

import java.net.URL;

public final class StylesheetLoader {

    private static final String[] BASE_STYLES = {
            "/css/global/theme.css",
            "/css/global/layout.css",
            "/css/global/pages.css",
            "/css/global/cards.css",
            "/css/global/forms.css",
            "/css/global/buttons.css",
            "/css/global/tables.css",
            "/css/pages/dashboard.css",
            "/css/pages/vehicle.css",
            "/css/pages/vehicle-document.css",
            "/css/pages/document-alert.css"
    };

    private StylesheetLoader() {
    }

    public static void addBaseStyles(Scene scene) {
        scene.getStylesheets().clear();
        for (String stylesheet : BASE_STYLES) {
            URL url = StylesheetLoader.class.getResource(stylesheet);
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
            } else {
                System.out.println("Khong tim thay stylesheet: " + stylesheet);
            }
        }
    }
}
