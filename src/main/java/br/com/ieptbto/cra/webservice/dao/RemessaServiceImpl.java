package br.com.ieptbto.cra.webservice.dao;

import java.io.StringWriter;
import java.io.Writer;

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

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.entidade.vo.ArquivoVO;
import br.com.ieptbto.cra.enumeration.TipoArquivoEnum;
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

	RemessaMediator remessaMediator;
	UsuarioMediator usuarioMediator;
	Usuario usuario;
	@Resource
	WebServiceContext wsctx;
	ClassPathXmlApplicationContext context;

	@WebMethod(operationName = "remessa")
	@GET
	@Override
	public String remessa(@WebParam(name = "nomeArquivo") String nomeArquivo, @WebParam(name = "login") String login,
	        @WebParam(name = "senha") String senha, @WebParam(name = "dados") String dados) {
		context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		remessaMediator = (RemessaMediator) context.getBean("remessaMediator");
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");

		setUsuario(login, senha);

		ArquivoVO arquivo = getArquivo(nomeArquivo);
		return setResposta(arquivo, nomeArquivo);
	}

	private String setResposta(ArquivoVO arquivo, String nomeArquivo) {
		if (getUsuario() == null) {
			return setRespostaUsuarioInvalido(nomeArquivo);
		}
		if (arquivo == null) {
			return setRespostaArquivoInexistente(nomeArquivo);
		}

		return gerarMensagem(arquivo);
	}

	private String setRespostaUsuarioInvalido(String nomeArquivo) {
		MensagemDeErro msg = new MensagemDeErro(nomeArquivo, new Usuario(), CodigoErro.FALHA_NA_AUTENTICACAO);
		return gerarMensagem(msg.getMensagemErro());

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
		return gerarMensagem(msg.getMensagemErro());
	}

	private ArquivoVO getArquivo(String nome) {
		return remessaMediator.buscarArquivos(nome);
	}

	private void setUsuario(String login, String senha) {
		this.usuario = usuarioMediator.autenticar(login, senha);
	}

	public Usuario getUsuario() {
		return usuario;
	}

	private String gerarMensagem(Object mensagem) {
		Writer writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(mensagem.getClass());

			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty("jaxb.encoding", "ISO-8859-1");
			JAXBElement<Object> element = new JAXBElement<Object>(new QName("remessa"), Object.class, mensagem);
			marshaller.marshal(element, writer);

			return writer.toString();

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
