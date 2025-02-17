package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dao.BancoDados;
import dao.EventoDao;
import service.EventoManager;

public class PainelAdmin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PainelAdmin frame = new PainelAdmin();
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
    public PainelAdmin() {
        this.inicializarComponentes();
        this.inicializarConexao();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton btnCriarEvento = new JButton("Criar Evento");
        btnCriarEvento.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirCriarEventoWindow();
            }
        });

        btnCriarEvento.setBounds(136, 55, 128, 21);
        contentPane.add(btnCriarEvento);

        JButton btnEditarEvento = new JButton("Editar Evento");
        btnEditarEvento.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirEditarEventoWindow();
            }
        });
        btnEditarEvento.setBounds(136, 86, 128, 21);
        contentPane.add(btnEditarEvento);

        JButton btnVisualizarEvento = new JButton("Visualizar Evento");
        btnVisualizarEvento.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirVisualizarEventosWindow(); // Altere para chamar o método correto
            }
        });
        btnVisualizarEvento.setBounds(136, 117, 128, 21);
        contentPane.add(btnVisualizarEvento);

        JButton btnExcluirEvento = new JButton("Excluir Evento");
        btnExcluirEvento.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                excluirEvento();
            }
        });
        btnExcluirEvento.setBounds(136, 148, 128, 21);
        contentPane.add(btnExcluirEvento);

        JLabel lblPainelAdmin = new JLabel("Painel Admin");
        lblPainelAdmin.setBounds(175, 10, 103, 13);
        contentPane.add(lblPainelAdmin);
    }

    private void inicializarConexao() {
        try {
            EventoDao.inicializarConexao(BancoDados.getConexao());
            System.out.println("[DEBUG] Conexão inicializada para EventoDao.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao inicializar a conexão com o banco: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Ação do botão "Excluir Evento".
     */
    private void excluirEvento() {
        try {
            int eventoId = Integer
                    .parseInt(JOptionPane.showInputDialog(this, "Informe o ID do evento que deseja excluir:"));
            boolean sucesso = EventoManager.excluirEvento(eventoId);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Evento excluído com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir o evento.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir evento: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ação do botão "Criar Evento".
     */
    private void abrirCriarEventoWindow() {
        CriarEventoWindow criarEventoWindow = new CriarEventoWindow();
        criarEventoWindow.setVisible(true);
    }

    /**
     * Ação do botão "Editar Evento".
     */
    private void abrirEditarEventoWindow() {
        try {
            int eventoId = Integer
                    .parseInt(JOptionPane.showInputDialog(this, "Informe o ID do evento que deseja editar:"));
            EditarEventoWindow editarEventoWindow = new EditarEventoWindow(eventoId);
            editarEventoWindow.setVisible(true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ação do botão "Visualizar Evento".
     */
    private void abrirVisualizarEventosWindow() {
        // Abra a janela para visualizar a lista de eventos
        VisualizarEventoWindow visualizarEventosWindow = new VisualizarEventoWindow();
        visualizarEventosWindow.setVisible(true);
    }
}
