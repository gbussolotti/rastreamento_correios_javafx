package com.rastreamento.correios.utils;

import javafx.scene.control.Alert;

public class Erro extends Throwable {

    public Erro(Alert.AlertType type,String title, String msg){
        this.type = type;
        this.msg = msg;
        this.title = title;
    }

    /**
     *
     * @param msg
     */
    public Erro(String msg) {
        super(msg);
    }

    private Alert.AlertType type;
    private String msg;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Alert.AlertType getType() {
        return type;
    }

    public void setType(Alert.AlertType type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
