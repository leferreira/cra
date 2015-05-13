package br.com.ieptbto.cra.conversor.arquivo;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.enumeration.CraEnum;
import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
public class EnumConversor extends AbstractConversor<CraEnum> {

	private static final String VAZIO = " ";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CraEnum getValorConvertido(String valor) {
		BeanWrapper propertyAccess = PropertyAccessorFactory.forBeanPropertyAccess(getArquivo());
		Class<?> propertyType = propertyAccess.getPropertyType(getFieldName());

		return getValorConvertido(valor, propertyType);

	}

	/**
	 * 
	 * @param valor
	 * @param propertyType
	 * @return
	 */
	@Override
	public CraEnum getValorConvertido(String valor, Class<?> propertyType) {
		try {
			if (CraEnum.class.isAssignableFrom(propertyType)) {
				Object[] enumConstants = propertyType.getEnumConstants();
				for (Object object : enumConstants) {
					CraEnum enumObject = (CraEnum) object;
					if (enumObject.getConstante().equals(valor)) {
						return enumObject;
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new ConvertException(ErroConversao.CONVERSAO_ENUM, e, getFieldName());
		}
		throw new ConvertException(ErroConversao.CONVERSAO_ENUM, getFieldName());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValorConvertidoParaString(CraEnum objeto) {
		int tamanho = getAnotacaoAtributo().tamanho();
		if (objeto != null) {
			String codigo = objeto.getConstante();
			return StringUtils.leftPad(codigo, tamanho, VAZIO);
		}
		return StringUtils.repeat(VAZIO, tamanho);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(CraEnum.class.cast(objeto));
	}
}
