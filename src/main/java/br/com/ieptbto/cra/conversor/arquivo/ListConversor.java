package br.com.ieptbto.cra.conversor.arquivo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.exception.ConvertException;

/**
 * 
 * @author Lefer
 *
 */
public class ListConversor extends AbstractConversor<List<?>> {

	private static final String VAZIO = " ";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<?> getValorConvertido(String valor) {
		ArrayList<Object> listaRetorno = new ArrayList<Object>();
		int posicaoInicial = 0;
		int tamanho = getAnotacaoAtributo().tamanho();
		int posicaoFinal = tamanho;
		while (StringUtils.isNotBlank(StringUtils.substring(valor, posicaoInicial, posicaoFinal))) {
			Class<?> listType = getAnotacaoAtributo().tipo();
			Object valorConvertido = FabricaConversor.getValorConvertido(listType,
			        StringUtils.substring(valor, posicaoInicial, posicaoFinal), null, null);
			if (valorConvertido instanceof String) {
				valorConvertido = StringUtils.trim((String) valorConvertido);
			}
			listaRetorno.add(valorConvertido);
			posicaoInicial = posicaoFinal;
			posicaoFinal = posicaoInicial + tamanho;
		}

		return listaRetorno;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String getValorConvertidoParaString(List<?> objeto) {
		int tamanho = getAnotacaoAtributo().tamanho();
		int quantidadeItens = getAnotacaoAtributo().quantidadeItens();
		if (CollectionUtils.isNotEmpty(objeto)) {
			Class<?> listType = getAnotacaoAtributo().tipo();
			String valores = StringUtils.EMPTY;

			for (Object object : objeto) {
				AbstractConversor<Object> conversor = (AbstractConversor<Object>) FabricaConversor.getConversor(getCampoArquivo(),
				        getArquivo(), listType);
				valores = valores.concat(conversor.getValorConvertidoParaString(object));
			}

			return valores.concat(StringUtils.repeat(VAZIO, tamanho * (quantidadeItens - objeto.size())));
		}
		return StringUtils.repeat(VAZIO, tamanho * quantidadeItens);
	}

	@Override
	public List<?> getValorConvertido(String valor, Class<?> propertyType) throws ConvertException {
		return getValorConvertido(valor);
	}

	public String getValorConvertidoString(Object objeto) {
		return getValorConvertidoParaString(List.class.cast(objeto));
	}

}
