package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dao.EventoDao;
import entities.Evento;
import entities.StatusEvento;

public class AtualizadorStatusEvento {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Inicia a verificação periódica
    public static void iniciarAtualizacao() {
        scheduler.scheduleAtFixedRate(
            AtualizadorStatusEvento::verificarEAtualizarStatusEventos,
            0, 1, TimeUnit.MINUTES
        );
    }

    // Método para parar a atualização
    public static void pararAtualizacao() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    // Lógica de atualização de status
    private static void verificarEAtualizarStatusEventos() {
    	System.out.println("Verificando...");
        try {
            List<Evento> eventos = EventoDao.listarTodosEventos();
            LocalDateTime agora = LocalDateTime.now();

            for (Evento evento : eventos) {
                atualizarStatusDoEvento(evento, agora);
            }
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao atualizar eventos: " + e.getMessage());
        }
    }

    // Método auxiliar para atualizar o status de um único evento
    private static void atualizarStatusDoEvento(Evento evento, LocalDateTime agora) {
        try {
            LocalDateTime dataHoraInicio = LocalDateTime.of(
                evento.getDataEvento().toLocalDate(),
                evento.getHoraEvento().toLocalTime()
            );
            LocalDateTime dataHoraFim = dataHoraInicio.plus(evento.getDuracaoEvento());

            if (agora.isAfter(dataHoraFim) && evento.getStatus() != StatusEvento.ENCERRADO) {
                evento.setStatus(StatusEvento.ENCERRADO);
                EventoDao.editar(evento);
                System.out.println("[INFO] Evento " + evento.getId() + " encerrado.");
            } else if (agora.isAfter(dataHoraInicio) && agora.isBefore(dataHoraFim) 
                    && evento.getStatus() != StatusEvento.ANDAMENTO) {
                evento.setStatus(StatusEvento.ANDAMENTO);
                EventoDao.editar(evento);
                System.out.println("[INFO] Evento " + evento.getId() + " em andamento.");
            }
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao atualizar evento " + evento.getId() + ": " + e.getMessage());
        }
    }
}