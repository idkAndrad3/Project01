package exceptions;

public class UsuarioNaoLogadoException extends LoginException {
	private static final long serialVersionUID = 1L;

	public UsuarioNaoLogadoException() {
        super("Usuário não está logado.");
    }
}
