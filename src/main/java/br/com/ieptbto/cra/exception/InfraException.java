package br.com.ieptbto.cra.exception;

/**
 * 
 * @author Lefer
 * 
 *         Exceção de Infra do sistema. <br>
 *         São exceções inesperadas, que geralmente não possuem tratamento
 *         específico dentro do sistema. <br>
 *         Quando exceção deste tipo é lançada, é registrado o log no servidor.
 *
 */
public class InfraException extends RuntimeException {

	/****/
	private static final long serialVersionUID = 1L;

	/**
	 * Cria uma nova exceção.
	 * 
	 * @param message
	 *            mensagem
	 * @param cause
	 *            exceção raiz
	 */
	public InfraException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Cria uma nova exceção.
	 * 
	 * @param message
	 *            mensagem
	 */
	public InfraException(String message) {
		super(message);
	}
}
