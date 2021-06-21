package com.rastreamento.correios.model;

public class Configuration {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private ConfigurationType configurationType;

    private String mysqlHost;
    private String mysqlDataBase;
    private String mysqlUser;
    private String mysqlPass;
    private String mysqlPort;

    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(String configurationType) {
        for (ConfigurationType c : ConfigurationType.values()) {
            if (c.toString().equals(configurationType)) {
                this.configurationType = c;
                return;
            }
        }
        this.configurationType = ConfigurationType.LOCAL;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public void setMysqlHost(String mysqlHost) {
        this.mysqlHost = mysqlHost;
    }

    public String getMysqlDataBase() {
        return mysqlDataBase;
    }

    public void setMysqlDataBase(String mysqlDataBase) {
        this.mysqlDataBase = mysqlDataBase;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public void setMysqlUser(String mysqlUser) {
        this.mysqlUser = mysqlUser;
    }

    public String getMysqlPass() {
        return mysqlPass;
    }

    public void setMysqlPass(String mysqlPass) {
        this.mysqlPass = mysqlPass;
    }

    public String getMysqlPort() {
        return mysqlPort;
    }

    public void setMysqlPort(String mysqlPort) {
        this.mysqlPort = "3306";
        try {
            int portNumber = Integer.parseInt(mysqlPort != null ? mysqlPort : "3306");
            if (portNumber >= 1 && portNumber <= 65536) {
                this.mysqlPort = "" + portNumber;
            }
        } catch (NumberFormatException e) {
        }
    }

}
