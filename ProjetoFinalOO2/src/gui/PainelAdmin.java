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
        setBounds(100, 100, 450, 500); // Aumentei o tamanho para acomodar mais botões
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
                abrirVisualizarEventosWindow();
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

        JButton btnRelatorioParticipantes = new JButton("Relatório Participantes");
        btnRelatorioParticipantes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirRelatorioParticipantesWindow();
            }
        });
        btnRelatorioParticipantes.setBounds(136, 179, 180, 21); // Botão para Relatório de Participantes
        contentPane.add(btnRelatorioParticipantes);

        JButton btnEventosPopulares = new JButton("Eventos Populares");
        btnEventosPopulares.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirRelatorioEventosPopularesWindow();
            }
        });
        btnEventosPopulares.setBounds(136, 210, 180, 21); // Botão para Eventos Populares
        contentPane.add(btnEventosPopulares);

        JButton btnEventosFuturos = new JButton("Eventos Futuros");
        btnEventosFuturos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirRelatorioEventosFuturosWindow();
            }
        });
        btnEventosFuturos.setBounds(136, 241, 180, 21); // Botão para Eventos Futuros
        contentPane.add(btnEventosFuturos);

        JButton btnRelatorioDetalhado = new JButton("Relatório Detalhado Evento");
        btnRelatorioDetalhado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirRelatorioDetalhadoEventoWindow();
            }
        });
        btnRelatorioDetalhado.setBounds(136, 272, 180, 21); // Botão para Relatório Detalhado de Evento
        contentPane.add(btnRelatorioDetalhado);

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
        VisualizarEventoWindow visualizarEventosWindow = new VisualizarEventoWindow();
        visualizarEventosWindow.setVisible(true);
    }

    /**
     * Ação do botão "Relatório Participantes".
     */
    private void abrirRelatorioParticipantesWindow() {
        try {
            // Garantindo que a conexão esteja sendo obtida corretamente
            Connection conexao = BancoDados.getConexao();
            RelatorioParticipantesWindow relatorioWindow = new RelatorioParticipantesWindow(conexao);
            relatorioWindow.setVisible(true);
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o relatório de participantes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Ação do botão "Eventos Populares".
     */
 // Exemplo de como a conexão é passada corretamente para a janela de relatórios
    private void abrirRelatorioEventosPopularesWindow() {
        try {
            // Garantindo que a conexão esteja sendo obtida corretamente
            Connection conexao = BancoDados.getConexao();  
            RelatorioPopularesWindow relatorioWindow = new RelatorioPopularesWindow(conexao);
            relatorioWindow.setVisible(true);
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ação do botão "Eventos Futuros".
     */
    private void abrirRelatorioEventosFuturosWindow() {
        try {
            // Garantindo que a conexão esteja sendo obtida corretamente
            Connection conexao = BancoDados.getConexao();
            RelatorioEventosFuturosWindow relatorioWindow = new RelatorioEventosFuturosWindow(conexao);
            relatorioWindow.setVisible(true);
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ação do botão "Relatório Detalhado de Evento".
     */
    private void abrirRelatorioDetalhadoEventoWindow() {
        try {
            // Garantindo que a conexão esteja sendo obtida corretamente
            Connection conexao = BancoDados.getConexao();
            RelatorioDetalhadoWindow relatorioWindow = new RelatorioDetalhadoWindow(conexao);
            relatorioWindow.setVisible(true);
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir o relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
