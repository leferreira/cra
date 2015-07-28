package br.com.ieptbto.cra.webservice.dao;

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

/**
 * @author Thasso Araújo
 *
 */
@WebService(name = "/CartorioProtestoService", endpointInterface = "br.com.ieptbto.cra.webservice.dao.ICartorioProtestoWS")
@Path("/CartorioProtestoService")
public class CartorioProtestoServiceImpl implements ICartorioProtestoWS {

	@Resource
	private WebServiceContext wsctx;
	public static final Logger logger = Logger.getLogger(CartorioProtestoServiceImpl.class);
	private ClassPathXmlApplicationContext context;
	private UsuarioMediator usuarioMediator;
	private CartorioProtestoService cartorioService;
	private Usuario usuario;
	
	@Override
	public String remessaCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha) {
		
		init(login, senha);
		return cartorioService.processarRemessa(nomeArquivo, getUsuario());
	}

	@Override
	public String confirmacaoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		
		init(login, senha);
		return cartorioService.processarConfirmacao(nomeArquivo, getUsuario(), dados);
	}

	@Override
	public String retornoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {
		
		init(login, senha);
		return cartorioService.processarRetorno(nomeArquivo, getUsuario(), dados);
	}

	@Override
	public String cancelamentoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {

		init(login, senha);
		return null;
	}

	@Override
	public String desistenciaCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {

		init(login, senha);
		return null;
	}

	@Override
	public String autorizacaoCancelamentoCartorio(@WebParam(name = "user_arq") String nomeArquivo, @WebParam(name = "user_code") String login,
	        @WebParam(name = "user_pass") String senha, @WebParam(name = "user_dados") String dados) {

		init(login, senha);
		return null;
	}
	
	@Override
	@WebMethod(operationName = "Ocorrencia")
	@GET
	public String ocorrencia(@WebParam(name = "user_code") String login, @WebParam(name = "user_pass") String senha, 
			@WebParam(name = "id_ocorrencia") String ocorrencia, @WebParam(name = "data_ocorrencia") String dataOcorrencia, 
			@WebParam(name = "cod_portador") String codigoPortador,@WebParam(name = "nosso_num") String nossoNumero,
			@WebParam(name = "num_titulo") String numeroTitulo) {
		
		init(login, senha);
		return cartorioService.processarOcorrencia(codigoPortador, nossoNumero, numeroTitulo, ocorrencia, dataOcorrencia);
	}
	
	private void init(String login, String senha) {
		context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		cartorioService = (CartorioProtestoService) context.getBean("cartorioProtestoService");
		usuarioMediator = (UsuarioMediator) context.getBean("usuarioMediator");
		setUsuario(login, senha);
	}

	private void setUsuario(String login, String pass) {
		logger.info("Inicio WebService pelo usuario= " + login);
		this.usuario = usuarioMediator.autenticar(login, pass);
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
}
