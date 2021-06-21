package com.rastreamento.correios.controller;


import java.net.URL;
import java.util.ResourceBundle;

import com.rastreamento.correios.dataset.ConfigurationHSQLDB;
import com.rastreamento.correios.model.Configuration;
import com.rastreamento.correios.model.ConfigurationType;
import com.rastreamento.correios.utils.Erro;
import com.rastreamento.correios.utils.StringUtils;
import com.rastreamento.correios.utils.SystemApp;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Gustavo Bussolotti
 */
public class ConfigurationController implements Initializable {

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializeFields();
    }

    private void initializeFields() {
        //Initialize ComboBox Type Configuration
        jCTypeStorage.setItems(FXCollections.observableArrayList(ConfigurationType.values()));
        jCTypeStorage.getSelectionModel().select(InitialController.CONFIGURATION.getConfigurationType());
        changeSelectionTypeConfiguration();

        //Mysql Fields
        jTDataBaseMysql.setText(StringUtils.stringNotNull(InitialController.CONFIGURATION.getMysqlDataBase()));
        jTHostMysql.setText(StringUtils.stringNotNull(InitialController.CONFIGURATION.getMysqlHost()));
        jTPassMysql.setText(StringUtils.stringNotNull(InitialController.CONFIGURATION.getMysqlPass()));
        jTPortMysql.setText(StringUtils.stringNotNull(InitialController.CONFIGURATION.getMysqlPort()));
        jTUserMysql.setText(StringUtils.stringNotNull(InitialController.CONFIGURATION.getMysqlUser()));


    }

    @FXML
    void actionTypeStorage(ActionEvent event) {
        changeSelectionTypeConfiguration();
    }

    private void changeSelectionTypeConfiguration() {
        boolean visibleMysql = false;
        boolean visibleSocial = false;

        if (jCTypeStorage.getSelectionModel().getSelectedItem() == ConfigurationType.MYSQL) {
            visibleMysql = true;
        } else if (jCTypeStorage.getSelectionModel().getSelectedItem() == ConfigurationType.NUVEM) {
            visibleSocial = true;
        }

        jPaneSocial.setVisible(visibleSocial);
        jPaneMysql.setVisible(visibleMysql);

    }

    @FXML
    private void actionSave(ActionEvent event) {
        //Validate the fields
        Configuration config = new Configuration();
        try {
            if (null != jCTypeStorage.getSelectionModel().getSelectedItem()) switch (jCTypeStorage.getSelectionModel().getSelectedItem()) {
                case MYSQL:
                    String mysqlHost = jTHostMysql.getText();
                    if (StringUtils.isNullOrEmpty(mysqlHost)) {
                        jTHostMysql.requestFocus();
                        throw new Erro("Campo do host servidor Mysql não foi preenchido");
                    }   String mysqlPort = "3306";
                    try {
                        int portaMysql_ = Integer.parseInt(jTPortMysql.getText());
                        if (portaMysql_ <= 0 || portaMysql_ >= 65536) {
                            throw new NumberFormatException();
                        }
                        mysqlPort = "" + portaMysql_;
                    } catch (NumberFormatException e) {
                        jTPortMysql.requestFocus();
                        throw new Erro("Porta do servidor mysql não está no formato correto. Deve ser informada a porta com numeração entre 1 e 65536. Porta default 3306");
                    }   String mysqlDataBase = jTDataBaseMysql.getText();
                    if (mysqlDataBase == null || mysqlDataBase.isEmpty()) {
                        jTDataBaseMysql.requestFocus();
                        throw new Erro("Nome do banco de dados não foi informado");
                    }   String mysqlUser = jTUserMysql.getText();
                    if (StringUtils.isNullOrEmpty(mysqlUser)) {
                        jTUserMysql.requestFocus();
                        throw new Erro("O usuário do banco de dados não foi informado");
                    }   String mysqlPass = jTPassMysql.getText();
                    config.setConfigurationType(ConfigurationType.MYSQL.toString());
                    config.setMysqlHost(mysqlHost);
                    config.setMysqlPort(mysqlPort);
                    config.setMysqlDataBase(mysqlDataBase);
                    config.setMysqlUser(mysqlUser);
                    config.setMysqlPass(mysqlPass);
                    break;
                case LOCAL:
                    config.setConfigurationType(ConfigurationType.LOCAL.toString());
                    break;
                case NUVEM:
                    break;
                default:
                    break;
            }

            //Save in HSQLDB the configuration
            ConfigurationHSQLDB configDB = new ConfigurationHSQLDB();
            configDB.saveConfiguration(config);
            InitialController.CONFIGURATION = config;

            SystemApp.ALERT(Alert.AlertType.INFORMATION, null, "Configurações salvas com sucesso. Reinicie o aplicativo.", jTHostMysql);

            //close configuration window
            (((Node) event.getSource()).getScene()).getWindow().hide();

        } catch (Erro e) {
            SystemApp.ALERT(Alert.AlertType.ERROR, "Erro ao gravar configurações", e.getMessage(), root);
        }

    }

    @FXML
    private void actionCancel(ActionEvent event) {
        //Fecha a janela
        (((Node) event.getSource()).getScene()).getWindow().hide();
    }


    public static final String PATH = "/view/configuration.fxml";

    @FXML
    private TextField jTHostMysql;

    @FXML
    private TextField jTPortMysql;

    @FXML
    private TextField jTDataBaseMysql;

    @FXML
    private TextField jTUserMysql;

    @FXML
    private PasswordField jTPassMysql;

    @FXML
    private ComboBox<ConfigurationType> jCTypeStorage;

    @FXML
    private Button jBSave;

    @FXML
    private Button jBCancel;

    @FXML
    private Pane jPaneMysql;

    @FXML
    private Pane jPaneSocial;

    @FXML
    private AnchorPane root;

}
