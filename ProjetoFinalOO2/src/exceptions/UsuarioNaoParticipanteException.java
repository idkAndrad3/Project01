package exceptions;

public class UsuarioNaoParticipanteException extends LoginException{
	private static final long serialVersionUID = 1L;

	public UsuarioNaoParticipanteException() {
        super("O usuário não é um participante");
    }
}	
