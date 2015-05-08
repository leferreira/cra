package br.com.ieptbto.cra.conversor.arquivo;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.exception.ConvertException;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
public class DateTimeConversor extends AbstractConversor<LocalDateTime> {

	@Override
	public LocalDateTime getValorConvertido(String valor) throws ConvertException {
		try {
			return DataUtil.stringToLocalDateTime(DataUtil.PADRAO_FORMATACAO_DATAHORASEG, valor);
		} catch (IllegalArgumentException e) {
			if (getAnotacaoAtributo().obrigatoriedade()) {
				throw new ConvertException(ErroConversao.CONVERSAO_DATE, e, getFieldName());
			}
			return null;
		}
	}

	@Override
	public LocalDateTime getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	@Override
	public String getValorConvertidoParaString(LocalDateTime objeto) {
		if (objeto != null) {
			return DataUtil.localDateTimeToString(objeto);
		}
		return StringUtils.repeat(" ", getAnotacaoAtributo().tamanho());
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(LocalDateTime.class.cast(objeto));
	}

}
