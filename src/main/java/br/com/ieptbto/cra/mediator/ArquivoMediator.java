package br.com.ieptbto.cra.mediator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.dao.ArquivoDAO;
import br.com.ieptbto.cra.dao.TipoArquivoDAO;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Instituicao;
import br.com.ieptbto.cra.entidade.StatusArquivo;
import br.com.ieptbto.cra.entidade.TipoArquivo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.enumeration.SituacaoArquivo;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.processador.ProcessadorArquivo;


/**
 * @author Thasso Araújo
 *
 */
@Service
public class ArquivoMediator {

	@Autowired
	private TipoArquivoDAO tipoArquivoDAO;
	@Autowired
	private ArquivoDAO arquivoDAO;
	private List<Exception> erros;
	private Arquivo arquivo;

	@Autowired
	private ProcessadorArquivo processadorArquivo;

	public ArquivoMediator salvar(Arquivo arquivo, FileUpload uploadedFile, Usuario usuario) {
		arquivo.setTipoArquivo(getTipoArquivo(arquivo));
		arquivo.setStatusArquivo(setStatusArquivo());
		processarArquivo(arquivo, uploadedFile);
		setArquivo(arquivoDAO.salvar(arquivo, usuario));
		return this;
	}

	private StatusArquivo setStatusArquivo() {
		StatusArquivo status = new StatusArquivo();
		status.setData(new Date());
		status.setStatus(SituacaoArquivo.AGUARDANDO.getLabel());
		return status;
	}

	private TipoArquivo getTipoArquivo(Arquivo arquivo) {
		return tipoArquivoDAO.buscarTipoArquivo(arquivo);
	}

	private void processarArquivo(Arquivo arquivo, FileUpload uploadedFile) throws InfraException {
		processadorArquivo.processarArquivo(uploadedFile, arquivo, getErros());
	}

	public List<Arquivo> buscarArquivos() {
		return arquivoDAO.buscarTodosArquivos();
	}

	public List<Exception> getErros() {
		if (erros == null) {
			erros = new ArrayList<Exception>();
		}
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public List<Arquivo> buscarArquivosPorInstituicao(Instituicao instituicao,	ArrayList<String> tiposSelect, 
			ArrayList<String> statusSelect,	LocalDate dataInicio, LocalDate dataFim) {
		return arquivoDAO.buscarArquivosPorInstituicao(instituicao, tiposSelect, statusSelect, dataInicio, dataFim);
	}
}
