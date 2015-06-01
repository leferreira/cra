package br.com.ieptbto.cra.conversor.arquivo;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.exception.ConvertException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
public class DateConversor extends AbstractConversor<LocalDate> {

	private static final String ZEROS = "00000000";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDate getValorConvertido(String valor) {
		try {
			if (StringUtils.isBlank(valor) || valor.equals(ZEROS)) {
				return null;
			}
			return DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_DDMMYYYY, valor);
		} catch (IllegalArgumentException e) {
			if (getAnotacaoAtributo().obrigatoriedade()) {
				throw new ConvertException(ErroConversao.CONVERSAO_DATE, e, getFieldName());
			}
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValorConvertidoParaString(LocalDate objeto) {
		if (objeto != null) {
			return DataUtil.localDateToStringddMMyyyy(objeto);
		}
		return StringUtils.repeat(" ", getAnotacaoAtributo().tamanho());
	}

	@Override
	public LocalDate getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(LocalDate.class.cast(objeto));
	}

}
