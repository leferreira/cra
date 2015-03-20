package br.com.ieptbto.cra.validacao.regra;

import java.io.File;

import br.com.ieptbto.cra.conversor.enumeration.ErroValidacao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

public class RegraNomeArquivo extends RegrasDeEntrada {

	@Override
	protected void validar(File arquivo, Usuario usuario) {
		this.usuario = usuario;
		this.arquivo = arquivo;

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
		if (arquivo.getName().startsWith("TipoArquivo")) {

		}

		if (arquivo.getName().length() > ConfiguracaoBase.TAMANHO_NOME_ARQUIVO) {
			new InfraException(ErroValidacao.NOME_DO_ARQUIVO_INVALIDO.getMensagemErro());
		}

	}

}
