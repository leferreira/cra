package br.com.ieptbto.cra.validacao.regra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.ValidacaoErroException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraValidaTipoArquivoTXT extends RegrasDeEntrada {

	private static final int POSICAO_FINAL_NUMERO_LINHA = 600;
	private static final int POSICAO_INICIAL_NUMERO_LINHA = 596;
	private static final Logger logger = Logger.getLogger(RegraValidaTipoArquivoTXT.class);
	private File arquivo;
	private String linha;

	public void validar(File arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		setErros(erros);
		executar();
	}

	@Override
	protected void executar() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)));
			linha = reader.readLine();
			reader.close();
			validaInicioLinha(linha);
			validaTamanhoLinha(linha);

		} catch (IOException e) {
			logger.error(e.getMessage());
			getErros().add(new ValidacaoErroException(arquivo.getName(), e.getMessage(), e.getCause()));
			throw new InfraException(Erro.NAO_FOI_POSSIVEL_VERIFICAR_A_PRIMEIRA_LINHA_DO_ARQUIVO.getMensagemErro());
		}

	}

	/**
	 * Verifica o tamanho da linha
	 * 
	 * @param linha
	 */
	private void validaTamanhoLinha(String linha) {
		if (linha.length() != ConfiguracaoBase.TAMANHO_PADRAO_LINHA) {
			logger.error(Erro.O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO.getMensagemErro());
			getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.TAMANHO_LINHA_FORA_DO_PADRAO, getNumeroLinha()));
		}

	}

	/**
	 * Verifica se o primeiro caracter do arquivo corresponde a um válido para
	 * arquivos TXT
	 * 
	 * @param linha
	 */
	private void validaInicioLinha(String linha) {
		if (!linha.startsWith(ConfiguracaoBase.CARACTER_INICIO_TXT)) {
			logger.error(Erro.O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO.getMensagemErro());
			throw new InfraException(Erro.O_ARQUIVO_NAO_E_UM_TIPO_TXT_VALIDO.getMensagemErro());
		}
	}

	private int getNumeroLinha() {
		return Integer.parseInt(linha.substring(POSICAO_INICIAL_NUMERO_LINHA, POSICAO_FINAL_NUMERO_LINHA));

	}
}
