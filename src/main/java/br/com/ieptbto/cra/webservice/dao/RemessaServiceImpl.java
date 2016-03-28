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
	private CancelamentoProtestoService cancelamentoProtestoService;
	private AutorizacaoCancelamentoService autorizacaoCancelamentoService;
	private UsuariosComarcasHomologadasService usuariosComarcasHomologadasService;
	private ArquivosPendentesCartorioService arquivosPendentesCartorioService;

	@Override
	@WebMethod(operationName = "remessa")
	@GET
	public String remessa(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return remessaService.processar(nomeArquivo, getUsuario(), dados);
	}

	@Override
	@WebMethod(operationName = "buscarRemessa")
	@GET
	public String buscarRemessa(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return remessaService.buscarRemessa(nomeArquivo, getUsuario());
	}

	@Override
	@WebMethod(operationName = "confirmacao")
	@GET
	public String confirmacao(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return confirmacaoService.processar(nomeArquivo, getUsuario());
	}

	@Override
	@WebMethod(operationName = "enviarConfirmacao")
	@GET
	public String enviarConfirmacao(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return confirmacaoService.enviarConfirmacao(nomeArquivo, getUsuario(), dados);
	}

	@Override
	@WebMethod(operationName = "retorno")
	@GET
	public String retorno(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return retornoService.processar(nomeArquivo, getUsuario());
	}

	@Override
	@WebMethod(operationName = "enviarRetorno")
	@GET
	public String enviarRetorno(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return retornoService.processar(nomeArquivo, getUsuario(), dados);
	}

	@Override
	@WebMethod(operationName = "buscarDesistenciaCancelamento")
	@GET
	public String buscarDesistenciaCancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return desistenciaProtestoService.buscarDesistenciaCancelamento(nomeArquivo, getUsuario());
	}

	@Override
	@WebMethod(operationName = "cancelamento")
	@GET
	public String cancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return cancelamentoProtestoService.processar(nomeArquivo, getUsuario(), dados);
	}

	@Override
	@WebMethod(operationName = "desistencia")
	@GET
	public String desistencia(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return desistenciaProtestoService.processar(nomeArquivo, getUsuario(), dados);
	}

	@Override
	@WebMethod(operationName = "autorizacaoCancelamento")
	@GET
	public String autorizacaoCancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		init(login, senha);
		return autorizacaoCancelamentoService.processar(nomeArquivo, getUsuario(), dados);
	}

	@Override
	@WebMethod(operationName = "comarcasHomologadas")
	@GET
	public String comarcasHomologadas(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "codapres") String codigoApresentante) {
		init(login, senha);
		return usuariosComarcasHomologadasService.verificarComarcasHomologadas(usuario, codigoApresentante);
	}

	@Override
	@WebMethod(operationName = "arquivosPendentesCartorio")
	@GET
	public String arquivosPendentesCartorio(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return arquivosPendentesCartorioService.buscarArquivosPendentesCartorio(getUsuario());
	}

	@Override
	@WebMethod(operationName = "verificarAcessoUsuario")
	@GET
	public String verificarAcessoUsuario(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		init(login, senha);
		return usuariosComarcasHomologadasService.verificarAcessoUsuario(getUsuario());
	}

	private void init(String login, String senha) {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}
		remessaService = (RemessaService) context.getBean("remessaService");
		confirmacaoService = (ConfirmacaoService) context.getBean("confirmacaoService");
		retornoService = (RetornoService) context.getBean("retornoService");
		desistenciaProtestoService = (DesistenciaProtestoService) context.getBean("desistenciaProtestoService");
		cancelamentoProtestoService = (CancelamentoProtestoService) context.getBean("cancelamentoProtestoService");
		autorizacaoCancelamentoService = (AutorizacaoCancelamentoService) context.getBean("autorizacaoCancelamentoService");
		usuariosComarcasHomologadasService = (UsuariosComarcasHomologadasService) context.getBean("usuariosComarcasHomologadasService");
		arquivosPendentesCartorioService = (ArquivosPendentesCartorioService) context.getBean("arquivosPendentesCartorioService");
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
