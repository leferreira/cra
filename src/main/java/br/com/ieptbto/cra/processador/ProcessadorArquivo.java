package br.com.ieptbto.cra.processador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ieptbto.cra.conversor.arquivo.FabricaDeArquivo;
import br.com.ieptbto.cra.entidade.Arquivo;
import br.com.ieptbto.cra.entidade.Remessa;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.exception.ValidacaoErroException;
import br.com.ieptbto.cra.mediator.ConfiguracaoBase;
import br.com.ieptbto.cra.validacao.FabricaValidacaoArquivo;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * @author Thasso Araújo
 *
 */
@Service
public class ProcessadorArquivo extends Processador {

	private static final Logger logger = Logger.getLogger(ProcessadorArquivo.class);

	@Autowired
	private FabricaDeArquivo fabricaDeArquivo;
	@Autowired
	private FabricaValidacaoArquivo fabricaValidacaoArquivo;

	private FileUpload file;
	private Usuario usuario;
	private File arquivoFisico;
	private String pathInstituicao;
	private String pathUsuario;
	private Arquivo arquivo;
	private List<Exception> erros;
	private String pathInstituicaoTemp;
	private String pathUsuarioTemp;

	public void processarArquivo(FileUpload uploadedFile, Arquivo arquivo, List<Exception> erros) {
		this.file = uploadedFile;
		this.usuario = arquivo.getUsuarioEnvio();
		this.arquivo = arquivo;
		this.erros = erros;

		if (getFile() != null) {

			logger.info("Início do processamento do arquivoFisico " + getFile().getClientFileName() + " do usuário "
			        + getUsuario().getLogin());
			verificaDiretorio();
			copiarArquivoParaDiretorioDoUsuarioTemporario(getFile().getClientFileName());
			validarArquivo();
			converterArquivo();
			copiarArquivoEapagarTemporario();

			logger.info("Fim do processamento do arquivoFisico " + getFile().getClientFileName() + " do usuário " + getUsuario().getLogin());

		} else {
			throw new InfraException("O arquivoFisico " + getFile().getClientFileName() + "enviado não pode ser processado.");
		}

	}

	public void processarArquivo(ArquivoVO arquivoRecebido, Usuario usuario, String nomeArquivo, Arquivo arquivo) {
		this.usuario = usuario;
		this.arquivo = arquivo;
		verificaDiretorio();
		setArquivoFisico(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + nomeArquivo));
		salvarXMLTemporario(arquivoRecebido);

		fabricaDeArquivo.processarArquivoXML(arquivoRecebido, usuario, nomeArquivo, arquivo, getErros());

	}

	private void salvarXMLTemporario(ArquivoVO arquivoRecebido) {
		try {
			FileWriter fw = new FileWriter(getArquivoFisico());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(gerarXML(arquivoRecebido));
			bw.close();
			fw.close();
			logger.info("Arquivo XML gerado com Sucesso.");
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
	}

	private String gerarXML(ArquivoVO mensagem) {
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(mensagem.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty("jaxb.encoding", "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName("remessa"), Object.class, mensagem);
			marshaller.marshal(element, writer);
			logger.info("Remessa processada com sucesso.");
			return writer.toString();

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	private void copiarArquivoEapagarTemporario() {
		try {
			if (getArquivoFisico().renameTo(new File(getPathUsuario() + ConfiguracaoBase.BARRA + getArquivo().getId()))) {
				logger.info("Arquivo " + getArquivoFisico().getName() + " movido para pasta do usuário.");
				return;
			}
			new InfraException("Não foi possível mover o arquivo temporário para o diretório do usuário.");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			getErros().add(ex);
			new InfraException("Não foi possível mover o arquivo temporário para o diretório do usuário.");
		}
	}

	private void converterArquivo() {
		fabricaDeArquivo.processarArquivoFisico(getArquivoFisico(), getArquivo(), getErros());
	}

	private void validarArquivo() {
		logger.info("Iniciar validação do arquivoFisico " + getFile().getClientFileName() + " enviado pelo usuário "
		        + getUsuario().getLogin());

		fabricaValidacaoArquivo.validar(getArquivoFisico(), getUsuario(), getErros());

		logger.info("Fim validação do arquivoFisico " + getFile().getClientFileName() + " enviado pelo usuário " + getUsuario().getLogin());
	}

	private void copiarArquivoParaDiretorioDoUsuarioTemporario(String nomeArquivo) {
		setArquivoFisico(new File(getPathUsuarioTemp() + ConfiguracaoBase.BARRA + nomeArquivo));
		try {
			getFile().writeTo(getArquivoFisico());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			getErros().add(new ValidacaoErroException(e.getMessage(), e.getCause()));
			throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + getFile().getClientFileName());
		}

	}

	private void verificaDiretorio() {
		pathInstituicao = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO + getUsuario().getInstituicao().getId();
		pathInstituicaoTemp = ConfiguracaoBase.DIRETORIO_BASE_INSTITUICAO_TEMP + getUsuario().getInstituicao().getId();
		pathUsuario = pathInstituicao + ConfiguracaoBase.BARRA + usuario.getId();
		pathUsuarioTemp = pathInstituicaoTemp + ConfiguracaoBase.BARRA + usuario.getId();
		File diretorioTemp = new File(ConfiguracaoBase.DIRETORIO_TEMP_BASE);
		File diretorioArquivo = new File(ConfiguracaoBase.DIRETORIO_BASE);
		File diretorioInstituicao = new File(pathInstituicao);
		File diretorioUsuario = new File(pathUsuario);
		File diretorioUsuarioTemp = new File(pathUsuarioTemp);

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
		if (!diretorioUsuarioTemp.exists()) {
			diretorioUsuarioTemp.mkdirs();
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
		if (!arquivoFisico.exists()) {
			try {
				arquivoFisico.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e.getCause());
				getErros().add(new ValidacaoErroException(e.getMessage(), e.getCause()));
				throw new InfraException("Não foi possível criar arquivo Físico temporário para o arquivo " + arquivoFisico.getName());
			}
		}
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

	public List<Exception> getErros() {
		return erros;
	}

	public void setErros(List<Exception> erros) {
		this.erros = erros;
	}

	public String getPathInstituicaoTemp() {
		return pathInstituicaoTemp;
	}

	public String getPathUsuarioTemp() {
		return pathUsuarioTemp;
	}

	public String getPathInstituicao() {
		return pathInstituicao;
	}

	public String getPathUsuario() {
		return pathUsuario;
	}

}
