package com.rastreamento.correios.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import com.rastreamento.correios.model.CadItem;
import com.rastreamento.correios.model.CadItemStatus;
import com.rastreamento.correios.utils.StringUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Gustavo Nascimento Bussolotti
 */
public class ItemPane {

    /**
     *
     * @param item
     * @return
     */
    public static Node generateItemPane(CadItem item) {
        //before generate the pane, will order the listStatus.
        //Check the last status if has address
        boolean hasStatus = false;
        if (item.getItemStatusList() != null && item.getItemStatusList().size() > 0) {
            hasStatus = true;
            Collections.sort(item.getItemStatusList());
        }

        HBox principalPane = new HBox();
        principalPane.setSpacing(20);
        //painelPrincipal.setStyle("-fx-background-color: #c8ced3");

        VBox boxQtdeDays = new VBox();
        boxQtdeDays.setAlignment(Pos.CENTER);

        String daysText = "---\nDias";

        //Calculate diff between today and the dtPost as days
        if (item.getDtPost() != null) {
            long diff = Math.abs(ChronoUnit.DAYS.between(LocalDateTime.now(), item.getDtPost().atStartOfDay()));
            daysText = (diff == 1) ? (diff + "\nDia") : (diff + "\nDias");
        }

        Label labelDays = new Label(daysText);
        labelDays.setTextAlignment(TextAlignment.CENTER);
        labelDays.setStyle("-fx-text-fill: white; -fx-font-weight:bold");

        //Add the label days int box Days
        boxQtdeDays.getChildren().addAll(labelDays);

        VBox boxDescriptionItem = new VBox();
        boxDescriptionItem.setAlignment(Pos.CENTER_LEFT);

        TextField labelTrackCode = new TextField(item.getTrackCode());
        //Label labelTrackCode = new Label(item.getCodigoRastreio());
        //labelTrackCode.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight:bold");

        Label labelDescription = new Label(item.getDescription());
        labelDescription.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight:bold");

        String textDescriptionUpdate = item.getDescriptioUpdate();

        if (hasStatus) {
            CadItemStatus st = item.getItemStatusList().get(0);
            if (!StringUtils.isNullOrEmpty(st.getDestinationCity())) {
                textDescriptionUpdate += "\n"
                        + "De: " + StringUtils.stringNotNull(st.getEventCity()) + "/"
                        + StringUtils.stringNotNull(st.getEventUf())
                        + "   Para: "
                        + StringUtils.stringNotNull(st.getDestinationCity()) + "/"
                        + StringUtils.stringNotNull(st.getDestinationUf());
            }

        }
        Label labelDescriptionUpdate = new Label(textDescriptionUpdate);
        labelDescriptionUpdate.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight:bold");

        boxDescriptionItem.getChildren().addAll(labelTrackCode, labelDescription, labelDescriptionUpdate);

        VBox boxDateTime = new VBox();
        boxDateTime.setAlignment(Pos.CENTER_RIGHT);


        Label labelDate = new Label(item.getDtUpdate() != null ? item.getDtUpdate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")): "");
        labelDate.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight:bold");

        Label labelHour = new Label(item.getDtUpdate() != null ? item.getDtUpdate().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        labelHour.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight:bold");

        boxDateTime.getChildren();
        boxDateTime.getChildren().addAll(labelDate, labelHour);

        principalPane.getChildren();
        principalPane.getChildren().addAll(boxQtdeDays, boxDescriptionItem, boxDateTime);

        TitledPane containerTitled = new TitledPane();
        containerTitled.setGraphic(principalPane);

        if (hasStatus) {
            //Add the status pane
            //Generate one pane for each Status
            VBox box = new VBox();
            AnchorPane internalPaneTitled = new AnchorPane(box);
            internalPaneTitled.setStyle("-fx-background-color: #333");

            AnchorPane.setBottomAnchor(box, 0.0);
            AnchorPane.setTopAnchor(box, 0.0);
            AnchorPane.setLeftAnchor(box, 0.0);
            AnchorPane.setRightAnchor(box, 0.0);

            item.getItemStatusList().forEach((is) -> {
                box.getChildren().add(StatusPane.generateStatusPane(is));
                //box.getChildren().add(new Separator(Orientation.HORIZONTAL));
            });
            containerTitled.setContent(internalPaneTitled);
        }

        return containerTitled;
    }

}
