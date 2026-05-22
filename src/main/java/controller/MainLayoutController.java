package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private void openVehicle() {
        loadView("/view/vehicle-view.fxml");
    }

    @FXML
    private void openVehicleDocument() {
        loadView("/view/vehicle-document-view.fxml");
    }

    @FXML
    private void openDocumentAlert() {
        loadView("/view/document-alert-view.fxml");
    }

    private void loadView(String path) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}