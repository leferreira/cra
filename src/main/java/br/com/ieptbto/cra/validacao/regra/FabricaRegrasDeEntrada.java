package br.com.ieptbto.cra.validacao.regra;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Usuario;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class FabricaRegrasDeEntrada {

	@Autowired
	private RegraValidarInstituicaoEnvio regraValidarInstituicaoEnvio;
	@Autowired
	private RegraNomeArquivo regraNomeArquivo;
	@Autowired
	private RegraValidarUsuarioEnvio regraValidarUsuarioEnvio;

	public void validar(File arquivo, Usuario usuario, List<Exception> erros) {
		regraNomeArquivo.validar(arquivo, usuario, erros);
		regraValidarInstituicaoEnvio.validar(arquivo, usuario, erros);
		regraValidarUsuarioEnvio.validar(arquivo, usuario, erros);
	}

}
