package service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import dao.BancoDados;
import dao.EventoDao;
import dao.UserDao;
import entities.TipoUsuario;
import entities.User;
import exceptions.CredenciaisInvalidasException;
import exceptions.LoginException;
import exceptions.UsuarioJaLogadoException;
import exceptions.UsuarioNaoAdminException;
import exceptions.UsuarioNaoEncontradoException;
import exceptions.UsuarioNaoParticipanteException;

public final class LoginManager {

    private static User usuarioLogado; // Usuário atualmente logado
    private static boolean conexaoInicializada = false; // Flag para indicar se a conexão já foi inicializada
    private static Connection conexaoBD;

    private LoginManager() {
        // Construtor privado para evitar instância
    }



    /**
     * Realiza login do usuário no sistema.
     * 
     * @param email O email do usuário.
     * @param senha A senha do usuário.
     * @throws LoginException Caso ocorra algum erro de login.
     */
    public static void login(String email, String senha) throws LoginException {
        if (usuarioLogado != null) {
            throw new UsuarioJaLogadoException(); // Não permite logar com outro usuário enquanto já existe um logado.
        }

        User user;

        try {
            user = UserDao.getUserPorEmail(email); // Busca o usuário pelo email no banco de dados

            if (user == null) {
                throw new UsuarioNaoEncontradoException(); // Dispara exceção se o email não for encontrado
            }

            // Valida a senha
            if (!user.getSenha().equals(senha)) {
                throw new CredenciaisInvalidasException(); // Lança exceção se a senha estiver errada
            }

            // Define o usuário como logado
            usuarioLogado = user;
            System.out.println("[DEBUG] Login bem-sucedido para o usuário: " + usuarioLogado.getEmail());
        } catch (SQLException erro) {
            throw new LoginException("Erro ao acessar o banco de dados: " + erro.getMessage());
        }
    }

    /**
     * Realiza logout do usuário logado.
     */
    public static void logOff() {
        usuarioLogado = null; // Remove o usuário logado
        System.out.println("[DEBUG] Logout realizado com sucesso.");
    }

    /**
     * Verifica se há um usuário logado.
     * 
     * @return {@code true} se houver um usuário logado, {@code false} caso contrário.
     */
    public static boolean isLogado() {
        return usuarioLogado != null;
    }

    /**
     * Verifica se o usuário logado é um administrador.
     * 
     * @throws LoginException Caso o usuário logado não seja um administrador.
     */
    public static void verAdmin() throws LoginException {
        if (usuarioLogado == null || usuarioLogado.getTipoUsuario() != TipoUsuario.admin) {
            throw new UsuarioNaoAdminException();
        }
    }

    /**
     * Verifica se o usuário logado é um participante.
     * 
     * @throws LoginException Caso o usuário logado não seja um participante.
     */
    public static void verParticipante() throws LoginException {
        if (usuarioLogado == null || usuarioLogado.getTipoUsuario() != TipoUsuario.DEFAULT) {
            throw new UsuarioNaoParticipanteException();
        }
    }

    /**
     * Retorna o usuário atualmente logado.
     * 
     * @return O usuário logado ou {@code null} se nenhum usuário estiver logado.
     */
    public static User getUsuario() {
        return usuarioLogado;
    }

    public static void inicializarConexao() throws SQLException, IOException {
        if (!conexaoInicializada || conexaoBD == null || conexaoBD.isClosed()) {
            conexaoBD = BancoDados.conectar(); // Inicializa a conexão
            UserDao.inicializarConexao(conexaoBD); // Passa a conexão para o UserDao
            EventoDao.inicializarConexao(conexaoBD); // Passa a conexão para o EventoDao
            conexaoInicializada = true;
            System.out.println("[DEBUG] Conexão inicializada no LoginManager.");
        }
    }

    /**
     * Retorna a conexão atual com o banco de dados.
     */
    public static Connection getConexao() throws SQLException {
        if (conexaoBD == null || conexaoBD.isClosed()) {
            throw new SQLException("Conexão com o banco de dados não está inicializada ou está fechada!");
        }
        return conexaoBD;
    }
}

