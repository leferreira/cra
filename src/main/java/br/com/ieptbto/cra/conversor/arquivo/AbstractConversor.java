package br.com.ieptbto.cra.conversor.arquivo;

import br.com.ieptbto.cra.annotations.IAtributoArquivo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivo;
import br.com.ieptbto.cra.exception.ConvertException;

/**
 * Classe base para conversão dos valores para as propriedades anotadas.
 * 
 * @param <T>
 * @author Lefer
 *
 */
public abstract class AbstractConversor<T> {

	/**
	 * Instância do arquivo que será trabalhada.
	 */
	private AbstractArquivo arquivo;

	private CampoArquivo campoArquivo;

	/**
	 * Realiza a conversão do valor para a propriedade.
	 * 
	 * @param valor
	 * 
	 * @return
	 * @throws ConvertException
	 */
	public abstract T getValorConvertido(String valor) throws ConvertException;

	/**
	 * Converte o objeto para {@link String}.
	 * 
	 * @param objeto
	 * @return
	 */
	public abstract String getValorConvertidoParaString(T objeto);

	/**
	 * Método responsável por definir o campo arquivo.
	 * 
	 * @param arquivo
	 *            valor atribuído a arquivo
	 */

	public void setArquivo(AbstractArquivo arquivo) {
		this.arquivo = arquivo;
	}

	/**
	 * Método responsável por retornar arquivo .
	 * 
	 * @return arquivo
	 */
	protected AbstractArquivo getArquivo() {
		return arquivo;
	}

	/**
	 * Método responsável por retornar fieldName .
	 * 
	 * @return fieldName
	 */
	protected String getFieldName() {
		return campoArquivo.getName();
	}

	/**
	 * Método responsável por retornar campoArquivo .
	 * 
	 * @return campoArquivo
	 */
	public CampoArquivo getCampoArquivo() {
		return campoArquivo;
	}

	/**
	 * Método responsável por definir o campo campoArquivo.
	 * 
	 * @param campoArquivo
	 *            valor atribuído a campoArquivo
	 */

	public void setCampoArquivo(CampoArquivo CampoArquivo) {
		this.campoArquivo = CampoArquivo;
	}

	/**
	 * Retorna a anotação pertinente.
	 * 
	 * @return
	 */
	public IAtributoArquivo getAnotacaoAtributo() {
		return campoArquivo.getAnotacaoAtributo();
	}

}