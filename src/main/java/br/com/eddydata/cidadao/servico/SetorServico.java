package br.com.eddydata.cidadao.servico;

import br.com.eddydata.minhacidade.util.EddyServerException;
import br.com.eddydata.minhacidade.util.ErrorCode;
import br.com.eddydata.minhacidade.util.Repositorio;
import br.com.eddydata.cidadao.repositorio.SetorRepositorio;
import br.com.eddydata.cidadao.entidade.Setor;
import br.com.eddydata.minhacidade.util.Servico;
import java.util.List;

public class SetorServico extends Servico<Setor> {

    private final SetorRepositorio repositorio = new SetorRepositorio(Setor.class);

    @Override
    protected Repositorio getRepositorio() throws EddyServerException {
        return repositorio;
    }

    @Override
    protected void antesInserir(Setor entidade) throws EddyServerException {
        if (entidade == null) {
            throw new EddyServerException("Objeto não passado para inserção", ErrorCode.SERVER_ERROR.getCode());
        }
        if (entidade.getId() != null) {
            throw new EddyServerException("Registro já encontrado!");
        }

        validarRegisto(entidade);
    }

    @Override
    protected void aposInserir(Setor entidade) throws EddyServerException {
        //
    }

    @Override
    protected void antesSalvar(Setor entidade) throws EddyServerException {
        if (entidade == null) {
            throw new EddyServerException("Objeto não passado para atualização", ErrorCode.SERVER_ERROR.getCode());
        }
        if (entidade.getId() == null) {
            throw new EddyServerException("Registro não encontrado!");
        }

        validarRegisto(entidade);
    }

    @Override
    protected void aposSalvar(Setor entidade) throws EddyServerException {
        //
    }

    @Override
    protected void antesRemover(Setor entidade) throws EddyServerException {
        if (entidade == null) {
            throw new EddyServerException("Entidade não passada para exclusão", ErrorCode.SERVER_ERROR.getCode());
        }
    }

    @Override
    protected void aposRemover(Setor entidade) throws EddyServerException {
        //
    }

    private void validarRegisto(Setor entidade) throws EddyServerException {
        if (entidade == null) {
            throw new EddyServerException("Objeto não passado para validação", ErrorCode.SERVER_ERROR.getCode());
        }

        if (entidade.getNome() == null || entidade.getNome().trim().isEmpty()) {
            throw new EddyServerException("Nome não informado!");
        }

        entidade.setNome(entidade.getNome().trim());

        if (entidade.getNome().length() > 100) {
            throw new EddyServerException("O campo nome deve ter no máximo 100 caracteres");
        }
    }

    public List<Setor> listarPorNome(String nome, Integer inicio, Integer qtde) throws EddyServerException {
        if (inicio == null) {
            inicio = 0;
        }
        if (qtde == null) {
            qtde = 0;
        }
        if (nome == null) {
            nome = "";
        }

        return repositorio.listarPorNome(nome, inicio, qtde);
    }

    public int contarPorNome(String nome) throws EddyServerException {
        if (nome == null) {
            nome = "";
        }
        return repositorio.contarPorNome(nome);
    }
}
