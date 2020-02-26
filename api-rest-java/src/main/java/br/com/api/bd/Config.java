package br.com.api.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Config {

    //private static final String DRIVER = "oracle:thin", // FOR ORACLE
    private static final String DRIVER = "mysql",
            HOST = "127.0.0.1",
            PORT = "3306",
            DATABASE = "api",
            USER = "root",
            PASSWORD = "";

    public static Connection con() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("oracle.jdbc.driver.OracleDriver"); // For Oracle
            return DriverManager.getConnection("jdbc:" + DRIVER + "://" + HOST + "/" + DATABASE, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new Exception(ex);
        }
    }
}
