package br.com.ieptbto.cra.conversor.arquivo;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.util.CraConstructorUtils;

/**
 * 
 * @author Lefer
 *
 * @param <R>
 * @param <E>
 */
public abstract class AbstractConversorArquivo<R extends AbstractArquivoVO, E extends AbstractEntidade<?>> {

	/**
	 * Converte um arquivo Entidade em um ArquivoVO
	 * 
	 * @param entidade
	 * @param arquivoVO
	 * @return
	 */
	protected final R converter(E entidade, Class<R> arquivoVO) {
		BeanWrapper propertyAccessCCR = PropertyAccessorFactory.forBeanPropertyAccess(entidade);
		R arquivo = CraConstructorUtils.newInstance(arquivoVO);
		BeanWrapper propertyAccessArquivo = PropertyAccessorFactory.forBeanPropertyAccess(arquivo);
		PropertyDescriptor[] propertyDescriptors = propertyAccessArquivo.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String propertyName = propertyDescriptor.getName();
			if (propertyAccessCCR.isReadableProperty(propertyName) && propertyAccessArquivo.isWritableProperty(propertyName)) {
				Object valor = propertyAccessCCR.getPropertyValue(propertyName);
				propertyAccessArquivo.setPropertyValue(propertyName, valor);
			}

		}
		return arquivo;
	}

	/**
	 * 
	 * @param entidade
	 * @param entidadeVO
	 * @return
	 */
	protected E converter(Class<E> entidade, R entidadeVO) {
		E arquivo = CraConstructorUtils.newInstance(entidade);
		BeanWrapper propertyAccessEntidadeVO = PropertyAccessorFactory.forBeanPropertyAccess(entidadeVO);
		BeanWrapper propertyAccessEntidade = PropertyAccessorFactory.forBeanPropertyAccess(arquivo);

		PropertyDescriptor[] propertyDescriptorsVO = propertyAccessEntidadeVO.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptorVO : propertyDescriptorsVO) {
			String propertyName = propertyDescriptorVO.getName();
			if (propertyAccessEntidadeVO.isReadableProperty(propertyName) && propertyAccessEntidade.isWritableProperty(propertyName)) {
				String valor = String.class.cast(propertyAccessEntidadeVO.getPropertyValue(propertyName));
				Class<?> propertyType = propertyAccessEntidade.getPropertyType(propertyName);
				propertyAccessEntidade.setPropertyValue(propertyName, getValorTipado(valor, propertyType));
			}
		}

		return arquivo;
	}

	private Object getValorTipado(String valor, Class<?> propertyType) {
		return FabricaConversor.getValorConvertido(propertyType, valor);
	}
}
