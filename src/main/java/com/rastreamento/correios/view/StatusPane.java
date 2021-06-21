package com.rastreamento.correios.view;

import java.time.format.DateTimeFormatter;

import com.rastreamento.correios.model.CadItemStatus;
import com.rastreamento.correios.utils.StringUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Gustavo Nascimento Bussolotti
 */
public class StatusPane {

    public static Node generateStatusPane(CadItemStatus itemStatus) {
        HBox statusPane = new HBox();
        statusPane.setSpacing(10);

        Label labelDate = new Label(itemStatus.getDtEvent().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        labelDate.setStyle("-fx-text-fill: white;");

        Label labelHour = new Label(itemStatus.getDtEvent().format(DateTimeFormatter.ofPattern("HH:mm")));
        labelHour.setStyle("-fx-text-fill: white;");

        labelHour.setTextAlignment(TextAlignment.CENTER);
        labelDate.setTextAlignment(TextAlignment.CENTER);

        VBox panelDateHour = new VBox(labelDate, labelHour);

        if (!StringUtils.isNullOrEmpty(itemStatus.getEventCity())) {
            Label labelCidade = new Label(itemStatus.getEventCity() + "/" + itemStatus.getEventUf());
            labelCidade.setStyle("-fx-text-fill: white;");
            labelCidade.setTextAlignment(TextAlignment.LEFT);
            panelDateHour.getChildren().add(labelCidade);
        } else {
            //Deverá verificar o pais de origem do item para informar
        }

        panelDateHour.setPrefWidth(120);
        panelDateHour.setMinWidth(120);
        panelDateHour.setMaxWidth(120);
        panelDateHour.setAlignment(Pos.CENTER_LEFT);

        //Create a Vbox for aditional informations
        VBox paneDetails = new VBox();

        paneDetails.setSpacing(5);
        paneDetails.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(paneDetails, Priority.ALWAYS);

        Label labelDescription = new Label(itemStatus.getEventDescription());
        labelDescription.setStyle("-fx-font-weight:bold; -fx-text-fill: white;");

        paneDetails.getChildren().addAll(labelDescription);

        //Verify if have Destination
        if (!StringUtils.isNullOrEmpty(itemStatus.getDestinationCity())) {
            String textDestination = "De: " + StringUtils.stringNotNull(itemStatus.getEventLocal()) + " "
                    + "em " + StringUtils.stringNotNull(itemStatus.getEventCity()) + " / " + StringUtils.stringNotNull(itemStatus.getEventUf()) + " "
                    + "para " + StringUtils.stringNotNull(itemStatus.getDestinationLocal()) + " "
                    + "em " + StringUtils.stringNotNull(itemStatus.getDestinationCity()) + " / " + StringUtils.stringNotNull(itemStatus.getDestinationUf());

            Label labelDestination = new Label(textDestination);
            labelDestination.setWrapText(true);
            labelDestination.setStyle("-fx-text-fill: white;");
            paneDetails.getChildren().addAll(labelDestination);
        }

        //Verify if have Address
        if (!StringUtils.isNullOrEmpty(itemStatus.getAddressLocation())) {
            String textAddress = "Endereço: " + StringUtils.stringNotNull(itemStatus.getAddressStreet()) + ", "
                    + StringUtils.stringNotNull(itemStatus.getAddressNumber()) + ", "
                    + StringUtils.stringNotNull(itemStatus.getAddressBairro()) + ", "
                    + StringUtils.stringNotNull(itemStatus.getAddressLocation()) + "/"
                    + StringUtils.stringNotNull(itemStatus.getAddressUf()) + " - "
                    + StringUtils.stringNotNull(itemStatus.getAddressCep());
            Label labelAddress = new Label(textAddress);
            labelAddress.setWrapText(true);
            labelAddress.setStyle("-fx-text-fill: white;");
            paneDetails.getChildren().addAll(labelAddress);
        }

        //Verify if have Details
        if (!StringUtils.isNullOrEmpty(itemStatus.getEventDetail())) {
            String textDetails = "******* " + StringUtils.stringNotNull(itemStatus.getEventDetail());
            Label labelDetails = new Label(textDetails);
            labelDetails.setWrapText(true);
            labelDetails.setStyle("-fx-text-fill: white;");
            paneDetails.getChildren().addAll(labelDetails);
        }

        statusPane.setOnMouseEntered(e -> statusPane.setStyle("-fx-background-color: #1a1a1a"));
        statusPane.setOnMouseExited(e -> statusPane.setStyle("-fx-background-color: #333"));
        //h.setCursor(Cursor.HAND);
        statusPane.setPadding(new Insets(0, 0, 5, 0));

        statusPane.getChildren().addAll(panelDateHour, paneDetails);
        return statusPane;
    }
}
