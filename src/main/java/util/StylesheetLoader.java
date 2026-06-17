package util;

import javafx.scene.Scene;
import javafx.scene.Parent;

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
        addStylesheets(scene.getStylesheets());
    }

    public static void addBaseStyles(Parent parent) {
        parent.getStylesheets().clear();
        addStylesheets(parent.getStylesheets());
    }

    private static void addStylesheets(java.util.List<String> stylesheets) {
        for (String stylesheet : BASE_STYLES) {
            URL url = StylesheetLoader.class.getResource(stylesheet);
            if (url != null) {
                stylesheets.add(url.toExternalForm());
            } else {
                System.out.println("Không tìm thấy stylesheet: " + stylesheet);
            }
        }
    }
}
