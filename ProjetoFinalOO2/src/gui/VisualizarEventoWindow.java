package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import dao.EventoDao;
import entities.Evento;

public class VisualizarEventoWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable tabelaEventos;
    private JScrollPane scrollPane;
    private JPanel painelDetalhes;

    // Labels para exibir detalhes do evento
    private JLabel lblTitulo, lblDescricao, lblLocal, lblData, lblHora, lblDuracao, lblPreco, lblCapacidadeMaxima, lblStatus, lblCategoria;

    public VisualizarEventoWindow() {
        setTitle("Visualizar Todos os Eventos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Configuração da tabela de eventos
        String[] colunas = {"ID", "Título", "Data", "Hora", "Local"};
        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEventos = new JTable(modeloTabela);
        tabelaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Adiciona a tabela a um JScrollPane
        scrollPane = new JScrollPane(tabelaEventos);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Painel inferior para detalhes do evento
        painelDetalhes = new JPanel();
        painelDetalhes.setLayout(new GridLayout(10, 2, 10, 10));
        painelDetalhes.setBorder(BorderFactory.createTitledBorder("Detalhes do Evento"));

        // Adicionando labels para os detalhes
        lblTitulo = adicionarLabel("Título:");
        lblDescricao = adicionarLabel("Descrição:");
        lblLocal = adicionarLabel("Local:");
        lblData = adicionarLabel("Data:");
        lblHora = adicionarLabel("Hora:");
        lblDuracao = adicionarLabel("Duração:");
        lblPreco = adicionarLabel("Preço:");
        lblCapacidadeMaxima = adicionarLabel("Capacidade Máxima:");
        lblStatus = adicionarLabel("Status:");
        lblCategoria = adicionarLabel("Categoria:");

        contentPane.add(painelDetalhes, BorderLayout.SOUTH);

        // Botão para carregar detalhes do evento selecionado
        JButton btnCarregarDetalhes = new JButton("Carregar Detalhes");
        btnCarregarDetalhes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarDetalhesEvento();
            }
        });
        contentPane.add(btnCarregarDetalhes, BorderLayout.NORTH);

        // Carrega os eventos na tabela
        carregarEventos();
    }

    /**
     * Adiciona uma linha de título e valor ao painel de detalhes.
     */
    private JLabel adicionarLabel(String titulo) {
        JLabel lblTitulo = new JLabel(titulo);
        JLabel lblValor = new JLabel("-");
        painelDetalhes.add(lblTitulo);
        painelDetalhes.add(lblValor);
        return lblValor;
    }

    /**
     * Carrega todos os eventos na tabela.
     */
    private void carregarEventos() {
        try {
            List<Evento> eventos = EventoDao.listarTodosEventos();
            DefaultTableModel modeloTabela = (DefaultTableModel) tabelaEventos.getModel();
            for (Evento evento : eventos) {
                modeloTabela.addRow(new Object[]{
                    evento.getId(),
                    evento.getTitulo(),
                    evento.getDataEvento().toString(),
                    evento.getHoraEvento().toString(),
                    evento.getLocal()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carrega os detalhes do evento selecionado na tabela.
     */
    private void carregarDetalhesEvento() {
        int linhaSelecionada = tabelaEventos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um evento para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obter ID do evento selecionado
            int eventoId = (int) tabelaEventos.getValueAt(linhaSelecionada, 0);
            Evento evento = EventoDao.getEventoPorId(eventoId);

            if (evento != null) {
                // Atualiza os labels com os detalhes do evento
                lblTitulo.setText(evento.getTitulo());
                lblDescricao.setText(evento.getDescricao());
                lblLocal.setText(evento.getLocal());
                lblData.setText(evento.getDataEvento().toString());
                lblHora.setText(evento.getHoraEvento().toString());
                
                // Exibir duração corretamente formatada (HH:mm)
                long horas = evento.getDuracaoEvento().toHours();
                long minutos = evento.getDuracaoEvento().toMinutes() % 60;
                lblDuracao.setText(String.format("%02d:%02d", horas, minutos));
                
                lblPreco.setText(String.valueOf(evento.getPreco()));
                lblCapacidadeMaxima.setText(String.valueOf(evento.getCapacidadeMaxima()));
                lblStatus.setText(evento.getStatus().toString());
                lblCategoria.setText(evento.getCategoria().toString());
            } else {
                JOptionPane.showMessageDialog(this, "Evento não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar detalhes do evento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
