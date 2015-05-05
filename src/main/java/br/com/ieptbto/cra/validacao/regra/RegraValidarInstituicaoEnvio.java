package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.enumeration.ErroValidacao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class RegraValidarInstituicaoEnvio extends RegrasDeEntrada {

	@Override
	protected void validar(File arquivo, Usuario usuario, List<Exception> erros) {
		this.arquivo = arquivo;
		this.usuario = usuario;
		setErros(erros);

		executar();

	}

	@Override
	protected void executar() {
		verificarInstituicaoDeEnvio();
		verificarPermissaoDeEnvioDaInstituicao();

	}

	/**
	 * Verifica se a instituição não está bloqueada para envio do arquivo
	 */
	private void verificarInstituicaoDeEnvio() {
		try {
			if (!usuario.getInstituicao().isSituacao()) {
				logger.error(ErroValidacao.INSTITUICAO_BLOQUEADA.getMensagemErro());
				throw new InfraException(ErroValidacao.INSTITUICAO_BLOQUEADA.getMensagemErro());
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new InfraException(ErroValidacao.INSTITUICAO_BLOQUEADA.getMensagemErro(), ex.getCause());
		}
	}

	/**
	 * Verifica se a instituição tem permissão para envio do tipo de arquivo (B,
	 * C, R ou DP)
	 */
	private void verificarPermissaoDeEnvioDaInstituicao() {
	}

}
