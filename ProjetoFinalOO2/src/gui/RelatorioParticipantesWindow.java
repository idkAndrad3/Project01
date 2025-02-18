package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.EventoDao;
import entities.Evento;
import entities.Participante;

public class RelatorioParticipantesWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tabelaParticipantes;
    private JComboBox<Evento> comboBoxEventos;
    

    public RelatorioParticipantesWindow(Connection conexao) {
        setTitle("Relatório de Participantes por Evento");
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
                carregarParticipantes();
            }
        });
        contentPane.add(btnGerarRelatorio);

        tabelaParticipantes = new JTable(new DefaultTableModel(new String[]{"ID", "Nome", "Email", "Status", "Presença"}, 0));
        JScrollPane scrollPane = new JScrollPane(tabelaParticipantes);
        scrollPane.setBounds(10, 50, 570, 300);
        contentPane.add(scrollPane);

        carregarEventos();
    }

    private void carregarEventos() {
        try {
            // Obtém a lista de eventos completos do EventoDao
            List<Evento> eventos = EventoDao.listarTodosEventos();
            
            // Limpa o comboBox antes de adicionar novos itens
            comboBoxEventos.removeAllItems();
            
            // Adiciona os eventos ao comboBox
            for (Evento evento : eventos) {
                comboBoxEventos.addItem(evento); // Adiciona o objeto Evento diretamente
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void carregarParticipantes() {
        try {
        	Evento eventoSelecionado = (Evento) comboBoxEventos.getSelectedItem();
        	if (eventoSelecionado == null) {
        	    JOptionPane.showMessageDialog(this, "Selecione um evento.", "Aviso", JOptionPane.WARNING_MESSAGE);
        	    return;
        	}
        	int eventoId = eventoSelecionado.getId();
 // Assume formato "ID - Título"
            List<Participante> participantes = EventoDao.listarParticipantesEvento(eventoId);

            DefaultTableModel model = (DefaultTableModel) tabelaParticipantes.getModel();
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
            JOptionPane.showMessageDialog(this, "Erro ao carregar participantes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
