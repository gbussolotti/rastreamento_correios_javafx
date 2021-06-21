package com.rastreamento.correios.model;

public enum ConfigurationType {

    LOCAL("Local"), MYSQL("Mysql"), NUVEM("Nuvem");

    private final String description;

    ConfigurationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.description;
    }

}
