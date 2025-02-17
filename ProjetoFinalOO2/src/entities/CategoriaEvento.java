package entities;

public enum CategoriaEvento {
	WORKSHOP("workshop"),
	PALESTRA("palestra"),
	CONFERENCIA("conferencia");
	
	private String sqlName;
	
	CategoriaEvento(String nome) {
		this.sqlName = nome;
	}
	
	public String getSQL() {
		return this.sqlName;
	}
	
	public static CategoriaEvento getFromString(String tipoString) {
		for (CategoriaEvento tipo : CategoriaEvento.values()) 
			if (tipo.getSQL().equals(tipoString))
				return tipo;
		
		return null;
	}
	
}
