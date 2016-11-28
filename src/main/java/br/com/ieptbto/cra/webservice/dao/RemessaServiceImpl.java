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
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.ieptbto.cra.entidade.Usuario;
import br.com.ieptbto.cra.mediator.UsuarioMediator;
import br.com.ieptbto.cra.util.DataUtil;

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
	private ClassPathXmlApplicationContext context;
	private RemessaService remessaService;
	private ConfirmacaoService confirmacaoService;
	private RetornoService retornoService;
	private DesistenciaProtestoService desistenciaProtestoService;
	private CancelamentoProtestoService cancelamentoProtestoService;
	private AutorizacaoCancelamentoService autorizacaoCancelamentoService;
	private UsuariosComarcasHomologadasService usuariosComarcasHomologadasService;
	private CartorioService cartorioService;

	@Override
	@WebMethod(operationName = "remessa")
	@GET
	public String remessa(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		Usuario usuario = init(login, senha, "remessa");
		return remessaService.processar(nomeArquivo, usuario, dados);
	}

	@Override
	@WebMethod(operationName = "buscarRemessa")
	@GET
	public String buscarRemessa(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "buscarRemessa");
		return remessaService.buscarRemessa(nomeArquivo, usuario);
	}

	@Override
	@WebMethod(operationName = "confirmacao")
	@GET
	public String confirmacao(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "confirmacao");
		return confirmacaoService.processar(nomeArquivo, usuario);
	}

	@Override
	@WebMethod(operationName = "enviarConfirmacao")
	@GET
	public String enviarConfirmacao(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		Usuario usuario = init(login, senha, "enviarConfirmacao");
		return confirmacaoService.enviarConfirmacao(nomeArquivo, usuario, dados);
	}

	@Override
	@WebMethod(operationName = "retorno")
	@GET
	public String retorno(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "retorno");
		return retornoService.processar(nomeArquivo, usuario);
	}

	@Override
	@WebMethod(operationName = "enviarRetorno")
	@GET
	public String enviarRetorno(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		Usuario usuario = init(login, senha, "enviarRetorno");
		return retornoService.enviarRetorno(nomeArquivo, usuario, dados);
	}

	@Override
	@WebMethod(operationName = "buscarDesistenciaCancelamento")
	@GET
	public String buscarDesistenciaCancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "buscarDesistenciaCancelamento");
		return desistenciaProtestoService.buscarDesistenciaCancelamento(nomeArquivo, usuario);
	}

	@Override
	@WebMethod(operationName = "cancelamento")
	@GET
	public String cancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		Usuario usuario = init(login, senha, "cancelamento");
		return cancelamentoProtestoService.processar(nomeArquivo, usuario, dados);
	}

	@Override
	@WebMethod(operationName = "desistencia")
	@GET
	public String desistencia(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		Usuario usuario = init(login, senha, "desistencia");
		return desistenciaProtestoService.processar(nomeArquivo, usuario, dados);
	}

	@Override
	@WebMethod(operationName = "confirmarEnvioConfirmacaoRetorno")
	@GET
	public String confirmarEnvioConfirmacaoRetorno(@WebParam(name = "user_arq") String nomeArquivo,
			@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "confirmarEnvioConfirmacaoRetorno");
		return cartorioService.confirmarEnvioConfirmacaoRetorno(nomeArquivo, usuario);
	}

	@Override
	@WebMethod(operationName = "confirmarRecebimentoDesistenciaCancelamento")
	@GET
	public String confirmarRecebimentoDesistenciaCancelamento(@WebParam(name = "user_arq") String nomeArquivo,
			@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "confirmarRecebimentoDesistenciaCancelamento");
		return desistenciaProtestoService.confirmarRecebimentoDesistenciaCancelamento(nomeArquivo, usuario);
	}

	@Override
	@WebMethod(operationName = "autorizacaoCancelamento")
	@GET
	public String autorizacaoCancelamento(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
			@WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		Usuario usuario = init(login, senha, "autorizacaoCancelamento");
		return autorizacaoCancelamentoService.processar(nomeArquivo, usuario, dados);
	}

	@Override
	@WebMethod(operationName = "comarcasHomologadas")
	@GET
	public String comarcasHomologadas(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "codapres") String codigoApresentante) {
		Usuario usuario = init(login, senha, "comarcasHomologadas");
		return usuariosComarcasHomologadasService.verificarComarcasHomologadas(usuario, codigoApresentante);
	}

	@Override
	@WebMethod(operationName = "arquivosPendentesCartorio")
	@GET
	public String arquivosPendentesCartorio(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "arquivosPendentesCartorio");
		return cartorioService.buscarArquivosPendentesCartorio(usuario);
	}

	@Override
	@WebMethod(operationName = "desistenciasPendentesCartorio")
	@GET
	public String desistenciasPendentesCartorio(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "desistenciasPendentesCartorio");
		return cartorioService.buscarDesistenciaPendentesCartorio(usuario);
	}

	@Override
	@WebMethod(operationName = "verificarAcessoUsuario")
	@GET
	public String verificarAcessoUsuario(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha) {
		Usuario usuario = init(login, senha, "verificarAcessoUsuario");
		return usuariosComarcasHomologadasService.verificarAcessoUsuario(usuario);
	}

	@WebMethod(operationName = "consultaDadosApresentante")
	@GET
	public String consultaDadosApresentante(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha,
			@WebParam(name = "apres_code") String codigoAprensentante) {
		Usuario usuario = init(login, senha, "consultaDadosApresentante");
		return cartorioService.consultaDadosApresentante(usuario, codigoAprensentante);
	}

	private Usuario init(String login, String senha, String metodo) {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		}
		this.remessaService = (RemessaService) context.getBean("remessaService");
		this.confirmacaoService = (ConfirmacaoService) context.getBean("confirmacaoService");
		this.retornoService = (RetornoService) context.getBean("retornoService");
		this.desistenciaProtestoService = (DesistenciaProtestoService) context.getBean("desistenciaProtestoService");
		this.cancelamentoProtestoService = (CancelamentoProtestoService) context.getBean("cancelamentoProtestoService");
		this.autorizacaoCancelamentoService = (AutorizacaoCancelamentoService) context.getBean("autorizacaoCancelamentoService");
		this.usuariosComarcasHomologadasService =
				(UsuariosComarcasHomologadasService) context.getBean("usuariosComarcasHomologadasService");
		this.cartorioService = (CartorioService) context.getBean("cartorioService");
		this.usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");

		logger.info("[ " + DataUtil.localDateToString(new LocalDate()) + " " + DataUtil.localTimeToString("HH:mm:ss", new LocalTime())
				+ " ] ======= WebService ====== Autenticando : " + login + "... ");
		Usuario usuario = usuarioMediator.autenticarWS(login, senha);
		if (usuario != null) {
			logger.info("[ " + DataUtil.localDateToString(new LocalDate()) + " " + DataUtil.localTimeToString("HH:mm:ss", new LocalTime())
					+ " ] ======= WebService ====== Usuario : " + login + " ===== Serviço: " + metodo + " =======");
		} else {
			logger.info("[ " + DataUtil.localDateToString(new LocalDate()) + " " + DataUtil.localTimeToString("HH:mm:ss", new LocalTime())
					+ " ] ======= WebService ====== Falha na autenticação do usuário " + login + " !");
		}
		return usuario;
	}
}