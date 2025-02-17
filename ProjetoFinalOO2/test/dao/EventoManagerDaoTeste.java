package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import entities.Administrador;
import entities.Evento;
import entities.CategoriaEvento;
import entities.StatusEvento;
import entities.Participante;

class EventoManagerDaoTeste {

    private static Administrador adminTeste;
    private static Participante participanteTeste;
    private static Evento eventoTeste;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        BancoDados.conectar();
        UserDao.inicializarConexao(BancoDados.getConexao());  // <-- Garante que o UserDao tenha uma conexão ativa
        EventoDao.inicializarConexao(BancoDados.getConexao()); // <-- Agora inicializamos o EventoDao também!
    }


    @AfterAll
    static void tearDownAfterClass() throws Exception {
        BancoDados.desconectar();
    }

    @BeforeEach
    void setUp() throws Exception {
    	criarUsuariosTeste();
        criarEventoTeste();
    }

    /*@AfterEach
    void tearDown() throws Exception {
        limparDadosTeste();
    }*/

    // ==========================|| TESTES ||========================== //
    @Test
    void testAdicionarEventoValido() throws Exception {
        boolean resultado = EventoDao.cadastrarEvento(eventoTeste);
        
        assertTrue(resultado, "Evento válido deveria ser adicionado");
        
        Evento eventoRecuperado = EventoDao.getEventoPorId(eventoTeste.getId());
        
        assertNotNull(eventoRecuperado, "Evento não foi encontrado após inserção");
    }

    /*@Test
    void testAdicionarEventoSemOrganizadores() throws SQLException {
    	System.out.println("====================================> testAdicionarEventoSemOrganizadores");
        eventoTeste.getOrganizadores().clear();
        eventoTeste.removerOrganizador(adminTeste.getId());
        
        assertFalse(EventoDao.cadastrarEvento(eventoTeste), "Deveria falhar ao adicionar evento sem organizadores");
        
        eventoTeste.adicionarOrganizador(adminTeste);
        System.out.println("====================================> /testAdicionarEventoSemOrganizadores");
    }*/
    
   /* @Test
    void testExcluirEvento() throws Exception {
        // Adiciona o evento (o ID é definido automaticamente)
        boolean SummerEletroHits = EventoDao.cadastrarEvento(eventoTeste);
        System.out.println("Evento: "+ SummerEletroHits);
        // Recupera o ID atualizado
        int eventoId = eventoTeste.getId();
        
        // Exclui o evento
        boolean resultado = EventoDao.excluir(eventoId);
        assertTrue(resultado, "Exclusão deveria ser bem-sucedida");
        
        // Verifica se o evento foi removido
        Evento eventoExcluido = EventoDao.getEventoPorId(eventoId);
        assertNull(eventoExcluido, "Evento deveria ser removido");
    }*/
    
    @Test
    void testEditarEvento() throws Exception {
        EventoDao.cadastrarEvento(eventoTeste);
        
        eventoTeste.setTitulo("Novo Título Editado");
        boolean resultado = EventoDao.editar(eventoTeste);
        
        assertTrue(resultado, "Edição deveria ser bem-sucedida");
        Evento eventoEditado = EventoDao.getEventoPorId(eventoTeste.getId());
        assertEquals("Novo Título Editado", eventoEditado.getTitulo());
    }

    // ==========================|| MÉTODOS AUXILIARES ||========================== //
    private static void criarUsuariosTeste() throws Exception {
        // Cria administrador de teste
        adminTeste = new Administrador(
            0, 
            "Admin Evento Teste",
            "admin.evento@teste.com",
            "senha123",
            "Cargo Teste",
            new Date(System.currentTimeMillis())
        );
        
        boolean cadastradoAdmin = UserDao.cadastrar(adminTeste);
        System.out.println("Administrador cadastrado? " + cadastradoAdmin);

        // Busca usuário do banco para confirmar se foi cadastrado corretamente
        adminTeste = (Administrador) UserDao.getUserPorEmail("admin.evento@teste.com");
        
        System.out.println("Admin encontrado no banco? " + (adminTeste != null ? "Sim" : "Não"));

        // Cria participante de teste
        participanteTeste = new Participante(
            0,
            "Bla bla 2m",
            "blabla@teste.com2m",
            "senha456", new Date(System.currentTimeMillis()),
            "104.559.399-00" // CPF válido
        );
        
        boolean cadastradoParticipante = UserDao.cadastrar(participanteTeste);
        System.out.println("Participante cadastrado? " + cadastradoParticipante);

        participanteTeste = (Participante) UserDao.getUserPorEmail("blabla@teste.com2m");
        System.out.println("Participante encontrado no banco? " + (participanteTeste != null ? "Sim" : "Não"));
    }


    private void criarEventoTeste() {
        HashMap<Integer, Administrador> organizadores = new HashMap<>();
        if (adminTeste != null && adminTeste.getId() != null) {
            organizadores.put(adminTeste.getId(), adminTeste);
            System.out.println("Organizador adicionado com ID: " + adminTeste.getId());
        } else {
            System.out.println("ERRO: Nenhum organizador disponível!");
        }

        eventoTeste = new Evento(
            0,
            "Evento de Teste",
            "Auditório Principal",
            false,
            "Descrição do Evento de Teste",
            100,
            StatusEvento.ABERTO,
            CategoriaEvento.CONFERENCIA,
            0.0,
            Date.valueOf(LocalDate.now().plusDays(7)),
            Time.valueOf(LocalTime.of(14, 0)),
            Duration.ofHours(2),
            organizadores,
            new HashMap<>()
        );
    }

     private void limparDadosTeste() throws SQLException {
        try {
           
            if (eventoTeste.getId() != null) {
                EventoDao.excluir(eventoTeste.getId());
            }

            UserDao.apagarUserPorEmail("admin.evento@teste.com");
            UserDao.apagarUserPorEmail("participante.evento@teste.com");
            
        } catch (SQLException e) {
            System.err.println("Erro na limpeza de dados: " + e.getMessage());
            throw e; // Propaga para falhar o teste se houver erro crítico
        }
    }

    
}