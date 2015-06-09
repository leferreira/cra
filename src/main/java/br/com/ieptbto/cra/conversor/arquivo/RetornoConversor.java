package br.com.ieptbto.cra.conversor.arquivo;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.entidade.Retorno;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.util.CraConstructorUtils;
import br.com.ieptbto.cra.util.DataUtil;

/**
 * 
 * @author Lefer
 *
 */
public class RetornoConversor extends AbstractConversorArquivo<TituloVO, Retorno> {
	/**
	 * Converte um arquivo Entidade em um ArquivoVO
	 * 
	 * @param entidade
	 * @param arquivoVO
	 * @return
	 */
	public TituloVO converter(Retorno entidade, Class<TituloVO> arquivoVO) {

		BeanWrapper propertyAccessCRA = PropertyAccessorFactory.forBeanPropertyAccess(entidade.getTitulo());
		TituloVO tituloVO = CraConstructorUtils.newInstance(arquivoVO);
		BeanWrapper propertyAccessArquivo = PropertyAccessorFactory.forBeanPropertyAccess(tituloVO);
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

		tituloVO.setValorCustaCartorio(new BigDecimalConversor().getValorConvertidoParaString(entidade.getValorCustaCartorio()));
		tituloVO.setTipoOcorrencia(entidade.getTipoOcorrencia());
		tituloVO.setDataOcorrencia(DataUtil.localDateToStringddMMyyyy(entidade.getDataOcorrencia()));
		tituloVO.setCodigoIrregularidade(entidade.getCodigoIrregularidade());
		tituloVO.setCodigoCartorio(entidade.getCodigoCartorio().toString());
		tituloVO.setNumeroProtocoloCartorio(entidade.getNumeroProtocoloCartorio());
		tituloVO.setDataProtocolo(DataUtil.localDateToStringddMMyyyy(entidade.getDataProtocolo()));
		tituloVO.setNumeroSequencialArquivo(entidade.getNumeroSequencialArquivo());
		tituloVO.setDeclaracaoPortador(" ");

		return tituloVO;
	}
}
