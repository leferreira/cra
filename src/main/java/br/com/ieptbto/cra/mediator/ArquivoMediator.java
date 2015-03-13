package br.com.ieptbto.cra.mediator;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;

/**
 * 
 * @author Lefer
 *
 */
@Service
public class ArquivoMediator {

	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;

	public void salvar(Arquivo arquivo, FileUpload uploadedFile) {
		processarArquivo(arquivo, uploadedFile);
		arquivo.setTipoArquivo(getTipoArquivo(arquivo));

	}

	private TipoArquivo getTipoArquivo(Arquivo arquivo) {
		return tipoArquivoDAO.buscarTipoArquivo(arquivo);
	}

	private void processarArquivo(Arquivo arquivo, FileUpload uploadedFile) throws InfraException {
		new ProcessadorArquivo().processarArquivo(uploadedFile, arquivo.getUsuarioEnvio());
	}

}
