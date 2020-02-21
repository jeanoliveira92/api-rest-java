package br.com.api.bd;

import java.sql.*;
import java.util.*;
import br.com.api.model.Usuario;

public class DaoUsuario {

    // RETORNA TODOS OS FUNCIONARIOS
    public HashMap listarFuncionarios() throws Exception {
        String sql = "SELECT * FROM usuarios ORDER BY usuario";
        return DAO.createStatement(sql);
    }

    public void trocarSenha(Usuario user) throws Exception {

        if (validarFuncionario(user.getUsuario(), user.getSenha()) < 1) {
            throw new Exception("Usuario ou senha invalidos!");
        }

        String SQL = "UPDATE usuarios SET senha = ? where usuario = ?";

        ArrayList<String> params = new ArrayList<>();
        params.add(user.getSenha2());
        params.add(user.getUsuario());
        
        DAO.createInsertOrUpdatePrepareStatement(SQL, params);
    }

    // VALIDA USUARIO E SENHA NO BANCO DE DADOS
    public int validarFuncionario(String usuario, String senha) throws Exception {
        String query = "SELECT usuario FROM usuarios WHERE usuario = ? AND senha = ?";

        ArrayList<String> params = new ArrayList<>();
        params.add(usuario);
        params.add(senha);
        // SIZE -1. DEVIDO 1 LINHA SER O CABEÇALHO E 1 LINHA A RESPOSTA. EM CARRO DE ERRO, APENANS CABEÇALHO.
        return DAO.createPrepareStatement(query, params).size() - 1;
    }

    public static String getFuncionarioNome(String usuario) throws Exception {
        Connection conexao = Config.con();

        Statement statement = conexao.createStatement();

        String query = "SELECT nome FROM usuarios WHERE usuario=" + usuario;
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        String nome = rs.getString(1);

        rs.close();
        statement.close();
        conexao.close();
        return nome;
    }

    // VALIDA USUARIO E SENHA NO BANCO DE DADOS
    public static void setLog(String usuario, String tela) throws Exception {
        int acesso = temAcesso(usuario, tela);

        if (acesso == 1) {
            Connection conexao = Config.con();

            Statement statement = conexao.createStatement();

            String funcionario = getFuncionarioNome(usuario);

            String SQL = "INSERT INTO log VALUES(sysdate,'" + funcionario + "','" + tela + "')";
            String query = SQL;
            System.out.println(SQL);
            statement.executeUpdate(query);

            statement.close();
            conexao.close();
        } else {
            throw new Exception("Não possui acesso!");
        }
    }

    // RETORNA O NIVEL DE ACESSO DO USUARIO
    public static String NivelDeAcesso(String login) throws Exception {
        Connection conexao = Config.con();

        Statement statement = conexao.createStatement();

        String query = "SELECT permissao FROM usuarios WHERE usuario = '" + login + "'";
        ResultSet rs = statement.executeQuery(query);

        rs.next();
        String permissao = rs.getString(1);

        rs.close();
        statement.close();
        conexao.close();
        return permissao;
    }

    // RETORNA SE TEM PERMISSAO A DETERMINADO ITEM
    public static int temAcesso(String login, String tela) throws Exception {
        Connection conexao = Config.con();

        Statement statement = conexao.createStatement();

        String query = "SELECT count(rowid) FROM funcionarios_auth WHERE tela = '" + tela + "'"
                + " AND permissao <= (SELECT permissao FROM usuarios WHERE usuario = '" + login + "')";
        System.out.println(query);
        ResultSet rs = statement.executeQuery(query);

        rs.next();

        int permissao = rs.getInt(1);

        rs.close();
        statement.close();
        conexao.close();

        return permissao;
    }
}
