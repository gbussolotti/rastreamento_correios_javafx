package com.rastreamento.correios.persistence;

import com.rastreamento.correios.model.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class NoResult extends Throwable {

    /**
     *
     * @param msg
     */
    public NoResult(String msg) {
        super(msg);
    }
}