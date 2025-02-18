package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import entities.TipoUsuario;
import service.LoginManager;

public class LoginWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtLogin;
    private JTextField txtSenha;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginWindow frame = new LoginWindow();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LoginWindow() {
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 520, 425);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        txtLogin = new JTextField();
        txtLogin.setBounds(205, 106, 150, 25);
        contentPane.add(txtLogin);
        txtLogin.setColumns(10);

        txtSenha = new JTextField();
        txtSenha.setBounds(205, 170, 150, 25);
        contentPane.add(txtSenha);
        txtSenha.setColumns(10);

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setBounds(205, 83, 150, 13);
        contentPane.add(lblLogin);

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setBounds(205, 147, 150, 13);
        contentPane.add(lblSenha);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    LoginManager.inicializarConexao();
                    realizarLogin();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnEntrar.setBounds(205, 215, 150, 30);
        contentPane.add(btnEntrar);

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
					new CadastroUsuarioWindow().setVisible(true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        btnCadastrar.setBounds(205, 260, 150, 30);
        contentPane.add(btnCadastrar);

        JLabel lblTelaLogin = new JLabel("Tela de Login");
        lblTelaLogin.setBounds(205, 22, 150, 13);
        contentPane.add(lblTelaLogin);
    }

    private void realizarLogin() {
        String email = txtLogin.getText();
        String senha = txtSenha.getText();

        try {
            LoginManager.login(email, senha);
            TipoUsuario tipoUsuario = LoginManager.getUsuario().getTipoUsuario();

            if (tipoUsuario == TipoUsuario.admin) {
                JOptionPane.showMessageDialog(this, "Bem-vindo, Administrador!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                PainelAdmin adminPanel = new PainelAdmin();
                adminPanel.setVisible(true);
            } else if (tipoUsuario == TipoUsuario.DEFAULT) {
                JOptionPane.showMessageDialog(this, "Bem-vindo, Usuário!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                PainelUsuario userPanel = new PainelUsuario();
                userPanel.setVisible(true);
            }
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao realizar login: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
