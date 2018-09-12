package br.com.eddydata.minhacidade.util;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class Repositorio<T> {

    private final Class<T> entityClass;

    public Repositorio(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public T inserir(T entity) throws EddyServerException {
        if (entity == null) {
            throw new EddyServerException("Entidade não passada para ser salva", ErrorCode.SERVER_ERROR.getCode());
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(entity);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            getEntityManager().getTransaction().rollback();
            throw new EddyServerException("Erro ao atualizar registro no banco de dados: " + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        } finally {
            getEntityManager().close();
        }
        return entity;
    }

    public T salvar(T entity) throws EddyServerException {
        if (entity == null) {
            throw new EddyServerException("Entidade não passada para ser salva", ErrorCode.SERVER_ERROR.getCode());
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(entity);
            getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            getEntityManager().getTransaction().rollback();
            throw new EddyServerException("Erro ao salvar registro no banco de dados: " + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        } finally {
            getEntityManager().close();
        }
        return entity;
    }

    public void remover(T entity) throws EddyServerException {
        if (entity == null) {
            throw new EddyServerException("Entidade não passada para ser removida", ErrorCode.SERVER_ERROR.getCode());
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(getEntityManager().merge(entity));
            getEntityManager().getTransaction().commit();
        } catch (Exception ex) {
            getEntityManager().getTransaction().rollback();
            throw new EddyServerException("Erro ao remover registro do banco de dados: " + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        } finally {
            getEntityManager().close();
        }
    }

    public T obter(Object id) throws EddyServerException {
        if (id == null) {
            throw new EddyServerException("Id não passado para consulta", ErrorCode.SERVER_ERROR.getCode());
        }
        try {
            return getEntityManager().find(entityClass, id);
        } catch (Exception ex) {
            throw new EddyServerException("Erro ao buscar registro por id no banco de dados: " + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        } finally {
            getEntityManager().close();
        }
    }

	protected <T> T obter(Class<T> classeToCast, String query, Object... valores) throws EddyServerException {
        Query qr = criarConsulta(query, valores);
        qr.setMaxResults(1);
        List resultList = qr.getResultList();
		List<T> list = resultList;
        return list.isEmpty() ? null : (T) list.get(0);
    }

    public List<T> listar() throws EddyServerException {
        try {
            CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (Exception ex) {
            throw new EddyServerException("Erro ao recuperar os registros do banco: " + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        } finally {
            getEntityManager().close();
        }
    }

    /**
     * @param <T>
     * @param classeToCast Tipo a ser retornado da lista
     * @param query        HQL para a consulta
     * @param valores      parametros do HQL
     * @return
     */
    protected <T> List<T> listar(Class<T> classeToCast, String query, Object... valores) throws EddyServerException {
        try {
            Query qr = criarConsulta(query, valores);
            return qr.getResultList();
        } catch (EddyServerException ex) {
            throw new EddyServerException("Erro ao buscar lista no banco de dados: " + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        } finally {
            getEntityManager().close();
        }
    }

    protected <T> List<T> listar(Class<T> classeToCast, String query, Integer firstRow, Integer rowCount, Object... valores) throws EddyServerException {
        if (firstRow == null) {
            firstRow = 0;
        }
        if (rowCount == null) {
            rowCount = 0;
        }
        Query qr;
        if (rowCount > 0) {
            qr = criarConsulta(query, valores).setFirstResult(firstRow).setMaxResults(rowCount);
        } else {
            qr = criarConsulta(query, valores);
        }
        return qr.getResultList();
    }
    
    public List<T> listar(Integer inicio, Integer qtde) throws EddyServerException {
        if (inicio == null) {
            inicio = 0;
        }
        if (qtde == null) {
            qtde = 0;
        }
        try {
            CriteriaQuery<Object> cq = getEntityManager().getCriteriaBuilder().createQuery();
            cq.select(cq.from(entityClass));
            Query qr = getEntityManager().createQuery(cq);
            if (qtde > 0) {
                qr = qr.setFirstResult(inicio).setMaxResults(qtde);
            }
            return qr.getResultList();
        } catch (Exception ex) {
            throw new EddyServerException("Erro ao recuperar os registros do banco: " + ex.getMessage(), ErrorCode.SERVER_ERROR.getCode());
        } finally {
            getEntityManager().close();
        }
    }

    public int contar() throws EddyServerException {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    protected int contar(Class<T> classeToCast, String query, Integer firstRow, Integer rowCount, Object... valores) {
        Query qr;
        if (firstRow == null) {
            firstRow = 0;
        }
        if (rowCount == null) {
            rowCount = 0;
        }
        if (rowCount > 0) {
            qr = criarConsulta(query, valores).setFirstResult(firstRow).setMaxResults(rowCount);
        } else {
            qr = criarConsulta(query, valores);
        }
        return qr.getResultList().size();
    }

    protected Query criarConsulta(String query, Object... valores) throws EddyServerException {
        if (query == null || query.trim().isEmpty()) {
            throw new EddyServerException("Query não passada para consulta", ErrorCode.SERVER_ERROR.getCode());
        }
        Query qr = getEntityManager().createQuery(query);
        for (int i = 0; i < valores.length; i++) {
            qr.setParameter((i + 1), valores[i]);
        }
        return qr;
    }

    protected int executarComando(String query, Object... valores) throws EddyServerException {
        Query qr = getEntityManager().createNativeQuery(query);
        for (int i = 0; i < valores.length; i++) {
            qr.setParameter((i + 1), valores[i]);
        }
        int result = qr.executeUpdate();
        return result;
    }

}
