package br.com.ieptbto.cra.processador;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import br.com.ieptbto.cra.conversor.arquivo.AbstractConversor;
import br.com.ieptbto.cra.conversor.arquivo.CampoArquivo;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;

public class GerenciadorArquivo extends AbstractGerenciadorArquivoLinha {

	private BeanWrapper propertyAccess;

	/**
	 * Cria um registro a partir de uma linha.
	 * 
	 * @param linha
	 * @param registro
	 */
	public GerenciadorArquivo(String linha, AbstractArquivoVO arquivo) {
		this.arquivo = arquivo;
		this.propertyAccess = PropertyAccessorFactory.forBeanPropertyAccess(arquivo);

		List<CampoArquivo> camposSequenciais = getAnnotatedFields();
		for (CampoArquivo campoCorrente : camposSequenciais) {
			Integer posicaoInicial = campoCorrente.getPosicaoInicio() - 1;
			Integer posicaoFinal = posicaoInicial + campoCorrente.getTamanho();

			String valor = StringUtils.substring(linha, posicaoInicial, posicaoFinal);

			boolean isString = String.class.isAssignableFrom(campoCorrente.getType());
			if (StringUtils.isNotBlank(valor) || isString) {
				converter(campoCorrente, valor);
			}

		}
	}

	private void converter(CampoArquivo campoArquivo, String valor) {
		AbstractConversor<?> conversor = getConversor(campoArquivo);
		Field campo = campoArquivo.getField();
		Object valorConvertido = conversor.getValorConvertido(valor);
		propertyAccess.setPropertyValue(campo.getName(), valorConvertido);

	}

}
