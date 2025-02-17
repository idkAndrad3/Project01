package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entities.Administrador;
import entities.CategoriaEvento;
import entities.Evento;
import entities.Participante;
import entities.StatusEvento;
import entities.User;

public class EventoDao {

    private static Connection conexaoBD;

    public static void inicializarConexao(Connection conexao) {
        conexaoBD = conexao;
    }

    public static boolean cadastrarEvento(Evento evento) throws SQLException {
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        int eventoId = -1;

        try {
            // Validação: evento precisa ter pelo menos um organizador
            if (evento.getOrganizadores().isEmpty()) {
                throw new IllegalArgumentException("O evento deve ter pelo menos um organizador.");
            }


            // Inserir evento na tabela 'evento'
            String sql = "INSERT INTO evento (titulo, descricao, categoria, data, hora, duracao, status, preco, capacidade_maxima, local, is_link) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = conexaoBD.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            Duration duracao = evento.getDuracaoEvento();
	        String intervalo = String.format("P%dDT%dH%dM", 
	            duracao.toDaysPart(),
	            duracao.toHoursPart(),
	            duracao.toMinutesPart()
	        );

            statement.setString(1, evento.getTitulo());
            statement.setString(2, evento.getDescricao());
            statement.setString(3, evento.getCategoria().getSQL());
            statement.setDate(4, evento.getDataEvento());
            statement.setTime(5, evento.getHoraEvento());
            statement.setString(6, intervalo);
            statement.setString(7, evento.getStatus().getSQL());
            statement.setDouble(8, evento.getPreco());
            statement.setInt(9, evento.getCapacidadeMaxima());
            statement.setString(10, evento.getLocal());
            statement.setBoolean(11, evento.isLink());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Erro ao inserir evento, nenhuma linha afetada.");
            }

            // Obter ID gerado para o evento
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                eventoId = generatedKeys.getInt(1);
                evento.setId(eventoId);
            } else {
                throw new SQLException("Erro ao obter o ID do evento.");
            }

            // Fechar statement e ResultSet antes de novos cadastros
            BancoDados.finalizarResultSet(generatedKeys);
            BancoDados.finalizarStatement(statement);

            // Cadastrar organizadores na tabela 'evento_organizadores'
            for (Administrador admin : evento.getOrganizadores().values()) {
                if (!adicionarAdministradorEvento(admin.getId(), eventoId)) {
                    throw new SQLException("Falha ao adicionar administrador " + admin.getId());
                }
            }

            for (Participante participante : evento.getParticipantes().values()) {
                if (!adicionarParticipanteEvento(participante.getId(), eventoId)) {
                    throw new SQLException("Falha ao adicionar participante " + participante.getId());
                }
            }
            System.out.println("[DEBUG] Verificando organizadores antes de cadastrar o evento...");
            for (Administrador admin : evento.getOrganizadores().values()) {
                System.out.println("[DEBUG] Administrador: ID = " + admin.getId() + ", Nome = " + admin.getNome());
            }
            

