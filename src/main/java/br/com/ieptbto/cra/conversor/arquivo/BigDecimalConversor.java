package br.com.ieptbto.cra.conversor.arquivo;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
public class BigDecimalConversor extends AbstractConversor<BigDecimal> {
	private static final int TAMANHO_PADRAO_CAMPO_VALOR = 10;
	private static final String VALOR_ZERO = "0";
	private static final BigDecimal VALOR_CEM = BigDecimal.valueOf(100);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getValorConvertido(String valor) {
		try {
			BigDecimal retorno = new BigDecimal(StringUtils.trim(valor));
			if (BigDecimal.ZERO.equals(retorno)) {
				return BigDecimal.ZERO;
			}
			return retorno.divide(VALOR_CEM);
		} catch (NumberFormatException e) {
			throw new ConvertException(ErroConversao.CONVERSAO_BIG_DECIMAL, e, getFieldName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValorConvertidoParaString(BigDecimal objeto) {
		int tamanho = TAMANHO_PADRAO_CAMPO_VALOR;
		if (getCampoArquivo() != null) {
			tamanho = getAnotacaoAtributo().tamanho();
		}
		if (objeto != null) {
			BigDecimal retorno = objeto.multiply(VALOR_CEM);
			String numero = retorno.toPlainString().replace(".00", StringUtils.EMPTY);
			return StringUtils.leftPad(numero, tamanho, VALOR_ZERO);
		}
		return StringUtils.repeat(VALOR_ZERO, tamanho);
	}

	@Override
	public BigDecimal getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(BigDecimal.class.cast(objeto));
	}

}
