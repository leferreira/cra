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
import br.com.ieptbto.cra.enumeration.LayoutPadraoXML;
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
	public static final String CONSTANTE_RELATORIO_XML = "relatorio";
	public static final String CONSTANTE_COMARCA_XML = "comarcas";
	public static final String CONSTANTE_REMESSA_XML = "remessa";
	public static final String CONSTANTE_CONFIRMACAO_XML = "confirmacao";
	public static final String CONSTANTE_RETORNO_XML = "retorno";
	protected Usuario usuario;
	protected String nomeArquivo;

	protected String setResposta(LayoutPadraoXML layoutPadraoResposta ,ArquivoVO arquivo, String nomeArquivo, String nomeNode) {
		if (getUsuario() == null) {
			logger.error("Usuario inválido.");
			return setRespostaUsuarioInvalido(layoutPadraoResposta, nomeArquivo);
		}
		if (arquivo == null) {
			logger.error("Remessa não encontrada.");
			return setRespostaArquivoInexistente(layoutPadraoResposta, nomeArquivo);
		}
		
		return gerarMensagem(arquivo, nomeNode);
	}

	protected String setRespostaArquivoEmBranco(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		logger.error("dados enviados em branco");
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.DADOS_DE_ENVIO_INVALIDOS);
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.USUARIO_INSTITUICAO_DIFERENTE_ARQUIVO);
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	private String setRespostaUsuarioInvalido(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), CodigoErro.FALHA_NA_AUTENTICACAO);
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);

	}

	private String setRespostaArquivoInexistente(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
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
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
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
