package br.com.ieptbto.cra.exception;

public class ArquivoException extends RuntimeException {

	/** **/
	private static final long serialVersionUID = 1L;
	private final ErroArquivo erro;
	private final String campo;

	/**
	 * Construtor.
	 * 
	 * @param erro
	 * @param campo
	 */
	public ArquivoException(ErroArquivo erro, String campo) {
		super(erro.getMensagemErro());
		this.erro = erro;
		this.campo = campo;
	}

	/**
	 * Método responsável por retornar erro.
	 * 
	 * @return erro
	 */
	public ErroArquivo getErro() {
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
