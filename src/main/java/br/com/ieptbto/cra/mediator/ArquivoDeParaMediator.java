package br.com.ieptbto.cra.mediator;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.arquivoDePara.ArquivoBancoDoBrasil;
import br.com.ieptbto.cra.arquivoDePara.ArquivoBradesco;
import br.com.ieptbto.cra.arquivoDePara.ArquivoCAF;
import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoDeParaMediator {

	@Autowired
	ArquivoDeParaDAO deParaDAO;
	
	public void processarArquivo(FileUpload uploadedFile) {
		
		if (uploadedFile.getClientFileName().contains(PadraoArquivoDePara.CAF.getModelo())) {
			deParaDAO.salvarArquivoCAF(new ArquivoCAF().processar(uploadedFile));
		} else if (PadraoArquivoDePara.BANCO_DO_BRASIL.equals(uploadedFile)) {
			deParaDAO.salvarArquivoBancoDoBrasil(new ArquivoBancoDoBrasil().processar(uploadedFile));
		} else if (uploadedFile.getClientFileName().toUpperCase().contains(PadraoArquivoDePara.BRADESCO.getModelo())) {
			deParaDAO.salvarArquivoBradesco(new ArquivoBradesco().processar(uploadedFile));
		} else {
			new InfraException("Não foi possível definir o modelo do arquivo de/para ! Entre em contato com a CRA !");
		}
	}
}
