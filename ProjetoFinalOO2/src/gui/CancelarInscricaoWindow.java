package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import dao.EventoDao;
import entities.Evento;
import service.LoginManager;

public class CancelarInscricaoWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<Evento> comboBoxEventos;
    private JButton btnCancelar;
    

    public CancelarInscricaoWindow() {
        try {
            // Inicializa a conexão com o banco de dados
            LoginManager.inicializarConexao();
            EventoDao.inicializarConexao(LoginManager.getConexao());
            System.out.println("[DEBUG] Conexão inicializada no CancelarInscricaoWindow.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inicializar conexão com o banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose(); // Fecha a janela em caso de erro crítico
            return;
        }

        iniciarComponentes();
        carregarEventos();
    }

    private void iniciarComponentes() {
        setTitle("Cancelar Inscrição");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 200);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblSelecioneEvento = new JLabel("Selecione o evento:");
        lblSelecioneEvento.setBounds(20, 20, 200, 25);
        contentPane.add(lblSelecioneEvento);

        comboBoxEventos = new JComboBox<>();
        comboBoxEventos.setBounds(20, 50, 350, 25);
        contentPane.add(comboBoxEventos);

        btnCancelar = new JButton("Cancelar Inscrição");
        btnCancelar.setBounds(20, 100, 350, 25);
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarInscricao();
            }
        });
        contentPane.add(btnCancelar);
    }

    private void carregarEventos() {
        try {
            // Obtém os eventos nos quais o usuário está inscrito
            List<Evento> eventosInscritos = EventoDao.listarEventosInscritos(LoginManager.getUsuario().getId());
            if (eventosInscritos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Você não está inscrito em nenhum evento.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }

            // Popula o comboBox com os eventos inscritos (usa o título para exibição)
            for (Evento evento : eventosInscritos) {
                comboBoxEventos.addItem(evento);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cancelarInscricao() {
        try {
            // Obtém o evento selecionado do comboBox
            Evento eventoSelecionado = (Evento) comboBoxEventos.getSelectedItem();
            if (eventoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um evento para cancelar a inscrição.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Cancela a inscrição no banco de dados
            boolean cancelado = EventoDao.cancelarInscricao(LoginManager.getUsuario().getId(), eventoSelecionado.getId());
            if (cancelado) {
                JOptionPane.showMessageDialog(this, "Inscrição cancelada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cancelar a inscrição.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao cancelar inscrição: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
