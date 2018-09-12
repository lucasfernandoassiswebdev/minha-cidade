package br.com.eddydata.minhacidade.util;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class EddyServer<T> {

    protected abstract Servico<T> getServico();

    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response inserir(T entidade) {
        getServico().inserir(entidade);
        return Response.ok().build();
    }

    @PUT
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response salvar(T entidade) {
        getServico().salvar(entidade);
        return Response.ok().build();
    }

    @DELETE
    public Response remover(T entidade) {
        getServico().remover(entidade);
        return Response.ok().build();
    }

    @GET
    @Path("buscar/{id}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public T buscar(@PathParam("id") Integer id) {
        return (T) getServico().obter(id);
    }

    @GET
    @Path("listar/{inicio}/{fim}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<T> listar(@PathParam("inicio") Integer inicio, @PathParam("fim") Integer fim) {
        return getServico().listar(inicio, fim);
    }

    @GET
    @Path("contar")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String contar() {
        return String.valueOf(getServico().contar());
    }

}
