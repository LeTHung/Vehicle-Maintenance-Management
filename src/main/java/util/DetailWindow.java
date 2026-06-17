package util;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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
            detailStage.setMinWidth(width);
            detailStage.setMinHeight(height);
            detailStage.initModality(Modality.WINDOW_MODAL);
            Window owner = ownerNode == null || ownerNode.getScene() == null
                    ? null
                    : ownerNode.getScene().getWindow();
            if (owner != null) {
                detailStage.initOwner(owner);
            }
            Scene scene = new Scene(content, width, height);
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
        if (owner == null) {
            stage.centerOnScreen();
            return;
        }
        stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
        stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);
    }
}
