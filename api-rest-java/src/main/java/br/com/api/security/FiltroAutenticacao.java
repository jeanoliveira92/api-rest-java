package br.com.api.security;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.security.Principal;
import javax.annotation.Priority;
import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import br.com.api.rest.Login;

@Seguro
// PROVÊ A FUNÇÃO PARA O @SEGURO
@Provider
// PRIORIDADE NA CHAMADA NA ORDEM DE EXECUÇÃO
// EXECUTA ANTES DO FILTRO DE AUTORIZACAO
@Priority(Priorities.AUTHENTICATION)
public class FiltroAutenticacao implements ContainerRequestFilter {

    // OVERRIDE DO METODO FILTER. 
    // ContainerRequestContext É A VARIAVEL A SER MANIPULADA E QUE PERTENCE A REQUEST.
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // EXTRAI O TOKEN DO CABEÇALHO
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        System.out.println("token: " + authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header precisa ser provido");
        }
        //extrai o token do header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        //verificamos se o metodo é valido ou não
        //se não for valido  a requisição é abortada e retorna uma resposta com status 401 UNAUTHORIZED
        //se for valida modificamos o o SecurityContext da request 
        //para que quando usarmos o  getUserPrincipal retorne o login do usuario 
        try {
            // metodo que verifica  se o token é valido ou não 
            Claims claims = new Login().validaToken(token);
            //Caso não for valido vai retornar um objeto nulo e executar um exception
            if (claims == null) {
                throw new Exception("Token inválido");
            }

            //Metodo que modifica o SecurityContext pra disponibilizar o login do usuario
            modificarRequestContext(requestContext, claims.getIssuer());
            System.out.println(claims.getIssuer());

        } catch (Exception e) {
            e.printStackTrace();
            //Caso o token for invalido a requisição é abortada e retorna uma resposta com status 401 UNAUTHORIZED
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    //Metodo que modfica o SecurityContext
    private void modificarRequestContext(ContainerRequestContext requestContext, final String login) {
        final SecurityContext currentSecurityContext = requestContext.getSecurityContext();

        System.out.println("Requisitação de: " + login);

        requestContext.setSecurityContext(new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
                return new Principal() {
                    @Override
                    public String getName() {
                        return login;
                    }
                };
            }

            @Override
            public boolean isUserInRole(String role) {
                return true;
            }

            @Override
            public boolean isSecure() {
                return currentSecurityContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });
    }
}
