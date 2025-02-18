package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import dao.BancoDados;
import dao.EventoDao;

public class PainelUsuario extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PainelUsuario frame = new PainelUsuario();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public PainelUsuario() {
        try {
            inicializarConexao();
            iniciarComponentes();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar conexão com o banco de dados: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }

    private void inicializarConexao() throws SQLException, Exception {
        Connection conexao = BancoDados.getConexao();
        EventoDao.inicializarConexao(conexao);
        System.out.println("[DEBUG] Conexão inicializada para o Painel do Usuário.");
    }

    private void iniciarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblPainelUsuario = new JLabel("Painel do Usuário");
        lblPainelUsuario.setBounds(175, 10, 150, 15);
        contentPane.add(lblPainelUsuario);

        JButton btnVisualizarEventos = new JButton("Visualizar Eventos");
        btnVisualizarEventos.setBounds(140, 50, 160, 25);
        btnVisualizarEventos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new VisualizarEventoWindow().setVisible(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        contentPane.add(btnVisualizarEventos);

        JButton btnInscreverEvento = new JButton("Inscrever-se em Evento");
        btnInscreverEvento.setBounds(140, 90, 160, 25);
        btnInscreverEvento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InscricaoEventoWindow().setVisible(true);
            }
        });
        contentPane.add(btnInscreverEvento);

        JButton btnCancelarInscricao = new JButton("Cancelar Inscrição");
        btnCancelarInscricao.setBounds(140, 130, 160, 25);
        btnCancelarInscricao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CancelarInscricaoWindow().setVisible(true);
            }
        });
        contentPane.add(btnCancelarInscricao);

        JButton btnConfirmarPresenca = new JButton("Confirmar Presença");
        btnConfirmarPresenca.setBounds(140, 170, 160, 25);
        btnConfirmarPresenca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConfirmarPresencaWindow().setVisible(true);
            }
        });
        contentPane.add(btnConfirmarPresenca);

        JButton btnRelatoriosParticipantes = new JButton("Relatórios de Participação");
        btnRelatoriosParticipantes.setBounds(140, 210, 160, 25);
        btnRelatoriosParticipantes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					new RelatorioParticipanteWindow().setVisible(true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        contentPane.add(btnRelatoriosParticipantes);

        JButton btnHistoricoEventos = new JButton("Histórico de Eventos");
        btnHistoricoEventos.setBounds(140, 250, 160, 25);
        btnHistoricoEventos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
					new HistoricoEventosWindow().setVisible(true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        contentPane.add(btnHistoricoEventos);
    }
}
