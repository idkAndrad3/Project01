package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import dao.InscricaoDao;
import entities.Evento;
import service.LoginManager;

public class ConfirmarPresencaWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tabelaEventos;

    public ConfirmarPresencaWindow() {
        setTitle("Confirmar Presença");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Eventos para Confirmar Presença");
        lblTitulo.setBounds(180, 10, 250, 15);
        contentPane.add(lblTitulo);

        // Tabela para exibir eventos
        String[] colunas = {"ID", "Título", "Data", "Hora"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEventos = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaEventos);
        scrollPane.setBounds(10, 40, 560, 250);
        contentPane.add(scrollPane);

        JButton btnConfirmar = new JButton("Confirmar Presença");
        btnConfirmar.setBounds(230, 310, 150, 25);
        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarPresenca();
            }
        });
        contentPane.add(btnConfirmar);

        carregarEventosParaConfirmacao();
    }

    private void carregarEventosParaConfirmacao() {
        try {
            List<Evento> eventos = InscricaoDao.listarEventosParaConfirmacao(LoginManager.getUsuario().getId());
            DefaultTableModel modeloTabela = (DefaultTableModel) tabelaEventos.getModel();
            modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar novos dados
            for (Evento evento : eventos) {
                modeloTabela.addRow(new Object[]{
                    evento.getId(),
                    evento.getTitulo(),
                    evento.getDataEvento(),
                    evento.getHoraEvento()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarPresenca() {
        int linhaSelecionada = tabelaEventos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para confirmar presença.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int eventoId = (int) tabelaEventos.getValueAt(linhaSelecionada, 0);
            boolean sucesso = InscricaoDao.confirmarPresenca(LoginManager.getUsuario().getId(), eventoId);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Presença confirmada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarEventosParaConfirmacao(); // Atualiza a tabela
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao confirmar presença.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao confirmar presença: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
