package br.com.ieptbto.cra.exception;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;

/**
 * 
 * @author Lefer
 *
 */
public class ConvertException extends RuntimeException {
	/****/
	private static final long serialVersionUID = 18562L;
	private final ErroConversao erro;
	private final String campo;

	/**
	 * Cria uma {@link ConvertException} com um {@link ErroConversao} associado.
	 * 
	 * @param erro
	 * @param campo
	 */
	public ConvertException(ErroConversao erro, String campo) {
		super(erro.getMensagemErro());
		this.erro = erro;
		this.campo = campo;
	}

	/**
	 * Cria uma {@link ConvertException} com um {@link ErroConversao} associado.
	 * 
	 * @param erroAssociado
	 * @param e
	 *            exceção causadora
	 * @param campo
	 */
	public ConvertException(ErroConversao erroAssociado, Throwable e, String campo) {
		super(erroAssociado.getMensagemErro(), e);
		this.erro = erroAssociado;
		this.campo = campo;
	}

	/**
	 * Método responsável por retornar erro .
	 * 
	 * @return erro
	 */
	public ErroConversao getErro() {
		return erro;
	}

	/**
	 * @return
	 */
	public String getCampo() {
		return campo;
	}
}
