package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.Erro;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.ValidacaoErroException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

@Service
public class RegraNomeArquivo extends RegrasDeEntrada {

	@Override
	protected void validar(File arquivo, Usuario usuario, List<Exception> erros) {
		this.usuario = usuario;
		this.arquivo = arquivo;
		setErros(erros);

	}

	@Override
	protected void executar() {
		verificarNomeDoArquivo();

	}

	/**
	 * Valida a primeira letra do nome do arquivo
	 */
	private void verificarNomeDoArquivo() {

		/**
		 * verifica se o início do nome corresponde a algum tipo de arquivo
		 * válido
		 */
		try {
			TipoArquivoEnum.getTipoArquivoEnum(arquivo.getName());
		} catch (InfraException ex) {
			logger.error(ex.getMessage(), ex.getCause());
			getErros().add(new ValidacaoErroException(arquivo.getName(), ex.getMessage(), ex.getCause()));
			throw new InfraException(ex.getMessage(), ex.getCause());
		}

		if (arquivo.getName().length() > ConfiguracaoBase.TAMANHO_NOME_ARQUIVO) {
			logger.error(Erro.NOME_DO_ARQUIVO_INVALIDO.getMensagemErro());
			getErros().add(new ValidacaoErroException(arquivo.getName(), Erro.NOME_DO_ARQUIVO_INVALIDO, arquivo.getName()));
			throw new InfraException(Erro.NOME_DO_ARQUIVO_INVALIDO.getMensagemErro());
		}

	}
}
