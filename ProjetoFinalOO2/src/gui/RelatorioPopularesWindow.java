	package gui;
	
	import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.EventoDao;
import entities.Evento;
	
	public class RelatorioPopularesWindow extends JFrame {
	
	    private static final long serialVersionUID = 1L;
	    private JTable tabelaPopulares;
	
	    public RelatorioPopularesWindow(Connection conexao) {
	        setTitle("Eventos Mais Populares");
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        setBounds(100, 100, 600, 400);
	        JPanel contentPane = new JPanel();
	        setContentPane(contentPane);
	        contentPane.setLayout(null);
	
	        JLabel lblTitulo = new JLabel("Eventos Mais Populares");
	        lblTitulo.setBounds(200, 10, 200, 15);
	        contentPane.add(lblTitulo);
	
	        tabelaPopulares = new JTable(new DefaultTableModel(new String[]{"ID", "Título", "Inscrições"}, 0));
	        JScrollPane scrollPane = new JScrollPane(tabelaPopulares);
	        scrollPane.setBounds(10, 50, 570, 300);
	        contentPane.add(scrollPane);
	        
	 
	
	        JButton btnAtualizar = new JButton("Atualizar");
	        btnAtualizar.setBounds(250, 360, 100, 25);
	        btnAtualizar.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                carregarPopulares(conexao);
	            }
	        });
	        contentPane.add(btnAtualizar);
	        
	        JButton btnExportar = new JButton("Exportar");
	        btnExportar.setBounds(377, 7, 85, 21);
	        
	        contentPane.add(btnExportar);
	
	        carregarPopulares(conexao);
	    }
	
	    private void carregarPopulares(Connection conexao) {
	        try {
	            DefaultTableModel model = (DefaultTableModel) tabelaPopulares.getModel();
	            model.setRowCount(0); // Limpa a tabela
	
	            EventoDao.inicializarConexao(conexao); // Garante a conexão com o DAO
	            var populares = EventoDao.listarEventosPopulares();
	            for (var evento : populares) {
	                model.addRow(new Object[]{evento.getId(), evento.getTitulo(), evento.getQuantidadeInscricoes()});
	            }
	        } catch (Exception e) {
	            JOptionPane.showMessageDialog(this, "Erro ao carregar eventos populares: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    

	}
