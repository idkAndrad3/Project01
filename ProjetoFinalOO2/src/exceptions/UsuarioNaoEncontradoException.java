package exceptions;

public class UsuarioNaoEncontradoException extends LoginException {
	private static final long serialVersionUID = 1L;

	public UsuarioNaoEncontradoException() {
        super("Não existe nenhum usuário com este email.");
    }
}