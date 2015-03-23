package br.com.ieptbto.cra.validacao.regra;

import java.io.File;

import br.com.ieptbto.cra.entidade.Usuario;

public class FabricaRegrasDeEntrada {

	public static void validar(File arquivo, Usuario usuario) {
		new RegraValidaTipoArquivoTXT().validar(arquivo, usuario);
		new RegraValidarUsuarioEnvio().validar(arquivo, usuario);
		new RegraValidarInstituicaoEnvio().validar(arquivo, usuario);
		new RegraNomeArquivo().validar(arquivo, usuario);
	}

}
