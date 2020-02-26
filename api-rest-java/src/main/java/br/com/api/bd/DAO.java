package br.com.api.bd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Statement;
import java.text.*;
import java.util.*;

public class DAO {

    // RETORNA UM OBJETO COMO UM INTEIRO MESMO QUANDO NULO
    public static int getInt(Object obj) {
        if (obj != null) {
            return (int) obj;
        }
        return 0;
    }

    // RETORNA UM OBJETO COMO UM NUMERO REAL MESMO QUANDO NULO
    public static float getFloat(Object obj) {
        if (obj != null) {
            return (float) obj;
        }
        return 0;
    }

    // RETORNA UM OBJETO COMO UM BIGDECIMAL MESMO QUANDO NULO
    public static BigDecimal getBigDecimal(Object obj) {
        if (obj != null) {
            return (BigDecimal) obj;
        }
        return new BigDecimal(0.0);
    }

    // RETORNA UM OBJETO COMO UMA STRING MESMO QUANDO NULO
    public static String getString(Object obj) {
        if (obj != null) {
            return (String) obj;
        }
        return "";
    }

    // RETORNA UM OBJETO COMO UMA STRING MESMO QUANDO NULO
    public static String getDateHour(Object obj) {
        if (obj != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String strDate = dateFormat.format(obj);
            return strDate;
        }
        return "";
    }

    // RETORNA UM OBJETO COMO UM STRING MESMO QUANDO NULO
    public static String getDate(Object obj) {
        if (obj != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(obj);
            return strDate;
        } else {
            return "";
        }
    }

