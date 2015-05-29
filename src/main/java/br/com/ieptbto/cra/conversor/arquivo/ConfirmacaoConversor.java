package br.com.ieptbto.cra.conversor.arquivo;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.entidade.Confirmacao;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.util.CraConstructorUtils;

/**
 * 
 * @author Lefer
 *
 */
public class ConfirmacaoConversor extends AbstractConversorArquivo<TituloVO, Confirmacao> {

	/**
	 * Converte um arquivo Entidade em um ArquivoVO
	 * 
	 * @param entidade
	 * @param arquivoVO
	 * @return
	 */
	public TituloVO converter(Confirmacao entidade, Class<TituloVO> arquivoVO) {
		BeanWrapper propertyAccessCRA = PropertyAccessorFactory.forBeanPropertyAccess(entidade);
		TituloVO arquivo = CraConstructorUtils.newInstance(arquivoVO);
		BeanWrapper propertyAccessArquivo = PropertyAccessorFactory.forBeanPropertyAccess(arquivo);
		PropertyDescriptor[] propertyDescriptors = propertyAccessArquivo.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			String propertyName = propertyDescriptor.getName();
			if (propertyAccessCRA.isReadableProperty(propertyName) && propertyAccessArquivo.isWritableProperty(propertyName)) {
				Object valor = propertyAccessCRA.getPropertyValue(propertyName);
				if (String.class.isInstance(valor)) {
					propertyAccessArquivo.setPropertyValue(propertyName, valor);
				} else {
					propertyAccessArquivo.setPropertyValue(propertyName, converterValor(valor, new CampoArquivo(propertyName, arquivoVO)));
				}
			}

		}
		return arquivo;
	}

}
