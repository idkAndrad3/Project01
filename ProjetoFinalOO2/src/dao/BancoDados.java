package dao;

import java.util.Properties;
import java.io.*;
import java.sql.*;

public class BancoDados {
    private static Connection conn = null;

    /**
     * Conecta ao banco de dados, caso ainda não esteja conectado.
     */
    public static Connection conectar() throws SQLException, IOException {
        if (conn == null || conn.isClosed()) { // Verifica se a conexão já existe ou foi fechada
            Properties props = carregarPropriedades();
            String url = props.getProperty("dburl");
            conn = DriverManager.getConnection(url, props);
            conn.setAutoCommit(false); // Desativa o autocommit para controle manual de transações
        }
        return conn;
    }

    /**
     * Desconecta do banco de dados se houver uma conexão ativa.
     */
    public static void desconectar() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            conn = null;
        }
    }

    /**
     * Retorna a conexão ativa, caso exista.
     */
    public static Connection getConexao() throws SQLException, IOException {
        if (conn == null || conn.isClosed()) {
            conectar();
        }
        return conn;
    }

    /**
     * Carrega as propriedades de conexão a partir do arquivo database.properties.
     */
    private static Properties carregarPropriedades() throws IOException {
        try (FileInputStream propriedadesBanco = new FileInputStream("database.properties")) {
            Properties props = new Properties();
            props.load(propriedadesBanco);
            return props;
        }
    }

    /**
     * Finaliza um Statement, se não for nulo.
     */
    public static void finalizarStatement(Statement st) throws SQLException {
        if (st != null) {
            st.close();
        }
    }

    /**
     * Finaliza um ResultSet, se não for nulo.
     */
    public static void finalizarResultSet(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * Realiza um commit na transação atual.
     */
    public static void commit() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.commit();
        }
    }

    /**
     * Realiza um rollback na transação atual.
     */
    public static void rollback() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.rollback();
        }
    }
}
