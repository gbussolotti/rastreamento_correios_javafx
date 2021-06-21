package com.rastreamento.correios.dataset;

import com.rastreamento.correios.model.CadItem;
import com.rastreamento.correios.persistence.NoResult;
import com.rastreamento.correios.persistence.PersistenceFactory;
import com.rastreamento.correios.persistence.PersistenciaException;

import javax.persistence.Query;
import java.util.List;

/**
 *
 * @author Gustavo
 */
public class DataSetCadItem extends dao {

    @Override
    public List consultaCompleta() throws PersistenciaException {
        String sql = "SELECT c FROM CadItem c WHERE 1 = 1 ";
        Query query = PersistenceFactory.getQuery(sql);
        return query.getResultList();
    }


    public List consultaCompletaItensAtivos() throws PersistenciaException {
        String sql = "SELECT c FROM CadItem c WHERE c.toConsult <> 0";
        Query query = PersistenceFactory.getQuery(sql);
        return query.getResultList();
    }

    @Override
    public Object consultaEspecifica(Object id) throws PersistenciaException, NoResult {
        /*
        String nomeDispositivo = id.toString();
        String sql = "SELECT c FROM CadItem c WHERE c.codigoRastreio LIKE :rastreio";
        Query query = PersistenceFactory.getQuery(sql);
        query.setParameter("rastreio", nomeDispositivo);
        return (CadItem) PersistenceFactory.getSingleResult(query);
         */
        return null;
    }

    @Override
    public Object inserir(Object obj) throws PersistenciaException {
        return super.inserir(obj);
    }

    @Override
    public void excluir(Object obj) throws PersistenciaException {
        super.excluir(obj);
    }

}
