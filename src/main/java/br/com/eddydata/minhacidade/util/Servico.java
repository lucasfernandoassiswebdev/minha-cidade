package br.com.eddydata.minhacidade.util;

import java.util.List;

public abstract class Servico<T> {
    
    protected abstract Repositorio<T> getRepositorio() throws EddyServerException;
    
    protected abstract void antesInserir(T entidade) throws EddyServerException;
    
    protected abstract void aposInserir(T entidade) throws EddyServerException;
    
    protected abstract void antesSalvar(T entidade) throws EddyServerException;
    
    protected abstract void aposSalvar(T entidade) throws EddyServerException;
    
    protected abstract void antesRemover(T entidade) throws EddyServerException;
    
    protected abstract void aposRemover(T entidade) throws EddyServerException;
    
    public void inserir(T entidade) throws EddyServerException {
        antesInserir(entidade);
        getRepositorio().inserir(entidade);
        aposInserir(entidade);
    }
    
    public void salvar(T entidade) throws EddyServerException {
        antesSalvar(entidade);
        getRepositorio().salvar(entidade);
        aposSalvar(entidade);
    }
    
    public void remover(T entidade) throws EddyServerException {
        antesRemover(entidade);
        getRepositorio().remover(entidade);
        aposRemover(entidade);
    }
    
    public T obter(Integer id) throws EddyServerException {
        return (T) getRepositorio().obter(id);
    }
    
    public List<T> listar(Integer inicio, Integer qtde) throws EddyServerException {
        return getRepositorio().listar(inicio, qtde);
    }
    
    public int contar() throws EddyServerException {
        return getRepositorio().contar();
    }
    
}
