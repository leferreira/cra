package br.com.ieptbto.cra.processador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import br.com.ieptbto.cra.conversor.arquivo.FabricaDeArquivo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.validacao.FabricaValidacaoArquivo;

/**
 * 
 * @author Lefer
 *
 */
public class ProcessadorArquivo extends Processador {

	private static final Logger logger = Logger.getLogger(ProcessadorArquivo.class);
	private FileUpload file;
	private Usuario usuario;
	private File arquivoFisico;
	private String pathInstituicao;
	private String pathUsuario;
	private Arquivo arquivo;

	public void processarArquivo(FileUpload uploadedFile, Arquivo arquivo) {
		this.file = uploadedFile;
		this.usuario = arquivo.getUsuarioEnvio();
		this.arquivo = arquivo;

		logger.info("Início do processamento do arquivoFisico " + getFile().getClientFileName() + " do usuário " + getUsuario().getLogin());
		if (getFile() != null) {

			verificaDiretorio();
			copiarArquivoParaDiretorioDoUsuario();
			validarArquivo();
			converterArquivo();

		} else {
			throw new InfraException("O arquivoFisico " + getFile().getClientFileName() + "enviado não pode ser processado.");
		}

	}

	private void converterArquivo() {
		new FabricaDeArquivo(getArquivoFisico(), getArquivo()).converter();
	}

	private void validarArquivo() {
		logger.info("Iniciar validação do arquivoFisico " + getFile().getClientFileName() + " enviado pelo usuário "
		        + getUsuario().getLogin());
		new FabricaValidacaoArquivo(getArquivoFisico(), getUsuario()).validar();

	}

	private void copiarArquivoParaDiretorioDoUsuario() {
		setArquivoFisico(new File(pathUsuario + ConfiguracaoBase.BARRA + getFile().getClientFileName()));
		try {
			getArquivoFisico().createNewFile();
			getFile().writeTo(getArquivoFisico());
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + getFile().getClientFileName());
		}

	}

	private void verificaDiretorio() {
		pathInstituicao = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + ConfiguracaoBase.BARRA + getUsuario().getInstituicao().getId();
		pathUsuario = pathInstituicao + ConfiguracaoBase.BARRA + usuario.getId();
		File diretorioTemp = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);
		File diretorioArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioInstituicao = new File(pathInstituicao);
		File diretorioUsuario = new File(pathUsuario);

		if (!diretorioTemp.exists()) {
			diretorioTemp.mkdirs();
		}
		if (!diretorioArquivo.exists()) {
			diretorioArquivo.mkdirs();
		}
		if (!diretorioInstituicao.exists()) {
			diretorioInstituicao.mkdirs();
		}
		if (!diretorioUsuario.exists()) {
			diretorioUsuario.mkdirs();
		}

	}

	public FileUpload getFile() {
		return file;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setFile(FileUpload file) {
		this.file = file;
	}

	public File getArquivoFisico() {
		return arquivoFisico;
	}

	public void setArquivoFisico(File arquivo) {
		this.arquivoFisico = arquivo;
	}

	public Arquivo getArquivo() {
		if (this.arquivo == null) {
			arquivo = new Arquivo();
		}
		if (arquivo.getRemessas() == null) {
			arquivo.setRemessas(new ArrayList<Remessa>());
		}
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
}
