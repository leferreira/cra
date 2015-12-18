package br.com.ieptbto.cra.webservice.dao;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
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
	public static final String CONSTANTE_DESISTENCIA_XML = "desistencia";
	public static final String CONSTANTE_CANCELAMENTO_XML = "cancelamento";
	protected Usuario usuario;
	protected String nomeArquivo;

	protected String setResposta(LayoutPadraoXML layoutPadraoResposta ,ArquivoVO arquivo, String nomeArquivo, String nomeNode) {
		if (getUsuario() == null) {
			logger.error("Usuario inválido.");
			return setRespostaUsuarioInvalido(layoutPadraoResposta, nomeArquivo);
		}
		if (nomeArquivo == null || StringUtils.EMPTY.equals(nomeArquivo.trim())) {
			logger.error("Nome de arquivo inválido.");
			return setRespostaNomeArquivoInvalido(layoutPadraoResposta, nomeArquivo);
		}
		
		return gerarMensagem(arquivo, nomeNode);
	}

	private String setRespostaUsuarioInvalido(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), CodigoErro.SERPRO_FALHA_NA_AUTENTICACAO);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), CodigoErro.CRA_FALHA_NA_AUTENTICACAO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	private String setRespostaNomeArquivoInvalido(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		/**
		 * Regras validar nome arquivo
		 * */
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			CodigoErro codigoErro = getCodigoErroNomeInvalidoSerpro(nomeArquivo);
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), codigoErro);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}
	
	protected String setRespostaUsuarioDiferenteDaInstituicaoDoArquivo(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		logger.error("Este usuário não pode enviar o arquivo desta instituição");
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_USUARIO_INSTITUICAO_DIFERENTE_ARQUIVO);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_USUARIO_INSTITUICAO_DIFERENTE_ARQUIVO);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaArquivoEmBranco(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		logger.error("Dados do arquivo enviados em branco");
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.SERPRO_ARQUIVO_INVALIDO_REMESSA_DESISTENCIA_CANCELAMENTO);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), CodigoErro.CRA_DADOS_DE_ENVIO_INVALIDOS);
		return gerarMensagem(msg.getMensagemErro(), CONSTANTE_RELATORIO_XML);
	}

	protected String setRespostaArquivoEmProcessamento(LayoutPadraoXML layoutPadraoResposta ,String nomeArquivo) {
		logger.error("O arquivo ainda não foi gerado, ou ainda está em processamento.");
		if (layoutPadraoResposta.equals(LayoutPadraoXML.SERPRO)) {
			CodigoErro codigoErro = getCodigoErroEmProcessamentoSerpro(nomeArquivo);
			MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), codigoErro);
			return gerarMensagem(msg.getMensagemErroSerpro(), CONSTANTE_RELATORIO_XML);
		}
		CodigoErro codigoErro = getCodigoErroEmProcessamentoCra(nomeArquivo);
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, getUsuario(), codigoErro);
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
			String msg = writer.toString();
			msg = msg.replace(" xsi:type=\"mensagemXmlSerpro\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			msg = msg.replace(" xsi:type=\"mensagemXmlDesistenciaCancelamentoSerpro\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			writer.close();
			return msg;

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.CRA_ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	private CodigoErro getCodigoErroEmProcessamentoSerpro(String nomeArquivo) {
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);
		
		CodigoErro codigoErro = null;
		if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_AGUARDE_CONFIRMACAO_EM_PROCESSAMENTO;
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_NAO_HA_REGISTRO_DE_RETORNO_NESTA_DATA;
		} else {
			codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
		}
		return codigoErro;
	}
	
	private CodigoErro getCodigoErroEmProcessamentoCra(String nomeArquivo) {
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);
		
		CodigoErro codigoErro = null;
		if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.CRA_NAO_EXISTE_CONFIRMACAO_NA_DATA_INFORMADA;
		} else if (TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.CRA_NAO_EXISTE_RETORNO_NA_DATA_INFORMADA;
		} else {
			codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
		}
		return codigoErro;
	}
	
	private CodigoErro getCodigoErroNomeInvalidoSerpro(String nomeArquivo) {
		TipoArquivoEnum tipoArquivo = TipoArquivoEnum.getTipoArquivoEnum(nomeArquivo);
		
		CodigoErro codigoErro = null;
		if (TipoArquivoEnum.REMESSA.equals(tipoArquivo) ||
				TipoArquivoEnum.DEVOLUCAO_DE_PROTESTO.equals(tipoArquivo) ||
				TipoArquivoEnum.CANCELAMENTO_DE_PROTESTO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_NOME_ARQUIVO_INVALIDO_REMESSA_DESISTENCIA_CANCELAMENTO;
		} else if (TipoArquivoEnum.CONFIRMACAO.equals(tipoArquivo) ||
				TipoArquivoEnum.RETORNO.equals(tipoArquivo)) {
			codigoErro = CodigoErro.SERPRO_NOME_ARQUIVO_INVALIDO_CONFIRMACAO_RETORNO;
		} else {
			codigoErro = CodigoErro.CRA_NOME_DO_ARQUIVO_INVALIDO;
		}
		return codigoErro;
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
