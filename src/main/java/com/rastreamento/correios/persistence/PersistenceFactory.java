package com.rastreamento.correios.persistence;

import com.rastreamento.correios.model.Configuration;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

public class PersistenceFactory {

    private static EntityManager entityManager;
    private static EntityManagerFactory emf;

    public PersistenceFactory(Configuration config) throws PersistenciaException {

        Map<String, String> persistenceMap = new HashMap<>();
        String strConn = "";

        String user = "";
        String pass = "";
        String driver = "";
        String dialect = "";

        switch (config.getConfigurationType()) {
            case MYSQL:
                String host = config.getMysqlHost();
                String port = config.getMysqlPort();
                String dataBase = config.getMysqlDataBase();
                user = config.getMysqlUser();
                pass = config.getMysqlPass();

                strConn = "jdbc:mysql://" + host + ":" + port + "/" + dataBase + "?zeroDateTimeBehavior=convertToNull";
                driver = "com.mysql.cj.jdbc.Driver";
                dialect  = "org.hibernate.dialect.MySQL5InnoDBDialect";
                break;
            case LOCAL:
                strConn = "jdbc:h2:~/rastreamento";
                user = "sa";
                pass = "";
                driver = "org.h2.Driver";
                dialect = "org.hibernate.dialect.H2Dialect";
                break;
            default:
                break;

        }

        //Connection Properties
        persistenceMap.put("javax.persistence.jdbc.url", strConn);
        persistenceMap.put("javax.persistence.jdbc.user", user);
        persistenceMap.put("javax.persistence.jdbc.password", pass);
        persistenceMap.put("javax.persistence.jdbc.driver", driver);
        persistenceMap.put("javax.persistence.schema-generation.database.action", "update");

        //Hibernate properties
        persistenceMap.put("hibernate.dialect", dialect);
        persistenceMap.put("hibernate.show_sql", "true");
        persistenceMap.put("hibernate.format_sql", "true");
        persistenceMap.put("hibernate.use_sql_comments", "true");


        try {
            emf = Persistence.createEntityManagerFactory("CONN", persistenceMap);
            entityManager = java.beans.Beans.isDesignTime() ? null : emf.createEntityManager();
        } catch (IllegalArgumentException | PersistenceException | IllegalStateException e) {
            e.printStackTrace();
            throw new PersistenciaException(e.getMessage());
        }

    }


    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static Query getQuery(String sql) throws PersistenciaException {
        try {
            Query query = entityManager.createQuery(sql);

            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            return query;
        } catch (IllegalArgumentException | PersistenceException | IllegalStateException e) {
            throw new PersistenciaException(e.getMessage());
        }
    }

    public static Object getSingleResult(Query query) throws PersistenciaException, NoResult {
        try {
            return query.setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            throw new NoResult(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException | PersistenceException e) {
            throw new PersistenciaException(e.getMessage());
        }
    }

    public static void initTransaction() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }

    public static void rollBack() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }

    public static void commit() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }
}
