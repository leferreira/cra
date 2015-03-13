package br.com.ieptbto.cra.validacao.regra;

import java.io.File;

import org.apache.wicket.spring.injection.annot.SpringBean;

import br.com.ieptbto.cra.conversor.enumeration.ErroValidacao;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

public class RegraValidarUsuarioEnvio extends RegrasDeEntrada {

	@SpringBean
	UsuarioMediator usuarioMediator;

	protected void validar(File arquivo, Usuario usuario) {
		verificaInstituicaoDoUsuario();
		verificarPermissaoDoUsuario();

	}

	/**
	 * Verifica se o usuário tem permissão para enviar o arquivo.
	 */
	private void verificarPermissaoDoUsuario() {
		try {

		} catch (Exception ex) {
			new InfraException(ErroValidacao.USUARIO_SEM_PERMISSAO_DE_ENVIO_DE_ARQUIVO.getMensagemErro(), ex.getCause());
		}

	}

	/**
	 * Verifica se a instituição do usuário de envio tem permissão para envio do
	 * arquivo.
	 */
	private void verificaInstituicaoDoUsuario() {

	}

	@Override
	protected void executar() {
		// TODO Auto-generated method stub

	}

}
