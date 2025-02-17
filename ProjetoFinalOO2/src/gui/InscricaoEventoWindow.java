package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import dao.EventoDao;
import dao.InscricaoDao;
import entities.Evento;
import service.LoginManager;

public class InscricaoEventoWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tabelaEventos;

    public InscricaoEventoWindow() {
        try {
            // Inicializa a conexão com o banco de dados
            LoginManager.inicializarConexao();
            EventoDao.inicializarConexao(LoginManager.getConexao());
            InscricaoDao.inicializarConexao(LoginManager.getConexao());
            System.out.println("[DEBUG] Conexão inicializada no InscricaoEventoWindow.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar conexão com o banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose(); // Fecha a janela em caso de erro crítico
            return;
        }

        iniciarComponentes();
        carregarEventosAbertos();
    }

    private void iniciarComponentes() {
        setTitle("Inscrever-se em Evento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Eventos Abertos");
        lblTitulo.setBounds(250, 10, 150, 15);
        contentPane.add(lblTitulo);

        // Tabela para exibir os eventos abertos
        String[] colunas = {"ID", "Título", "Data", "Hora", "Vagas Disponíveis"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEventos = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaEventos);
        scrollPane.setBounds(10, 40, 560, 250);
        contentPane.add(scrollPane);

        JButton btnInscrever = new JButton("Inscrever-se");
        btnInscrever.setBounds(230, 310, 120, 25);
        btnInscrever.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inscreverEmEvento();
            }
        });
        contentPane.add(btnInscrever);
    }

    private void carregarEventosAbertos() {
        try {
            List<Evento> eventos = EventoDao.listarEventosAbertos();
            DefaultTableModel modeloTabela = (DefaultTableModel) tabelaEventos.getModel();
            modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar novos dados

            for (Evento evento : eventos) {
                int vagasDisponiveis = evento.getCapacidadeMaxima() - evento.getParticipantes().size();

                if (vagasDisponiveis > 0) { // Apenas exibe eventos com vagas disponíveis
                    modeloTabela.addRow(new Object[]{
                        evento.getId(),
                        evento.getTitulo(),
                        evento.getDataEvento(),
                        evento.getHoraEvento(),
                        vagasDisponiveis
                    });
                }
            }

            if (modeloTabela.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum evento disponível para inscrição.", "Informação", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void inscreverEmEvento() {
        int linhaSelecionada = tabelaEventos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para se inscrever.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtém o ID do evento selecionado
            int eventoId = (int) tabelaEventos.getValueAt(linhaSelecionada, 0);

            // Verifica se o usuário já está inscrito
            if (InscricaoDao.verificarInscricao(LoginManager.getUsuario().getId(), eventoId)) {
                JOptionPane.showMessageDialog(this, "Você já está inscrito neste evento.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Realiza a inscrição
            boolean sucesso = InscricaoDao.inscrever(LoginManager.getUsuario().getId(), eventoId);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Inscrição realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarEventosAbertos(); // Atualiza a tabela
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao realizar inscrição.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao se inscrever no evento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