            // Realizar commit da transação
            conexaoBD.commit();
            return true;

        } catch (SQLException erro) {
            conexaoBD.rollback(); // Reverter transação em caso de erro
            throw new SQLException("Erro ao cadastrar evento: " + erro.getMessage(), erro);
        } finally {
            BancoDados.finalizarResultSet(generatedKeys);
            BancoDados.finalizarStatement(statement);
        }
    }
    
    
    
    public static boolean adicionarParticipanteEvento(int participanteId, int eventoId) throws SQLException {
	    PreparedStatement statement = null;
	    try {
	        String sql = "INSERT INTO participante_evento (participante_id, evento_id, confirmou_presenca) VALUES (?, ?, ?)";
	        statement = conexaoBD.prepareStatement(sql);
	        statement.setInt(1, participanteId);
	        statement.setInt(2, eventoId);
	        statement.setBoolean(3, false);
	        return statement.executeUpdate() > 0; // Não gerencia transação!
	        
	    } finally {
	        BancoDados.finalizarStatement(statement);
	    }
	}

	public static boolean adicionarAdministradorEvento(int adminId, int eventoId) throws SQLException {
	    PreparedStatement statement = null;
	    try {
	        String sql = "INSERT INTO administrador_evento (admin_id, evento_id) VALUES (?, ?)";
	        statement = conexaoBD.prepareStatement(sql);
	        statement.setInt(1, adminId);
	        statement.setInt(2, eventoId);
	        return statement.executeUpdate() > 0; // Não gerencia transação!
	        
	    } finally {
	        BancoDados.finalizarStatement(statement);
	    }
	}
	
	
	public static boolean editar(Evento evento) throws SQLException {
	    PreparedStatement updateEventoStmt = null;
	    try {
	        conexaoBD.setAutoCommit(false);
	        
	        // Validações
	        if (evento.getId() == null) {
	            throw new SQLException("Evento não possui ID válido para edição.");
	        }
	        if (evento.getStatus() == StatusEvento.ENCERRADO || evento.getStatus() == StatusEvento.CANCELADO) {
	            throw new SQLException("Evento não pode ser editado no status atual.");
	        }

	        // Query de atualização do evento
	        String updateEventoSql = 
	            "UPDATE evento SET " +
	            "titulo = ?, descricao = ?, categoria = ?, hora = ?, data = ?, " +
	            "duracao = ?, status = ?, preco = ?, capacidade_maxima = ?, local = ? " +
	            "WHERE id = ?";
	        
	        updateEventoStmt = conexaoBD.prepareStatement(updateEventoSql);
	        
	        // Formatação da duração (mesmo padrão do método adicionar)
	        Duration duracao = evento.getDuracaoEvento();
	        String intervalo = String.format("P%dDT%dH%dM", 
	            duracao.toDaysPart(),
	            duracao.toHoursPart(),
	            duracao.toMinutesPart()
	        );

	        // Preenche os parâmetros
	        updateEventoStmt.setString(1, evento.getTitulo());
	        updateEventoStmt.setString(2, evento.getDescricao());
	        updateEventoStmt.setString(3, evento.getCategoria().getSQL());
	        updateEventoStmt.setTime(4, evento.getHoraEvento());
	        updateEventoStmt.setDate(5, evento.getDataEvento());
	        updateEventoStmt.setString(6, intervalo);
	        updateEventoStmt.setString(7, evento.getStatus().getSQL());
	        updateEventoStmt.setDouble(8, evento.getPreco());
	        updateEventoStmt.setInt(9, evento.getCapacidadeMaxima());
	        updateEventoStmt.setString(10, evento.getLocal());
	        updateEventoStmt.setInt(11, evento.getId()); // WHERE id = ?

	        int rowsUpdated = updateEventoStmt.executeUpdate();
	        if (rowsUpdated == 0) {
	            throw new SQLException("Evento não encontrado ou nenhum dado alterado.");
	        }

	        conexaoBD.commit(); // Confirma a transação
	        return true;

	    } catch (SQLException erro) {
	    	conexaoBD.rollback();
	        System.out.println("Erro ao editar evento [" + erro.getMessage() + "]");
	        return false;
	    } finally {
	        BancoDados.finalizarStatement(updateEventoStmt);
	        conexaoBD.setAutoCommit(true);
	    }
	}
		
	public static boolean excluir(int id) throws SQLException {
	    PreparedStatement statementAdmins = null;
	    PreparedStatement statementParticipantes = null;
	    PreparedStatement statementEvento = null;

	    try {
	        conexaoBD.setAutoCommit(false); // Inicia transação

	        // 1. Remove administradores associados ao evento
	        String deleteAdminsSql = "DELETE FROM administrador_evento WHERE evento_id = ?";
	        statementAdmins = conexaoBD.prepareStatement(deleteAdminsSql);
	        statementAdmins.setInt(1, id);
	        statementAdmins.executeUpdate();

	        // 2. Remove participantes associados ao evento
	        String deleteParticipantesSql = "DELETE FROM participante_evento WHERE evento_id = ?";
	        statementParticipantes = conexaoBD.prepareStatement(deleteParticipantesSql);
	        statementParticipantes.setInt(1, id);
	        statementParticipantes.executeUpdate();

	        // 3. Remove o evento da tabela principal
	        String deleteEventoSql = "DELETE FROM evento WHERE id = ?";
	        statementEvento = conexaoBD.prepareStatement(deleteEventoSql);
	        statementEvento.setInt(1, id);
	        int linhasAfetadas = statementEvento.executeUpdate();

	        if (linhasAfetadas > 0) {
	            conexaoBD.commit(); // Confirma as exclusões
	            System.out.println("Evento e relações excluídos com sucesso.");
	            return true;
	        } else {
	            System.out.println("Evento não encontrado.");
	            return false;
	        }

	    } catch (SQLException erro) {
	        conexaoBD.rollback(); // Reverte todas as operações em caso de erro
	        System.err.println("Erro ao excluir evento: " + erro.getMessage());
	        throw erro; // Propaga a exceção para tratamento externo
	    } finally {
	        // Restaura o auto-commit e fecha os recursos
	        conexaoBD.setAutoCommit(true); 
	        BancoDados.finalizarStatement(statementAdmins);
	        BancoDados.finalizarStatement(statementParticipantes);
	        BancoDados.finalizarStatement(statementEvento);
	    }
	}

	public static boolean usuarioEstaNoEvento(int userId, int eventoId) throws SQLException {
	    PreparedStatement statement = null;
	    ResultSet resultado = null;

	    try {
	        statement = conexaoBD.prepareStatement(
	            "SELECT 1 FROM participante_evento WHERE participante_id = ? AND evento_id = ?"
	        );
	        statement.setInt(1, userId); // Usa o ID recebido como parâmetro
	        statement.setInt(2, eventoId);

	        resultado = statement.executeQuery();
	        return resultado.next(); // True se encontrou registro
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	}
	
	public static boolean confirmarPresencaEvento(int userId, int eventoId) throws Exception {
	    PreparedStatement statement = null;
	    try {
	        conexaoBD.setAutoCommit(false); // Inicia transação

	        // 1. Busca o evento
	        Evento evento = EventoDao.getEventoPorId(eventoId);
	        if (evento == null) {
	            System.out.println("Evento não encontrado.");
	            return false;
	        }

	        // 2. Valida status do evento
	        if (evento.getStatus() != StatusEvento.ABERTO) {
	            System.out.println("Confirmação só é permitida em eventos ABERTOS.");
	            return false;
	        }

	        // 3. Valida horário
	        LocalDateTime dataHoraEvento = LocalDateTime.of(
	            evento.getDataEvento().toLocalDate(),
	            evento.getHoraEvento().toLocalTime()
	        );
	        LocalDateTime agora = LocalDateTime.now();
	        
	        if (agora.isAfter(dataHoraEvento.minusHours(1))) {
	            System.out.println("Confirmação permitida apenas até 1h antes do evento.");
	            return false;
	        }

	        // 4. Verifica se o usuário está inscrito
	        if (!usuarioEstaNoEvento(userId, eventoId)) {
	            System.out.println("Usuário não está registrado no evento.");
	            return false;
	        }

	        // 5. Atualiza a confirmação
	        statement = conexaoBD.prepareStatement(
	            "UPDATE participante_evento SET confirmou_presenca = true " +
	            "WHERE participante_id = ? AND evento_id = ?"
	        );
	        statement.setInt(1, userId);
	        statement.setInt(2, eventoId);

	        int rowsUpdated = statement.executeUpdate();
	        if (rowsUpdated == 0) {
	            throw new SQLException("Falha na confirmação de presença.");
	        }

	        conexaoBD.commit(); // Confirma transação
	        return true;

	    } catch (SQLException erro) {
	        conexaoBD.rollback();
	        System.err.println("Erro ao confirmar presença: " + erro.getMessage());
	        return false;
	    } finally {
	        BancoDados.finalizarStatement(statement);
	        conexaoBD.setAutoCommit(true); // Restaura auto-commit
	    }
	}
	
	
	
	
	public static boolean sairParticipanteEvento(int userId, int eventoId) throws Exception {
	    PreparedStatement statement = null;
	    try {
	        conexaoBD.setAutoCommit(false); // Inicia transação

	        // 1. Verifica se o evento está ABERTO
	        Evento evento = getEventoPorId(eventoId);
	        if (evento == null) {
	            throw new SQLException("Evento não encontrado.");
	        }
	        if (evento.getStatus() != StatusEvento.ABERTO) {
	            throw new SQLException("Só é permitido sair de eventos ABERTOS.");
	        }

	        // 2. Verifica se o usuário está no evento
	        if (!usuarioEstaNoEvento(userId, eventoId)) {
	            System.out.println("Usuário não está registrado no evento.");
	            return false;
	        }

	        // 3. Remove o participante
	        statement = conexaoBD.prepareStatement(
	            "DELETE FROM participante_evento WHERE participante_id = ? AND evento_id = ?"
	        );
	        statement.setInt(1, userId);
	        statement.setInt(2, eventoId);

	        int rowsDeleted = statement.executeUpdate();
	        if (rowsDeleted == 0) {
	            throw new SQLException("Falha ao remover participante.");
	        }

	        conexaoBD.commit(); // Confirma a transação
	        return true;

	    } catch (SQLException erro) {
	        conexaoBD.rollback(); // Reverte em caso de erro
	        System.err.println("Erro ao sair do evento: " + erro.getMessage());
	        return false;
	    } finally {
	        BancoDados.finalizarStatement(statement);
	        conexaoBD.setAutoCommit(true); // Restaura auto-commit
	    }
	}

	public static boolean sairAdministradorEvento(int adminId, int eventoId) throws Exception {
	    PreparedStatement countAdminsStmt = null;
	    PreparedStatement deleteAdminStmt = null;
	    ResultSet rs = null;
	    
	    try {
	    	
	        Evento evento = getEventoPorId(eventoId);
	        if (evento == null) {
	            throw new SQLException("Evento não encontrado.");
	        }
	    	
	        if (evento.getStatus() != StatusEvento.ABERTO) {
	            throw new SQLException("Só é permitido sair de eventos ABERTOS.");
	        }
	    	
	        conexaoBD.setAutoCommit(false); // Inicia transação

	        // 1. Verifica quantos administradores restam no evento
	        String countSql = "SELECT COUNT(*) AS total FROM administrador_evento WHERE evento_id = ?";
	        countAdminsStmt = conexaoBD.prepareStatement(countSql);
	        countAdminsStmt.setInt(1, eventoId);
	        rs = countAdminsStmt.executeQuery();

	        int totalAdmins = 0;
	        if (rs.next()) {
	            totalAdmins = rs.getInt("total");
	        }

	        // 2. Valida se é o último administrador
	        if (totalAdmins <= 1) {
	            throw new SQLException("Não é possível remover o último administrador do evento.");
	        }

	        // 3. Remove o administrador
	        String deleteSql = "DELETE FROM administrador_evento WHERE admin_id = ? AND evento_id = ?";
	        deleteAdminStmt = conexaoBD.prepareStatement(deleteSql);
	        deleteAdminStmt.setInt(1, adminId);
	        deleteAdminStmt.setInt(2, eventoId);

	        int rowsDeleted = deleteAdminStmt.executeUpdate();
	        if (rowsDeleted == 0) {
	            throw new SQLException("Administrador não está associado a este evento.");
	        }

	        conexaoBD.commit(); // Confirma as alterações
	        return true;

	    } catch (SQLException erro) {
	        conexaoBD.rollback(); // Reverte em caso de erro
	        System.err.println("Erro ao remover administrador: " + erro.getMessage());
	        return false;
	    } finally {
	        BancoDados.finalizarResultSet(rs);
	        BancoDados.finalizarStatement(countAdminsStmt);
	        BancoDados.finalizarStatement(deleteAdminStmt);
	        conexaoBD.setAutoCommit(true); // Restaura auto-commit
	    }
	}
	
	public static HashMap<Integer, Participante> getParticipantesEvento(int eventoId) throws Exception {
	    HashMap<Integer, Participante> participantes = new HashMap<>();
	    PreparedStatement statement = null;
	    ResultSet resultado = null;
	    
	    try {
	        // Query corrigida: seleciona participante_id vinculado ao evento
	        statement = conexaoBD.prepareStatement(
	            "SELECT participante_id FROM participante_evento WHERE evento_id = ?"
	        );
	        statement.setInt(1, eventoId); // Define o parâmetro do evento
	        
	        resultado = statement.executeQuery();
	        
	        while (resultado.next()) {
	            int idParticipante = resultado.getInt("participante_id");
	            User user = UserDao.getUserPorId(idParticipante); // Busca o usuário
	            
	            if (user instanceof Participante) { // Verifica se é um Participante
	                participantes.put(idParticipante, (Participante) user);
	            }
	        }
	        
	    } catch (SQLException erro) {
	        System.err.println("Erro ao buscar participantes: " + erro.getMessage());
	        throw erro; // Propaga a exceção para tratamento externo
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	    
	    return participantes;
	}
	
	public static HashMap<Integer, Administrador> getAdministradoresEvento(int eventoId) throws Exception {
	    HashMap<Integer, Administrador> administradores = new HashMap<>();
	    PreparedStatement statement = null;
	    ResultSet resultado = null;
	    
	    try {
	        // Query para buscar admins vinculados ao evento
	        statement = conexaoBD.prepareStatement(
	            "SELECT admin_id FROM administrador_evento WHERE evento_id = ?"
	        );
	        statement.setInt(1, eventoId); // Define o parâmetro do evento
	        
	        resultado = statement.executeQuery();
	        
	        while (resultado.next()) {
	            int idAdmin = resultado.getInt("admin_id");
	            User user = UserDao.getUserPorId(idAdmin); // Busca o usuário
	            
	            if (user instanceof Administrador) { // Verifica se é um Administrador
	                administradores.put(idAdmin, (Administrador) user);
	            }
	        }
	        
	    } catch (SQLException erro) {
	        System.err.println("Erro ao buscar administradores: " + erro.getMessage());
	        throw erro; // Propaga a exceção
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	    
	    return administradores;
	}
	
	public static Evento getEventoPorId(int id) throws Exception {
	    PreparedStatement statement = null;
	    ResultSet resultado = null;

	    try {
	        // Query inclui o campo "id" para compatibilidade com mapearEventoDoResultSet
	        statement = conexaoBD.prepareStatement(
	            "SELECT * FROM evento WHERE id = ?"
	        );
	        statement.setInt(1, id);

	        resultado = statement.executeQuery();
	        
	        if (resultado.next()) {
	            return mapearEventoDoResultSet(resultado); // Toda a lógica de mapeamento aqui
	        } else {
	            System.out.println("Nenhum evento encontrado com ID: " + id);
	            return null;
	        }
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	}
	
	// ==========================|| ================= ||========================== //
	
	public static List<Evento> buscarEventosPorNome(String nome) throws Exception {
	    List<Evento> eventos = new ArrayList<>();
	    PreparedStatement statement = null;
	    ResultSet resultado = null;

	    try {
	        String sql = "SELECT * FROM evento WHERE titulo LIKE ?";
	        statement = conexaoBD.prepareStatement(sql);
	        statement.setString(1, "%" + nome + "%"); // Busca parcial (ex: "Works" encontra "Workshop")

	        resultado = statement.executeQuery();
	        while (resultado.next()) {
	            Evento evento = mapearEventoDoResultSet(resultado);
	            eventos.add(evento);
	        }

	        return eventos;
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	}
	
	public static List<Evento> buscarEventosPorCategoria(CategoriaEvento categoria) throws Exception {
	    List<Evento> eventos = new ArrayList<>();
	    PreparedStatement statement = null;
	    ResultSet resultado = null;

	    try {
	        String sql = "SELECT * FROM evento WHERE categoria = ?";
	        statement = conexaoBD.prepareStatement(sql);
	        statement.setString(1, categoria.getSQL()); // Usa o valor do Enum no formato SQL

	        resultado = statement.executeQuery();
	        while (resultado.next()) {
	            Evento evento = mapearEventoDoResultSet(resultado);
	            eventos.add(evento);
	        }

	        return eventos;
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	}
	
	public static List<Evento> listarTodosEventos() throws Exception {
	    List<Evento> eventos = new ArrayList<>();
	    PreparedStatement statement = null;
	    ResultSet resultado = null;

	    try {
	        String sql = "SELECT * FROM evento";
	        statement = conexaoBD.prepareStatement(sql);

	        resultado = statement.executeQuery();
	        while (resultado.next()) {
	            Evento evento = mapearEventoDoResultSet(resultado);
	            eventos.add(evento);
	        }

	        return eventos;
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	}
	
	static Evento mapearEventoDoResultSet(ResultSet resultado) throws Exception {
	    int id = resultado.getInt("id");
	    String titulo = resultado.getString("titulo");
	    String descricao = resultado.getString("descricao");
	    Date dataEvento = resultado.getDate("data");
	    Time horaEvento = resultado.getTime("hora");
	    
	    // Converter duração para Duration (assumindo que o formato no banco é HH:MM:SS)
	    String duracaoString = resultado.getString("duracao");
	    Duration duracao = converterDuracao(duracaoString);

	    String local = resultado.getString("local");
	    boolean isLink = resultado.getBoolean("is_link");
	    int capacidadeMaxima = resultado.getInt("capacidade_maxima");
	    CategoriaEvento categoria = CategoriaEvento.getFromString(resultado.getString("categoria"));
	    StatusEvento status = StatusEvento.getFromString(resultado.getString("status"));
	    double preco = resultado.getDouble("preco");

	    // Busca relações
	    HashMap<Integer, Administrador> organizadores = getAdministradoresEvento(id);
	    HashMap<Integer, Participante> participantes = getParticipantesEvento(id);

	    return new Evento(
	        id,
	        titulo,
	        local,
	        isLink,
	        descricao,
	        capacidadeMaxima,
	        status,
	        categoria,
	        preco,
	        dataEvento,
	        horaEvento,
	        duracao,
	        organizadores,
	        participantes
	    );
	}

	/**
	 * Converte uma string no formato "HH:MM:SS" para um objeto Duration.
	 */
	private static Duration converterDuracao(String duracaoStr) {
	    if (duracaoStr == null || duracaoStr.isEmpty()) {
	        return Duration.ZERO; // Assume duração 0 se a string estiver vazia ou nula.
	    }
	    
	    try {
	        return Duration.parse(duracaoStr); // Usa o método parse para strings no formato ISO-8601.
	    } catch (Exception e) {
	        throw new IllegalArgumentException("Formato de duração inválido: " + duracaoStr, e);
	    }
	}
	
	public static List<Evento> getEventosCadastrados(int userId) throws Exception {
	    List<Evento> eventos = new ArrayList<>();
	    PreparedStatement statement = null;
	    ResultSet resultado = null;

	    try {
	        String sql = "SELECT e.* FROM evento e " +
	                     "LEFT JOIN administrador_evento ae ON e.id = ae.evento_id " +
	                     "LEFT JOIN participante_evento pe ON e.id = pe.evento_id " +
	                     "WHERE ae.admin_id = ? OR pe.participante_id = ?";
	        
	        statement = conexaoBD.prepareStatement(sql);
	        statement.setInt(1, userId);
	        statement.setInt(2, userId);

	        resultado = statement.executeQuery();

	        // Mapeia os resultados para objetos Evento
	        while (resultado.next()) {
	            Evento evento = mapearEventoDoResultSet(resultado);
	            eventos.add(evento);
	        }

	        return eventos;
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	}
	
	public static List<Evento> listarEventosAbertos() throws Exception {
	    if (conexaoBD == null || conexaoBD.isClosed()) {
	        throw new SQLException("Conexão com o banco de dados não foi inicializada ou está fechada.");
	    }

	    PreparedStatement statement = null;
	    List<Evento> eventos = new ArrayList<>();

	    try {
	        statement = conexaoBD.prepareStatement("SELECT * FROM evento WHERE status = 'ABERTO'");
	        ResultSet rs = statement.executeQuery();

	        while (rs.next()) {
	            eventos.add(mapearEventoDoResultSet(rs)); // Usando o método mapeador
	        }

	        return eventos;

	    } finally {
	        BancoDados.finalizarStatement(statement);
	    }
	}
}
