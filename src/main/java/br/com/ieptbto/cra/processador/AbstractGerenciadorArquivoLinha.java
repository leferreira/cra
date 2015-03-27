package br.com.ieptbto.cra.processador;

import java.util.List;

import br.com.ieptbto.cra.conversor.arquivo.AbstractConversor;
import br.com.ieptbto.cra.conversor.arquivo.AtributoArquivoUtil;
import br.com.ieptbto.cra.conversor.arquivo.CampoArquivo;
import br.com.ieptbto.cra.conversor.arquivo.FabricaConversor;
import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;

/**
 * 
 * @author Lefer
 *
 */
public abstract class AbstractGerenciadorArquivoLinha {

	protected AbstractArquivoVO arquivo;

	/**
	 * @return os campos anotados do registro.
	 */
	protected List<CampoArquivo> getAnnotatedFields() {
		return AtributoArquivoUtil.getAnnotatedFields(arquivo);
	}

	/**
	 * Retorna o conversor pelo tipo do campo.
	 * 
	 * @param campoRegistro
	 * @return
	 */
	protected AbstractConversor<?> getConversor(CampoArquivo campoRegistro) {
		return FabricaConversor.getConversor(campoRegistro, arquivo);
	}

}
