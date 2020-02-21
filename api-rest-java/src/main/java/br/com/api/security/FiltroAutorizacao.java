package br.com.api.security;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import javax.ws.rs.core.*;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import br.com.api.model.NivelDeAcesso;
import br.com.api.rest.Login;

@Seguro
// PROVÊ A FUNÇÃO PARA O @SEGURO
@Provider
// PRIORIDADE NA CHAMADA NA ORDEM DE EXECUÇÃO
// EXECUTA DEPOIS DO FILTRO DE AUTENTICACAO 
//  AUTHENTICATION É MAIOR QUE AUTHORIZATION
@Priority(Priorities.AUTHORIZATION)
public class FiltroAutorizacao implements ContainerRequestFilter {
    //O JAX-RS faz a injeção do ResourceInfo que vai ter os informações
    //do metodo que ta sendo verificado 
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Pega a classe que contem URL requisitada 
        // E extrai os nivel de permissão dela
        Class<?> classe = resourceInfo.getResourceClass();
        List<NivelDeAcesso> nivelPermissaoClasse = extrairNivelPermissao(classe);

        // Pega o metodo que contem URL requisitada 
        // E extrai os nivel de permissão dele
        Method metodo = resourceInfo.getResourceMethod();
        List<NivelDeAcesso> nivelPermisaoMetodo = extrairNivelPermissao(metodo);

        try {
            //Como modificamos o securityContext na hora de validar o token, para podemos pegar
            //O login do usuario, para fazer a verificação se ele tem o nivel de permissao necessario
            //para esse endpoint
            String login = requestContext.getSecurityContext().getUserPrincipal().getName();
            // Verifica se o usuario tem permissão pra executar esse metodo
            // Os niveis de acesso do metodo sobrepoe o da classe
            if (nivelPermisaoMetodo.isEmpty()) {
                checarPermissoes(nivelPermissaoClasse, login);
            } else {
                checarPermissoes(nivelPermisaoMetodo, login);
            }

        } catch (Exception e) {
            //Se caso o usuario não possui permissao é dado um execption, 
            //e retorna um resposta com o status 403 FORBIDDEN 
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN).build());
        }
    }
    //Metodo que extrai os niveis de permissao que foram definidos no @Seguro

    private List<NivelDeAcesso> extrairNivelPermissao(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<NivelDeAcesso>();
        } else {
            Seguro secured = annotatedElement.getAnnotation(Seguro.class);
            if (secured == null) {
                return new ArrayList<NivelDeAcesso>();
            } else {
                NivelDeAcesso[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }
    //Verifica se o usuario tem permissao pra executar o metodo, se não for definido nenhum nivel de acesso no @Seguro,
    //Entao todos vao poder executar desde que possuam um token valido

    private void checarPermissoes(List<NivelDeAcesso> nivelPermissaoPermitidos, String login) throws Exception {
        try {
            if (nivelPermissaoPermitidos.isEmpty()) {
                return;
            }

            boolean temPermissao = false;
            //Busca quais os niveis de acesso o usuario tem.
            NivelDeAcesso nivelPermissaoUsuario = new Login().buscarNivelPermissao(login);

            for (NivelDeAcesso nivelPermissao : nivelPermissaoPermitidos) {
                if (nivelPermissao.equals(nivelPermissaoUsuario)) {
                    temPermissao = true;
                    break;
                }
            }

            if (!temPermissao) {
                throw new Exception("Cliente não possui o nível de permissão para esse método");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}