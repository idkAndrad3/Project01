package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import dao.InscricaoDao;
import dao.BancoDados;
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

        JLabel lblTitulo = new JLabel("Eventos para Confirmação de Presença");
        lblTitulo.setBounds(180, 10, 250, 15);
        contentPane.add(lblTitulo);

        // Tabela para exibir os eventos onde a presença pode ser confirmada
        String[] colunas = {"ID", "Título", "Data", "Hora", "Local"};
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

        try {
            inicializarConexao();
            carregarEventosParaConfirmacao();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Inicializa a conexão com o banco de dados para garantir que a conexão não seja nula.
     */
    private void inicializarConexao() throws Exception {
        if (!BancoDados.getConexao().isValid(1)) {
            BancoDados.conectar();
        }
        InscricaoDao.inicializarConexao(BancoDados.getConexao());
        System.out.println("[DEBUG] Conexão inicializada no ConfirmarPresencaWindow.");
    }

    /**
     * Carrega os eventos nos quais o usuário pode confirmar presença.
     */
    private void carregarEventosParaConfirmacao() {
        try {
            int participanteId = LoginManager.getUsuario().getId();
            List<Evento> eventos = InscricaoDao.listarEventosParaConfirmacao(participanteId);

            DefaultTableModel modeloTabela = (DefaultTableModel) tabelaEventos.getModel();
            modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar novos dados
            for (Evento evento : eventos) {
                modeloTabela.addRow(new Object[]{
                    evento.getId(),
                    evento.getTitulo(),
                    evento.getDataEvento(),
                    evento.getHoraEvento(),
                    evento.getLocal()
                });
            }

            if (eventos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum evento disponível para confirmação de presença.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Confirma a presença no evento selecionado.
     */
    private void confirmarPresenca() {
        int linhaSelecionada = tabelaEventos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para confirmar presença.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int eventoId = (int) tabelaEventos.getValueAt(linhaSelecionada, 0);
            int participanteId = LoginManager.getUsuario().getId();

            boolean sucesso = InscricaoDao.confirmarPresenca(participanteId, eventoId);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Presença confirmada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarEventosParaConfirmacao(); // Atualiza a tabela
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao confirmar presença.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao confirmar presença: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
