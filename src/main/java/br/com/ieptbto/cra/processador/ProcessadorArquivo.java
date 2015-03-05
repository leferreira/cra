package br.com.ieptbto.cra.processador;

import java.io.File;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;

/**
 * 
 * @author Lefer
 *
 */
public class ProcessadorArquivo extends Processador {

	public static void processarArquivo(FileUpload uploadedFile, Usuario usuario) {
		if (uploadedFile != null) {

			verificaDiretorio(usuario);
			File novoArquivo = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);
			if (novoArquivo.exists()) {
				new InfraException("Arquivo já armazenado. Por favor verifique o nome do arquivo");
			}

		} else {
			new InfraException("O arquivo enviado não pode ser processado.");
		}

	}

	private static void verificaDiretorio(Usuario usuario) {
		File diretorioTemp = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);
		File diretorioArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioUsuario = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + usuario.getInstituicao().getId());

		if (!diretorioTemp.exists()) {
			diretorioTemp.mkdirs();
		}
		if (!diretorioArquivo.exists()) {
			diretorioArquivo.mkdirs();
		}
		if (!diretorioUsuario.exists()) {
			diretorioUsuario.mkdirs();
		}

	}
}
