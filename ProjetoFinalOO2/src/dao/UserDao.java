package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Administrador;
import entities.Participante;
import entities.TipoUsuario;
import entities.User;

public class UserDao {

    // Fazer a conexão com o banco de dados
    private static Connection conexaoBD;

    // Método para inicializar a conexão manualmente
    public static void inicializarConexao(Connection conexao) {
        conexaoBD = conexao;
    }

    public static boolean cadastrar(User user) throws SQLException {
        if (conexaoBD == null) {
            throw new SQLException("Conexão com o banco de dados não foi inicializada!");
        }

        PreparedStatement statement = null;
        ResultSet userIds = null;
        int userId;

        try {
            // Inserção na tabela 'usuario'
            statement = conexaoBD.prepareStatement(
                "INSERT INTO usuario (nome_completo, email, senha, tipo) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, user.getNome());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getSenha());
            statement.setString(4, user.getTipoUsuario().getSQLValue());
            System.out.println("[DEBUG] Tipo do usuário sendo salvo: " + user.getTipoUsuario().getSQLValue());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Falha ao inserir usuário, nenhuma linha afetada.");
            }

            userIds = statement.getGeneratedKeys();

            if (userIds.next()) {
                userId = userIds.getInt(1);
                user.setId(userId);  // Garante que o ID seja atribuído corretamente
                System.out.println("[DEBUG] ID gerado para o usuário: " + userId);
            } else {
                throw new SQLException("Falha ao obter ID gerado para usuário.");
            }

            // Inserção na tabela 'administrador', caso o usuário seja um Administrador
            if (user instanceof Administrador) {
                Administrador admin = (Administrador) user;
                try (PreparedStatement stmtAdmin = conexaoBD.prepareStatement(
                    "INSERT INTO administrador (id, cargo, data_contratado) VALUES (?, ?, ?)"
                )) {
                    stmtAdmin.setInt(1, user.getId()); // Usa o ID gerado na tabela 'usuario'
                    stmtAdmin.setString(2, admin.getCargo());
                    stmtAdmin.setDate(3, admin.getDataContratacao());
                    stmtAdmin.executeUpdate();
                    System.out.println("[DEBUG] Administrador inserido com sucesso na tabela 'administrador'.");
                }
            }

            // Inserção na tabela 'participante', caso o usuário seja um Participante
            if (user instanceof Participante) {
                Participante participante = (Participante) user;
                try (PreparedStatement stmtParticipante = conexaoBD.prepareStatement(
                    "INSERT INTO participante (id, cpf, data_nascimento) VALUES (?, ?, ?)"
                )) {
                    stmtParticipante.setInt(1, user.getId()); // Usa o ID gerado na tabela 'usuario'
                    stmtParticipante.setString(2, participante.getCPF());
                    stmtParticipante.setDate(3, participante.getDataNascimento());
                    
                    stmtParticipante.executeUpdate();
                    System.out.println("[DEBUG] Participante inserido com sucesso na tabela 'participante'.");
                }
            }

            // Finaliza a transação
            conexaoBD.commit();
            System.out.println("[DEBUG] Transação comitada com sucesso.");
            return true;
        } catch (SQLException erro) {
            conexaoBD.rollback();
            throw new SQLException("Erro com o banco de dados: " + erro.getMessage(), erro);
        } finally {
            BancoDados.finalizarResultSet(userIds);
            BancoDados.finalizarStatement(statement);
        }
    }



	// Obter usuário pela sua ID
	public static User getUserPorId(int id) throws SQLException {
		PreparedStatement statement = null;
		ResultSet rs = null;
		User user = null;

		try {
			// Consulta inicial para obter os dados do usuário
			statement = conexaoBD.prepareStatement("SELECT id, nome_completo, email, senha, tipo FROM usuario WHERE id = ?");
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				int userId = rs.getInt("id");
				String nome = rs.getString("nome_completo");
				String email = rs.getString("email");
				String senha = rs.getString("senha");
				String tipoStr = rs.getString("tipo");

				TipoUsuario tipoUsuario = (tipoStr != null) ? TipoUsuario.valueOf(tipoStr) : TipoUsuario.DEFAULT;

				// Fecha recursos da consulta inicial
				BancoDados.finalizarResultSet(rs);
				BancoDados.finalizarStatement(statement);

				if (tipoUsuario == TipoUsuario.admin) {
					statement = conexaoBD
							.prepareStatement("SELECT data_contratado, cargo FROM administrador WHERE id = ?");
					statement.setInt(1, id);
					rs = statement.executeQuery();

					if (rs.next()) {
						Date dataContratacao = rs.getDate("data_contratado");
						String cargo = rs.getString("cargo");

						user = new Administrador(userId, nome, email, senha, cargo, dataContratacao);
					}
				} else {
					statement = conexaoBD
							.prepareStatement("SELECT data_nascimento, cpf FROM participante WHERE id = ?");
					statement.setInt(1, id);
					rs = statement.executeQuery();

					if (rs.next()) {
						Date dataNascimento = rs.getDate("data_nascimento");
						String cpf = rs.getString("cpf");

						user = new Participante(userId, nome, email, senha, dataNascimento, cpf);
					}
				}
			}
		} finally {
			BancoDados.finalizarResultSet(rs);
			BancoDados.finalizarStatement(statement);
		}

