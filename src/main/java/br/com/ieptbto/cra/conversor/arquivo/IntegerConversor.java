package br.com.ieptbto.cra.conversor.arquivo;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
public class IntegerConversor extends AbstractConversor<Integer> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getValorConvertido(String valor) {
		try {
			return Integer.valueOf(valor);
		} catch (NumberFormatException e) {
			throw new ConvertException(ErroConversao.CONVERSAO_INTEGER, e, getFieldName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertidoParaString(Integer objeto) {
		int tamanho = getAnotacaoAtributo().tamanho();
		if (objeto != null) {
			return StringUtils.leftPad(objeto.toString(), tamanho, '0');
		}
		return StringUtils.repeat("0", tamanho);
	}

	@Override
	public Integer getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

}
