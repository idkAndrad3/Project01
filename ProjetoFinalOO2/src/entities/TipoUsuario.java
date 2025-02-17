package entities;

public enum TipoUsuario {
	admin("admin"),
	DEFAULT("participante");
	
	private String SQLValue;
	
	TipoUsuario(String nome){
		this.SQLValue = nome;
	}
	
	public static TipoUsuario getFromString(String tipoString)	 {
	    if (tipoString == null) {
	        return null;
	    }

	    for (TipoUsuario tipo : TipoUsuario.values()) {
	        if (tipo.getSQLValue().equalsIgnoreCase(tipoString)) { // Ignora diferença de maiúsculas/minúsculas
	            return tipo;
	        }
	    }
	    
	    return null;
	}



	
	public String getSQLValue() {
		return SQLValue;
	}
}
