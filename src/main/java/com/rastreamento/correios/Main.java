package com.rastreamento.correios;

import com.rastreamento.correios.controller.InitialController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource(InitialController.PATH));
        Region pane = (Region) loader.load();

        Scene scene = new Scene(pane);
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/image/icon.png"))));   //Add icon
        primaryStage.setTitle(PROGRAM_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);            //Init maximized
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static String PROGRAM_TITLE = "Gerenciador de Rastreamento";
}
