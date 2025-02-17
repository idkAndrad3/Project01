package exceptions;

public class UsuarioNaoAdminException extends LoginException{
	private static final long serialVersionUID = 1L;

	public UsuarioNaoAdminException() {
        super("O usuário não é um administrador");
    }
}	
