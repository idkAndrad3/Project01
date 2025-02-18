package gui;

import dao.EventoDao;
import entities.Participante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class RelatorioDetalhadoWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> comboBoxEventos;
    private JTable tabelaDetalhes;

    public RelatorioDetalhadoWindow(Connection conexao) {
        setTitle("Relatório Detalhado de Evento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        JPanel contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblEventos = new JLabel("Selecione um Evento:");
        lblEventos.setBounds(10, 10, 150, 15);
        contentPane.add(lblEventos);

        comboBoxEventos = new JComboBox<>();
        comboBoxEventos.setBounds(170, 10, 300, 25);
        contentPane.add(comboBoxEventos);

        JButton btnGerarRelatorio = new JButton("Gerar Relatório");
        btnGerarRelatorio.setBounds(480, 10, 100, 25);
        btnGerarRelatorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarDetalhesEvento(conexao);
            }
        });
        contentPane.add(btnGerarRelatorio);

        tabelaDetalhes = new JTable(new DefaultTableModel(new String[]{"ID", "Nome", "Email", "Status Inscrição", "Presença"}, 0));
        JScrollPane scrollPane = new JScrollPane(tabelaDetalhes);
        scrollPane.setBounds(10, 50, 570, 300);
        contentPane.add(scrollPane);

        carregarEventos(conexao);
    }

    private void carregarEventos(Connection conexao) {
        try {
            EventoDao.inicializarConexao(conexao);
            var eventos = EventoDao.listarTodosEventosSimplificado();
            for (String evento : eventos) {
                comboBoxEventos.addItem(evento);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDetalhesEvento(Connection conexao) {
        try {
            String eventoSelecionado = (String) comboBoxEventos.getSelectedItem();
            if (eventoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um evento.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int eventoId = Integer.parseInt(eventoSelecionado.split(" - ")[0]); // Assume formato "ID - Título"
            var participantes = EventoDao.listarParticipantesEvento(eventoId);

            DefaultTableModel model = (DefaultTableModel) tabelaDetalhes.getModel();
            model.setRowCount(0); // Limpa a tabela
            for (Participante participante : participantes) {
                model.addRow(new Object[]{
                    participante.getId(),
                    participante.getNome(),
                    participante.getEmail(),
                    participante.getStatusInscricao(),
                    participante.isPresencaConfirmada() ? "Sim" : "Não"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar detalhes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
