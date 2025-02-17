package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import dao.EventoDao;
import entities.Administrador;
import entities.CategoriaEvento;
import entities.Evento;
import entities.StatusEvento;
import service.LoginManager;

public class CriarEventoWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtTitulo;
    private JTextField txtLocal;
    private JTextField txtDescricao;
    private JFormattedTextField txtData;
    private JFormattedTextField txtHora;
    private JTextField txtDuracao;

    public CriarEventoWindow() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 400);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setBounds(30, 30, 100, 25);
        contentPane.add(lblTitulo);

        txtTitulo = new JTextField();
        txtTitulo.setBounds(150, 30, 200, 25);
        contentPane.add(txtTitulo);

        JLabel lblLocal = new JLabel("Local:");
        lblLocal.setBounds(30, 70, 100, 25);
        contentPane.add(lblLocal);

        txtLocal = new JTextField();
        txtLocal.setBounds(150, 70, 200, 25);
        contentPane.add(txtLocal);

        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setBounds(30, 110, 100, 25);
        contentPane.add(lblDescricao);

        txtDescricao = new JTextField();
        txtDescricao.setBounds(150, 110, 200, 25);
        contentPane.add(txtDescricao);

        JLabel lblData = new JLabel("Data (dd/MM/yyyy):");
        lblData.setBounds(30, 150, 150, 25);
        contentPane.add(lblData);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/##/####");
            dataFormatter.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(dataFormatter);
            txtData.setBounds(150, 150, 200, 25);
            contentPane.add(txtData);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao configurar o campo de data: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JLabel lblHora = new JLabel("Hora (HH:mm):");
        lblHora.setBounds(30, 190, 150, 25);
        contentPane.add(lblHora);

        try {
            MaskFormatter horaFormatter = new MaskFormatter("##:##");
            horaFormatter.setPlaceholderCharacter('_');
            txtHora = new JFormattedTextField(horaFormatter);
            txtHora.setBounds(150, 190, 200, 25);
            contentPane.add(txtHora);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao configurar o campo de hora: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JLabel lblDuracao = new JLabel("Duração (em horas):");
        lblDuracao.setBounds(30, 230, 150, 25);
        contentPane.add(lblDuracao);

        txtDuracao = new JTextField();
        txtDuracao.setBounds(150, 230, 200, 25);
        contentPane.add(txtDuracao);

        JButton btnSalvar = new JButton("Salvar Evento");
        btnSalvar.setBounds(150, 280, 150, 30);
        contentPane.add(btnSalvar);

        btnSalvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                salvarEvento();
            }
        });
    }

    private void salvarEvento() {
        try {
            // Obtém o administrador logado
            Administrador adminLogado = (Administrador) LoginManager.getUsuario();
            if (adminLogado == null) {
                JOptionPane.showMessageDialog(this, "Erro: Nenhum administrador logado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validação dos campos de data e hora
            String dataTexto = txtData.getText();
            String horaTexto = txtHora.getText();
            if (dataTexto.contains("_") || horaTexto.contains("_")) {
                JOptionPane.showMessageDialog(this, "Data ou hora estão incompletas!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Converte os dados do formulário para os formatos adequados
            String[] dataSplit = dataTexto.split("/");
            Date dataEvento = Date.valueOf(dataSplit[2] + "-" + dataSplit[1] + "-" + dataSplit[0]); // Formato yyyy-MM-dd
            Time horaEvento = Time.valueOf(horaTexto + ":00"); // Adiciona os segundos para Time
            long duracaoHoras = Long.parseLong(txtDuracao.getText());

            // Cria o novo evento
            Evento novoEvento = new Evento(
                0,
                txtTitulo.getText(),
                txtLocal.getText(),
                false, // Não é um link
                txtDescricao.getText(),
                100, // Capacidade máxima (fixa para o exemplo)
                StatusEvento.ABERTO,
                CategoriaEvento.CONFERENCIA, // Categoria fixa para o exemplo
                0.0, // Preço fixo para o exemplo
                dataEvento, // Data do evento
                horaEvento, // Hora do evento
                Duration.ofHours(duracaoHoras), // Duração
                new HashMap<>() {{
                    put(adminLogado.getId(), adminLogado); // Adiciona o admin como organizador
                }},
                new HashMap<>() // Nenhum participante inicialmente
            );

            // Salva o evento no banco de dados
            boolean sucesso = EventoDao.cadastrarEvento(novoEvento);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Evento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Fecha a janela após salvar
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao criar o evento.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar evento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
