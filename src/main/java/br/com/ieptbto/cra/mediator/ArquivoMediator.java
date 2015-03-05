package br.com.ieptbto.cra.mediator;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ArquivoMediator {

	public void salvar(Arquivo arquivo, FileUpload uploadedFile) {
		processarArquivo(arquivo, uploadedFile);
		arquivo.setTipoArquivo(getTipoArquivo(arquivo));

	}

	private TipoArquivo getTipoArquivo(Arquivo arquivo) {

		return null;
	}

	private void processarArquivo(Arquivo arquivo, FileUpload uploadedFile) {
		ProcessadorArquivo.processarArquivo(uploadedFile, arquivo.getUsuarioEnvio());
	}

}
