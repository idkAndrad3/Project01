package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import dao.EventoDao;
import entities.Administrador;
import entities.CategoriaEvento;
import entities.Evento;
import entities.StatusEvento;
import service.LoginManager;

public class EditarEventoWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtTitulo;
    private JTextField txtDescricao;
    private JTextField txtLocal;
    private JFormattedTextField txtData;
    private JFormattedTextField txtHora;
    private JTextField txtDuracao;
    private JTextField txtPreco;
    private JTextField txtCapacidadeMaxima;
    private JComboBox<StatusEvento> cbStatus;
    private JComboBox<CategoriaEvento> cbCategoria;
    private JButton btnSalvar;

    private Evento evento;

    /**
     * Construtor para a janela de edição de eventos.
     * 
     * @param eventoId ID do evento a ser editado.
     */
    public EditarEventoWindow(int eventoId) {
        // Busca o evento a ser editado
        try {
            evento = EventoDao.getEventoPorId(eventoId);
            if (evento == null) {
                JOptionPane.showMessageDialog(this, "Evento não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar evento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        iniciarComponentes();
        preencherDadosEvento();
    }

    /**
     * Inicializa os componentes da janela.
     */
    private void iniciarComponentes() {
        setTitle("Editar Evento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 550, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setBounds(20, 20, 100, 25);
        contentPane.add(lblTitulo);

        txtTitulo = new JTextField();
        txtTitulo.setBounds(150, 20, 300, 25);
        contentPane.add(txtTitulo);

        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setBounds(20, 60, 100, 25);
        contentPane.add(lblDescricao);

        txtDescricao = new JTextField();
        txtDescricao.setBounds(150, 60, 300, 25);
        contentPane.add(txtDescricao);

        JLabel lblLocal = new JLabel("Local:");
        lblLocal.setBounds(20, 100, 100, 25);
        contentPane.add(lblLocal);

        txtLocal = new JTextField();
        txtLocal.setBounds(150, 100, 300, 25);
        contentPane.add(txtLocal);

        JLabel lblData = new JLabel("Data (dd/MM/yyyy):");
        lblData.setBounds(20, 140, 150, 25);
        contentPane.add(lblData);

        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/##/####");
            txtData = new JFormattedTextField(dataFormatter);
        } catch (Exception e) {
            txtData = new JFormattedTextField();
        }
        txtData.setBounds(150, 140, 150, 25);
        contentPane.add(txtData);

        JLabel lblHora = new JLabel("Hora (HH:mm):");
        lblHora.setBounds(20, 180, 150, 25);
        contentPane.add(lblHora);

        try {
            MaskFormatter horaFormatter = new MaskFormatter("##:##");
            txtHora = new JFormattedTextField(horaFormatter);
        } catch (Exception e) {
            txtHora = new JFormattedTextField();
        }
        txtHora.setBounds(150, 180, 150, 25);
        contentPane.add(txtHora);

        JLabel lblDuracao = new JLabel("Duração (hh:mm):");
        lblDuracao.setBounds(20, 220, 150, 25);
        contentPane.add(lblDuracao);

        txtDuracao = new JTextField();
        txtDuracao.setBounds(150, 220, 150, 25);
        contentPane.add(txtDuracao);

        JLabel lblPreco = new JLabel("Preço:");
        lblPreco.setBounds(20, 260, 150, 25);
        contentPane.add(lblPreco);

        txtPreco = new JTextField();
        txtPreco.setBounds(150, 260, 150, 25);
        contentPane.add(txtPreco);

        JLabel lblCapacidadeMaxima = new JLabel("Capacidade Máxima:");
        lblCapacidadeMaxima.setBounds(20, 300, 150, 25);
        contentPane.add(lblCapacidadeMaxima);

        txtCapacidadeMaxima = new JTextField();
        txtCapacidadeMaxima.setBounds(150, 300, 150, 25);
        contentPane.add(txtCapacidadeMaxima);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setBounds(20, 340, 150, 25);
        contentPane.add(lblStatus);

        cbStatus = new JComboBox<>(StatusEvento.values());
        cbStatus.setBounds(150, 340, 150, 25);
        contentPane.add(cbStatus);

        JLabel lblCategoria = new JLabel("Categoria:");
        lblCategoria.setBounds(20, 380, 150, 25);
        contentPane.add(lblCategoria);

        cbCategoria = new JComboBox<>(CategoriaEvento.values());
        cbCategoria.setBounds(150, 380, 150, 25);
        contentPane.add(cbCategoria);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(200, 420, 100, 30);
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarAlteracoes();
            }
        });
        contentPane.add(btnSalvar);
    }

    /**
     * Preenche os campos com os dados do evento a ser editado.
     */
    private void preencherDadosEvento() {
        txtTitulo.setText(evento.getTitulo());
        txtDescricao.setText(evento.getDescricao());
        txtLocal.setText(evento.getLocal());
        txtData.setText(evento.getDataEvento().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtHora.setText(evento.getHoraEvento().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        
        // Exibir duração corretamente formatada (HH:mm)
        long horas = evento.getDuracaoEvento().toHours();
        long minutos = evento.getDuracaoEvento().toMinutes() % 60;
        txtDuracao.setText(String.format("%02d:%02d", horas, minutos));
        
        txtPreco.setText(String.valueOf(evento.getPreco()));
        txtCapacidadeMaxima.setText(String.valueOf(evento.getCapacidadeMaxima()));
        cbStatus.setSelectedItem(evento.getStatus());
        cbCategoria.setSelectedItem(evento.getCategoria());
    }


    /**
     * Salva as alterações realizadas no evento.
     */
    private void salvarAlteracoes() {
        try {
            evento.setTitulo(txtTitulo.getText());
            evento.setDescricao(txtDescricao.getText());
            evento.setLocal(txtLocal.getText());
            evento.setDataEvento(Date.valueOf(LocalDate.parse(txtData.getText(), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            evento.setHoraEvento(Time.valueOf(LocalTime.parse(txtHora.getText(), java.time.format.DateTimeFormatter.ofPattern("HH:mm"))));

            // Processar duração no formato HH:mm
            String[] duracaoParts = txtDuracao.getText().split(":");
            long horas = Long.parseLong(duracaoParts[0]);
            long minutos = Long.parseLong(duracaoParts[1]);
            evento.setDuracaoEvento(Duration.ofHours(horas).plusMinutes(minutos));

            evento.setPreco(Double.parseDouble(txtPreco.getText()));
            evento.setCapacidadeMaxima(Integer.parseInt(txtCapacidadeMaxima.getText()));
            evento.setStatus((StatusEvento) cbStatus.getSelectedItem());
            evento.setCategoria((CategoriaEvento) cbCategoria.getSelectedItem());

            if (EventoDao.editar(evento)) {
                JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar o evento.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar alterações: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
