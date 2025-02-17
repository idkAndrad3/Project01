package entities;

public enum StatusEvento {
		
		FECHADO("fechado"),
		ABERTO("aberto"),
		ENCERRADO("encerrado"),
		CANCELADO("cancelado"),
		ANDAMENTO("andamento");
		
		
		private String sqlName;
		
		StatusEvento(String nome){
			this.sqlName = nome;
		}
		
		public String getSQL() {
			return this.sqlName;
		}
		
		public static StatusEvento getFromString(String tipoString) {
			for (StatusEvento tipo : StatusEvento.values()) 
				if (tipo.getSQL().equals(tipoString))
					return tipo;
			
			return null;
		}
		
	}
