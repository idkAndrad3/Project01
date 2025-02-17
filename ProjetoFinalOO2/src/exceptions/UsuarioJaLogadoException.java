package exceptions;

public class UsuarioJaLogadoException extends LoginException {
	private static final long serialVersionUID = 1L;

	public UsuarioJaLogadoException() {
        super("Usuário já está logado.");
    }
}