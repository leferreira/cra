package br.com.ieptbto.cra.exception;

/**
 * 
 * @author Lefer
 *
 */
public enum Erro {

	/** */
	ARQUIVO_NAO_POSSUI_CABECALHO("0001", "Arquivo não possuí cabeçalho.");

	private String codigoErro;
	private String mensagem;

	private Erro(String codigoErro, String mensagem) {
		this.codigoErro = codigoErro;
		this.mensagem = mensagem;
	}

	/**
	 * Retorna o codigo de erro
	 * 
	 * @return
	 */
	public String getCodigoErro() {
		return codigoErro;
	}

	/**
	 * Retorna a mensagem de erro
	 * 
	 * @return
	 */
	public String getMensagemErro() {
		return mensagem;
	}
}
