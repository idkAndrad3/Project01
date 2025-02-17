/*package dao;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import entities.Administrador;
import entities.Participante;
import entities.User;
import service.LoginManager;
import service.UserManager;

class UserAndLoginManagerTeste {
	
	private static User user1;
	private static User user2;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		BancoDados.conectar();
		
		user1 = new Administrador(null, "Eduardo ADM", "EduMassu@gmail.com", "krta", "Organizador geral", Date.valueOf("2004-03-07"));
		user2 = new Participante(null, "Eduardo Participante", "EduMassuCliente@gmail.com", "krta", Date.valueOf("2004-03-07"), "124.255.529-17");
		
		UserManager.cadastrarUsuario(user1);
		LoginManager.logOff();
		
		UserManager.registrarUsuario(user2);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		LoginManager.logOff();
		
		LoginManager.login("EduMassu@utfpr.edu.br", "Legal12");
		UserManager.apagarUsuario();
		
		LoginManager.login("EduMassu@cliente.utfpr.edu.br", "Legal123");
		UserManager.apagarUsuario();
		
		BancoDados.desconectar();
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	void falharAoRegistrarAdmin() {
		
		assertFalse(true);
	}
	
	@Test
	void test() {
		assertFalse(true);
	}

}
*/
