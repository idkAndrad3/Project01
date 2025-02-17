package entities;

public class User {
	
	protected Integer id;
	protected String nome;
	protected String email;
	protected String senha;
	protected TipoUsuario tipoUsuario; 
	
	public User(Integer id, String nome, String email, String senha, entities.TipoUsuario tipoUsuario) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.tipoUsuario = tipoUsuario;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public Integer getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getSenha() {
		return senha;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", nome=" + nome + ", email=" + email + ", senha=" + senha + ", TipoUsuario="
				+ tipoUsuario + "]";
	}
	
	
}
