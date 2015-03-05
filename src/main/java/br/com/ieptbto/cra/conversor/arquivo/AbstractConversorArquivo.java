package br.com.ieptbto.cra.conversor.arquivo;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.vo.AbstractRegistro;
import br.com.ieptbto.cra.util.CraConstructorUtils;

/**
 * 
 * @author Lefer
 *
 * @param <R>
 * @param <E>
 */
public abstract class AbstractConversorArquivo<R extends AbstractRegistro, E extends AbstractEntidade<?>> {

	/**
	 * @param entidade
	 * @param arquivoClass
	 * @return
	 */
	protected final R converter(E entidade, Class<R> arquivoClass) {
		BeanWrapper propertyAccessCCR = PropertyAccessorFactory.forBeanPropertyAccess(entidade);
		R arquivo = CraConstructorUtils.newInstance(arquivoClass);
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
}
