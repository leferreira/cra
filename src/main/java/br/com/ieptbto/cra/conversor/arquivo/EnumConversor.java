package br.com.ieptbto.cra.conversor.arquivo;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.conversor.enumeration.ErroConversao;
import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
@SuppressWarnings("rawtypes")
public class EnumConversor extends AbstractConversor<Enum> {

	private static final String VAZIO = " ";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enum getValorConvertido(String valor) {
		try {
			BeanWrapper propertyAccess = PropertyAccessorFactory.forBeanPropertyAccess(getArquivo());

			Class<?> propertyType = propertyAccess.getPropertyType(getFieldName());
			Integer integerValue = Integer.valueOf(valor);
			if (Enum.class.isAssignableFrom(propertyType)) {
				Object[] enumConstants = propertyType.getEnumConstants();
				for (Object object : enumConstants) {
					Enum enumObject = (Enum) object;
					if (enumObject.name().equals(integerValue)) {
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
	public String getValorConvertidoParaString(Enum objeto) {
		int tamanho = getAnotacaoAtributo().tamanho();
		if (objeto != null) {
			String codigo = objeto.name();
			return StringUtils.leftPad(codigo, tamanho, VAZIO);
		}
		return StringUtils.repeat(VAZIO, tamanho);
	}

}
