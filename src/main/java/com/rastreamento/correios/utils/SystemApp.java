package com.rastreamento.correios.utils;

import com.rastreamento.correios.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SystemApp {

    /**
     *  @param path
     * @param parentRoot
     * @param windowName
     * @param modal
     * @return
     */
    public static FXMLLoader openWindow(String path, Node parentRoot, String windowName, boolean modal) {

        try {
            FXMLLoader loader = new FXMLLoader(SystemApp.class.getResource(path));
            Stage palco = new Stage();
            Parent pane = (Parent) loader.load();
            Scene scene = new Scene(pane);
            palco.setScene(scene);
            if (modal) {
                palco.initModality(Modality.WINDOW_MODAL);
            }

            palco.initOwner((parentRoot).getScene().getWindow());
            palco.setTitle(windowName);
            palco.showAndWait();
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            ALERT(Alert.AlertType.INFORMATION, "Erro ao abrir janela", e.getMessage(), parentRoot);
        }
        return null;
    }

    public static void ALERT(Alert.AlertType tipo, String HeaderText, String ContextText, Node parent) {
        Alert alert = new Alert(tipo);
        alert.setTitle(Main.PROGRAM_TITLE);
        alert.setHeaderText(HeaderText);
        alert.setContentText(ContextText);
        alert.initOwner(parent.getScene().getWindow());
        alert.showAndWait();
    }
}
