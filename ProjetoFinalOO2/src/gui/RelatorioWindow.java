package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dao.BancoDados;

public class RelatorioWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public RelatorioWindow() {
        setTitle("Relatórios - Administradores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Módulo de Relatórios");
        lblTitulo.setBounds(150, 10, 150, 15);
        contentPane.add(lblTitulo);

        // Conectar ao banco de dados
        final Connection conexao; // Declara a conexão como final
        try {
            conexao = BancoDados.conectar(); // Estabelece a conexão com o banco
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return; // Se não conseguir conectar, retorna e não segue a execução
        }

        // Botão Participantes por Evento
        JButton btnParticipantesPorEvento = new JButton("Participantes por Evento");
        btnParticipantesPorEvento.setBounds(120, 50, 200, 25);
        btnParticipantesPorEvento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RelatorioParticipantesWindow(conexao).setVisible(true);
            }
        });
        contentPane.add(btnParticipantesPorEvento);

        // Botão Eventos Mais Populares
        JButton btnEventosMaisPopulares = new JButton("Eventos Mais Populares");
        btnEventosMaisPopulares.setBounds(120, 90, 200, 25);
        btnEventosMaisPopulares.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RelatorioPopularesWindow(conexao).setVisible(true);
            }
        });
        contentPane.add(btnEventosMaisPopulares);

        // Botão Eventos Futuros
        JButton btnEventosFuturos = new JButton("Eventos Futuros");
        btnEventosFuturos.setBounds(120, 130, 200, 25);
        btnEventosFuturos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RelatorioEventosFuturosWindow(conexao).setVisible(true);
            }
        });
        contentPane.add(btnEventosFuturos);

        // Botão Detalhes de Evento
        JButton btnDetalhesEvento = new JButton("Detalhes de Evento");
        btnDetalhesEvento.setBounds(120, 170, 200, 25);
        btnDetalhesEvento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RelatorioDetalhadoWindow(conexao).setVisible(true);
            }
        });
        contentPane.add(btnDetalhesEvento);
    }

    public static void main(String[] args) {
        // Executa a janela de relatórios
        try {
            // Cria e exibe a janela
            new RelatorioWindow().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
