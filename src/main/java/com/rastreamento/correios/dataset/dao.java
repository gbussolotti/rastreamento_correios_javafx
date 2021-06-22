package com.rastreamento.correios.dataset;

import com.rastreamento.correios.model.Cadastro;
import com.rastreamento.correios.persistence.NoResult;
import com.rastreamento.correios.persistence.PersistenceFactory;
import com.rastreamento.correios.persistence.PersistenciaException;

import javax.persistence.PersistenceException;
import java.util.List;

public abstract class dao {

    public List findAll() throws PersistenciaException {
        return null;
    }

    public Object consultaEspecifica(Object id) throws PersistenciaException, NoResult {
        return id;
    }

    public Object inserir(Object obj) throws PersistenciaException {
        Cadastro _obj = (Cadastro) obj;
        try {
            if (_obj.getId() != null && _obj.getId() > 0) {
                //edit a object
                PersistenceFactory.initTransaction();
                _obj = PersistenceFactory.getEntityManager().merge(_obj);
                PersistenceFactory.commit();
                return _obj;
            } else {
                //Insert a new object
                PersistenceFactory.initTransaction();
                PersistenceFactory.getEntityManager().persist(_obj);
                PersistenceFactory.getEntityManager().flush();
                PersistenceFactory.commit();

                return _obj;
            }
        } catch (IllegalArgumentException | PersistenceException | IllegalStateException e) {
            PersistenceFactory.rollBack();
            throw new PersistenciaException(e.getMessage());
        }
    }

    public void excluir(Object obj) throws PersistenciaException {
        try {
            PersistenceFactory.initTransaction();
            PersistenceFactory.getEntityManager().remove(obj);
            PersistenceFactory.commit();
        } catch (Exception ex) {
            /*
            if (ex instanceof javax.persistence.RollbackException) {
                throw new PersistenciaException("Há registros em outras tabelas que impedem a exclusão. Por favor, inative o registro\n\n" + ex.getMessage());
            }
             */
            throw new PersistenceException(ex.getMessage());
        }
    }
}
