package org.trab.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao
{
    private static String URL = "jdbc:mysql://localhost:3306/agendamentoConsulta";
    private static String PASSWORD = "que@Y2003";
    private static String USER = "root";

    public static Connection getConn() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Error: nao foi possivel obter conexao com o banco! "+ e.getMessage());
        }
    }

    public static void closeConn(Connection conn) throws SQLException {
        if(conn != null && !conn.isClosed()) {
            conn.close();
        } else {
            throw new SQLException("Error: nao foi possivel fechar a conexao com o banco!");
        }
    }

}
