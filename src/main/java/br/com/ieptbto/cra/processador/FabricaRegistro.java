package br.com.ieptbto.cra.processador;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import br.com.ieptbto.cra.entidade.vo.AbstractArquivoVO;
import br.com.ieptbto.cra.entidade.vo.CabecalhoVO;
import br.com.ieptbto.cra.entidade.vo.RodapeVO;
import br.com.ieptbto.cra.entidade.vo.TituloVO;
import br.com.ieptbto.cra.enumeration.TipoRegistro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.util.CraConstructorUtils;

/**
 * 
 * @author Lefer
 *
 */
public class FabricaRegistro extends Processador {
	/** Tamanho padrao de uma linha de registro */

	private static final Map<String, Class<? extends AbstractArquivoVO>> TIPOS_ARQUIVOS;
	static {
		HashMap<String, Class<? extends AbstractArquivoVO>> map = new HashMap<String, Class<? extends AbstractArquivoVO>>();
		map.put(TipoRegistro.TITULO.getConstante(), TituloVO.class);
		map.put(TipoRegistro.CABECALHO.getConstante(), CabecalhoVO.class);
		map.put(TipoRegistro.RODAPE.getConstante(), RodapeVO.class);

		TIPOS_ARQUIVOS = Collections.unmodifiableMap(map);
	}

	private String linha;
	private String tipoReg;
	private TipoRegistro tipoRegistro;

	/**
	 * @param linha
	 */
	public FabricaRegistro(String linha) {
		if (linha == null) {
			throw new IllegalArgumentException("linha não pode ser null");
		}
		validarTamanhoLinha(linha);
		this.tipoReg = StringUtils.left(linha, 1);
		this.linha = linha;
		this.tipoRegistro = TipoRegistro.get(linha);
	}

	/**
	 * Cria um registro de acordo com o tipo
	 * 
	 * @return
	 */
	public AbstractArquivoVO criarRegistro() {
		Class<? extends AbstractArquivoVO> type = TIPOS_ARQUIVOS.get(tipoReg);
		if (type == null) {
			throw new InfraException("Tipo de registro desconhecido ");
		}
		return criarRegistro(type);
	}

	/**
	 * Criar um registro do tipo header ou tipo trailer.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	private <T extends AbstractArquivoVO> T criarRegistro(Class<T> type) {
		T novoRegistro = novoRegistro(type);
		new GerenciadorArquivo(linha, novoRegistro);
		return novoRegistro;
	}

	/**
	 * Retorna uma instância da fábrica.
	 * 
	 * @param linha
	 * @return
	 */
	public static FabricaRegistro getInstance(String linha) {
		return new FabricaRegistro(linha);
	}

	private void validarTamanhoLinha(String linha) {
		int tamLinha = linha.length();
		if (tamLinha != ConfiguracaoBase.TAMANHO_PADRAO_LINHA) {
			throw new InfraException("tamanho inválido de linha do arquivo. Esperado: [" + ConfiguracaoBase.TAMANHO_PADRAO_LINHA
			        + "], Recebido:[" + tamLinha + ConfiguracaoBase.FECHA_CHAVE);
		}
	}

	private <T> T novoRegistro(Class<T> classRegistro) {
		return CraConstructorUtils.newInstance(classRegistro);
	}

	public TipoRegistro getTipoRegistro() {
		return tipoRegistro;
	}
}
