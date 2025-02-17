package service;

import java.sql.SQLException;

import dao.UserDao;
import entities.Administrador;
import entities.Participante;
import entities.TipoUsuario;
import entities.User;
import exceptions.UsuarioNaoLogadoException;

public final class UserManager {
	// ==========================|| FUNÇÕES PÚBLICAS  ||========================== //
	
	public static void registrarUsuario(User user) throws Exception {
		if (LoginManager.isLogado() && LoginManager.getUsuario().getTipoUsuario() != TipoUsuario.admin)
			throw new Exception("Um usuário precisa estar deslogado para registrar outro.");
		
		if (usuariosMaiorQue(0) == true) // Define o primeiro usuário como administrador obrigatóriamente
			user = (Administrador) user;
		else
			user = (Participante) user;
		
		cadastrarUsuario(user);
	}
	
	public static void cadastrarUsuario(User user) throws Exception {			
		User usuarioExistente = getUsuarioByEmail(user.getEmail());
		
		System.out.println(usuarioExistente);
		
		if (usuarioExistente != null)
			throw new Exception("Este Email já está sendo ultilizado.");
		
		try {
			// Criar requisitos mínimos para senha depois
			user.setSenha(user.getSenha());
			UserDao.cadastrar(user);
		} catch (Exception erro) {
			throw new Exception("Não foi possível cadastrar o usuário ["+erro+"]");
		}
		
	}
		
	public static void apagarUsuario() throws SQLException, UsuarioNaoLogadoException {
		if (!LoginManager.isLogado()) 
			throw new UsuarioNaoLogadoException();
		
		UserDao.apagarUserPorEmail(LoginManager.getUsuario().getEmail());
		LoginManager.logOff();
	}
	

	// ==========================|| FUNÇÕES DE ADMIN  ||========================== //
	
	public static void cadastrarAdministrador(Administrador user) throws Exception {
		LoginManager.verAdmin();
		
		try {
			// Criar requisitos mínimos para senha depois
			user.setSenha(user.getSenha());
			UserDao.cadastrar(user);
		} catch (Exception erro) {
			throw new Exception("Não foi possível cadastrar o administrador ["+erro+"]");
		}
	}
	
	public static boolean usuariosMaiorQue(int quantidade) throws Exception {
		return UserDao.UsuariosMaiorQue(quantidade); 
	}
		
	// ==========================|| GETTERS E SETTERS ||========================== //
	private static User getUsuarioByEmail(String email) throws Exception {
		return UserDao.getUserPorEmail(email);
	}
	
	// ==========================|| ================= ||========================== //
}

/*
 * DOCUMENTAÇÃO:
 * 	
 * 
 */


 