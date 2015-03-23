package br.com.ieptbto.cra.exception;

public class ArquivoException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;
	private final Erro erro;
	private final String campo;

	/**
	 * Construtor.
	 * 
	 * @param erro
	 * @param campo
	 */
	public ArquivoException(Erro erro, String campo) {
		super(erro.getMensagemErro());
		this.erro = erro;
		this.campo = campo;
	}

	/**
	 * Método responsável por retornar erro.
	 * 
	 * @return erro
	 */
	public Erro getErro() {
		return erro;
	}

	/**
	 * Método responsável por retornar campo.
	 * 
	 * @return campo
	 */
	public String getCampo() {
		return campo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return erro + " " + campo;
	}
}
