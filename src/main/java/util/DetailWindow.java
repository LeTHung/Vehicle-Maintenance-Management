package util;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class DetailWindow {

    private DetailWindow() {
    }

    public static Stage show(Stage stage, Parent content, Node ownerNode, String title, double width, double height) {
        Stage detailStage = stage;
        if (detailStage == null) {
            detachFromCurrentParent(content);
            detailStage = new Stage();
            AlertUtil.applyFleetCareIcon(detailStage);
            detailStage.initModality(Modality.WINDOW_MODAL);
            Window owner = ownerNode == null || ownerNode.getScene() == null
                    ? null
                    : ownerNode.getScene().getWindow();
            if (owner != null) {
                detailStage.initOwner(owner);
            }
            Rectangle2D bounds = resolveScreenBounds(owner);
            double sceneWidth = Math.min(width, Math.max(480, bounds.getWidth() - 80));
            double sceneHeight = Math.min(height, Math.max(360, bounds.getHeight() - 100));
            detailStage.setMinWidth(Math.min(sceneWidth, 760));
            detailStage.setMinHeight(Math.min(sceneHeight, 520));

            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(false);
            scrollPane.getStyleClass().add("page-scroll");

            Scene scene = new Scene(scrollPane, sceneWidth, sceneHeight);
            StylesheetLoader.addBaseStyles(scene);
            detailStage.setScene(scene);
        }

        boolean wasShowing = detailStage.isShowing();
        content.setVisible(true);
        content.setManaged(true);
        detailStage.setTitle(title);
        detailStage.show();
        if (!wasShowing) {
            centerOnOwner(detailStage);
        }
        detailStage.toFront();
        detailStage.requestFocus();
        return detailStage;
    }

    public static void hide(Stage stage) {
        if (stage != null && stage.isShowing()) {
            stage.hide();
        }
    }

    private static void detachFromCurrentParent(Parent content) {
        Parent parent = content.getParent();
        if (parent instanceof Pane pane) {
            pane.getChildren().remove(content);
        }
    }

    private static void centerOnOwner(Stage stage) {
        Window owner = stage.getOwner();
        Rectangle2D bounds = resolveScreenBounds(owner);
        if (owner == null) {
            stage.centerOnScreen();
            clampToBounds(stage, bounds);
            return;
        }
        stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
        stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
        clampToBounds(stage, bounds);
    }

    private static Rectangle2D resolveScreenBounds(Window owner) {
        if (owner != null) {
            return Screen.getScreensForRectangle(owner.getX(), owner.getY(), owner.getWidth(), owner.getHeight())
                    .stream()
                    .findFirst()
                    .orElse(Screen.getPrimary())
                    .getVisualBounds();
        }
        return Screen.getPrimary().getVisualBounds();
    }

    private static void clampToBounds(Stage stage, Rectangle2D bounds) {
        double maxX = bounds.getMaxX() - stage.getWidth();
        double maxY = bounds.getMaxY() - stage.getHeight();
        stage.setX(Math.max(bounds.getMinX(), Math.min(stage.getX(), maxX)));
        stage.setY(Math.max(bounds.getMinY(), Math.min(stage.getY(), maxY)));
    }
}
