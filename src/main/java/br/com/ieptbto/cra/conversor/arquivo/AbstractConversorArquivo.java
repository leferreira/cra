package br.com.ieptbto.cra.conversor.arquivo;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.entidade.AbstractEntidade;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.enumeration.PosicaoCampoVazio;
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
	public R converter(E entidade, Class<R> arquivoVO) {
		BeanWrapper propertyAccessCRA = PropertyAccessorFactory.forBeanPropertyAccess(entidade);
		R arquivo = CraConstructorUtils.newInstance(arquivoVO);
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

	/**
	 * 
	 * @param entidade
	 * @param entidadeVO
	 * @return
	 */
	public E converter(Class<E> entidade, R entidadeVO) {
		E arquivo = CraConstructorUtils.newInstance(entidade);
		BeanWrapper propertyAccessEntidadeVO = PropertyAccessorFactory.forBeanPropertyAccess(entidadeVO);
		BeanWrapper propertyAccessEntidade = PropertyAccessorFactory.forBeanPropertyAccess(arquivo);

		PropertyDescriptor[] propertyDescriptorsVO = propertyAccessEntidadeVO.getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptorVO : propertyDescriptorsVO) {
			String propertyName = propertyDescriptorVO.getName();
			if (propertyAccessEntidadeVO.isReadableProperty(propertyName) && propertyAccessEntidade.isWritableProperty(propertyName)) {
				String valor = String.class.cast(propertyAccessEntidadeVO.getPropertyValue(propertyName));
				Class<?> propertyType = propertyAccessEntidade.getPropertyType(propertyName);
				propertyAccessEntidade.setPropertyValue(propertyName,
				        getValorTipado(valor, propertyType, entidadeVO, new CampoArquivo(propertyName, entidadeVO.getClass())));
			}
		}

		return arquivo;
	}

	private Object getValorTipado(String valor, Class<?> propertyType, R entidadeVO, CampoArquivo campoArquivo) {
		return FabricaConversor.getValorConvertido(propertyType, valor, entidadeVO, campoArquivo);
	}

	public String criarLinhaArquivo(R entidadeVO) {
		Map<Integer, String> valores = new HashMap<Integer, String>();
		BeanWrapper propsEntidadeVO = PropertyAccessorFactory.forBeanPropertyAccess(entidadeVO);
		PropertyDescriptor[] propsDescritores = propsEntidadeVO.getPropertyDescriptors();
		for (PropertyDescriptor descritor : propsDescritores) {
			String nomePropriedade = descritor.getName();
			if (propsEntidadeVO.isReadableProperty(nomePropriedade) && propsEntidadeVO.isWritableProperty(nomePropriedade)) {
				CampoArquivo anotacoes = new CampoArquivo(nomePropriedade, entidadeVO.getClass());
				String valor = prepararValor(String.valueOf(propsEntidadeVO.getPropertyValue(nomePropriedade)), anotacoes);
				valores.put(anotacoes.getOrdem(), valor);
			}
		}

		return montarLinha(valores);
	}

	private String montarLinha(Map<Integer, String> valores) {
		String linha = "";
		for (int i = 1; i <= valores.size(); i++) {
			linha += valores.get(i);
		}

		return linha;
	}

	private String prepararValor(String valor, CampoArquivo anotacoes) {
		if (valor == null) {
			valor = "";
		}
		if (anotacoes.getPosicaoCampoVazio() == PosicaoCampoVazio.ESQUERDO) {
			return StringUtils.leftPad(valor, anotacoes.getTamanho(), anotacoes.getFormato());
		}
		return StringUtils.rightPad(valor, anotacoes.getTamanho(), anotacoes.getFormato());
	}

	protected String converterValor(Object propriedade, CampoArquivo campo) {
		if (propriedade == null) {
			return "";
		}
		return FabricaConversor.getValorConvertidoParaString(campo, propriedade.getClass(), propriedade).trim();
	}

	/**
	 * @return os campos anotados do registro.
	 */
	protected List<CampoArquivo> getAnnotatedFields(R arquivo) {
		return AtributoArquivoUtil.getAnnotatedFields(arquivo);
	}
}
