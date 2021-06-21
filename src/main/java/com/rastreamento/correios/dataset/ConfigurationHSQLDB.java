package com.rastreamento.correios.dataset;

import com.rastreamento.correios.model.Configuration;
import com.rastreamento.correios.model.ConfigurationType;
import com.rastreamento.correios.utils.Erro;
import com.rastreamento.correios.utils.StringUtils;

import java.sql.*;

/**
 *
 * @author Gustavo Bussolotti
 */
public class ConfigurationHSQLDB {

    private Connection conn;

    /**
     *
     * @return
     * @throws Erro
     */
    public Configuration createConnectionAndReadInitialConfiguration() throws Erro {
        Statement statement;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");

            conn = DriverManager.getConnection("jdbc:hsqldb:file:configRastreamento;crypt_key=a74a68858bcda65325bf37840abc39ea;crypt_type=blowfish;shutdown=true;hsqldb.write_delay=false", "SA", "");
            //conn = DriverManager.getConnection("jdbc:hsqldb:file:configRastreamento;shutdown=true;hsqldb.write_delay=false", "SA", "");

            if (conn != null) {
                try {
                    String select = "SELECT * FROM CONFIG WHERE ID = 0 LIMIT 1";
                    statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(select);
                    Configuration config = new Configuration();
                    if (rs.next()) {
                        config.setMysqlDataBase(rs.getString("MYSQL_DATABASE"));
                        config.setMysqlHost(rs.getString("MYSQL_HOST"));
                        config.setMysqlUser(rs.getString("MYSQL_USER"));
                        config.setMysqlPass(rs.getString("MYSQL_PASS"));
                        config.setMysqlPort(rs.getString("MYSQL_PORT"));
                        config.setConfigurationType(rs.getString("CONFIGURATION_TYPE"));

                        rs.close();
                        statement.close();
                        conn.close();
                        return config;
                    } else {
                        //insert a default value
                        String insertDefaultValue = "INSERT INTO CONFIG (CONFIGURATION_TYPE) VALUES('" + ConfigurationType.LOCAL.toString() + "' )";
                        statement = conn.createStatement();
                        statement.executeUpdate(insertDefaultValue);
                        statement.close();
                        conn.close();

                        config.setConfigurationType(ConfigurationType.LOCAL.toString());
                        return config;
                    }

                } catch (SQLException e) {
                    Configuration config = new Configuration();
                    try {
                        //Database not configured. Create a default configuration
                        String createTableConfig = ""
                                + "DROP TABLE CONFIG IF EXISTS;"
                                + "CREATE TABLE CONFIG(ID INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL CONSTRAINT id,"
                                + "CONFIGURATION_TYPE varchar(30) default null,"
                                + "MYSQL_HOST varchar(50) default null, "
                                + "MYSQL_DATABASE varchar(50) default null, "
                                + "MYSQL_USER varchar(50) default null, "
                                + "MYSQL_PASS varchar(50) default null, "
                                + "MYSQL_PORT varchar(4) default null);";

                        statement = conn.createStatement();
                        statement.executeUpdate(createTableConfig);
                        statement.close();

                        //Insert a default value
                        String insertDefaultValue = "INSERT INTO CONFIG (CONFIGURATION_TYPE) VALUES('" + ConfigurationType.LOCAL.toString() + "' )";
                        statement = conn.createStatement();
                        statement.executeUpdate(insertDefaultValue);
                        statement.close();
                        conn.close();

                        config.setConfigurationType(ConfigurationType.LOCAL.toString());
                        return config;
                    } catch (SQLException ee) {
                        ee.printStackTrace();
                        throw new Erro("Erro " + e.getClass() + ": " + e.getMessage());
                    }
                }
            } else {
                throw new Erro("Connection null: HSQLDB");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new Erro("Erro " + e.getClass() + ": " + e.getMessage());
        }

    }

    /**
     *
     * @param c
     * @throws Erro
     */
    public void saveConfiguration(Configuration c) throws Erro {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            //conn = DriverManager.getConnection("jdbc:hsqldb:file:configRastreamento;shutdown=true;hsqldb.write_delay=false", "SA", "");
            conn = DriverManager.getConnection("jdbc:hsqldb:file:configRastreamento;shutdown=true;hsqldb.write_delay=false", "SA", "");

            if (conn != null) {
                try {
                    String select = "SELECT * FROM CONFIG WHERE ID = 0 LIMIT 1";
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery(select);

                    if (rs.next()) {
                        rs.close();
                        statement.close();

                        String stringUpdate = "UPDATE CONFIG SET CONFIGURATION_TYPE = " + StringUtils.stringBD(c.getConfigurationType().toString()) + ", "
                                + "MYSQL_HOST = " + StringUtils.stringBD(c.getMysqlHost()) + ", "
                                + "MYSQL_PORT = " + StringUtils.stringBD(c.getMysqlPort()) + ", "
                                + "MYSQL_DATABASE = " + StringUtils.stringBD(c.getMysqlDataBase()) + ", "
                                + "MYSQL_USER = " + StringUtils.stringBD(c.getMysqlUser()) + ", "
                                + "MYSQL_PASS = " + StringUtils.stringBD(c.getMysqlPass()) + " WHERE ID = 0";

                        statement = conn.createStatement();
                        statement.executeUpdate(stringUpdate);
                        statement.close();
                        conn.close();
                    }

                } catch (SQLException e) {
                    throw e;
                }
            } else {
                throw new Erro("Connection null: HSQLDB");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new Erro("Erro " + e.getClass() + ": " + e.getMessage());
        }

    }

}