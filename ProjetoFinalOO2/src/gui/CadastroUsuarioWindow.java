package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import dao.BancoDados;
import dao.UserDao;
import entities.Administrador;
import entities.Participante;
import entities.TipoUsuario;
import entities.User;

public class CadastroUsuarioWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JComboBox<TipoUsuario> comboTipo;
    private JTextField txtCargo;
    private JFormattedTextField txtDataContratacao;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtCpf;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CadastroUsuarioWindow frame = new CadastroUsuarioWindow();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CadastroUsuarioWindow() throws IOException {
        setTitle("Cadastro de Usuário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            dateFormatter.setPlaceholderCharacter('_');
            try {
                Connection conexao = BancoDados.getConexao();
                UserDao.inicializarConexao(conexao);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao conectar com o banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JLabel lblNome = new JLabel("Nome Completo:");
            lblNome.setBounds(30, 30, 120, 25);
            contentPane.add(lblNome);

            txtNome = new JTextField();
            txtNome.setBounds(160, 30, 180, 25);
            contentPane.add(txtNome);
            txtNome.setColumns(10);

            JLabel lblEmail = new JLabel("Email:");
            lblEmail.setBounds(30, 70, 120, 25);
            contentPane.add(lblEmail);

            txtEmail = new JTextField();
            txtEmail.setBounds(160, 70, 180, 25);
            contentPane.add(txtEmail);
            txtEmail.setColumns(10);

            JLabel lblSenha = new JLabel("Senha:");
            lblSenha.setBounds(30, 110, 120, 25);
            contentPane.add(lblSenha);

            txtSenha = new JPasswordField();
            txtSenha.setBounds(160, 110, 180, 25);
            contentPane.add(txtSenha);

            JLabel lblTipo = new JLabel("Tipo de Usuário:");
            lblTipo.setBounds(30, 150, 120, 25);
            contentPane.add(lblTipo);

            comboTipo = new JComboBox<>(TipoUsuario.values());
            comboTipo.setBounds(160, 150, 180, 25);
            contentPane.add(comboTipo);
            comboTipo.addActionListener(e -> atualizarCampos());

            JLabel lblCargo = new JLabel("Cargo:");
            lblCargo.setBounds(30, 190, 120, 25);
            contentPane.add(lblCargo);

            txtCargo = new JTextField();
            txtCargo.setBounds(160, 190, 180, 25);
            contentPane.add(txtCargo);

            JLabel lblDataContratacao = new JLabel("Data de Contratação:");
            lblDataContratacao.setBounds(30, 230, 150, 25);
            contentPane.add(lblDataContratacao);

            txtDataContratacao = new JFormattedTextField(dateFormatter);
            txtDataContratacao.setBounds(180, 230, 160, 25);
            contentPane.add(txtDataContratacao);

            JLabel lblDataNascimento = new JLabel("Data de Nascimento:");
            lblDataNascimento.setBounds(30, 270, 150, 25);
            contentPane.add(lblDataNascimento);

            txtDataNascimento = new JFormattedTextField(dateFormatter);
            txtDataNascimento.setBounds(180, 270, 160, 25);
            contentPane.add(txtDataNascimento);

            JLabel lblCpf = new JLabel("CPF:");
            lblCpf.setBounds(30, 310, 120, 25);
            contentPane.add(lblCpf);

            txtCpf = new JFormattedTextField(cpfFormatter);
            txtCpf.setBounds(160, 310, 180, 25);
            contentPane.add(txtCpf);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(130, 350, 120, 30);
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });
        contentPane.add(btnCadastrar);
        atualizarCampos();
    }

    private void cadastrarUsuario() {
        try {
            User novoUsuario;
            if (comboTipo.getSelectedItem() == TipoUsuario.admin) {
                novoUsuario = new Administrador(0, txtNome.getText(), txtEmail.getText(), new String(txtSenha.getPassword()), txtCargo.getText(), Date.valueOf(txtDataContratacao.getText()));
            } else {
                novoUsuario = new Participante(0, txtNome.getText(), txtEmail.getText(), new String(txtSenha.getPassword()), Date.valueOf(txtDataNascimento.getText()), txtCpf.getText());
            }
            boolean sucesso = UserDao.cadastrar(novoUsuario);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro no banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido! Use YYYY-MM-DD", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void atualizarCampos() {
        boolean isAdmin = comboTipo.getSelectedItem() == TipoUsuario.admin;
        txtCargo.setEnabled(isAdmin);
        txtDataContratacao.setEnabled(isAdmin);
        txtDataNascimento.setEnabled(!isAdmin);
        txtCpf.setEnabled(!isAdmin);
    }

}
