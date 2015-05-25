package br.com.ieptbto.cra.webservice.dao;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.conversor.ConversorArquivoVo;
import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.entidade.vo.RemessaVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
import br.com.ieptbto.cra.exception.InfraException;
import br.com.ieptbto.cra.mediator.RemessaMediator;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.webservice.VO.CodigoErro;

/**
 * 
 * @author Lefer
 *
 */
@WebService(name = "/RemessaService", endpointInterface = "br.com.ieptbto.cra.webservice.dao.IRemessaWS")
@Path("/RemessaService")
public class RemessaServiceImpl implements IRemessaWS {// extends HttpServlet {

	private static final String CONSTANTE_REMESSA_XML = "remessa";
	public static final Logger logger = Logger.getLogger(RemessaServiceImpl.class);
	RemessaMediator remessaMediator;
	UsuarioMediator usuarioMediator;
	Usuario usuario;
	@Resource
	WebServiceContext wsctx;
	ClassPathXmlApplicationContext context;
	ArquivoVO arquivoRecebido;
	List<RemessaVO> remessas;

	@GET
	@WebMethod
	@Override
	public String arquivo(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "remessa") ArquivoVO remessa,
	        @WebParam(name = "user_sign") String assinatura) {
		context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		remessaMediator = (RemessaMediator) context.getBean("remessaMediator");
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");
		this.arquivoRecebido = remessa;

		setUsuario(login, senha);

		if (getArquivoRecebido() == null || getArquivoRecebido().getCabecalhos() == null) {
			ArquivoVO arquivo = getArquivo(nomeArquivo);
			return setResposta(arquivo, nomeArquivo);
		}
		converterArquivo();

		return gerarMensagem(remessaMediator.processarArquivoXML(getRemessas(), getUsuario(), nomeArquivo), "relatorio");

	}

	private void converterArquivo() {
		setRemessas(ConversorArquivoVo.converterParaRemessaVO(getArquivoRecebido()));
	}

	private String setResposta(ArquivoVO arquivo, String nomeArquivo) {
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

	private ArquivoVO getArquivo(String nome) {
		return remessaMediator.buscarArquivos(nome);
	}

	private void setUsuario(String login, String senha) {
		logger.info("Inicio WebService pelo usuario= " + login);
		this.usuario = usuarioMediator.autenticar(login, senha);
	}

	public Usuario getUsuario() {
		return usuario;
	}

	private String gerarMensagem(Object mensagem, String nomeNo) {
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(mensagem.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty("jaxb.encoding", "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName(nomeNo), Object.class, mensagem);
			marshaller.marshal(element, writer);
			logger.info("Remessa processada com sucesso.");
			return writer.toString();

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e.getCause());
			new InfraException(CodigoErro.ERRO_NO_PROCESSAMENTO_DO_ARQUIVO.getDescricao(), e.getCause());
		}
		return null;
	}

	public ArquivoVO getArquivoRecebido() {
		return arquivoRecebido;
	}

	public void setArquivoRecebido(ArquivoVO arquivoRecebido) {
		this.arquivoRecebido = arquivoRecebido;
	}

	public List<RemessaVO> getRemessas() {
		return remessas;
	}

	public void setRemessas(List<RemessaVO> remessas) {
		this.remessas = remessas;
	}
}
