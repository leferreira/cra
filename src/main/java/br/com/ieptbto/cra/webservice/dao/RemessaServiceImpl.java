package br.com.ieptbto.cra.webservice.dao;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;

/**
 * 
 * @author Lefer
 *
 */
@WebService(name = "/RemessaService", endpointInterface = "br.com.ieptbto.cra.webservice.dao.IRemessaWS")
@Path("/RemessaService")
public class RemessaServiceImpl implements IRemessaWS {

	public static final Logger logger = Logger.getLogger(RemessaServiceImpl.class);

	@Resource
	private WebServiceContext wsctx;
	private UsuarioMediator usuarioMediator;
	private Usuario usuario;
	private ClassPathXmlApplicationContext context;
	private RemessaService remessaService;
	private ConfirmacaoService confirmacaoService;
	private RetornoService retornoService;
	private DesistenciaProtestoService desistenciaProtestoService;
	private ComarcasHomologadasService comarcasHomologadasService;

	@Override
	@WebMethod(operationName = "Remessa")
	@GET
	public String remessa(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return remessaService.processar(nomeArquivo, getUsuario(), dados);
	}

	@WebMethod(operationName = "BuscarRemessa")
	@Override
	public String buscarRemessa(String nomeArquivo, String login, String senha) {
		init(login, senha);
		return remessaService.buscarRemessa(nomeArquivo, getUsuario());
	}

	@Override
	@WebMethod(operationName = "EnviarConfirmacao")
	@GET
	public String enviarConfirmacao(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return confirmacaoService.enviarConfirmacao(nomeArquivo, getUsuario(), dados);
	}

	@WebMethod(operationName = "Confirmacao")
	@Override
	public String confirmacao(String nomeArquivo, String login, String senha) {
		init(login, senha);
		return confirmacaoService.processar(nomeArquivo, getUsuario());
	}

	@Override
	@WebMethod(operationName = "Retorno")
	public String retorno(String nomeArquivo, String login, String senha) {
		init(login, senha);
		return retornoService.processar(nomeArquivo, getUsuario());
	}

	@WebMethod(operationName = "Cancelamento")
	@Override
	public String cancelamento(String nomeArquivo, String login, String senha, String dados) {
		init(login, senha);
		return null;
	}

	@Override
	@WebMethod(operationName = "Desistencia")
	@GET
	public String desistencia(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return desistenciaProtestoService.processar(getUsuario().getInstituicao().getLayoutPadraoXML() ,nomeArquivo, getUsuario(), dados);
	}

	@Override
	@WebMethod(operationName = "AutorizacaoCancelamento")
	public String autorizacaoCancelamento(String nomeArquivo, String login, String senha, String dados) {
		init(login, senha);
		return null;
	}
	
	@Override
	@WebMethod(operationName = "ComarcasHomologadas")
	@GET
	public String comarcasHomologadas(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha, 
			@WebParam(name = "codapres") String codigoApresentante) {
		init(login, senha);
		return comarcasHomologadasService.verificarComarcasHomologadas(usuario, codigoApresentante);
	}
	
	private void init(String login, String senha) {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}
		remessaService = (RemessaService) context.getBean("remessaService");
		confirmacaoService = (ConfirmacaoService) context.getBean("confirmacaoService");
		retornoService = (RetornoService) context.getBean("retornoService");
		desistenciaProtestoService = (DesistenciaProtestoService) context.getBean("desistenciaProtestoService");
		comarcasHomologadasService = (ComarcasHomologadasService) context.getBean("comarcasHomologadasService");
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");
		setUsuario(login, senha);
	}

	private void setUsuario(String login, String senha) {
		logger.info("Inicio WebService pelo usuario= " + login);
		this.usuario = usuarioMediator.autenticarWS(login, senha);
	}

	public Usuario getUsuario() {
		return usuario;
	}

}
