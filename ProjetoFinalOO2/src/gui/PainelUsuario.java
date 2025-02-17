package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    /**
     * Launch the application.
     */
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

    /**
     * Create the frame.
     */
    public PainelUsuario() {
        try {
            inicializarConexao();
            iniciarComponentes();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar conexão com o banco de dados: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose(); // Fecha a janela se a inicialização falhar
        }
    }

    private void inicializarConexao() throws SQLException, Exception {
        Connection conexao = BancoDados.getConexao();
        EventoDao.inicializarConexao(conexao); // Inicializa o EventoDao com a conexão ativa
        System.out.println("[DEBUG] Conexão inicializada para o Painel do Usuário.");
    }

    private void iniciarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
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
                new VisualizarEventoWindow().setVisible(true);
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
    }
}
