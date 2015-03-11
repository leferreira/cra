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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDate getValorConvertido(String valor) {
		String formato = getAnotacaoAtributo().formato();
		try {
			if (StringUtils.isBlank(formato)) {
				return DataUtil.stringToLocalDate(DataUtil.PADRAO_FORMATACAO_DATA_YYYYMMDD, valor);
			}
			return DataUtil.stringToLocalDate(formato, valor);
		} catch (IllegalArgumentException e) {
			throw new ConvertException(ErroConversao.CONVERSAO_DATE, e, getFieldName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValorConvertidoParaString(LocalDate objeto) {
		if (objeto != null) {
			return DataUtil.localDateToStringyyyyMMdd(objeto);
		}
		return StringUtils.repeat(" ", getAnotacaoAtributo().tamanho());
	}

}
