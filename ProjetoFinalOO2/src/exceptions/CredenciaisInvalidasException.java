package exceptions;

public class CredenciaisInvalidasException extends LoginException {
	private static final long serialVersionUID = 1L;

	public CredenciaisInvalidasException() {
        super("Senha ou Email incorretos.");
    }
}