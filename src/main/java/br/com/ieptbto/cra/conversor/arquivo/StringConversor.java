package br.com.ieptbto.cra.conversor.arquivo;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
public class StringConversor extends AbstractConversor<String> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertido(String valor) {
		return valor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertidoParaString(String objeto) {
		int tamanho = getAnotacaoAtributo().tamanho();
		if (objeto != null) {
			return StringUtils.rightPad(objeto, tamanho, ' ');
		}
		return StringUtils.repeat(" ", tamanho);
	}

	@Override
	public String getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(String.class.cast(objeto));
	}

}
