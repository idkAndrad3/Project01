package entities;

import java.sql.Date;

public class Participante extends User{
	
	private Date dataNascimento;
	private String CPF;
	private String statusInscricao;
	private boolean presencaConfirmada;
	
	public Participante(Integer id, String nome, String email, String senha, Date dataNascimento, String CPF) {
	    super(id, nome, email, senha, TipoUsuario.DEFAULT);
	    this.dataNascimento = dataNascimento;

	    if (!validarCPF(CPF)) {
	        throw new IllegalArgumentException("CPF INVÁLIDO");
	    }

	    this.CPF = CPF;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public String getCPF() {
		return CPF;
	}
	
	
	private boolean validarCPF(String cpf) {
	    // Remove caracteres não numéricos
	    cpf = cpf.replaceAll("[^0-9]", "");

	    // Verifica se tem 11 dígitos
	    if (cpf.length() != 11) {
	        return false;
	    }

	    // Aqui você pode adicionar mais regras de validação (ex: cálculo do dígito verificador)

	    return true;
	}

	public String getStatusInscricao() {
	    return statusInscricao;
	}

	public void setStatusInscricao(String statusInscricao) {
	    this.statusInscricao = statusInscricao;
	}

	public boolean isPresencaConfirmada() {
	    return presencaConfirmada;
	}

	public void setPresencaConfirmada(boolean presencaConfirmada) {
	    this.presencaConfirmada = presencaConfirmada;
	}
}
