package br.com.ieptbto.cra.mediator;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.arquivoDePara.ArquivoCAF;
import br.com.ieptbto.cra.dao.ArquivoDeParaDAO;
import br.com.ieptbto.cra.entidade.ArquivoDePara;
import br.com.ieptbto.cra.enumeration.PadraoArquivoDePara;
import br.com.ieptbto.cra.exception.InfraException;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoDeParaMediator {

	@SpringBean
	InstituicaoMediator instituicaoMediator;
	
	private ArquivoDePara arquivoDePara;
	private FileUpload file;

	public ArquivoDePara processarArquivo(FileUpload uploadedFile) throws Exception {
		this.arquivoDePara = new ArquivoDePara();
		setFile(file);
		processar();
		
		return salvarArquivo(); 
	}
	
	private ArquivoDePara salvarArquivo() {
		return new ArquivoDeParaDAO().salvarArquivoDePara(getArquivoDePara());
	}

	private void processar() {
		
		if (PadraoArquivoDePara.CAF.equals(getFile())) {
			arquivoDePara = new ArquivoCAF().processar(getFile());
		} else if (PadraoArquivoDePara.BANCO_DO_BRASIL.equals(getFile())) {
			
		} else if (PadraoArquivoDePara.BRADESCO.equals(getFile())) {
			
		} else {
			new InfraException("Não foi possível definir o modelo do arquivo de/para ! Entre em contato com a CRA !");
		}
	}

	private FileUpload getFile() {
		return file;
	}

	private void setFile(FileUpload file) {
		this.file = file;
	}

	private ArquivoDePara getArquivoDePara() {
		return arquivoDePara;
	}
}
