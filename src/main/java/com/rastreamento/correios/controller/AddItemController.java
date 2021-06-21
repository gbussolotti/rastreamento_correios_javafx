package com.rastreamento.correios.controller;

import com.rastreamento.correios.model.CadItem;
import com.rastreamento.correios.utils.Erro;
import com.rastreamento.correios.utils.StringUtils;
import com.rastreamento.correios.utils.SystemApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FXML Controller class
 *
 * @author Gustavo Bussolotti
 */
public class AddItemController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void actionAdd(ActionEvent actionEvent) {
        //Validate the insert form
        try {
            if (StringUtils.isNullOrEmpty(jTDescription.getText())) {
                jTDescription.requestFocus();
                throw new Erro("A descrição não foi informada");
            }

            //test regex trackCode
            //13 characteres
            //A-Z - 2 chars
            //0-9 - 9 chars
            //A-Z - 2 chars
            Pattern pattern = Pattern.compile("^([a-zA-Z]{2}[\\d]{9}[a-zA-Z]{2})$");
            Matcher matcher = pattern.matcher(jTTrackNumber.getText());
            if (matcher.find()) {
                //Cria um novo objeto que será enviado para o InicialController
                returnObject = returnObject == null ? new CadItem() : returnObject;

                //setters
                returnObject.setDescription(jTDescription.getText());
                returnObject.setTrackCode(jTTrackNumber.getText());
                returnObject.setDtInsert(returnObject.getDtInsert() == null ? LocalDateTime.now() : returnObject.getDtInsert());

                //Close Window
                (((Node) actionEvent.getSource()).getScene()).getWindow().hide();

            } else {
                //Match not found
                jTTrackNumber.selectAll();
                jTTrackNumber.requestFocus();
                throw new Erro("O código informado não está correto\n\n"
                        + "O código de rastreio deve ser informado da seguinte forma: \n"
                        + "13 caracteres\n"
                        + "2 primeiros caracteres: A-Z\n"
                        + "9 caracteres seguintes: 0-9\n"
                        + "2 últimos caracteres: A-Z");
            }

        } catch (Erro ex) {
            //error in form
            SystemApp.ALERT(Alert.AlertType.INFORMATION, null, ex.getMessage(), root);
        }
    }

    @FXML
    public void actionCancel(ActionEvent actionEvent) {
        (((Node) actionEvent.getSource()).getScene()).getWindow().hide();
    }


    public CadItem getRetorno() {
        return returnObject;
    }

    private CadItem returnObject;

    @FXML
    private TextField jTDescription;

    @FXML
    private TextField jTTrackNumber;

    @FXML
    private AnchorPane root;

    public static final String PATH = "/view/addItem.fxml";


}

