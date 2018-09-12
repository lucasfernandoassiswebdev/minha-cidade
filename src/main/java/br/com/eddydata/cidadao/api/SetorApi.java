package br.com.eddydata.cidadao.api;

import br.com.eddydata.minhacidade.util.EddyServer;
import br.com.eddydata.minhacidade.util.Servico;
import br.com.eddydata.cidadao.entidade.Setor;
import br.com.eddydata.cidadao.servico.SetorServico;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/setores")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SetorApi extends EddyServer<Setor> {

    private final SetorServico servico = new SetorServico();

    @Override
    protected Servico getServico() {
        return servico;
    }

    @GET
    @Path("listar/{busca}/{inicio}/{fim}")
    public List<Setor> listar(@PathParam("busca") String busca, @PathParam("inicio") Integer inicio, @PathParam("fim") Integer fim) {
        return servico.listarPorNome(busca, inicio, fim);
    }

    @GET
    @Path("contar/{busca}")
    @Produces(MediaType.TEXT_PLAIN)
    public String contar(@PathParam("busca") String busca) {
        return String.valueOf(servico.contarPorNome(busca));
    }

}
