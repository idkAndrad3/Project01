package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BancoDadosTest {

    private static Connection conn;

    @BeforeAll
    public static void setUp() throws SQLException, IOException {
        conn = BancoDados.conectar();
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        BancoDados.desconectar();
    }

    @Test
    public void conectarTest() throws SQLException, IOException {
        assertNotNull(conn, "A conexão não deveria ser nula após conectar ao banco de dados.");
        assertFalse(conn.isClosed(), "A conexão deveria estar aberta.");
    }

    @Test
    public void desconectarTest() throws SQLException, IOException {
        BancoDados.desconectar(); // Fecha a conexão
        assertTrue(conn.isClosed(), "A conexão deveria estar fechada após chamar desconectar.");
    }
}
