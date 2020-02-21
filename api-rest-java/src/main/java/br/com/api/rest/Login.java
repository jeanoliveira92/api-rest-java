package br.com.api.rest;

import br.com.api.model.NivelDeAcesso;
import br.com.api.model.Usuario;
import br.com.api.bd.DaoUsuario;
import br.com.api.security.Seguro;
import com.google.gson.Gson;
import io.jsonwebtoken.*;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.*;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.*;

@Path("/login")
public class Login {

    // CODIFICAÇÃO DOS ACENTOS EM TEXTOS
    private static final String CHARSET = ";charset=utf-8";
    //FRASE SEGREDO DO TOKEN
    private final static String FRASE_SEGREDO = "InsiraSuaFraseSegredo";
    //FRASE SEGREDO DO TOKEN
    private static byte[] apiKeySecretBytes;
    // ACESSO NO BANCO TABELA USUARIO
    private final static DaoUsuario DAO = new DaoUsuario();
    // TEMPO DE DURACAO DO TOKEN
    private final static int MAXTIME = 4;

    public Login() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // ENCODA A FRASE SERGREDO PRA BASE64
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        String secret = FRASE_SEGREDO;
        md.update(secret.getBytes("UTF-8"));
        apiKeySecretBytes = md.digest();
    }

    // LOGIN
    @POST
    @Consumes(MediaType.APPLICATION_JSON + CHARSET)
    public Response login(String crendenciaisJson) {
        try {
            // TRANSCREVE O JSON PARA CLASSE
            Gson gson = new Gson();
            Usuario user = gson.fromJson(crendenciaisJson, Usuario.class);

            // VALIDAÇÃO DO USUARIO E SENHA
            if (DAO.validarFuncionario(user.getUsuario(), user.getSenha()) < 1) {
                throw new Exception("Usuário ou Senha Inválida!");
            }
            return Response.ok(gerarToken(user.getUsuario())).build();
        } catch (Exception e) {
            e.printStackTrace(); // DEBUG DE ERROS
            // CASO OCORRA ALGUM ERRO RETORNA UMA RESPOSTA COM O STATUS 401 UNAUTHORIZED
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

    // TROCAR SENHA
    @PUT
    @Consumes(MediaType.APPLICATION_JSON + CHARSET)
    public Response trocarSenha(String crendenciaisJson) {
        try {
            // TRANSCREVE O JSON PARA CLASSE
            Gson gson = new Gson();
            Usuario user = gson.fromJson(crendenciaisJson, Usuario.class);
            // FUNÇÃO PARA TROCAR A SENHA
            DAO.trocarSenha(user);
            // RETORNAR STATUS OK
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); // DEBUG DE ERROS
            // CASO OCORRA ALGUM ERRO RETORNA UMA RESPOSTA COM O STATUS 401 UNAUTHORIZED
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }
    
    // RENOVAR UM TOQUEM QUASE VENCENDO    
    @Seguro
    @GET
    public Response renovarToken(@Context SecurityContext securityContext) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Principal principal = securityContext.getUserPrincipal();

        String token = this.gerarToken(principal.getName());

        return Response.ok(token).build();
    }

    // FUNÇÃO GERADORA DO TOKEN
    // PARAM 1: LOGIN DO USUARIO | PARAM 2: HORAS
    private String gerarToken(String login) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // ALGORITMO DE ASSINATURA: HMAC SHA512
        SignatureAlgorithm algoritimoAssinatura = SignatureAlgorithm.HS512;
        // DATA DE CRIAÇÃO
        Date agora = new Date();
        // GERA O TEMPO DE EXPIRAÇÃO
        Calendar expira = Calendar.getInstance();
        expira.add(Calendar.HOUR_OF_DAY, MAXTIME);

        // GERA A KEY PARA O TOKEN COM OS DADOS E FRASE SECRETA ASSINADA
        SecretKeySpec key = new SecretKeySpec(apiKeySecretBytes, algoritimoAssinatura.getJcaName());
        //E finalmente utiliza o JWT builder pra gerar o token
        JwtBuilder construtor = Jwts.builder()
                .setIssuedAt(agora) // DATA GERADO
                .setIssuer(login) // UM DADO DO USUARIO. LOGIN NESSE CASO.
                .signWith(algoritimoAssinatura, key) // ALGORITIMO DE ASSINATURA E FRASE SEGREDO
                .setExpiration(expira.getTime());// VALIDADE DO TOKEN

        // RETORNA O TOKEN EM FORMA DE STRING
        return construtor.compact();
    }

    public Claims validaToken(String token) throws Exception {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(apiKeySecretBytes)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (Exception ex) {
            throw ex;
        }
    }

    //Metodo simples como não usamos banco de dados e foco é o parte autenticação
    //o metodo retorna somente um nivel de acesso, mas em uma aplicação normal
    //aqui seria feitor a verficação de que niveis de permissao o usuario tem e retornar eles
    public static NivelDeAcesso buscarNivelPermissao(String login) throws Exception {
        DaoUsuario dao = new DaoUsuario();

        switch (dao.NivelDeAcesso(login)) {
            case "BASICO": {
                return NivelDeAcesso.BASICO;
            }
        }

        return NivelDeAcesso.BASICO;
    }
}
