package com.rastreamento.correios.controller;

import com.rastreamento.correios.dataset.ConfigurationHSQLDB;
import com.rastreamento.correios.dataset.DataSetCadItem;
import com.rastreamento.correios.model.CadItem;
import com.rastreamento.correios.model.Configuration;
import com.rastreamento.correios.model.ConfigurationType;
import com.rastreamento.correios.persistence.PersistenceFactory;
import com.rastreamento.correios.persistence.PersistenciaException;
import com.rastreamento.correios.utils.Erro;
import com.rastreamento.correios.utils.StringUtils;
import com.rastreamento.correios.utils.SystemApp;
import com.rastreamento.correios.utils.WebService;
import com.rastreamento.correios.view.ItemPane;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.MaskerPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InitialController implements Initializable {

    public static Configuration CONFIGURATION;
    private List<CadItem> listItens = null;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listItens = new ArrayList<>();
        CONFIGURATION = new Configuration();
        CONFIGURATION.setConfigurationType(ConfigurationType.LOCAL.toString());

        root.getChildren().add(masker);
        masker.visibleProperty().bind(maskerOn);

        //Add CSS
        root.getStylesheets().add(getClass().getResource("/css/initial.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource("/css/titledPane.css").toExternalForm());

        maskerOn(null);

        //Read or create initial configuration
        Task<Erro> task = new Task<Erro>() {

            @Override
            public Erro call() throws Exception {


                //Initialize the masker
                Platform.runLater(() -> {
                    masker.setPrefSize(root.getWidth(), root.getHeight());
                    masker.setText("Aguarde a leitura das configurações iniciais...");
                });

                ConfigurationHSQLDB configHSQLDB = new ConfigurationHSQLDB();
                try {
                    CONFIGURATION = configHSQLDB.createConnectionAndReadInitialConfiguration();
                } catch (Erro ex) {
                    return ex;
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {

            Erro returnTask = task.valueProperty().get();
            if (returnTask != null) {
                //Initialize the masker
                Platform.runLater(() -> {
                    masker.setText("Erro ao ler as configurações iniciais:\n"+ returnTask);
                });
                System.err.println(returnTask);
                //SystemApp.ALERT(returnTask.getType(), returnTask.getTitle(), returnTask.getMsg(), root);
            }else{
                maskerOff();
            }
        });
        new Thread(task).start();
    }


    private void maskerOn(String msg) {
        maskerOn.set(true);
        masker.toFront();
        masker.setPrefSize(root.getWidth(), root.getHeight());
        if(!StringUtils.isNullOrEmpty(msg)){
            Platform.runLater(() -> {
                masker.setText(msg);
            });
        }
    }

    private void maskerOff() {
        maskerOn.set(false);
        masker.toBack();
    }

    @FXML
    void actionAdd(ActionEvent event) {

        //Open add form
        FXMLLoader loader = SystemApp.openWindow(AddItemController.PATH, root, "Adicionar item", true);

        //Get controller
        AddItemController controller = loader.<AddItemController>getController();

        //Get the return from controller
        //If null, do nothing. Window closed by user
        //If not null, consume web service
        CadItem returnItem = controller.getRetorno();
        if (returnItem != null) {
            maskerOn("Aguarde... Exibindo informações...\nObjeto: " + returnItem.getTrackCode());

            Task<Erro> task;
            task = new Task<Erro>() {

                @Override
                public Erro call() throws Exception {

                    CadItem internalItem = returnItem;
                    try {
                        //mark for to consult
                        internalItem.setToConsult(Boolean.TRUE);

                        //Consume web service
                        WebService ws = new WebService();
                        internalItem = (CadItem) ws.consumirWebService(returnItem);

                        //Update masker text
                        Platform.runLater(() -> {
                            masker.setText("Aguarde... Atualizando banco de dados");
                        });

                        //Create the persistence
                        PersistenceFactory ps = new PersistenceFactory(CONFIGURATION);

                        internalItem = (CadItem) new DataSetCadItem().inserir(internalItem);
                        listItens.add(internalItem);

                    } catch (PersistenciaException ex) {
                        ex.printStackTrace();
                        return new Erro(Alert.AlertType.ERROR, "Erro na camada de persistência", ex.getMessage());
                    } catch (Erro ex) {
                        return ex;
                    }
                    return null;
                }
            };

            taskOnSucceededFinalize(task);

            new Thread(task).start();
        }
    }

    @FXML
    void actionUpdate(ActionEvent event) {
        maskerOn("Aguarde... Consultando objetos no banco de dados");
        jPaneItens.getChildren();

        Task<Erro> task;
        task = new Task<Erro>() {

            @Override
            public Erro call() throws Exception {
                DataSetCadItem ds = new DataSetCadItem();

                try {
                    //Create the persistence
                    PersistenceFactory ps = new PersistenceFactory(CONFIGURATION);

                    //Consulta no banco de dados os itens que ainda estão pendentes
                    listItens = ds.consultaCompletaItensAtivos();
                    WebService ws = new WebService();
                    try {
                        listItens = (List<CadItem>) ws.consumirWebService(listItens);

                        //Atualiza o banco de dados
                        for (CadItem i : listItens) {
                            i = (CadItem) ds.inserir(i);
                        }
                    } catch (Erro ex) {
                        return ex;
                    }
                } catch (PersistenciaException ex) {
                    return new Erro(Alert.AlertType.ERROR, "Erro na camada de persistência", ex.getMessage());
                }
                return null;
            }
        };

        taskOnSucceededFinalize(task);
        new Thread(task).start();
    }

    @FXML
    void actionConfig(ActionEvent event) {
        SystemApp.openWindow(ConfigurationController.PATH, root, "Configuração", true);
    }

    private void taskOnSucceededFinalize(Task<Erro> task){
        task.setOnSucceeded(e -> {
            maskerOff();
            Erro returnTask = task.valueProperty().get();
            if(returnTask == null){
                //update View
                updatePaneItens();
            }else{
                SystemApp.ALERT(returnTask.getType(), returnTask.getTitle(), returnTask.getMsg(), root);
            }
        });

    }


    private void updatePaneItens() {
        //clean the pane
        jPaneItens.getChildren();
        jPaneItens.getChildren().clear();

        //initialize masker
        maskerOn("");

        new Thread() {
            @Override
            public void run() {

                //Order the list
                Collections.sort(listItens);

                //Filter list itens for show only active itens
                List<CadItem> listaConsultar = listItens
                        .stream()
                        .filter(c -> c.getToConsult())
                        .collect(Collectors.toList());

                //generate a pane for each item
                listaConsultar.forEach((i) -> {

                    Platform.runLater(() -> {

                        masker.setText("Aguarde... Exibindo informações...\nObjeto: " + i.getTrackCode());

                        //Irá gerar o painel com as informações do item
                        Node h = ItemPane.generateItemPane(i);
                        Separator s = new Separator(Orientation.HORIZONTAL);

                        jPaneItens.getChildren().add(h);
                    });
                });
            }
        }.start();

        maskerOff();
    }


    @FXML
    private AnchorPane root;

    @FXML
    private VBox jPaneItens;

    private final BooleanProperty maskerOn = new SimpleBooleanProperty(false);
    private final MaskerPane masker = new MaskerPane();

    public static String PATH = "/view/initial.fxml";
}
