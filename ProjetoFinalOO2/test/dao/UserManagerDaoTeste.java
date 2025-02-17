package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dao.BancoDados;
import dao.UserDao;
import entities.Administrador;
import entities.Participante;
import entities.User;
import entities.TipoUsuario;

class UserManagerDaoTeste {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	    BancoDados.conectar();
	    UserDao.inicializarConexao(BancoDados.getConexao());
	}
	
    @AfterAll
    static void tearDownAfterClass() throws Exception {
    	BancoDados.desconectar();
    }

    @Test
    void testCadastroEExclusaoDeAdministrador() throws Exception {
        // Criação do administrador
        Administrador admin = new Administrador(
            0, // O ID será gerado automaticamente
            "Admin Teste",
            "adminblabla@teste.com",
            "senha123",
            "Gerente",
            new Date(System.currentTimeMillis())
        );

        // Testa o cadastro
        boolean cadastrado = UserDao.cadastrar(admin);
        assertTrue(cadastrado, "Administrador não foi cadastrado corretamente");

        // Verifica se o administrador foi cadastrado
        User userBuscado = UserDao.getUserPorEmail("adminblabla@teste.com");
        assertNotNull(userBuscado, "Administrador não foi encontrado no banco de dados");
        assertEquals("Admin Teste", userBuscado.getNome(), "Nome do administrador não corresponde");
        assertEquals(TipoUsuario.admin, userBuscado.getTipoUsuario(), "Tipo do usuário não é ADMIN");

        // Testa a exclusão
        boolean excluido = UserDao.apagarUserPorEmail("adminblabla@teste.com");
        assertTrue(excluido, "Administrador não foi excluído corretamente");
    }

    @Test
    void testCadastroEExclusaoDeParticipante() throws Exception {
        // Criação do participante
        Participante participante = new Participante(
        		0,
                "Bla bla",
                "blabla@teste.com",
                "senha456", new Date(System.currentTimeMillis()),
                "104.559.349-40" // CPF válido
        );

        // Testa o cadastro
        boolean cadastrado = UserDao.cadastrar(participante);
        assertTrue(cadastrado, "Participante não foi cadastrado corretamente");

        // Verifica se o participante foi cadastrado
        User userBuscado = UserDao.getUserPorEmail("blabla@teste.com");
        assertNotNull(userBuscado, "Participante não foi encontrado no banco de dados");
        assertEquals("Bla bla", userBuscado.getNome(), "Nome do participante não corresponde");
        assertEquals(TipoUsuario.DEFAULT, userBuscado.getTipoUsuario(), "Tipo do usuário não é DEFAULT");

        // Testa a exclusão
        boolean excluido = UserDao.apagarUserPorEmail("blabla@teste.com");
        assertTrue(excluido, "Participante não foi excluído corretamente");
    }

    @Test
    void testUsuariosMaiorQue() throws Exception {
        // Quantidade inicial de usuários
        boolean resultado = UserDao.UsuariosMaiorQue(0);
        assertTrue(resultado, "A verificação de usuários não foi realizada corretamente");
    }

    @Test
    void testParticipanteCPFInvalido() {
        // Tentativa de criação de um participante com CPF inválido
        Exception exception = assertThrows(Exception.class, () -> {
            new Participante(
            		0,
                    "Participante Evento Teste",
                    "cdecachorroo@teste.com",
                    "senha456", new Date(System.currentTimeMillis()),
                    "1lkasjd" // CPF inválido
            );
        });

        // Verifica a mensagem de erro da exceção
        assertEquals("CPF INVÁLIDO", exception.getMessage(), "A mensagem de exceção não corresponde.");
    }

    
}