		return user;
	}

	private static PreparedStatement cadastroAdmin(Administrador admin, int id) {
		PreparedStatement statement = null;
		String sql = "INSERT INTO administrador (id, cargo, data_contratado) VALUES (?, ?, ?)";
		try {
			statement = conexaoBD.prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, admin.getCargo());
			statement.setDate(3, admin.getDataContratacao());
		} catch (SQLException erro) {
			if (conexaoBD != null) {
				try {
					conexaoBD.rollback();
				} catch (SQLException rollbackError) {
					System.out.println("Não foi possível dar RollBack [" + rollbackError + "]");
				}
			}

			System.out.println("Erro [" + erro + "]");
		}

		return statement;
	}

	private static PreparedStatement cadastroParticipante(Participante participante, int id) {
		PreparedStatement statement = null;
		String sql = "INSERT INTO participante (id, cpf, data_nascimento) VALUES (?, ?, ?)";
		try {
			statement = conexaoBD.prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, participante.getCPF());
			statement.setDate(3, participante.getDataNascimento());
		} catch (SQLException erro) {
			System.out.println("Erro [" + erro + "]");
		}

		return statement;
	}
	public static User getUserPorEmail(String email) throws SQLException {
	    if (email == null || email.isEmpty()) {
	        throw new IllegalArgumentException("[DEBUG] O email não pode ser nulo ou vazio.");
	    }

	    PreparedStatement statement = null;
	    ResultSet rs = null;
	    User user = null;

	    try {
	        if (conexaoBD == null || conexaoBD.isClosed()) {
	            throw new SQLException("[DEBUG] Conexão com o banco de dados não foi inicializada ou está fechada.");
	        }

	        statement = conexaoBD.prepareStatement("SELECT id, nome_completo, email, senha, tipo FROM usuario WHERE email = ?");
	        statement.setString(1, email);
	        rs = statement.executeQuery();

	        if (rs.next()) { // Verifica se encontrou um usuário
	            int userId = rs.getInt("id");
	            String nome = rs.getString("nome_completo");
	            String senha = rs.getString("senha");
	            String tipoStr = rs.getString("tipo");

	            System.out.println("[DEBUG] Tipo de usuário retornado: " + tipoStr);

	            TipoUsuario tipoUsuario;
	            try {
	                tipoUsuario = TipoUsuario.getFromString(tipoStr);
	            } catch (IllegalArgumentException e) {
	                throw new SQLException("[DEBUG] Erro ao mapear tipo de usuário: " + tipoStr, e);
	            }

	            if (tipoUsuario == TipoUsuario.admin) {
	                user = new Administrador(userId, nome, email, senha, "Cargo Padrão", new Date(System.currentTimeMillis()));
	            } else if (tipoUsuario == TipoUsuario.DEFAULT) {
	                user = new Participante(userId, nome, email, senha, new Date(System.currentTimeMillis()), "000.000.000-00");
	            } else {
	                throw new SQLException("[DEBUG] Tipo de usuário desconhecido: " + tipoStr);
	            }
	        } else {
	            System.out.println("[DEBUG] Nenhum usuário encontrado para o email: " + email);
	        }
	    } finally {
	        BancoDados.finalizarResultSet(rs);
	        BancoDados.finalizarStatement(statement);
	    }

	    return user;
	}



	public static boolean apagarUserPorEmail(String email) throws SQLException {
	    PreparedStatement statement = null;
	    ResultSet rs = null;

	    try {
	        // 1. Buscar o ID e Tipo do Usuário pelo email
	        statement = conexaoBD.prepareStatement("SELECT id, tipo FROM usuario WHERE email = ?");
	        statement.setString(1, email);
	        rs = statement.executeQuery();

	        if (!rs.next()) {
	        	System.out.println("Nenhum usuário encontrado");
	            return false;
	            
	        }

	        int userId = rs.getInt("id");
	        String tipoStr = rs.getString("tipo");

	        // Fechar consulta anterior
	        BancoDados.finalizarResultSet(rs);
	        BancoDados.finalizarStatement(statement);

	        // 2. Excluir os registros na tabela correspondente ao tipo de usuário
	        if (tipoStr.equalsIgnoreCase("admin")) {
	            statement = conexaoBD.prepareStatement("DELETE FROM administrador WHERE id = ?");
	        } else {
	            statement = conexaoBD.prepareStatement("DELETE FROM participante WHERE id = ?");
	        }

	        statement.setInt(1, userId);
	        statement.executeUpdate();
	        BancoDados.finalizarStatement(statement);

	        // 3. Excluir o usuário da tabela 'usuario'
	        statement = conexaoBD.prepareStatement("DELETE FROM usuario WHERE id = ?");
	        statement.setInt(1, userId);
	        int rowsAffected = statement.executeUpdate();

	        // Realizar commit na transação
	        conexaoBD.commit();
	        return rowsAffected > 0;

	    } catch (SQLException erro) {
	        conexaoBD.rollback(); // Reverter em caso de erro
	        throw new SQLException("Erro ao excluir usuário: " + erro.getMessage(), erro);
	    } finally {
	        BancoDados.finalizarResultSet(rs);
	        BancoDados.finalizarStatement(statement);
	    }
	}

	public static boolean UsuariosMaiorQue(int quantidade) throws Exception {
	    PreparedStatement statement = null;
	    ResultSet resultado = null;

	    int comparado = 0;

	    try {
	        // Corrigida a consulta SQL com COUNT(*)
	        statement = conexaoBD.prepareStatement("SELECT COUNT(*) AS total FROM usuario");
	        resultado = statement.executeQuery();

	        if (resultado.next()) {
	            comparado = resultado.getInt("total");
	        }

	    } catch (SQLException erro) {
	        throw new Exception("ERRO ao verificar a quantidade de usuários [ " + erro + " ]");
	    } finally {
	        BancoDados.finalizarResultSet(resultado);
	        BancoDados.finalizarStatement(statement);
	    }
	    
	    
	    System.out.println("[Função UsuariosMaiorQue] " +quantidade+ " < "+comparado+ " = "+ (quantidade < comparado));
	    
	    return quantidade < comparado;
	}

}