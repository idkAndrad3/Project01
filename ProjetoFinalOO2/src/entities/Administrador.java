package entities;

import java.sql.Date;

public class Administrador extends User{
	private String cargo;
	private Date dataContratacao;
	
	public Administrador(Integer id, String nome, String email, String senha, String cargo, Date dataContratacao) {
		super(id, nome, email, senha, TipoUsuario.admin);
		
		this.cargo = cargo;
		this.dataContratacao = dataContratacao;
		
	}





	public String getCargo() {
		return cargo;
	}

	public Date getDataContratacao() {
		return dataContratacao;
	}

}
