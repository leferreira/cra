package br.com.ieptbto.cra.webservice.service;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.webservice.dao.ConfirmacaoService;
import br.com.ieptbto.cra.webservice.dao.RemessaService;
import br.com.ieptbto.cra.webservice.interf.IRemessaWS;

/**
 * 
 * @author Lefer
 *
 */
@WebService(name = "/RemessaService", endpointInterface = "br.com.ieptbto.cra.webservice.dao.IRemessaWS")
@Path("/RemessaService")
public class RemessaServiceImpl implements IRemessaWS {

	@Resource
	private WebServiceContext wsctx;
	public static final Logger logger = Logger.getLogger(RemessaServiceImpl.class);
	private UsuarioMediator usuarioMediator;
	private Usuario usuario;
	private ClassPathXmlApplicationContext context;
	private RemessaService remessaService;
	private ConfirmacaoService confirmacaoService;

	@Override
	@WebMethod(operationName = "Remessa")
	@GET
	public String remessa(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {

		init(login, senha);
		return remessaService.processar(nomeArquivo, getUsuario(), dados);
	}

	@Override
	public String confirmacao(String nomeArquivo, String login, String senha) {
		init(login, senha);
		return confirmacaoService.processar(nomeArquivo, getUsuario());
	}

	@Override
	public String retorno(String nomeArquivo, String login, String senha) {
		init(login, senha);
		return null;
	}

	@Override
	public String cancelamento(String nomeArquivo, String login, String senha, String dados) {
		init(login, senha);
		return null;
	}

	@Override
	public String desistencia(String nomeArquivo, String login, String senha, String dados) {
		init(login, senha);
		return null;
	}

	@Override
	public String autorizacaoCancelamento(String nomeArquivo, String login, String senha, String dados) {
		init(login, senha);
		return null;
	}

	private void init(String login, String senha) {
		context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		remessaService = (RemessaService) context.getBean("remessaService");
		confirmacaoService = (ConfirmacaoService) context.getBean("confirmacaoService");
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");
		setUsuario(login, senha);
	}

	private void setUsuario(String login, String senha) {
		logger.info("Inicio WebService pelo usuario= " + login);
		this.usuario = usuarioMediator.autenticar(login, senha);
	}

	public Usuario getUsuario() {
		return usuario;
	}

}
