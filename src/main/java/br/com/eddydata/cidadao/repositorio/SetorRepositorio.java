package br.com.eddydata.cidadao.repositorio;

import br.com.eddydata.minhacidade.util.Repositorio;
import br.com.eddydata.cidadao.entidade.Setor;
import br.com.eddydata.minhacidade.util.EddyServerException;
import br.com.eddydata.minhacidade.util.JPAUtil;
import br.com.eddydata.minhacidade.util.Util;
import java.util.List;
import javax.persistence.EntityManager;

public class SetorRepositorio extends Repositorio<Setor> {

    private final EntityManager em = JPAUtil.getEntityManager();

    public SetorRepositorio(Class<Setor> entityClass) {
        super(entityClass);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Setor> listarPorNome(String nome, Integer inicio, Integer fim) throws EddyServerException {
        return super.listar(Setor.class, "select s from Setor s where function('rem_acento', upper(s.nome)) like ?1 order by s.nome", inicio, fim, Util.Texto.removerAcentos(nome.toUpperCase()) + "%");
    }

    public int contarPorNome(String nome) throws EddyServerException {
        return obter(Long.class, "select count(1) from Setor s where function('rem_acento', upper(s.nome)) like ?1", Util.Texto.removerAcentos(nome.toUpperCase()) + "%").intValue();
    }

}
