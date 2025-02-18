package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.EventoDao;
import entities.Evento;

public class RelatorioEventosFuturosWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tabelaEventosFuturos;

    public RelatorioEventosFuturosWindow(Connection conexao) {
        setTitle("Eventos Futuros");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        JPanel contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Eventos Futuros");
        lblTitulo.setBounds(250, 10, 200, 15);
        contentPane.add(lblTitulo);

        tabelaEventosFuturos = new JTable(new DefaultTableModel(new String[]{"ID", "Título", "Data", "Hora", "Capacidade Restante"}, 0));
        JScrollPane scrollPane = new JScrollPane(tabelaEventosFuturos);
        scrollPane.setBounds(10, 50, 570, 300);
        contentPane.add(scrollPane);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBounds(250, 360, 100, 25);
        btnAtualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarEventosFuturos(conexao);
            }
        });
        contentPane.add(btnAtualizar);

        carregarEventosFuturos(conexao);
    }

    private void carregarEventosFuturos(Connection conexao) {
        try {
            DefaultTableModel model = (DefaultTableModel) tabelaEventosFuturos.getModel();
            model.setRowCount(0); // Limpa a tabela

            EventoDao.inicializarConexao(conexao); // Garante a conexão com o DAO
            var eventos = EventoDao.listarEventosFuturos();
            for (var evento : eventos) {
                model.addRow(new Object[]{
                    evento.getId(),
                    evento.getTitulo(),
                    evento.getDataEvento(),
                    evento.getHoraEvento(),
                    evento.getCapacidadeRestante() // Usa o cálculo no Evento.java
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos futuros: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
   


}
    