    // RETORNA UMA MATRIZ COMO UM STRING JSON
    public static String mapToString(Map<String, String> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);
        return jsonResult;
    }

    // RETORNA UMA MATRIZ COMO UM STRING JSON
    public static String mapMapToString(Map<String, Map<String, String>> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(map);
        return jsonResult;
    }

    // RETORNA UMA LISTA COMO UM STRING JSON
    public static String listToString(List<String> list) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(list);
        return jsonResult;
    }

    // EXECUTA UMA CONSULTA SEM PARAMETROS E RETORNA UMA MATRIX
    public static HashMap createStatement(String sql) throws Exception {
        // INICIALIZA CONEXÃO E FUNÇÕES BASICAS
        Connection con = Config.con();
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        // HASHMAP PARA O JSON E UM HASHMAP PARA A LISTA
        HashMap node = new HashMap<>();
        HashMap list = new HashMap<>();
        // CONTA A QTD DE COLUNAS DA TABELA
        int columnCount = rsmd.getColumnCount();
        // CRIA A LISTA COM OS TITULOS DAS COLUNAS
        for (int i = 1; i <= columnCount; i++) {
            list.put(rsmd.getColumnName(i).toString(), rsmd.getColumnName(i));
        }
        // ADICIONA OS TITULOS NA LISTA GERAL COM A TAG "HEADERS"
        node.put("headers", list);
        // CRIA LISTA COM OS DADOS E ADICIONA NA LISTA GERAL. A TAG É UMA ID AUTOGERADA
        int count = 0;
        while (rs.next()) {
            list = new HashMap<String, String>();
            for (int i = 1; i <= columnCount; i++) {
                list.put(rsmd.getColumnName(i).toString(), DAO.getString(rs.getString(i)));
            }
            // INSERE A LISTA GERADA NA LISTA GERAL COM UMA ID VALOR INTEIRO
            node.put(Integer.toString(count), list);
            count += 1;
        }
        // ENCERRA TODOS OS ESTADOS E CONEXÃO
        rs.close();
        statement.close();
        con.close();
        return node;
    }

    // EXECUTA UMA CONSULTA COM PARAMETROS E RETORNA UMA MATRIX
    public static HashMap createPrepareStatement(String sql, ArrayList<String> params) throws Exception {
        Connection con = Config.con();
        PreparedStatement pstmt = con.prepareStatement(sql);
        // INSERE OS ELEMENTOS UM A UM NO PREPARE STATEMENT
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }
        }
        //System.out.println(con.nativeSQL(sql));
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        // HASHMAP PARA O JSON E UM HASHMAP PARA A LISTA
        HashMap node = new HashMap<>();
        HashMap list = new HashMap<>();
        // CONTA A QTD DE COLUNAS DA TABELA
        int columnCount = rsmd.getColumnCount();
        // CRIA A LISTA COM OS TITULOS DAS COLUNAS
        for (int i = 1; i <= columnCount; i++) {
            list.put(rsmd.getColumnName(i), rsmd.getColumnName(i));
        }
        // ADICIONA OS TITULOS NA LISTA GERAL COM A TAG "HEADERS"
        node.put("headers", list);
        // CRIA LISTA COM OS DADOS E ADICIONA NA LISTA GERAL. A TAG É UMA ID AUTOGERADA
        int count = 0;
        while (rs.next()) {
            list = new HashMap<String, String>();
            for (int i = 1; i <= columnCount; i++) {
                list.put(rsmd.getColumnName(i), DAO.getString(rs.getString(i)));
            }
            // INSERE A LISTA GERADA NA LISTA GERAL COM UMA ID VALOR INTEIRO
            node.put(Integer.toString(count), list);
            count += 1;
        }
        // ENCERRA TODOS OS ESTADOS E CONEXÃO
        rs.close();
        pstmt.close();
        con.close();
        return node;
    }

    // EXECUTA UMA CONULTA SEM PARAMETROS E RETORNA UMA LISTA
    public static List<String> createListStatement(String sql) throws Exception {
        // INICIALIZA CONEXÃO E FUNÇÕES BASICAS
        Connection con = Config.con();
        Statement statement = con.createStatement();
        String query = sql;
        ResultSet rs = statement.executeQuery(query);

        // CRIA LISTA COM OS DADOS
        List<String> list = new ArrayList<>();
        while (rs.next()) {
            list.add((rs.getString(1)));
        }
        // ENCERRA TODOS OS ESTADOS E CONEXÃO
        rs.close();
        statement.close();
        con.close();
        return list;
    }

    // EXECUTA UMA CONULTA COM PARAMETROS E RETORNA UMA LISTA
    public static List<String> createListPrepareStatement(String sql, ArrayList<String> params) throws Exception {
        Connection con = Config.con();
        PreparedStatement pstmt = con.prepareStatement(sql);
        // INSERE OS ELEMENTOS UM A UM NO PREPARE STATEMENT
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }
        }
        ResultSet rs = pstmt.executeQuery();

        // CRIA LISTA COM OS PARAMETROS
        List<String> list = new ArrayList<>();
        while (rs.next()) {
            list.add((rs.getString(1)));
        }
        // ENCERRA TODOS OS ESTADOS E CONEXÃO
        rs.close();
        pstmt.close();
        con.close();
        return list;
    }

    // EXECUTA UMA CONULTA COM PARAMETROS E RETORNA APENAS UMA COLUNA
    public static String createValuePrepareStatement(String sql, ArrayList<String> params) throws Exception {
        Connection con = Config.con();
        PreparedStatement pstmt = con.prepareStatement(sql);

        // INSERE OS ELEMENTOS UM A UM NO PREPARE STATEMENT
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }
        }

        ResultSet rs = pstmt.executeQuery();
        rs.next();
        
        // ARMAZENA O VALOR DA CONSULTA
        String value = rs.getString(1);

        // ENCERRA TODOS OS ESTADOS E CONEXÃO
        rs.close();
        pstmt.close();
        con.close();
        return value;
    }

    // EXECUTA UM INSERT OU UPDATE COM PARAMETROS
    public static void createInsertOrUpdatePrepareStatement(String sql, ArrayList<String> params) throws Exception {
        Connection con = Config.con();
        PreparedStatement pstmt = con.prepareStatement(sql);
        // INSERE OS ELEMENTOS UM A UM NO PREPARE STATEMENT
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }
        }
        System.out.println(con.nativeSQL(sql));
        pstmt.execute();

        pstmt.close();
        con.close();
    }

}
