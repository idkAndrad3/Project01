package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.BancoDados;
import dao.EventoDao;
import entities.Evento;

public class RelatorioParticipanteWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable tabelaEventos;

    public RelatorioParticipanteWindow() throws IOException {
        setTitle("Relatórios de Participação");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        JPanel contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Eventos Inscritos");
        lblTitulo.setBounds(250, 10, 200, 15);
        contentPane.add(lblTitulo);

        tabelaEventos = new JTable(new DefaultTableModel(new String[]{"ID", "Título", "Data", "Local"}, 0));
        JScrollPane scrollPane = new JScrollPane(tabelaEventos);
        scrollPane.setBounds(10, 50, 570, 300);
        contentPane.add(scrollPane);

        carregarEventos();
    }

    private void carregarEventos() throws IOException {
        try {
            Connection conexao = BancoDados.getConexao();
            List<Evento> eventos = EventoDao.listarEventosInscritosUsuario();
            DefaultTableModel model = (DefaultTableModel) tabelaEventos.getModel();
            model.setRowCount(0);
            for (Evento evento : eventos) {
                model.addRow(new Object[]{evento.getId(), evento.getTitulo(), evento.getDataEvento(), evento.getLocal()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}