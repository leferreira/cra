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
		String formato = getAnotacaoAtributo().formato();
		try {
			if (StringUtils.isBlank(formato)) {
				return DataUtil.stringToLocalDateTime(DataUtil.PADRAO_FORMATACAO_DATAHORASEG, valor);
			}
			return DataUtil.stringToLocalDateTime(formato, valor);
		} catch (IllegalArgumentException e) {
			throw new ConvertException(ErroConversao.CONVERSAO_DATE, e, getFieldName());
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

}
