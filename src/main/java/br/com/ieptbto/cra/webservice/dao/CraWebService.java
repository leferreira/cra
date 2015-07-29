package br.com.ieptbto.cra.webservice.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
public class CraWebService {

	protected static final Logger logger = Logger.getLogger(CraWebService.class);
	private static final String CONSTANTE_REMESSA_XML = "remessa";
	protected Usuario usuario;
	protected String nomeArquivo;

	protected String setResposta(ArquivoVO arquivo, String nomeArquivo) {
		if (getUsuario() == null) {
			logger.error("Usuario inválido.");
			return setRespostaUsuarioInvalido(nomeArquivo);
		}
		if (arquivo == null) {
			logger.error("Remessa não encontrada.");
			return setRespostaArquivoInexistente(nomeArquivo);
		}

		return gerarMensagem(arquivo, CONSTANTE_REMESSA_XML);
	}

	protected String setRespostaArquivoEmBranco(String nomeArquivo) {
		logger.error("dados enviados em branco");
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.DADOS_DE_ENVIO_INVALIDOS);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_REMESSA_XML);
	}

	private String setRespostaUsuarioInvalido(String nomeArquivo) {
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), CodigoErro.FALHA_NA_AUTENTICACAO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_REMESSA_XML);

	}

	private String setRespostaArquivoInexistente(String nomeArquivo) {
		TipoArquivoEnum tipo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);
		CodigoErro codigo = null;
		if (TipoArquivoEnum.REMESSA.equals(tipo)) {
			codigo = CodigoErro.NAO_EXISTE_REMESSA_NA_DATA_INFORMADA;
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipo)) {
			codigo = CodigoErro.NAO_EXISTE_CONFIRMACAO_NA_DATA_INFORMADA;
		} else if (TipoArquivoEnum.RETORNO.equals(tipo)) {
			codigo = CodigoErro.NAO_EXISTE_RETORNO_NA_DATA_INFORMADA;
		} else {
			codigo = CodigoErro.NOME_DO_ARQUIVO_INVALIDO;
		}

		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), codigo);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_REMESSA_XML);
	}

	protected String gerarMensagem(Object mensagem, String nomeNo) {
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(mensagem.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(nomeNo), Object.class, mensagem);
			marshaller.marshal(element, writer);
			logger.info("Remessa processada com sucesso.");
			String msg = writer.toString();
			writer.close();
			return msg;

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
}
