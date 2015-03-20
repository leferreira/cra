package br.com.ieptbto.cra.processador;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.upload.FileUpload;

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
	private File arquivo;

	public void processarArquivo(FileUpload uploadedFile, Usuario usuario) {
		this.file = uploadedFile;
		this.usuario = usuario;

		logger.info("Início do processamento do arquivo " + getFile().getClientFileName() + " do usuário " + getUsuario().getLogin());
		if (getFile() != null) {

			verificaDiretorio();
			copiarArquivoParaDiretorioDoUsuario();
			validarArquivo();

		} else {
			new InfraException("O arquivo " + getFile().getClientFileName() + "enviado não pode ser processado.");
		}

	}

	private void validarArquivo() {
		logger.info("Iniciar validação do arquivo " + getFile().getClientFileName() + " enviado pelo usuário " + getUsuario().getLogin());
		new FabricaValidacaoArquivo(getArquivo(), getUsuario()).validar();

	}

	private void copiarArquivoParaDiretorioDoUsuario() {
		setArquivo(new File(ConfiguracaoBase.DIRETORIO_BASE + usuario.getId() + ConfiguracaoBase.BARRA + getFile().getClientFileName()));
		try {
			getArquivo().createNewFile();
			getFile().writeTo(getArquivo());
		} catch (IOException e) {
			throw new InfraException("Não foi possível criar arquivo temporário para o arquivo " + getFile().getClientFileName());
		}

	}

	private void verificaDiretorio() {
		File diretorioTemp = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);
		File diretorioArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioUsuario = new File(ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + getUsuario().getInstituicao().getId());

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

	public File getArquivo() {
		return arquivo;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
	}
}
