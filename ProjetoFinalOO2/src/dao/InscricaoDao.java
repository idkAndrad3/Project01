package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.StatusInscricao;

public class InscricaoDao {

    private static Connection conexaoBD;

    /**
     * Inicializa a conexão com o banco de dados.
     */
    public static void inicializarConexao(Connection conexao) {
        conexaoBD = conexao;
        System.out.println("[DEBUG] Conexão inicializada para InscricaoDao.");
    }

    /**
     * Inscreve um participante em um evento.
     */
    public static boolean inscrever(int participanteId, int eventoId) throws SQLException {
        if (conexaoBD == null || conexaoBD.isClosed()) {
            throw new SQLException("Conexão com o banco de dados não está inicializada ou está fechada!");
        }

        PreparedStatement stmt = null;
        try {
            // Verificar se o evento está aberto
            if (!isEventoAberto(eventoId)) {
                throw new SQLException("O evento não está aberto para inscrições.");
            }

            // Verificar capacidade máxima do evento
            if (isEventoLotado(eventoId)) {
                throw new SQLException("O evento atingiu sua capacidade máxima.");
            }

            // Inserir inscrição
            stmt = conexaoBD.prepareStatement(
                "INSERT INTO inscricoes (evento_id, participante_id, data_inscricao, status_inscricao, presenca_confirmada) " +
                "VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, eventoId);
            stmt.setInt(2, participanteId);
            stmt.setDate(3, new Date(System.currentTimeMillis())); // Data atual
            stmt.setString(4, StatusInscricao.ATIVA.getSQLValue());
            stmt.setBoolean(5, false); // Presença inicialmente não confirmada

            int linhasAfetadas = stmt.executeUpdate();
            conexaoBD.commit();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            conexaoBD.rollback();
            throw new SQLException("Erro ao realizar a inscrição: " + e.getMessage(), e);
        } finally {
            BancoDados.finalizarStatement(stmt);
        }
    }

    /**
     * Cancela uma inscrição de um participante em um evento.
     */
    public static boolean cancelarInscricao(int participanteId, int eventoId) throws SQLException {
        if (conexaoBD == null || conexaoBD.isClosed()) {
            throw new SQLException("Conexão com o banco de dados não está inicializada ou está fechada!");
        }

        PreparedStatement stmt = null;
        try {
            // Verificar se o evento está aberto
            if (!isEventoAberto(eventoId)) {
                throw new SQLException("O evento não está aberto para cancelamento de inscrições.");
            }

            stmt = conexaoBD.prepareStatement(
                "UPDATE inscricoes SET status_inscricao = ? WHERE evento_id = ? AND participante_id = ?"
            );
            stmt.setString(1, StatusInscricao.CANCELADA.getSQLValue());
            stmt.setInt(2, eventoId);
            stmt.setInt(3, participanteId);

            int linhasAfetadas = stmt.executeUpdate();
            conexaoBD.commit();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            conexaoBD.rollback();
            throw new SQLException("Erro ao cancelar a inscrição: " + e.getMessage(), e);
        } finally {
            BancoDados.finalizarStatement(stmt);
        }
    }

    /**
     * Confirma a presença de um participante em um evento.
     */
    public static boolean confirmarPresenca(int participanteId, int eventoId) throws SQLException {
        if (conexaoBD == null || conexaoBD.isClosed()) {
            throw new SQLException("Conexão com o banco de dados não está inicializada ou está fechada!");
        }

        PreparedStatement stmt = null;
        try {
            stmt = conexaoBD.prepareStatement(
                "UPDATE inscricoes SET presenca_confirmada = ? WHERE evento_id = ? AND participante_id = ? AND status_inscricao = ?"
            );
            stmt.setBoolean(1, true);
            stmt.setInt(2, eventoId);
            stmt.setInt(3, participanteId);
            stmt.setString(4, StatusInscricao.ATIVA.getSQLValue());

            int linhasAfetadas = stmt.executeUpdate();
            conexaoBD.commit();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            conexaoBD.rollback();
            throw new SQLException("Erro ao confirmar presença: " + e.getMessage(), e);
        } finally {
            BancoDados.finalizarStatement(stmt);
        }
    }

    /**
     * Verifica se o evento está aberto para inscrições.
     */
    private static boolean isEventoAberto(int eventoId) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conexaoBD.prepareStatement(
                "SELECT status FROM evento WHERE id = ?"
            );
            stmt.setInt(1, eventoId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return "aberto".equalsIgnoreCase(rs.getString("status"));
            }
            return false;
        } finally {
            BancoDados.finalizarResultSet(rs);
            BancoDados.finalizarStatement(stmt);
        }
    }

    /**
     * Verifica se o evento já atingiu sua capacidade máxima.
     */
    private static boolean isEventoLotado(int eventoId) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conexaoBD.prepareStatement(
                "SELECT capacidade_maxima, " +
                "       (SELECT COUNT(*) FROM inscricoes WHERE evento_id = ? AND status_inscricao = ?) AS inscritos " +
                "FROM evento WHERE id = ?"
            );
            stmt.setInt(1, eventoId);
            stmt.setString(2, StatusInscricao.ATIVA.getSQLValue());
            stmt.setInt(3, eventoId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                int capacidadeMaxima = rs.getInt("capacidade_maxima");
                int inscritos = rs.getInt("inscritos");
                return inscritos >= capacidadeMaxima;
            }
            return false;
        } finally {
            BancoDados.finalizarResultSet(rs);
            BancoDados.finalizarStatement(stmt);
        }
    }
    
    public static boolean verificarInscricao(int participanteId, int eventoId) throws SQLException {
        if (conexaoBD == null) {
            throw new SQLException("Conexão com o banco de dados não foi inicializada!");
        }

        String sql = "SELECT COUNT(*) AS total FROM inscricoes WHERE participante_id = ? AND evento_id = ?";
        try (PreparedStatement statement = conexaoBD.prepareStatement(sql)) {
            statement.setInt(1, participanteId);
            statement.setInt(2, eventoId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0; // Retorna true se já estiver inscrito
                }
            }
        }

        return false; // Não inscrito
    }
}
