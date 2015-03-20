package br.com.ieptbto.cra.conversor.enumeration;

/**
 * 
 * @author Lefer
 *
 */
public enum ErroConversao {
	/** */
	CONVERSAO_BIG_DECIMAL("O valor bigdecimal de origem não é um número válido"),
	/** */
	CONVERSAO_ENUM("Não foi possível converter o valor da ENUM"),
	/** */
	CONVERSAO_DATE("O valor da data não é válido"),
	/** */
	CONVERSAO_INTEGER("O valor de origem não é um número válido"),
	/** */
	CONVERSAO_LIST("Não foi possível converter a lista"),
	/** */
	TIPO_ARQUIVO_DESCONHECIDO("Tipo de arquivo desconhecido");

	private String mensagemErro;

	private ErroConversao(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	/**
	 * @return
	 */
	public String getMensagemErro() {
		return mensagemErro;
	}
}
