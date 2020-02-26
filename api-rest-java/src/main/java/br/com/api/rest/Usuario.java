package br.com.api.rest;

import br.com.api.bd.*;
import br.com.api.model.LogFuncionario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/Usuario")
public class Usuario {

    // CONSULTAS AO BANCO DE DADOS
    private DaoUsuario dao;
    // CODIFICAÇÃO DOS ACENTOS EM TEXTOS
    private static final String CHARSET = ";charset=utf-8";

    @PostConstruct
    private void init() {
        dao = new DaoUsuario();
    }

    //@Seguro
    @GET
    @Produces(MediaType.APPLICATION_JSON + CHARSET)
    public Response listarUsuarios() throws JsonProcessingException {
        try {
            return Response.ok(DAO.mapToString(dao.listarUsuarios())).build();
        } catch (Exception e) {
            e.printStackTrace(); // DEBUG DE ERROS
            // CASO OCORRA ALGUM ERRO RETORNA UMA RESPOSTA COM O STATUS 401 UNAUTHORIZED
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // CASO PRECISE REALIZAR O LOG DE ALGO EXTERNO A API
    @POST
    @Path("/log")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response log(String crendenciaisJson) {
        try {
            Gson gson = new Gson();
            LogFuncionario log = gson.fromJson(crendenciaisJson, LogFuncionario.class);
            dao.setLog(log.getLogin(), log.getTela());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
